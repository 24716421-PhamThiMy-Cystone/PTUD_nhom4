package Entity;

import java.math.BigDecimal;

public class ThongKeTongQuan {
    private final BigDecimal doanhThu;
    private final int soHoaDon;
    private final int tongSanPhamBan;
    private final int soHoaDonHuy;
    public ThongKeTongQuan(BigDecimal doanhThu, int soHoaDon, int tongSanPhamBan) {
        this(doanhThu, soHoaDon, tongSanPhamBan, 0);
    }
    public ThongKeTongQuan(BigDecimal doanhThu, int soHoaDon, int tongSanPhamBan, int soHoaDonHuy) {
        this.doanhThu = doanhThu == null ? BigDecimal.ZERO : doanhThu;
        this.soHoaDon = soHoaDon;
        this.tongSanPhamBan = tongSanPhamBan;
        this.soHoaDonHuy = soHoaDonHuy;
    }
    public BigDecimal getDoanhThu() { return doanhThu; }
    public int getSoHoaDon() { return soHoaDon; }
    public int getTongSanPhamBan() { return tongSanPhamBan; }
    public int getSoHoaDonHuy() { return soHoaDonHuy; }
}
