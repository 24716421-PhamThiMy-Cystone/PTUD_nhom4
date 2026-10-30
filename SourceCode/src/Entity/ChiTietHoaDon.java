package Entity;



public class ChiTietHoaDon {
	private String maHD;
	private String maSP;
	private int soLuong;
	private double donGia;
	private double thanhTien;
	private double phanTramThue;
	private double tienThue;
	private double tienChietKhau;

	public ChiTietHoaDon() {
	}

	public ChiTietHoaDon(String maSP, int soLuong, double donGia, double phanTramThue, double tienChietKhau) {
		this.maSP = maSP;
		this.soLuong = soLuong;
		this.donGia = donGia;
		this.thanhTien = soLuong * donGia;
		this.phanTramThue = phanTramThue;
		this.tienThue = thanhTien * phanTramThue / 100.0;
		this.tienChietKhau = tienChietKhau;
	}

	public String getMaHD() {
		return maHD;
	}

	public void setMaHD(String maHD) {
		this.maHD = maHD;
	}

	public String getMaSP() {
		return maSP;
	}

	public void setMaSP(String maSP) {
		this.maSP = maSP;
	}

	public int getSoLuong() {
		return soLuong;
	}

	public void setSoLuong(int soLuong) {
		this.soLuong = soLuong;
	}

	public double getDonGia() {
		return donGia;
	}

	public void setDonGia(double donGia) {
		this.donGia = donGia;
	}

	public double getThanhTien() {
		return thanhTien;
	}

	public void setThanhTien(double thanhTien) {
		this.thanhTien = thanhTien;
	}

	public double getPhanTramThue() {
		return phanTramThue;
	}

	public void setPhanTramThue(double phanTramThue) {
		this.phanTramThue = phanTramThue;
	}

	public double getTienThue() {
		return tienThue;
	}

	public void setTienThue(double tienThue) {
		this.tienThue = tienThue;
	}

	public double getTienChietKhau() {
		return tienChietKhau;
	}

	public void setTienChietKhau(double tienChietKhau) {
		this.tienChietKhau = tienChietKhau;
	}

	public double getThanhToan() {
		return thanhTien + tienThue - tienChietKhau;
	}

}
