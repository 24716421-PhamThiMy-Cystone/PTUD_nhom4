package Controller;

import DAO.HoaDonDAO;
import Entity.HoaDon;
import Entity.ChiTietHoaDon;
import Entity.NganHangService;

import java.time.LocalDateTime;

public class HoaDonController {

    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private final NganHangService nganHangService =
            new NganHangService();

    // UC001: Lập hóa đơn
    public boolean lapHoaDon(HoaDon hd) {

        if (hd == null) {
            System.out.println("Hóa đơn không hợp lệ!");
            return false;
        }

        if (hd.getMaHD() == null || hd.getMaHD().isBlank()
                || hd.getMaNV() == null || hd.getMaNV().isBlank()
                || hd.getDsChiTiet() == null
                || hd.getDsChiTiet().isEmpty()) {
            System.out.println("Thiếu thông tin hóa đơn!");
            return false;
        }

        for (ChiTietHoaDon ct : hd.getDsChiTiet()) {
            if (ct.getMaSP() == null || ct.getMaSP().isBlank()
                    || ct.getSoLuong() <= 0
                    || ct.getDonGia() < 0
                    || ct.getPhanTramThue() < 0
                    || ct.getTienChietKhau() < 0) {
                System.out.println("Chi tiết hóa đơn không hợp lệ!");
                return false;
            }

            ct.setThanhTien(ct.getSoLuong() * ct.getDonGia());
            ct.setTienThue(
                    ct.getThanhTien() * ct.getPhanTramThue() / 100.0);
        }

        hd.setNgayLap(LocalDateTime.now());
        hd.setTongTien(hd.tinhTongTien());
        hd.setTrangThai("Chưa thanh toán");
        hd.setPhuongThucTT("Chưa thanh toán");
        hd.setTienKhachTra(0);

        boolean ketQua = hoaDonDAO.themHoaDon(hd);

        if (ketQua) {
            System.out.println("Lập hóa đơn thành công: " + hd.getMaHD());
        } else {
            System.out.println("Lập hóa đơn thất bại!");
        }

        return ketQua;
    }

    // UC009: Thanh toán hóa đơn
    public boolean thanhToan(
            String maHD, String phuongThuc, double tienKhachTra,
            double tongTien) {

        if (maHD == null || maHD.isBlank()
                || phuongThuc == null || phuongThuc.isBlank()
                || tongTien <= 0 || tienKhachTra < tongTien) {
            System.out.println("Thông tin thanh toán không hợp lệ!");
            return false;
        }

        if (phuongThuc.equalsIgnoreCase("Chuyển khoản")) {
            boolean nganHangOK =
                    nganHangService.thanhToanChuyenKhoan(maHD, tongTien);

            if (!nganHangOK) {
                System.out.println("Giao dịch chuyển khoản thất bại!");
                return false;
            }
        } else if (!phuongThuc.equalsIgnoreCase("Tiền mặt")) {
            System.out.println("Phương thức thanh toán không hợp lệ!");
            return false;
        }

        boolean ketQua = hoaDonDAO.capNhatThanhToan(
                maHD, phuongThuc, tienKhachTra);

        if (ketQua) {
            System.out.println("Thanh toán thành công!");
            if (phuongThuc.equalsIgnoreCase("Tiền mặt")) {
                System.out.println(
                        "Tiền thừa: " + (tienKhachTra - tongTien));
            }
        } else {
            System.out.println("Cập nhật thanh toán thất bại!");
        }

        return ketQua;
    }
}