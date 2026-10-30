package Entity;

public class NhaCungCap {
    private String maNCC, tenNCC, soDienThoai, diaChi;
    
    public NhaCungCap() {} 
    
    public NhaCungCap(String ma, String ten, String sdt, String dc) {
        maNCC = ma; 
        tenNCC = ten; 
        soDienThoai = sdt; 
        diaChi = dc;
    }
    
    public String getMaNCC() {
        return maNCC;
    } 
    
    public void setMaNCC(String v) {
        maNCC = v;
    } 
    
    public String getTenNCC() {
        return tenNCC;
    } 
    
    public void setTenNCC(String v) {
        tenNCC = v;
    } 
    
    public String getSoDienThoai() {
        return soDienThoai;
    } 
    
    public void setSoDienThoai(String v) {
        soDienThoai = v;
    } 
    
    public String getDiaChi() {
        return diaChi;
    } 
    
    public void setDiaChi(String v) {
        diaChi = v;
    }
}