package Entity;

import java.time.LocalTime;

public class CaLam {
    private String maCa;
    private String tenCa;
    private LocalTime gioBatDau;
    private LocalTime gioKetThuc;

    public CaLam() { }
    public CaLam(String maCa, String tenCa, LocalTime gioBatDau, LocalTime gioKetThuc) {
        setMaCa(maCa); setTenCa(tenCa); setGioBatDau(gioBatDau); setGioKetThuc(gioKetThuc);
    }
    public String getMaCa() { return maCa; }
    public void setMaCa(String value) { if (value == null || value.trim().isEmpty()) throw new IllegalArgumentException("Mã ca không được rỗng."); maCa = value.trim(); }
    public String getTenCa() { return tenCa; }
    public void setTenCa(String value) { if (value == null || value.trim().isEmpty()) throw new IllegalArgumentException("Tên ca không được rỗng."); tenCa = value.trim(); }
    public LocalTime getGioBatDau() { return gioBatDau; }
    public void setGioBatDau(LocalTime value) { if (value == null) throw new IllegalArgumentException("Giờ bắt đầu không được rỗng."); gioBatDau = value; }
    public LocalTime getGioKetThuc() { return gioKetThuc; }
    public void setGioKetThuc(LocalTime value) {
        if (value == null || gioBatDau == null || !value.isAfter(gioBatDau)) throw new IllegalArgumentException("Giờ kết thúc phải sau giờ bắt đầu trong cùng ngày.");
        gioKetThuc = value;
    }
    @Override public String toString() { return maCa + " - " + tenCa + " (" + gioBatDau + " - " + gioKetThuc + ")"; }
}

