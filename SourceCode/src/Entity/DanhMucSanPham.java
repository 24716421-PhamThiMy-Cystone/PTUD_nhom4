package Entity;

import java.math.BigDecimal;

public class DanhMucSanPham {
    private String maDM, tenDM, moTa, maThue; 
    private BigDecimal tyLeLoiNhuan;
    
    public DanhMucSanPham() {}
    
    public DanhMucSanPham(String maDM, String tenDM, BigDecimal tyLeLoiNhuan, String moTa, String maThue) {
        this.maDM = maDM;
        this.tenDM = tenDM;
        this.tyLeLoiNhuan = tyLeLoiNhuan;
        this.moTa = moTa;
        this.maThue = maThue;
    }
    
    public String getMaDM() {
        return maDM;
    } 
    
    public void setMaDM(String v) {
        maDM = v;
    } 
    
    public String getTenDM() {
        return tenDM;
    } 
    
    public void setTenDM(String v) {
        tenDM = v;
    } 
    
    public BigDecimal getTyLeLoiNhuan() {
        return tyLeLoiNhuan;
    } 
    
    public void setTyLeLoiNhuan(BigDecimal v) {
        tyLeLoiNhuan = v;
    } 
    
    public String getMoTa() {
        return moTa;
    } 
    
    public void setMoTa(String v) {
        moTa = v;
    } 
    
    public String getMaThue() {
        return maThue;
    } 
    
    public void setMaThue(String v) {
        maThue = v;
    }
}