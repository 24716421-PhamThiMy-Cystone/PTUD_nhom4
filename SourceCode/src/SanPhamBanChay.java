package Entity;

import java.math.BigDecimal;

public class SanPhamBanChay {
    private final String maSP;
    private final String tenSP;
    private final int soLuongBan;
    private final BigDecimal doanhThu;
    public SanPhamBanChay(String maSP, String tenSP, int soLuongBan, BigDecimal doanhThu) {
        this.maSP = maSP; this.tenSP = tenSP; this.soLuongBan = soLuongBan;
        this.doanhThu = doanhThu == null ? BigDecimal.ZERO : doanhThu;
    }
    public String getMaSP() { return maSP; }
    public String getTenSP() { return tenSP; }
    public int getSoLuongBan() { return soLuongBan; }
    public BigDecimal getDoanhThu() { return doanhThu; }
}

