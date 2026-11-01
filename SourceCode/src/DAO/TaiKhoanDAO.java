package DAO;

import Entity.NhanVien;
import Entity.TaiKhoan;
import Entity.VaiTro;
import Util.ConnectDB;
import Util.PasswordUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TaiKhoanDAO {
    public TaiKhoan dangNhap(String tenDangNhap, String matKhau) throws SQLException {
        // The supplied SieuThiDienMay.sql defines only these four TaiKhoan columns.
        // Read optional hash/status columns only when they exist in the connected database.
        String sql = "SELECT * FROM dbo.TaiKhoan WHERE tenDangNhap=?";
        try(Connection con=ConnectDB.getConnection(); PreparedStatement ps=con.prepareStatement(sql)) {
            ps.setString(1,tenDangNhap);
            try(ResultSet rs=ps.executeQuery()) {
                String connectionInfo = "Java đang đọc " + con.getCatalog() + " qua "
                        + con.getMetaData().getURL();
                if(!rs.next()) {
                    throw new SQLException("Không tìm thấy tài khoản '" + tenDangNhap + "'. " + connectionInfo);
                }
                String status = getOptionalString(rs, "trangThai");
                String storedHash = getOptionalString(rs, "matKhauHash");
                String storedPlain = rs.getString("matKhau");
                boolean active = isActive(rs, status);
                String effectiveHash = hasText(storedHash)
                        ? storedHash.trim()
                        : isSha256(storedPlain) ? storedPlain.trim() : null;
                boolean passwordMatches = effectiveHash != null
                        ? PasswordUtil.khop(matKhau, effectiveHash)
                        : matKhau != null && matKhau.equals(storedPlain);
                if(!active) {
                    throw new SQLException("Tài khoản đang không hoạt động (trạng thái: "
                            + status + "). " + connectionInfo);
                }
                if(!passwordMatches) {
                    throw new SQLException("Mật khẩu không khớp với dữ liệu tài khoản trên SQL Server. "
                            + connectionInfo);
                }
                String maNV=rs.getString("maNV");
                // TaiKhoan requires a non-empty hash in its model. For legacy rows
                // that store plaintext only, keep a derived hash in memory (never in SQL).
                String hashForSession = effectiveHash != null
                        ? effectiveHash
                        : PasswordUtil.sha256(storedPlain);
                TaiKhoan tk = new TaiKhoan(
                        rs.getString("tenDangNhap"),
                        hashForSession,
                        null,
                        VaiTro.fromDatabase(rs.getString("vaiTro")),
                        status);
                NhanVien nv = timNhanVienDangNhap(con, maNV);
                if (nv == null) {
                    throw new SQLException("Tài khoản đúng nhưng không tìm thấy nhân viên liên kết " + maNV + ".");
                }
                if (!nv.isDangLam()) {
                    throw new SQLException("Tài khoản đúng nhưng nhân viên " + maNV + " hiện không hoạt động.");
                }
                tk.setNhanVien(nv);
                if(nv!=null) nv.setTaiKhoan(tk);
                return tk;
            }
        }
    }

    /** Loads only the employee fields required by login; other profile fields vary by schema. */
    private static NhanVien timNhanVienDangNhap(Connection con, String maNV) throws SQLException {
        if (maNV == null || maNV.trim().isEmpty()) return null;
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM dbo.NhanVien WHERE maNV=?")) {
            ps.setString(1, maNV);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                NhanVien nv = new NhanVien();
                nv.setMaNV(rs.getString("maNV"));
                nv.setHoTen(rs.getString("hoTen"));
                nv.setSoDienThoai(getOptionalString(rs, "soDienThoai"));
                nv.setEmail(getOptionalString(rs, "email"));
                nv.setDiaChi(getOptionalString(rs, "diaChi"));

                Object employeeStatus = getOptionalObject(rs, "trangThai");
                if (employeeStatus instanceof Boolean) {
                    nv.setDangLam((Boolean) employeeStatus);
                } else if (employeeStatus != null) {
                    String value = employeeStatus.toString().trim();
                    nv.setDangLam("1".equals(value) || "Hoạt động".equalsIgnoreCase(value)
                            || "ACTIVE".equalsIgnoreCase(value) || "Đang làm".equalsIgnoreCase(value));
                }
                return nv;
            }
        }
    }

    private static boolean isActive(ResultSet rs, String status) throws SQLException {
        if (status == null || status.trim().isEmpty()) {
            Object value = getOptionalObject(rs, "trangThai");
            if (value instanceof Boolean) return (Boolean) value;
            if (value instanceof Number) return ((Number) value).intValue() != 0;
            return true;
        }
        return "Hoạt động".equalsIgnoreCase(status) || "ACTIVE".equalsIgnoreCase(status)
                || "1".equals(status.trim()) || "Đang làm".equalsIgnoreCase(status);
    }

    private static String getOptionalString(ResultSet rs, String column) throws SQLException {
        Object value = getOptionalObject(rs, column);
        return value == null ? null : value.toString();
    }

    private static Object getOptionalObject(ResultSet rs, String column) throws SQLException {
        java.sql.ResultSetMetaData metadata = rs.getMetaData();
        for (int i = 1; i <= metadata.getColumnCount(); i++) {
            if (column.equalsIgnoreCase(metadata.getColumnLabel(i))
                    || column.equalsIgnoreCase(metadata.getColumnName(i))) {
                return rs.getObject(i);
            }
        }
        return null;
    }

    private static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private static boolean isSha256(String value) {
        return value != null && value.trim().matches("(?i)[0-9a-f]{64}");
    }

    public boolean kiemTraTrungTenDangNhap(String username) throws SQLException {
        try(Connection con=ConnectDB.getConnection(); PreparedStatement ps=con.prepareStatement("SELECT COUNT(*) FROM TaiKhoan WHERE tenDangNhap=?")) {
            ps.setString(1,username); try(ResultSet rs=ps.executeQuery()) { return rs.next() && rs.getInt(1)>0; }
        }
    }

    public boolean them(TaiKhoan tk) throws SQLException {
        String sql="INSERT INTO TaiKhoan(tenDangNhap,matKhau,matKhauHash,maNV,vaiTro,trangThai) VALUES(?,?,?,?,?,?)";
        try(Connection con=ConnectDB.getConnection(); PreparedStatement ps=con.prepareStatement(sql)) {
            ps.setString(1,tk.getTenDangNhap()); ps.setString(2,tk.getMatKhauHash()); ps.setString(3,tk.getMatKhauHash());
            ps.setString(4,tk.getNhanVien().getMaNV()); ps.setString(5,tk.getVaiTro().name()); ps.setString(6,tk.getTrangThai());
            return ps.executeUpdate()>0;
        }
    }

    public boolean doiMatKhau(String username, String matKhauCu, String matKhauMoi) throws SQLException {
        if(!PasswordUtil.hopLe(matKhauMoi)) throw new IllegalArgumentException("Mật khẩu cần dài hơn 6 ký tự, có ít nhất 1 chữ in hoa, 1 chữ số và 1 ký tự đặc biệt.");
        String sql="SELECT matKhau,matKhauHash FROM TaiKhoan WHERE tenDangNhap=?";
        try(Connection con=ConnectDB.getConnection()) {
            String oldHash;
            String oldPlain;
            try(PreparedStatement ps=con.prepareStatement(sql)) {
                ps.setString(1,username);
                try(ResultSet rs=ps.executeQuery()) {
                    if(!rs.next()) return false;
                    oldHash=rs.getString("matKhauHash");
                    oldPlain=rs.getString("matKhau");
                }
            }
            boolean matches=oldHash!=null && !oldHash.trim().isEmpty()
                    ? PasswordUtil.khop(matKhauCu,oldHash) : matKhauCu!=null && matKhauCu.equals(oldPlain);
            if(!matches) return false;
            try(PreparedStatement ps=con.prepareStatement("UPDATE TaiKhoan SET matKhau=?,matKhauHash=? WHERE tenDangNhap=?")) {
                String newHash=PasswordUtil.sha256(matKhauMoi);
                ps.setString(1,newHash); ps.setString(2,newHash); ps.setString(3,username);
                return ps.executeUpdate()>0;
            }
        }
    }
}

