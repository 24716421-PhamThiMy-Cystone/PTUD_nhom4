package Entity;

import java.math.BigDecimal;

public class Thue { 
    private String maThue, tenThue; 
    private BigDecimal phanTram; 
    
    public Thue() {} 
    
    public Thue(String ma, String ten, BigDecimal pt) {
        maThue = ma; 
        tenThue = ten; 
        phanTram = pt;
    } 
    
    public String getMaThue() {
        return maThue;
    } 
    
    public void setMaThue(String v) {
        maThue = v;
    } 
    
    public String getTenThue() {
        return tenThue;
    } 
    
    public void setTenThue(String v) {
        tenThue = v;
    } 
    
    public BigDecimal getPhanTram() {
        return phanTram;
    } 
    
    public void setPhanTram(BigDecimal v) {
        phanTram = v;
    } 
}