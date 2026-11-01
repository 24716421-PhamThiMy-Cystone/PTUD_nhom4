package DAO;

import Entity.CaLam;
import Entity.MauCaLam;
import Util.ConnectDB;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CaLamDAO {
    private static final DateTimeFormatter MA_NGAY = DateTimeFormatter.ofPattern("yyMMdd");

    /** Trả về ba lựa chọn ca mẫu, không đưa mã ca ngày vào giao diện. */
    public List<MauCaLam> getMauCa() throws SQLException {
        String sql = "SELECT maLoaiCa, tenCa, gioBatDau, gioKetThuc FROM MauCaLam ORDER BY maLoaiCa";
        List<MauCaLam> result = new ArrayList<>();
        try (Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(new MauCaLam(rs.getString("maLoaiCa"), rs.getString("tenCa"),
                        rs.getTime("gioBatDau").toLocalTime(), rs.getTime("gioKetThuc").toLocalTime()));
            }
        }
        return result;
    }

    public MauCaLam getMauCa(String maLoaiCa) throws SQLException {
        String sql = "SELECT maLoaiCa, tenCa, gioBatDau, gioKetThuc FROM MauCaLam WHERE maLoaiCa=?";
        try (Connection con = ConnectDB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maLoaiCa);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new MauCaLam(rs.getString("maLoaiCa"), rs.getString("tenCa"),
                        rs.getTime("gioBatDau").toLocalTime(), rs.getTime("gioKetThuc").toLocalTime());
            }
        }
    }

    /** Tạo một mã dùng chung cho đúng một loại ca trong đúng một ngày. */
    public CaLam taoHoacLayCaNgay(String maLoaiCa, LocalDate ngay) throws SQLException {
        try (Connection con = ConnectDB.getConnection()) {
            CaLam existing = timCaNgay(con, maLoaiCa, ngay);
            if (existing != null) return existing;

            MauCaLam mau = timMauCa(con, maLoaiCa);
            if (mau == null) throw new SQLException("Không tìm thấy loại ca " + maLoaiCa + ".");

            String maCa = "CA" + ngay.format(MA_NGAY) + maLoaiCa;
            String sql = "INSERT INTO CaLam(maCa, ngay, maLoaiCa, tenCa, gioBatDau, gioKetThuc) "
                    + "VALUES(?,?,?,?,?,?)";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, maCa);
                ps.setDate(2, Date.valueOf(ngay));
                ps.setString(3, maLoaiCa);
                ps.setString(4, mau.getTenCa());
                ps.setTime(5, Time.valueOf(mau.getGioBatDau()));
                ps.setTime(6, Time.valueOf(mau.getGioKetThuc()));
                ps.executeUpdate();
            }
            return new CaLam(maCa, maLoaiCa, mau.getTenCa(), mau.getGioBatDau(), mau.getGioKetThuc(), ngay);
        }
    }

    private MauCaLam timMauCa(Connection con, String maLoaiCa) throws SQLException {
        String sql = "SELECT maLoaiCa, tenCa, gioBatDau, gioKetThuc FROM MauCaLam WHERE maLoaiCa=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maLoaiCa);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new MauCaLam(rs.getString("maLoaiCa"), rs.getString("tenCa"),
                        rs.getTime("gioBatDau").toLocalTime(), rs.getTime("gioKetThuc").toLocalTime());
            }
        }
    }

    private CaLam timCaNgay(Connection con, String maLoaiCa, LocalDate ngay) throws SQLException {
        String sql = "SELECT maCa, maLoaiCa, tenCa, gioBatDau, gioKetThuc, ngay "
                + "FROM CaLam WHERE maLoaiCa=? AND ngay=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maLoaiCa);
            ps.setDate(2, Date.valueOf(ngay));
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new CaLam(rs.getString("maCa"), rs.getString("maLoaiCa"), rs.getString("tenCa"),
                        rs.getTime("gioBatDau").toLocalTime(), rs.getTime("gioKetThuc").toLocalTime(),
                        rs.getDate("ngay").toLocalDate());
            }
        }
    }
}
