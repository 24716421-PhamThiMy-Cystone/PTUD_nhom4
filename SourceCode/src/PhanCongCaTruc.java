package Entity;

import java.time.LocalDate;

public class PhanCongCaTruc {
    private String maPhanCong;
    private NhanVien nhanVien;
    private CaLam caLam;
    private LocalDate ngayTruc;

    public PhanCongCaTruc() { }
    public PhanCongCaTruc(String maPhanCong, NhanVien nhanVien, CaLam caLam, LocalDate ngayTruc) {
        this.maPhanCong = maPhanCong; setNhanVien(nhanVien); setCaLam(caLam); setNgayTruc(ngayTruc);
    }
    public String getMaPhanCong() { return maPhanCong; }
    public void setMaPhanCong(String value) { maPhanCong = value; }
    public NhanVien getNhanVien() { return nhanVien; }
    public void setNhanVien(NhanVien value) { if (value == null) throw new IllegalArgumentException("Nhân viên không được rỗng."); nhanVien = value; }
    public CaLam getCaLam() { return caLam; }
    public void setCaLam(CaLam value) { if (value == null) throw new IllegalArgumentException("Ca làm không được rỗng."); caLam = value; }
    public LocalDate getNgayTruc() { return ngayTruc; }
    public void setNgayTruc(LocalDate value) { if (value == null) throw new IllegalArgumentException("Ngày trực không được rỗng."); ngayTruc = value; }
}

