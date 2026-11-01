package DAO;

import Entity.NhanVien;
import Entity.TaiKhoan;
import Entity.ThongTinTaiKhoanMoi;
import Entity.VaiTro;
import Util.ConnectDB;
import Util.PasswordUtil;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NhanVienDAO {
    private static final String SELECT_JOIN = "SELECT nv.*, tk.tenDangNhap, tk.vaiTro, tk.trangThai "
            + "FROM NhanVien nv LEFT JOIN TaiKhoan tk ON tk.maNV=nv.maNV ";

    public List<NhanVien> getAll() throws SQLException {
        List<NhanVien> result = new ArrayList<>();
        try (Connection con = ConnectDB.getConnection(); PreparedStatement ps = con.prepareStatement(SELECT_JOIN + "ORDER BY nv.maNV"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) result.add(map(rs));
        }
        return result;
    }

    public NhanVien timTheoMa(String maNV) throws SQLException {
        try (Connection con = ConnectDB.getConnection(); PreparedStatement ps = con.prepareStatement(SELECT_JOIN + "WHERE nv.maNV=?")) {
            ps.setString(1, maNV);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }

    /** Generates the employee code and linked account, then saves both atomically. */
    public ThongTinTaiKhoanMoi themTuDong(NhanVien nv, VaiTro vaiTro) throws SQLException {
        String sqlNV = "INSERT INTO NhanVien(maNV,hoTen,ngaySinh,diaChi,ngayVaoLam,soDienThoai,email,trangThai) "
                + "VALUES(?,?,?,?,?,?,?,?)";
        String sqlTK = "INSERT INTO TaiKhoan(tenDangNhap,matKhau,matKhauHash,maNV,vaiTro,trangThai) VALUES(?,?,?,?,?,?)";
        try (Connection con = ConnectDB.getConnection()) {
            boolean oldAutoCommit = con.getAutoCommit();
            int oldIsolation = con.getTransactionIsolation();
            con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            con.setAutoCommit(false);
            try {
                nv.setMaNV(phatSinhMaNV(con, nv.getNgayVaoLam()));
                String username = phatSinhTenDangNhap(con, nv.getMaNV());
                String rawPassword = PasswordUtil.taoMatKhau(username);
                String passwordHash = PasswordUtil.sha256(rawPassword);
                try (PreparedStatement ps = con.prepareStatement(sqlNV)) { setNhanVien(ps, nv); ps.executeUpdate(); }
                try (PreparedStatement ps = con.prepareStatement(sqlTK)) {
                    ps.setString(1, username); ps.setString(2, passwordHash);
                    ps.setString(3, passwordHash); ps.setString(4, nv.getMaNV());
                    ps.setString(5, vaiTro.name()); ps.setString(6, "Hoạt động");
                    ps.executeUpdate();
                }
                con.commit();
                return new ThongTinTaiKhoanMoi(nv.getMaNV(), username, rawPassword);
            } catch (SQLException | RuntimeException ex) {
                con.rollback();
                if (ex instanceof SQLException) throw (SQLException) ex;
                throw ex;
            } finally {
                con.setAutoCommit(oldAutoCommit);
                con.setTransactionIsolation(oldIsolation);
            }
        }
    }

    public boolean capNhat(NhanVien nv) throws SQLException {
        String sql = "UPDATE NhanVien SET hoTen=?,ngaySinh=?,diaChi=?,ngayVaoLam=?,"
                + "soDienThoai=?,email=?,trangThai=? WHERE maNV=?";
        try (Connection con = ConnectDB.getConnection()) {
            boolean oldAutoCommit = con.getAutoCommit(); con.setAutoCommit(false);
            try {
                int rows;
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1,nv.getHoTen()); ps.setDate(2,Date.valueOf(nv.getNgaySinh())); ps.setString(3,nv.getDiaChi());
                    ps.setDate(4,Date.valueOf(nv.getNgayVaoLam()));
                    ps.setString(5,nv.getSoDienThoai()); ps.setString(6,nv.getEmail());
                    ps.setBoolean(7,nv.isDangLam()); ps.setString(8,nv.getMaNV());
                    rows = ps.executeUpdate();
                }
                if (rows == 0) { con.rollback(); return false; }
                if (nv.getTaiKhoan() != null) {
                    try (PreparedStatement ps = con.prepareStatement("UPDATE TaiKhoan SET vaiTro=? WHERE maNV=?")) {
                        ps.setString(1,nv.getTaiKhoan().getVaiTro().name()); ps.setString(2,nv.getMaNV()); ps.executeUpdate();
                    }
                }
                con.commit(); return true;
            } catch (SQLException | RuntimeException ex) {
                con.rollback(); if (ex instanceof SQLException) throw (SQLException) ex; throw ex;
            } finally { con.setAutoCommit(oldAutoCommit); }
        }
    }

    public boolean kiemTraTrung(String soDienThoai, String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM NhanVien WHERE soDienThoai=? OR LOWER(email)=LOWER(?)";
        try (Connection con = ConnectDB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1,soDienThoai); ps.setString(2,email);
            try (ResultSet rs=ps.executeQuery()) { return rs.next() && rs.getInt(1)>0; }
        }
    }

    public boolean kiemTraTrung(String soDienThoai, String email, String maNVBohQua) throws SQLException {
        String sql = "SELECT COUNT(*) FROM NhanVien WHERE (soDienThoai=? OR LOWER(email)=LOWER(?)) AND maNV<>?";
        try (Connection con=ConnectDB.getConnection(); PreparedStatement ps=con.prepareStatement(sql)) {
            ps.setString(1,soDienThoai); ps.setString(2,email); ps.setString(3,maNVBohQua);
            try (ResultSet rs=ps.executeQuery()) { return rs.next() && rs.getInt(1)>0; }
        }
    }

    private String phatSinhMaNV(Connection con, LocalDate ngayVaoLam) throws SQLException {
        String prefix = String.format("NV%02d", ngayVaoLam.getYear() % 100);
        String sql = "SELECT MAX(TRY_CONVERT(INT,RIGHT(maNV,4))) FROM dbo.NhanVien WITH (UPDLOCK,HOLDLOCK) WHERE maNV LIKE ?";
        try (PreparedStatement ps=con.prepareStatement(sql)) {
            ps.setString(1, prefix + "[0-9][0-9][0-9][0-9]");
            try (ResultSet rs=ps.executeQuery()) {
                int max = 0;
                if (rs.next()) max = rs.getInt(1);
                int next = max + 1;
                if (next > 9999) throw new SQLException("Đã hết số thứ tự mã nhân viên cho năm " + ngayVaoLam.getYear() + ".");
                return prefix + String.format("%04d", next);
            }
        }
    }

    private String phatSinhTenDangNhap(Connection con, String maNV) throws SQLException {
        String base = "nv" + maNV.substring(2);
        String candidate = base;
        int suffix = 1;
        String sql = "SELECT 1 FROM dbo.TaiKhoan WITH (UPDLOCK,HOLDLOCK) WHERE tenDangNhap=?";
        while (true) {
            try (PreparedStatement ps=con.prepareStatement(sql)) {
                ps.setString(1, candidate);
                try (ResultSet rs=ps.executeQuery()) {
                    if (!rs.next()) return candidate;
                }
            }
            candidate = base + suffix++;
            if (candidate.length() > 30) throw new SQLException("Không thể phát sinh tên đăng nhập duy nhất.");
        }
    }

    private static void setNhanVien(PreparedStatement ps, NhanVien nv) throws SQLException {
        ps.setString(1,nv.getMaNV()); ps.setString(2,nv.getHoTen()); ps.setDate(3,Date.valueOf(nv.getNgaySinh()));
        ps.setString(4,nv.getDiaChi()); ps.setDate(5,Date.valueOf(nv.getNgayVaoLam()));
        ps.setString(6,nv.getSoDienThoai()); ps.setString(7,nv.getEmail()); ps.setBoolean(8,nv.isDangLam());
    }

    private static NhanVien map(ResultSet rs) throws SQLException {
        NhanVien nv = new NhanVien(rs.getString("maNV"),rs.getString("hoTen"),rs.getDate("ngaySinh").toLocalDate(),
                rs.getString("diaChi"),rs.getDate("ngayVaoLam").toLocalDate(),
                rs.getString("soDienThoai"),rs.getString("email"));
        boolean dangLam = rs.getBoolean("trangThai");
        nv.setDangLam(rs.wasNull() || dangLam);
        String username=rs.getString("tenDangNhap");
        if (username!=null) nv.setTaiKhoan(new TaiKhoan(username,"********",nv,VaiTro.fromDatabase(rs.getString("vaiTro")),rs.getString("trangThai")));
        return nv;
    }
}

