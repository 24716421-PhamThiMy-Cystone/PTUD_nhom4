package DAO;

import Entity.SanPhamBanChay;
import Entity.ThongKeTongQuan;
import Util.ConnectDB;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ThongKeDAO {
    public ThongKeTongQuan thongKe(Date tuNgay, Date denNgay) throws SQLException {
        String sql = "SELECT COALESCE(SUM(CASE WHEN hd.trangThai=N'Đã thanh toán' "
                + "THEN ct.soLuong*ct.donGia ELSE 0 END),0) AS doanhThu, "
                + "COUNT(DISTINCT CASE WHEN hd.trangThai=N'Đã thanh toán' "
                + "THEN hd.maHD END) AS soHoaDon, "
                + "COALESCE(SUM(CASE WHEN hd.trangThai=N'Đã thanh toán' "
                + "THEN ct.soLuong ELSE 0 END),0) AS tongSanPham, "
                + "COUNT(DISTINCT CASE WHEN hd.trangThai LIKE N'%hủy%' "
                + "OR hd.trangThai LIKE N'%huỷ%' THEN hd.maHD END) AS soHoaDonHuy "
                + "FROM HoaDon hd LEFT JOIN ChiTietHoaDon ct ON ct.maHD=hd.maHD "
                + "WHERE hd.ngayLap>=? AND hd.ngayLap<DATEADD(day,1,?)";
        try (Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, tuNgay);
            ps.setDate(2, denNgay);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ThongKeTongQuan(
                            rs.getBigDecimal("doanhThu"),
                            rs.getInt("soHoaDon"),
                            rs.getInt("tongSanPham"),
                            rs.getInt("soHoaDonHuy"));
                }
            }
        }
        return new ThongKeTongQuan(null, 0, 0);
    }
    public List<SanPhamBanChay> topSanPham(Date tuNgay, Date denNgay, int gioiHan)
            throws SQLException {
        if (gioiHan < 1 || gioiHan > 100) {
            throw new IllegalArgumentException("Số sản phẩm thống kê phải từ 1 đến 100.");
        }
        String sql = "SELECT TOP (?) sp.maSP, sp.tenSP, SUM(ct.soLuong) AS soLuongBan, "
                + "SUM(ct.soLuong*ct.donGia) AS doanhThu "
                + "FROM HoaDon hd "
                + "JOIN ChiTietHoaDon ct ON ct.maHD=hd.maHD "
                + "JOIN SanPham sp ON sp.maSP=ct.maSP "
                + "WHERE hd.ngayLap>=? AND hd.ngayLap<DATEADD(day,1,?) "
                + "AND hd.trangThai=N'Đã thanh toán' "
                + "GROUP BY sp.maSP,sp.tenSP "
                + "ORDER BY SUM(ct.soLuong) DESC,sp.tenSP";
        List<SanPhamBanChay> result = new ArrayList<>();
        try (Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, gioiHan);
            ps.setDate(2, tuNgay);
            ps.setDate(3, denNgay);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new SanPhamBanChay(
                            rs.getString("maSP"), rs.getString("tenSP"),
                            rs.getInt("soLuongBan"), rs.getBigDecimal("doanhThu")));
                }
            }
        }
        return result;
    }

    /** Trả về tổng doanh thu theo từng ngày hoặc tháng trong khoảng đã chọn. */
    public Map<Date, BigDecimal> doanhThuTheoKy(
            Date tuNgay, Date denNgay, boolean theoThang) throws SQLException {
        String ky = theoThang
                ? "CONVERT(date,DATEADD(month,DATEDIFF(month,0,hd.ngayLap),0))"
                : "CONVERT(date,hd.ngayLap)";
        String sql = "SELECT " + ky + " AS ky, "
                + "COALESCE(SUM(ct.soLuong*ct.donGia),0) AS doanhThu "
                + "FROM HoaDon hd JOIN ChiTietHoaDon ct ON ct.maHD=hd.maHD "
                + "WHERE hd.ngayLap>=? AND hd.ngayLap<DATEADD(day,1,?) "
                + "AND hd.trangThai=N'Đã thanh toán' "
                + "GROUP BY " + ky + " ORDER BY ky";
        Map<Date,BigDecimal> result = new LinkedHashMap<>();
        try (Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, tuNgay);
            ps.setDate(2, denNgay);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.put(rs.getDate("ky"), rs.getBigDecimal("doanhThu"));
            }
        }
        return result;
    }

    /** Trả về phần doanh thu của từng danh mục sản phẩm. */
    public Map<String, BigDecimal> doanhThuTheoDanhMuc(Date tuNgay, Date denNgay)
            throws SQLException {
        String sql = "SELECT COALESCE(dm.tenDM,N'Chưa phân loại') AS tenDM, "
                + "SUM(ct.soLuong*ct.donGia) AS doanhThu "
                + "FROM HoaDon hd "
                + "JOIN ChiTietHoaDon ct ON ct.maHD=hd.maHD "
                + "JOIN SanPham sp ON sp.maSP=ct.maSP "
                + "LEFT JOIN DanhMucSanPham dm ON dm.maDM=sp.maDM "
                + "WHERE hd.ngayLap>=? AND hd.ngayLap<DATEADD(day,1,?) "
                + "AND hd.trangThai=N'Đã thanh toán' "
                + "GROUP BY dm.tenDM ORDER BY doanhThu DESC";
        Map<String,BigDecimal> result = new LinkedHashMap<>();
        try (Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, tuNgay);
            ps.setDate(2, denNgay);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.put(rs.getString("tenDM"), rs.getBigDecimal("doanhThu"));
            }
        }
        return result;
    }
}
