package Entity;



import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HoaDon {
	private String maHD;
	private LocalDateTime ngayLap;
	private String phuongThucTT;
	private String trangThai;
	private double tongTien;
	private double tienKhachTra;
	private double tienDatCoc;
	private String maNV;
	private String maKH;
	private String maUD;

	private List<ChiTietHoaDon> dsChiTiet = new ArrayList<>();

	public HoaDon() {
	}

	public String getMaHD() {
		return maHD;
	}

	public void setMaHD(String maHD) {
		this.maHD = maHD;
	}

	public LocalDateTime getNgayLap() {
		return ngayLap;
	}

	public void setNgayLap(LocalDateTime ngayLap) {
		this.ngayLap = ngayLap;
	}

	public String getPhuongThucTT() {
		return phuongThucTT;
	}

	public void setPhuongThucTT(String phuongThucTT) {
		this.phuongThucTT = phuongThucTT;
	}

	public String getTrangThai() {
		return trangThai;
	}

	public void setTrangThai(String trangThai) {
		this.trangThai = trangThai;
	}

	public double getTongTien() {
		return tongTien;
	}

	public void setTongTien(double tongTien) {
		this.tongTien = tongTien;
	}

	public double getTienKhachTra() {
		return tienKhachTra;
	}

	public void setTienKhachTra(double tienKhachTra) {
		this.tienKhachTra = tienKhachTra;
	}

	public double getTienDatCoc() {
		return tienDatCoc;
	}

	public void setTienDatCoc(double tienDatCoc) {
		this.tienDatCoc = tienDatCoc;
	}

	public String getMaNV() {
		return maNV;
	}

	public void setMaNV(String maNV) {
		this.maNV = maNV;
	}

	public String getMaKH() {
		return maKH;
	}

	public void setMaKH(String maKH) {
		this.maKH = maKH;
	}

	public String getMaUD() {
		return maUD;
	}

	public void setMaUD(String maUD) {
		this.maUD = maUD;
	}

	public List<ChiTietHoaDon> getDsChiTiet() {
		return dsChiTiet;
	}

	public void setDsChiTiet(List<ChiTietHoaDon> dsChiTiet) {
		this.dsChiTiet = dsChiTiet;
	}

	public double tinhTongTien() {
		double tong = 0;
		for (ChiTietHoaDon ct : dsChiTiet) {
			tong += ct.getThanhToan();
		}
		return tong;
	}
}