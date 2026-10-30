package Entity;

import java.math.BigDecimal;

public class SanPham {
	private String maSP, tenSP, thongSoKyThuat, maDM, maNCC, tenDanhMuc;
	private String duongDanAnh;
    private BigDecimal donGiaNhap, giaBan; 
    private int soLuongTon; 
    private boolean trangThaiSanPham;
    
    public SanPham() {}
    
    public SanPham(String ma, String ten, BigDecimal giaNhap, int ton, String thongSo, boolean trangThai, BigDecimal giaBan, String maDM, String maNCC) {
        this.maSP = ma; 
        this.tenSP = ten; 
        this.donGiaNhap = giaNhap; 
        this.soLuongTon = ton; 
        this.thongSoKyThuat = thongSo; 
        this.trangThaiSanPham = trangThai; 
        this.giaBan = giaBan; 
        this.maDM = maDM; 
        this.maNCC = maNCC;
    }
    
    public String getDuongDanAnh() {
        return duongDanAnh;
    }

    public void setDuongDanAnh(String duongDanAnh) {
        this.duongDanAnh = duongDanAnh;
    }
    
    public String getMaSP() {
        return maSP;
    } 
    
    public void setMaSP(String v) {
        maSP = v;
    } 
    
    public String getTenSP() {
        return tenSP;
    } 
    
    public void setTenSP(String v) {
        tenSP = v;
    } 
    
    public BigDecimal getDonGiaNhap() {
        return donGiaNhap;
    } 
    
    public void setDonGiaNhap(BigDecimal v) {
        donGiaNhap = v;
    } 
    
    public int getSoLuongTon() {
        return soLuongTon;
    } 
    
    public void setSoLuongTon(int v) {
        soLuongTon = v;
    } 
    
    public String getThongSoKyThuat() {
        return thongSoKyThuat;
    } 
    
    public void setThongSoKyThuat(String v) {
        thongSoKyThuat = v;
    } 
    
    public boolean isTrangThaiSanPham() {
        return trangThaiSanPham;
    } 
    
    public boolean getTrangThaiSanPham() {
        return trangThaiSanPham;
    } 
    
    public void setTrangThaiSanPham(boolean v) {
        trangThaiSanPham = v;
    } 
    
    public BigDecimal getGiaBan() {
        return giaBan;
    } 
    
    public void setGiaBan(BigDecimal v) {
        giaBan = v;
    } 
    
    public String getMaDM() {
        return maDM;
    } 
    
    public void setMaDM(String v) {
        maDM = v;
    } 
    
    public String getMaNCC() {
        return maNCC;
    } 
    
    public void setMaNCC(String v) {
        maNCC = v;
    } 
    
    public String getTenDanhMuc() {
        return tenDanhMuc;
    } 
    
    public void setTenDanhMuc(String v) {
        tenDanhMuc = v;
    }
}