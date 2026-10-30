package Entity;

import java.math.BigDecimal; 
import java.sql.Timestamp;

public class UuDai {
    private String maUD, tenUD, giaTri, moTa, trangThai; 
    private Timestamp ngayBD, ngayKT; 
    private int tongSoLuong; 
    private BigDecimal chietKhau;
    
    public UuDai() {} 
    
    public UuDai(String ma, String ten, Timestamp bd, Timestamp kt, String giaTri, String moTa, int tong, String trangThai, BigDecimal ck) {
        maUD = ma; 
        tenUD = ten; 
        ngayBD = bd; 
        ngayKT = kt; 
        this.giaTri = giaTri; 
        this.moTa = moTa; 
        tongSoLuong = tong; 
        this.trangThai = trangThai; 
        chietKhau = ck;
    }
    
    public String getMaUD() {
        return maUD;
    } 
    
    public void setMaUD(String v) {
        maUD = v;
    } 
    
    public String getTenUD() {
        return tenUD;
    } 
    
    public void setTenUD(String v) {
        tenUD = v;
    } 
    
    public Timestamp getNgayBD() {
        return ngayBD;
    } 
    
    public void setNgayBD(Timestamp v) {
        ngayBD = v;
    } 
    
    public Timestamp getNgayKT() {
        return ngayKT;
    } 
    
    public void setNgayKT(Timestamp v) {
        ngayKT = v;
    } 
    
    public String getGiaTri() {
        return giaTri;
    } 
    
    public void setGiaTri(String v) {
        giaTri = v;
    } 
    
    public String getMoTa() {
        return moTa;
    } 
    
    public void setMoTa(String v) {
        moTa = v;
    } 
    
    public int getTongSoLuong() {
        return tongSoLuong;
    } 
    
    public void setTongSoLuong(int v) {
        tongSoLuong = v;
    } 
    
    public String getTrangThai() {
        return trangThai;
    } 
    
    public void setTrangThai(String v) {
        trangThai = v;
    } 
    
    public BigDecimal getChietKhau() {
        return chietKhau;
    } 
    
    public void setChietKhau(BigDecimal v) {
        chietKhau = v;
    } 
    
    public boolean kiemTraHieuLuc() {
        Timestamp now = new Timestamp(System.currentTimeMillis()); 
        return ("ACTIVE".equalsIgnoreCase(trangThai) || "Hoạt động".equalsIgnoreCase(trangThai)
                || "Đang áp dụng".equalsIgnoreCase(trangThai) || "1".equals(trangThai))
                && (ngayBD == null || !now.before(ngayBD))
                && (ngayKT == null || !now.after(ngayKT))
                && tongSoLuong != 0;
    }
}