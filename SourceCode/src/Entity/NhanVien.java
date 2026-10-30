package Entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

public class NhanVien {
    private String maNV;
    private String hoTen;
    private LocalDate ngaySinh;
    private String diaChi;
    private LocalDate ngayVaoLam;
    private String trinhDo;
    private double heSoLuong;
    private BigDecimal luongCanBan;
    private String soDienThoai;
    private String email;
    private boolean dangLam = true;
    private TaiKhoan taiKhoan;

    public NhanVien() { }

    public NhanVien(String maNV, String hoTen, LocalDate ngaySinh, String diaChi, LocalDate ngayVaoLam,
                    String trinhDo, double heSoLuong, BigDecimal luongCanBan, String soDienThoai, String email) {
        setMaNV(maNV); setHoTen(hoTen); setNgaySinh(ngaySinh); setDiaChi(diaChi);
        setNgayVaoLam(ngayVaoLam); setTrinhDo(trinhDo); setHeSoLuong(heSoLuong);
        setLuongCanBan(luongCanBan); setSoDienThoai(soDienThoai); setEmail(email);
    }

    public String getMaNV() { return maNV; }
    public void setMaNV(String value) {
        if (value == null || !value.matches("NV\\d{6}")) throw new IllegalArgumentException("Mã nhân viên có dạng NV + 2 số năm vào làm + 4 số thứ tự.");
        maNV = value;
    }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String value) {
        if (value == null || value.trim().isEmpty()) throw new IllegalArgumentException("Họ tên không được rỗng.");
        hoTen = value.trim();
    }
    public LocalDate getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(LocalDate value) {
        if (value == null || value.isAfter(LocalDate.now()) || Period.between(value, LocalDate.now()).getYears() < 18)
            throw new IllegalArgumentException("Nhân viên phải từ 18 tuổi trở lên.");
        ngaySinh = value;
    }
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String value) { diaChi = value == null ? "" : value.trim(); }
    public LocalDate getNgayVaoLam() { return ngayVaoLam; }
    public void setNgayVaoLam(LocalDate value) {
        if (value == null || value.isAfter(LocalDate.now())) throw new IllegalArgumentException("Ngày vào làm không hợp lệ.");
        ngayVaoLam = value;
    }
    public String getTrinhDo() { return trinhDo; }
    public void setTrinhDo(String value) { trinhDo = value == null ? "" : value.trim(); }
    public double getHeSoLuong() { return heSoLuong; }
    public void setHeSoLuong(double value) {
        if (!Double.isFinite(value) || value < 1.84 || value > 6.5)
            throw new IllegalArgumentException("Hệ số lương phải từ 1.84 đến 6.5.");
        heSoLuong = value;
    }
    public BigDecimal getLuongCanBan() { return luongCanBan; }
    public void setLuongCanBan(BigDecimal value) {
        if (value == null || value.signum() <= 0) throw new IllegalArgumentException("Lương cơ bản phải lớn hơn 0.");
        luongCanBan = value;
    }
    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String value) {
        if (value == null || value.trim().isEmpty()) { soDienThoai = ""; return; }
        if (!value.trim().matches("0[0-9]{9}")) throw new IllegalArgumentException("Số điện thoại phải gồm 10 chữ số và bắt đầu bằng 0.");
        soDienThoai = value.trim();
    }
    public String getEmail() { return email; }
    public void setEmail(String value) {
        if (value == null || value.trim().isEmpty()) { email = ""; return; }
        if (!value.trim().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) throw new IllegalArgumentException("Email không hợp lệ.");
        email = value.trim();
    }
    public boolean isDangLam() { return dangLam; }
    public void setDangLam(boolean value) { dangLam = value; }
    public TaiKhoan getTaiKhoan() { return taiKhoan; }
    public void setTaiKhoan(TaiKhoan value) { taiKhoan = value; }

    public BigDecimal tinhLuong() { return luongCanBan.multiply(BigDecimal.valueOf(heSoLuong)); }
    @Override public String toString() { return maNV + " - " + hoTen; }
}


