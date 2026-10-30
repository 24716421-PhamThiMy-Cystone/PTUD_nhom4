package Entity;

public class NganHangService {

    public boolean thanhToanChuyenKhoan(
            String maHD, double soTien) {

        if (maHD == null || maHD.isBlank() || soTien <= 0) {
            return false;
        }

        // Giả lập ngân hàng chấp nhận giao dịch
        System.out.println("Đang xử lý giao dịch chuyển khoản...");
        System.out.println("Mã hóa đơn: " + maHD);
        System.out.println("Số tiền: " + soTien);

        return true;
    }
}