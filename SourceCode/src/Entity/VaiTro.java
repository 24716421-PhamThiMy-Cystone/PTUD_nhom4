package Entity;

public enum VaiTro {
    QUAN_LY("Quản lý"), NHAN_VIEN("Nhân viên"), THU_NGAN("Thu ngân");

    private final String tenHienThi;
    VaiTro(String tenHienThi) { this.tenHienThi = tenHienThi; }
    public String getTenHienThi() { return tenHienThi; }

    public static VaiTro fromDatabase(String value) {
        if (value == null) return NHAN_VIEN;
        String role = value.trim();
        if ("NHAN_VIEN_QUAN_LY".equalsIgnoreCase(role)) return QUAN_LY;
        if ("NHAN_VIEN_CSKH".equalsIgnoreCase(role)) return THU_NGAN;
        if ("NHAN_VIEN_BAN_HANG".equalsIgnoreCase(role)) return NHAN_VIEN;
        for (VaiTro item : values()) {
            if (item.name().equalsIgnoreCase(role) || item.tenHienThi.equalsIgnoreCase(role)) return item;
        }
        return NHAN_VIEN;
    }
    @Override public String toString() { return tenHienThi; }
}
