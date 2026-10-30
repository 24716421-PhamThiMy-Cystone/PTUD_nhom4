package Entity;

public class TaiKhoan {
    private String tenDangNhap;
    private String matKhauHash;
    private NhanVien nhanVien;
    private VaiTro vaiTro;
    private String trangThai;

    public TaiKhoan() { }
    public TaiKhoan(String tenDangNhap, String matKhauHash, NhanVien nhanVien, VaiTro vaiTro, String trangThai) {
        setTenDangNhap(tenDangNhap); setMatKhauHash(matKhauHash); setNhanVien(nhanVien);
        setVaiTro(vaiTro); setTrangThai(trangThai);
    }
    public String getTenDangNhap() { return tenDangNhap; }
    public void setTenDangNhap(String value) {
        if (value == null || !value.trim().matches("[A-Za-z0-9._-]{4,30}")) throw new IllegalArgumentException("Tên đăng nhập cần 4-30 ký tự chữ, số, dấu chấm, gạch dưới hoặc gạch ngang.");
        tenDangNhap = value.trim();
    }
    public String getMatKhauHash() { return matKhauHash; }
    public void setMatKhauHash(String value) {
        if (value == null || value.trim().isEmpty()) throw new IllegalArgumentException("Mật khẩu không được rỗng.");
        matKhauHash = value.trim();
    }
    public NhanVien getNhanVien() { return nhanVien; }
    public void setNhanVien(NhanVien value) { nhanVien = value; }
    public VaiTro getVaiTro() { return vaiTro; }
    public void setVaiTro(VaiTro value) { if (value == null) throw new IllegalArgumentException("Vai trò không được rỗng."); vaiTro = value; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String value) { trangThai = value == null || value.trim().isEmpty() ? "Hoạt động" : value.trim(); }
    public boolean isHoatDong() { return "Hoạt động".equalsIgnoreCase(trangThai) || "ACTIVE".equalsIgnoreCase(trangThai); }
}

