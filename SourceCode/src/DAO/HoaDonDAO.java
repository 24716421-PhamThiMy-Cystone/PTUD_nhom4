package DAO;


import Entity.HoaDon;
import Entity.ChiTietHoaDon;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import ConnectDB.Connect;

public class HoaDonDAO {

    public boolean themHoaDon(HoaDon hd) {
        String sqlHD = """
            INSERT INTO HoaDon
            (maHD, ngayLap, phuongThucTT, trangThai, tongTien,
             tienKhachTra, tienDatCoc, maNV, maKH, maUD)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        String sqlCT = """
            INSERT INTO ChiTietHoaDon
            (maHD, maSP, soLuong, donGia, thanhTien,
             phanTramThue, tienThue, tienChietKhau)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        Connection conn = null;

        try {
            conn = Connect.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement psHD = conn.prepareStatement(sqlHD)) {
                psHD.setString(1, hd.getMaHD());
                psHD.setTimestamp(2,
                    java.sql.Timestamp.valueOf(hd.getNgayLap()));
                psHD.setString(3, hd.getPhuongThucTT());
                psHD.setString(4, hd.getTrangThai());
                psHD.setDouble(5, hd.getTongTien());
                psHD.setDouble(6, hd.getTienKhachTra());
                psHD.setDouble(7, hd.getTienDatCoc());
                psHD.setString(8, hd.getMaNV());
                psHD.setString(9, hd.getMaKH());

                if (hd.getMaUD() == null || hd.getMaUD().isBlank()) {
                    psHD.setNull(10, java.sql.Types.VARCHAR);
                } else {
                    psHD.setString(10, hd.getMaUD());
                }

                psHD.executeUpdate();
            }

            try (PreparedStatement psCT = conn.prepareStatement(sqlCT)) {
                for (ChiTietHoaDon ct : hd.getDsChiTiet()) {
                    psCT.setString(1, hd.getMaHD());
                    psCT.setString(2, ct.getMaSP());
                    psCT.setInt(3, ct.getSoLuong());
                    psCT.setDouble(4, ct.getDonGia());
                    psCT.setDouble(5, ct.getThanhTien());
                    psCT.setDouble(6, ct.getPhanTramThue());
                    psCT.setDouble(7, ct.getTienThue());
                    psCT.setDouble(8, ct.getTienChietKhau());
                    psCT.addBatch();
                }
                psCT.executeBatch();
            }

            conn.commit();
            return true;

        } catch (Exception e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;

        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean capNhatThanhToan(
            String maHD, String phuongThucTT, double tienKhachTra) {

        String sql = """
            UPDATE HoaDon
            SET phuongThucTT = ?,
                trangThai = N'Đã thanh toán',
                tienKhachTra = ?
            WHERE maHD = ?
            """;

        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, phuongThucTT);
            ps.setDouble(2, tienKhachTra);
            ps.setString(3, maHD);

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}