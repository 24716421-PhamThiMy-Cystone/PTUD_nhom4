package DAO;

import Entity.CaLam;
import Entity.NhanVien;
import Entity.PhanCongCaTruc;
import Util.ConnectDB;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PhanCongCaTrucDAO {
    public boolean kiemTraTrungCa(String maNV, Date ngay, java.sql.Time gioBD, java.sql.Time gioKT) throws SQLException {
        return kiemTraTrungCa(maNV, ngay, gioBD, gioKT, null);
    }

    public boolean kiemTraTrungCa(String maNV, Date ngay, java.sql.Time gioBD,
            java.sql.Time gioKT, String maPhanCongBoQua) throws SQLException {
        String sql="SELECT COUNT(*) FROM PhanCongCaTruc pc JOIN CaLam ca ON ca.maCa=pc.maCa "
                + "WHERE pc.maNV=? AND pc.ngayTruc=? "
                + "AND CONVERT(TIME, ca.gioBatDau) < CONVERT(TIME, ?) "
                + "AND CONVERT(TIME, ca.gioKetThuc) > CONVERT(TIME, ?) "
                + "AND (? IS NULL OR pc.maPhanCong<>?)";
        try(Connection con=ConnectDB.getConnection(); PreparedStatement ps=con.prepareStatement(sql)) {
            ps.setString(1,maNV); ps.setDate(2,ngay); ps.setTime(3,gioKT); ps.setTime(4,gioBD);
            ps.setString(5, maPhanCongBoQua); ps.setString(6, maPhanCongBoQua);
            try(ResultSet rs=ps.executeQuery()) { return rs.next() && rs.getInt(1)>0; }
        }
    }

    public boolean capNhat(PhanCongCaTruc assignment) throws SQLException {
        String sql = "UPDATE PhanCongCaTruc SET maNV=?, maCa=?, ngayTruc=? WHERE maPhanCong=?";
        try (Connection con = ConnectDB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, assignment.getNhanVien().getMaNV());
            ps.setString(2, assignment.getCaLam().getMaCa());
            ps.setDate(3, Date.valueOf(assignment.getNgayTruc()));
            ps.setString(4, assignment.getMaPhanCong());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean xoa(String maPhanCong) throws SQLException {
        try (Connection con = ConnectDB.getConnection(); PreparedStatement ps =
                con.prepareStatement("DELETE FROM PhanCongCaTruc WHERE maPhanCong=?")) {
            ps.setString(1, maPhanCong);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean them(PhanCongCaTruc pc) throws SQLException {
        String sql="INSERT INTO PhanCongCaTruc(maPhanCong,maNV,maCa,ngayTruc) VALUES(?,?,?,?)";
        try(Connection con=ConnectDB.getConnection(); PreparedStatement ps=con.prepareStatement(sql)) {
            ps.setString(1,pc.getMaPhanCong()==null?phatSinhMaPhanCong(con):pc.getMaPhanCong());
            ps.setString(2,pc.getNhanVien().getMaNV()); ps.setString(3,pc.getCaLam().getMaCa()); ps.setDate(4,Date.valueOf(pc.getNgayTruc()));
            return ps.executeUpdate()>0;
        }
    }

    public List<PhanCongCaTruc> getTheoNhanVien(String maNV) throws SQLException { return truyVan("WHERE pc.maNV=?",maNV,null); }
    public List<PhanCongCaTruc> getTheoNgay(Date ngay) throws SQLException { return truyVan("WHERE pc.ngayTruc=?",null,ngay); }

    private List<PhanCongCaTruc> truyVan(String dieuKien,String maNV,Date ngay) throws SQLException {
        String sql="SELECT pc.maPhanCong,pc.ngayTruc,nv.maNV,nv.hoten,ca.maCa,ca.maLoaiCa,ca.ngay,ca.tenCa,ca.gioBatDau,ca.gioKetThuc "
                + "FROM PhanCongCaTruc pc JOIN NhanVien nv ON nv.maNV=pc.maNV JOIN CaLam ca ON ca.maCa=pc.maCa "
                + dieuKien + " ORDER BY pc.ngayTruc,ca.gioBatDau";
        List<PhanCongCaTruc> result=new ArrayList<>();
        try(Connection con=ConnectDB.getConnection(); PreparedStatement ps=con.prepareStatement(sql)) {
            if(maNV!=null) ps.setString(1,maNV); else ps.setDate(1,ngay);
            try(ResultSet rs=ps.executeQuery()) {
                while(rs.next()) {
                    NhanVien nv=new NhanVien(); nv.setMaNV(rs.getString("maNV")); nv.setHoTen(rs.getString("hoten"));
                    CaLam ca=new CaLam(rs.getString("maCa"),rs.getString("maLoaiCa"),rs.getString("tenCa"),
                            rs.getTime("gioBatDau").toLocalTime(),rs.getTime("gioKetThuc").toLocalTime(),
                            rs.getDate("ngay").toLocalDate());
                    result.add(new PhanCongCaTruc(rs.getString("maPhanCong"),nv,ca,rs.getDate("ngayTruc").toLocalDate()));
                }
            }
        }
        return result;
    }

    private String phatSinhMaPhanCong(Connection con) throws SQLException {
        String sql="SELECT MAX(TRY_CONVERT(INT,SUBSTRING(maPhanCong,3,6))) FROM PhanCongCaTruc WHERE maPhanCong LIKE 'PC[0-9][0-9][0-9][0-9][0-9][0-9]'";
        try(PreparedStatement ps=con.prepareStatement(sql); ResultSet rs=ps.executeQuery()) {
            int next=1; if(rs.next()) next=rs.getInt(1)+1; return String.format("PC%06d",next);
        }
    }
}

