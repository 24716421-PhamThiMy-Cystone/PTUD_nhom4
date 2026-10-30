CREATE DATABASE QL_SieuThiDienMay;
GO
USE QL_SieuThiDienMay;
GO

-- 1. BẢNG ĐỘC LẬP (Không có khóa ngoại)
CREATE TABLE NhanVien (
    maNV VARCHAR(50) PRIMARY KEY,
    hoTen VARCHAR(100) NOT NULL,
    ngaySinh DATE,
    soDienThoai VARCHAR(20),
    email VARCHAR(100),
    trangThai BIT,
    diaChi VARCHAR(255),
    gioiTinh BIT
);

CREATE TABLE CaLam (
    maCa VARCHAR(50) PRIMARY KEY,
    ngay DATE,
    gioBatDau TIME,
    gioKetThuc TIME
);

CREATE TABLE KhachHang (
    maKH VARCHAR(50) PRIMARY KEY,
    hoTen VARCHAR(100) NOT NULL,
    soDienThoai VARCHAR(20),
    diaChi VARCHAR(255),
    diemTichLuy INT DEFAULT 0,
    hangThe VARCHAR(50) -- Enum: DONG, BAC, VANG, KIMCUONG
);

CREATE TABLE UuDai (
    maUD VARCHAR(50) PRIMARY KEY,
    tenUD VARCHAR(255),
    ngayBD DATETIME,
    ngayKT DATETIME,
    giaTri VARCHAR(50), 
    moTa TEXT,
    tongSoLuong INT,
    trangThai VARCHAR(50),
    chietKhau DECIMAL(5,2)
);

CREATE TABLE Thue (
    maThue VARCHAR(50) PRIMARY KEY,
    tenThue VARCHAR(100),
    phanTram DECIMAL(5,2)
);

CREATE TABLE NhaCungCap (
    maNCC VARCHAR(50) PRIMARY KEY,
    tenNCC VARCHAR(255) NOT NULL,
    soDienThoai VARCHAR(20),
    diaChi VARCHAR(255)
);

-- Thay thế LoaiSP thành DanhMucSanPham có liên kết với Thue
CREATE TABLE DanhMucSanPham (
    maDM VARCHAR(50) PRIMARY KEY,
    tenDM VARCHAR(100) NOT NULL,
    tyLeLoiNhuan DECIMAL(5,2),
    moTa TEXT,
    maThue VARCHAR(50),
    CONSTRAINT FK_DanhMucSanPham_Thue FOREIGN KEY (maThue) REFERENCES Thue(maThue)
);

-- 2. BẢNG PHỤ THUỘC (Có khóa ngoại)
CREATE TABLE TaiKhoan (
    tenDangNhap VARCHAR(50) PRIMARY KEY,
    matKhau VARCHAR(255) NOT NULL,
    vaiTro VARCHAR(50), -- Enum: NHAN_VIEN_QUAN_LY, NHAN_VIEN_BAN_HANG, NHAN_VIEN_CSKH
    maNV VARCHAR(50) UNIQUE,
    CONSTRAINT FK_TaiKhoan_NhanVien FOREIGN KEY (maNV) REFERENCES NhanVien(maNV)
);

-- Bảng trung gian giải quyết quan hệ N-N giữa NhanVien và CaLam
CREATE TABLE PhanCongCaTruc (
    maNV VARCHAR(50),
    maCa VARCHAR(50),
    PRIMARY KEY (maNV, maCa),
    CONSTRAINT FK_PhanCong_NhanVien FOREIGN KEY (maNV) REFERENCES NhanVien(maNV),
    CONSTRAINT FK_PhanCong_CaLam FOREIGN KEY (maCa) REFERENCES CaLam(maCa)
);

CREATE TABLE SanPham (
    maSP VARCHAR(50) PRIMARY KEY,
    tenSP VARCHAR(255) NOT NULL,
    donGiaNhap DECIMAL(18,2),
    soLuongTon INT,
    thongSoKyThuat TEXT,
    trangThaiSanPham BIT,
    giaBan DECIMAL(18,2),
    maDM VARCHAR(50),
    maNCC VARCHAR(50),
    CONSTRAINT FK_SanPham_DanhMuc FOREIGN KEY (maDM) REFERENCES DanhMucSanPham(maDM),
    CONSTRAINT FK_SanPham_NhaCungCap FOREIGN KEY (maNCC) REFERENCES NhaCungCap(maNCC)
);

CREATE TABLE HoaDon (
    maHD VARCHAR(50) PRIMARY KEY,
    ngayLap DATETIME,
    phuongThucTT VARCHAR(50),
    trangThai VARCHAR(50),
    tongTien DECIMAL(18,2), -- Thuộc tính dẫn xuất
    tienKhachTra DECIMAL(18,2),
    tienDatCoc DECIMAL(18,2),
    maNV VARCHAR(50),
    maKH VARCHAR(50),
    maUD VARCHAR(50),
    CONSTRAINT FK_HoaDon_NhanVien FOREIGN KEY (maNV) REFERENCES NhanVien(maNV),
    CONSTRAINT FK_HoaDon_KhachHang FOREIGN KEY (maKH) REFERENCES KhachHang(maKH),
    CONSTRAINT FK_HoaDon_UuDai FOREIGN KEY (maUD) REFERENCES UuDai(maUD)
);

-- Bảng chi tiết hóa đơn bổ sung các thuộc tính thuế và chiết khấu theo sơ đồ lớp
CREATE TABLE ChiTietHoaDon (
    maHD VARCHAR(50),
    maSP VARCHAR(50),
    soLuong INT NOT NULL,
    donGia DECIMAL(18,2),
    thanhTien DECIMAL(18,2), -- Thuộc tính dẫn xuất (soLuong * donGia)
    phanTramThue DECIMAL(5,2),
    tienThue DECIMAL(18,2),
    tienChietKhau DECIMAL(18,2),
    PRIMARY KEY (maHD, maSP),
    CONSTRAINT FK_CTHD_HoaDon FOREIGN KEY (maHD) REFERENCES HoaDon(maHD),
    CONSTRAINT FK_CTHD_SanPham FOREIGN KEY (maSP) REFERENCES SanPham(maSP)
);

-- 3. BỔ SUNG CÁC BẢNG LIÊN QUAN ĐẾN ĐỔI TRẢ (PhieuDoiTra & ChiTietDoiTra)
CREATE TABLE PhieuDoiTra (
    maPhieu VARCHAR(50) PRIMARY KEY,
    ngayLap DATETIME,
    lyDo TEXT,
    tongTienHoan DECIMAL(18,2),
    maHD VARCHAR(50),
    maNV VARCHAR(50),
    CONSTRAINT FK_PhieuDoiTra_HoaDon FOREIGN KEY (maHD) REFERENCES HoaDon(maHD),
    CONSTRAINT FK_PhieuDoiTra_NhanVien FOREIGN KEY (maNV) REFERENCES NhanVien(maNV)
);

CREATE TABLE ChiTietDoiTra (
    maPhieu VARCHAR(50),
    maSP VARCHAR(50),
    soLuong INT NOT NULL,
    donGia DECIMAL(18,2),
    thanhTien DECIMAL(18,2),
    hinhThuc VARCHAR(50), -- Enum: TRA_HANG, DOI_HANG
    PRIMARY KEY (maPhieu, maSP),
    CONSTRAINT FK_CTDT_PhieuDoiTra FOREIGN KEY (maPhieu) REFERENCES PhieuDoiTra(maPhieu),
    CONSTRAINT FK_CTDT_SanPham FOREIGN KEY (maSP) REFERENCES SanPham(maSP)
);
