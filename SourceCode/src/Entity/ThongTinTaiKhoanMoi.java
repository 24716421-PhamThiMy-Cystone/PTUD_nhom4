package Entity;

/** One-time credentials returned after an employee and account are saved. */
public final class ThongTinTaiKhoanMoi {
    private final String maNV;
    private final String tenDangNhap;
    private final String matKhau;

    public ThongTinTaiKhoanMoi(String maNV, String tenDangNhap, String matKhau) {
        this.maNV = maNV;
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
    }

    public String getMaNV() { return maNV; }
    public String getTenDangNhap() { return tenDangNhap; }
    public String getMatKhau() { return matKhau; }

    @Override public String toString() {
        return "ThongTinTaiKhoanMoi{maNV='" + maNV + "', tenDangNhap='" + tenDangNhap + "', matKhau='[ẩn]'}";
    }
}
