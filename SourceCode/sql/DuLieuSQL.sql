USE QL_SieuThiDienMay;
GO

/* ============================================================
   DATA MẪU ULTRAMART - MÃ THEO ĐẶC TẢ THỰC THỂ
   Chạy sau khi đã tạo các bảng trong database.
   Lưu ý: dữ liệu là dữ liệu minh họa; mật khẩu demo chưa mã hóa.
   ============================================================ */

/* 1. NHÂN VIÊN
   maNV = NV + 2 chữ số năm vào làm + 4 chữ số thứ tự
*/
INSERT INTO NhanVien
(maNV, hoTen, ngaySinh, soDienThoai, email, trangThai, diaChi, gioiTinh)
VALUES
('NV240001', N'Phạm Nguyễn Vịnh Thụy', '2002-03-15', '0901234567', 'vinhthuy@ultramart.vn', 1, N'Quận 1, TP.HCM', 0),
('NV260002', N'Phạm Thị My', '2001-07-22', '0912345678', 'thimyo@ultramart.vn', 1, N'Quận 3, TP.HCM', 1),
('NV260001', N'Đào Thị Thanh Loan', '2000-11-09', '0934567890', 'thanhloan@ultramart.vn', 1, N'Gò Vấp, TP.HCM', 1),
('NV250001', N'Trần Huỳnh Trân', '1999-01-30', '0987654321', 'huynhtran@ultramart.vn', 1, N'Tân Bình, TP.HCM', 1);

/* 2. CA LÀM VÀ PHÂN CÔNG CA */
INSERT INTO CaLam (maCa, ngay, gioBatDau, gioKetThuc)
VALUES
('CA2026102301', '2026-10-23', '07:30', '12:00'),
('CA2026102302', '2026-10-23', '12:00', '17:00'),
('CA2026102303', '2026-10-23', '17:00', '22:00');

INSERT INTO PhanCongCaTruc (maNV, maCa)
VALUES
('NV260001', 'CA2026102301'),
('NV260002', 'CA2026102302'),
('NV250001', 'CA2026102302'),
('NV240001', 'CA2026102303');

/* 3. KHÁCH HÀNG
   maKH = KH + 4 số cuối SĐT + 4 số thứ tự
   Ví dụ: 0901234567 -> KH45670001
*/
INSERT INTO KhachHang
(maKH, hoTen, soDienThoai, diaChi, diemTichLuy, hangThe)
VALUES
('KH45670001', N'Nguyễn Thị Lan', '0901234567', N'Quận 1, TP.HCM', 120, 'DONG'),
('KH56780002', N'Trần Văn Hùng', '0912345678', N'Quận 7, TP.HCM', 250, 'BAC'),
('KH78900003', N'Lê Hoàng Phúc', '0934567890', N'Thủ Đức, TP.HCM', 650, 'VANG'),
('KH43210004', N'Phạm Thu Hà', '0987654321', N'Bình Thạnh, TP.HCM', 1200, 'KIMCUONG'),
('KH11220005', N'Võ Thanh Tâm', '0909871122', N'Tân Phú, TP.HCM', 80, 'DONG');

/* 4. ƯU ĐÃI, THUẾ, NHÀ CUNG CẤP, DANH MỤC */
INSERT INTO UuDai
(maUD, tenUD, ngayBD, ngayKT, giaTri, moTa, tongSoLuong, trangThai, chietKhau)
VALUES
('UD2026001', N'Ưu đãi thành viên Bạc', '2026-01-01', '2026-12-31',5, N'Chiết khấu theo điều kiện chương trình.', 500, 1 ,5),
('UD2026002', N'Ưu đãi tháng 10', '2026-10-01', '2026-10-31', 10, N'Ưu đãi sản phẩm trong chương trình tháng 10.', 200, 1, 10),
('UD2026003', N'Ưu đãi khách hàng thân thiết', '2026-01-01', '2026-12-31', 3, N'Ưu đãi dành cho khách hàng thân thiết.', 300, 1, 3),
('UD2026004', N'Khuyến mãi khai trương','2026-10-01', '2026-10-31',10, N'Giảm giá 10% cho các sản phẩm áp dụng', 100, 1, 10),
('UD2026005', N'Ưu đãi khách hàng thành viên','2026-10-01', '2026-12-31',5, N'Giảm giá 5% dành cho khách hàng thành viên',200, 1, 5),
('UD2026006', N'Khuyến mãi mua sắm cuối tuần','2026-10-01', '2026-12-31',8, N'Giảm giá 8% cho chương trình mua sắm cuối tuần',150, 1, 8),
('UD2026008', N'Khuyến mãi cuối năm','2026-12-01', '2026-12-31',15, N'Chương trình giảm giá cuối năm cho sản phẩm áp dụng',200, 1, 15);

INSERT INTO Thue (maThue, tenThue, phanTram)
VALUES
('TAX2610001', N'Thuế giá trị gia tăng 8%', 8.00),
('TAX2610002', N'Thuế giá trị gia tăng 10%', 10.00);

INSERT INTO NhaCungCap (maNCC, tenNCC, soDienThoai, diaChi)
VALUES
('NCC001', N'Công ty Điện tử Ánh Dương', '02838220001', N'Quận 10, TP.HCM'),
('NCC002', N'Công ty Gia dụng Việt', '02838220002', N'Tân Bình, TP.HCM'),
('NCC003', N'Công ty Công nghệ Sao Việt', '02838220003', N'Thủ Đức, TP.HCM');

INSERT INTO DanhMucSanPham (maDM, tenDM, tyLeLoiNhuan, moTa, maThue)
VALUES
('DM001', N'Điện lạnh', 18.00, N'Tủ lạnh, máy lạnh, tủ đông và thiết bị làm lạnh.', 'TAX2610002'),
('DM002', N'Điện máy', 16.00, N'Máy giặt, máy sấy và các thiết bị điện máy.', 'TAX2610002'),
('DM003', N'Điện gia dụng', 15.00, N'Nồi cơm điện, bếp điện, lò vi sóng và thiết bị gia dụng.', 'TAX2610002'),
('DM004', N'Máy lọc nước', 17.00, N'Máy lọc nước RO và thiết bị lọc nước gia đình.', 'TAX2610002'),
('DM005', N'Điện tử', 12.00, N'Tivi, loa và các thiết bị điện tử giải trí.', 'TAX2610002'),
('DM006', N'Phụ kiện', 20.00, N'Dây cáp, ổ cắm, điều khiển và phụ kiện điện tử.', 'TAX2610002');

/* 5. TÀI KHOẢN */
INSERT INTO TaiKhoan (tenDangNhap, matKhau, vaiTro, maNV)
VALUES
('vinhthuy', 'thuy@123', 'NHAN_VIEN_QUAN_LY', 'NV240001'),
('thimy', 'my@123', 'NHAN_VIEN_BAN_HANG', 'NV260002'),
('thanhloan', 'loan@123', 'NHAN_VIEN_CSKH', 'NV260001'),
('huynhtran', 'tran@123', 'NHAN_VIEN_BAN_HANG', 'NV250001');

/* 6. SẢN PHẨM
   maSP = 2 ký tự viết tắt loại SP + 5 chữ số tăng dần
   TL: Tủ lạnh; DH: Điều hòa; TD: Tủ đông; MG: Máy giặt; MS: Máy sấy; MR: Máy rửa chén; NC: Nồi cơm; BL: Máy xay; BD: Bếp điện; LW: Lò vi sóng; ML: Máy lọc nước; DT: Điện thoại; LO: Loa; TV: Tivi; PK: Phụ kiện
*/
INSERT INTO SanPham
(maSP, tenSP, donGiaNhap, soLuongTon, thongSoKyThuat, trangThaiSanPham, giaBan, maDM, maNCC)
VALUES
-- DM001: Điện lạnh
('TV00001', N'Smart TV Samsung 55 inch', 10500000, 20, N'55 inch, 4K UHD, Smart TV', 1, 12500000, 'DM005', 'NCC001'),
('TV00002', N'Smart TV LG 50 inch', 8500000, 15, N'50 inch, 4K UHD, webOS', 1, 10200000, 'DM005', 'NCC001'),
('TL00001', N'Tủ lạnh Panasonic 300 lít', 9200000, 12, N'Dung tích 300 lít, Inverter', 1, 11000000, 'DM001', 'NCC002'),
('TL00002', N'Tủ lạnh Toshiba 250 lít', 7100000, 10, N'Dung tích 250 lít, tiết kiệm điện', 1, 8500000, 'DM001', 'NCC002'),
('DH00001', N'Điều hòa Daikin 1.5 HP', 8900000, 11, N'1.5 HP, Inverter', 1, 10800000, 'DM001', 'NCC001'),
('DH00002', N'Điều hòa Panasonic 1 HP', 7200000, 7, N'1 HP, Inverter', 1, 8800000, 'DM001', 'NCC001'),
('TD00001', N'Tủ đông Sanaky 250 lít', 6500000, 5, N'Dung tích 250 lít, làm lạnh trực tiếp', 1, 7900000, 'DM001', 'NCC002'),
-- DM002: Điện máy
('MG00001', N'Máy giặt Electrolux 9 kg', 7800000, 9, N'Cửa trước, 9 kg, Inverter', 1, 9500000, 'DM002', 'NCC002'),
('MG00002', N'Máy giặt Samsung 10 kg', 8200000, 8, N'Cửa trước, 10 kg, EcoBubble', 1, 9900000, 'DM002', 'NCC002'),
('MS00001', N'Máy sấy quần áo Casper 8 kg', 5900000, 6, N'Khối lượng sấy 8 kg', 1, 7200000, 'DM002', 'NCC002'),
('MR00001', N'Máy rửa chén Bosch 12 bộ', 12500000, 4, N'Sức chứa 12 bộ đồ ăn', 1, 14900000, 'DM002', 'NCC001'),
-- DM003: Điện gia dụng
('NC00001', N'Nồi cơm điện Sharp 1.8 lít', 650000, 25, N'Dung tích 1.8 lít, công suất 700 W', 1, 890000, 'DM003', 'NCC002'),
('BL00001', N'Máy xay sinh tố Philips', 780000, 18, N'Công suất 450 W, cối 1.5 lít', 1, 1090000, 'DM003', 'NCC002'),
('BD00001', N'Bếp điện từ Sunhouse', 920000, 14, N'Công suất 2000 W, điều khiển cảm ứng', 1, 1290000, 'DM003', 'NCC002'),
('LW00001', N'Lò vi sóng Panasonic 23 lít', 1850000, 8, N'Dung tích 23 lít, công suất 800 W', 1, 2290000, 'DM003', 'NCC002'),
-- DM004: Máy lọc nước
('ML00001', N'Máy lọc nước RO Karofi 10 lõi', 3200000, 10, N'Công nghệ RO, 10 lõi lọc', 1, 3990000, 'DM004', 'NCC003'),
('ML00002', N'Máy lọc nước nóng lạnh Kangaroo', 4900000, 7, N'Lọc RO, vòi nóng và lạnh', 1, 5990000, 'DM004', 'NCC003'),
-- DM005: Điện tử
('DT00001', N'Điện thoại Samsung Galaxy A56', 8200000, 18, N'RAM 8 GB, bộ nhớ 256 GB', 1, 9990000, 'DM005', 'NCC003'),
('DT00002', N'iPhone 15 128 GB', 14500000, 6, N'Bộ nhớ 128 GB, màn hình OLED', 1, 16990000, 'DM005', 'NCC003'),
('LO00001', N'Loa Bluetooth JBL', 1250000, 13, N'Kết nối Bluetooth, pin sạc', 1, 1690000, 'DM005', 'NCC003'),
('TV00003', N'Smart TV Sony 43 inch', 7200000, 9, N'43 inch, Full HD, Smart TV', 1, 8900000, 'DM005', 'NCC001'),
-- DM006: Phụ kiện
('PK00001', N'Cáp HDMI 2 mét', 65000, 50, N'HDMI 2.0, dài 2 mét', 1, 99000, 'DM006', 'NCC003'),
('PK00002', N'Ổ cắm điện 5 lỗ', 95000, 40, N'5 ổ cắm, có công tắc', 1, 149000, 'DM006', 'NCC002'),
('PK00003', N'Điều khiển từ xa đa năng', 80000, 30, N'Tương thích nhiều dòng tivi', 1, 129000, 'DM006', 'NCC003'),
('PK00004', N'Dây nguồn máy tính 1.5 mét', 45000, 35, N'Dây nguồn dài 1.5 mét', 1, 79000, 'DM006', 'NCC003');

/* 7. HÓA ĐƠN
   maHD = HD + ddMMyyyy + 4 chữ số thứ tự
   Ngày trong mã trùng với ngayLap.
*/
INSERT INTO HoaDon
(maHD, ngayLap, phuongThucTT, trangThai, tongTien, tienKhachTra, tienDatCoc, maNV, maKH, maUD)
VALUES
('HD201020260001', '2026-10-20T10:15:00', N'Tiền mặt', N'Đã thanh toán', 22490000, 22490000, 0, 'NV260002', 'KH45670001', 'UD2026002'),
('HD211020260001', '2026-10-21T14:20:00', N'Chuyển khoản', N'Đã thanh toán', 11000000, 11000000, 0, 'NV260002', 'KH56780002', 'UD2026001'),
('HD221020260001', '2026-10-22T09:45:00', N'Tiền mặt', N'Đã thanh toán', 9500000, 9500000, 0, 'NV240001', 'KH78900003', NULL),
('HD231020260001', '2026-10-23T11:10:00', N'Chuyển khoản', N'Đã đặt cọc', 10800000, 5000000, 2000000, 'NV260002', 'KH43210004', 'UD2026003'),
('HD241020260001', '2026-10-24T09:20:00', N'Tiền mặt', N'Đã thanh toán', 2032900, 2032900, 0, 'NV260002', 'KH45670001', 'UD2026002'),
('HD241020260002', '2026-10-24T10:05:00', N'Chuyển khoản', N'Đã thanh toán', 4606800, 4606800, 0, 'NV260002', 'KH56780002', NULL),
('HD241020260003', '2026-10-24T15:40:00', N'Tiền mặt', N'Đã thanh toán', 12348500, 12348500, 0, 'NV240001', 'KH78900003', 'UD2026001'),
('HD251020260001', '2026-10-25T11:15:00', N'Chuyển khoản', N'Đã thanh toán', 11144000, 11144000, 0, 'NV250001', 'KH43210004', 'UD2026003'),
('HD251020260002', '2026-10-25T16:30:00', N'Tiền mặt', N'Đã thanh toán', 4079900, 4079900, 0, 'NV260002', 'KH11220005', NULL),
('HD261020260001', '2026-10-26T09:00:00', N'Chuyển khoản', N'Đã thanh toán', 10800000, 10800000, 0, 'NV240001', 'KH45670001', 'UD2026002'),
('HD261020260002', '2026-10-26T14:45:00', N'Tiền mặt', N'Đã thanh toán', 8247800, 8247800, 0, 'NV260002', 'KH56780002', NULL);
INSERT INTO ChiTietHoaDon
(maHD, maSP, soLuong, donGia, thanhTien, phanTramThue, tienThue, tienChietKhau)
VALUES
('HD201020260001', 'TV00001', 1, 12500000, 12500000, 10.00, 1250000, 250000),
('HD201020260001', 'DT00001', 1, 9990000, 9990000, 10.00, 999000, 0),
('HD211020260001', 'TL00001', 1, 11000000, 11000000, 10.00, 1100000, 550000),
('HD221020260001', 'MG00001', 1, 9500000, 9500000, 10.00, 950000, 0),
('HD231020260001', 'DH00001', 1, 10800000, 10800000, 10.00, 1080000, 324000),
('HD241020260001', 'NC00001', 2, 890000, 1780000, 10.00, 178000, 89000),
('HD241020260001', 'PK00002', 1, 149000, 149000, 10.00, 14900, 0),
('HD241020260002', 'ML00001', 1, 3990000, 3990000, 10.00, 399000, 0),
('HD241020260002', 'PK00001', 2, 99000, 198000, 10.00, 19800, 0),
('HD241020260003', 'DT00001', 1, 9990000, 9990000, 10.00, 999000, 499500),
('HD241020260003', 'LO00001', 1, 1690000, 1690000, 10.00, 169000, 0),
('HD251020260001', 'MG00001', 1, 9500000, 9500000, 10.00, 950000, 285000),
('HD251020260001', 'NC00001', 1, 890000, 890000, 10.00, 89000, 0),
('HD251020260002', 'LW00001', 1, 2290000, 2290000, 10.00, 229000, 0),
('HD251020260002', 'BD00001', 1, 1290000, 1290000, 10.00, 129000, 0),
('HD251020260002', 'PK00003', 1, 129000, 129000, 10.00, 12900, 0),
('HD261020260001', 'DH00001', 1, 10800000, 10800000, 10.00, 1080000, 1080000),
('HD261020260002', 'MS00001', 1, 7200000, 7200000, 10.00, 720000, 0),
('HD261020260002', 'PK00002', 2, 149000, 298000, 10.00, 29800, 0)

/* 8. PHIẾU ĐỔI TRẢ
   maPhieu = PDT + ddMMyyyy + 4 chữ số thứ tự
   Ngày trong mã trùng với ngayLap của phiếu.
*/
INSERT INTO PhieuDoiTra
(maPhieu, ngayLap, lyDo, tongTienHoan, maHD, maNV)
VALUES
('PDT211020260001', '2026-10-21T16:30:00', N'Khách yêu cầu đổi sản phẩm do sản phẩm không phù hợp.', 0, 'HD201020260001', 'NV260001'),
('PDT231020260001', '2026-10-23T15:00:00', N'Sản phẩm phát sinh lỗi, khách yêu cầu trả hàng.', 9500000, 'HD221020260001', 'NV260001');

INSERT INTO ChiTietDoiTra
(maPhieu, maSP, soLuong, donGia, thanhTien, hinhThuc)
VALUES
('PDT211020260001', 'TV00001', 1, 12500000, 12500000, 'DOI_HANG'),
('PDT231020260001', 'MG00001', 1, 9500000, 9500000, 'TRA_HANG');

ALTER TABLE NhanVien
ALTER COLUMN hoTen NVARCHAR(100);

ALTER TABLE NhanVien
ALTER COLUMN diaChi NVARCHAR(255);
ALTER TABLE KhachHang
ALTER COLUMN hoTen NVARCHAR(100);

ALTER TABLE KhachHang
ALTER COLUMN diaChi NVARCHAR(255);

ALTER TABLE DanhMucSanPham
ALTER COLUMN tenDM NVARCHAR(100);

ALTER TABLE DanhMucSanPham
ALTER COLUMN moTa NVARCHAR(255);

ALTER TABLE SanPham
ALTER COLUMN tenSP NVARCHAR(255);
ALTER TABLE SanPham
ALTER COLUMN thongSoKyThuat NVARCHAR(255);

ALTER TABLE HoaDon
ALTER COLUMN phuongThucTT NVARCHAR(255);

ALTER TABLE PhieuDoiTra
ALTER COLUMN lyDo NVARCHAR(255);

/* 9. TRUY VẤN KIỂM TRA */
SELECT * FROM NhanVien;
SELECT * FROM KhachHang;
SELECT * FROM DanhMucSanPham;
SELECT * FROM SanPham;
SELECT * FROM HoaDon;
SELECT * FROM ChiTietHoaDon;
SELECT * FROM PhieuDoiTra;
SELECT * FROM ChiTietDoiTra;
GO

--USE QL_SieuThiDienMay;
--GO

--BEGIN TRY
--    BEGIN TRANSACTION;

--    -- 1. Xóa dữ liệu bảng chi tiết và bảng phụ thuộc
--    DELETE FROM ChiTietDoiTra;
--    DELETE FROM PhieuDoiTra;
--    DELETE FROM ChiTietHoaDon;
--    DELETE FROM HoaDon;

--    -- 2. Xóa dữ liệu phân công và tài khoản
--    DELETE FROM PhanCongCaTruc;
--    DELETE FROM TaiKhoan;

--    -- 3. Xóa dữ liệu danh mục và sản phẩm
--    DELETE FROM SanPham;
--    DELETE FROM DanhMucSanPham;
--    DELETE FROM NhaCungCap;
--    DELETE FROM Thue;
--    DELETE FROM UuDai;

--    -- 4. Xóa dữ liệu khách hàng, nhân viên và ca làm
--    DELETE FROM KhachHang;
--    DELETE FROM CaLam;
--    DELETE FROM NhanVien;

--    COMMIT TRANSACTION;
--    PRINT N'Đã xóa dữ liệu cũ thành công!';
--END TRY
--BEGIN CATCH
--    IF @@TRANCOUNT > 0
--        ROLLBACK TRANSACTION;

--    PRINT ERROR_MESSAGE();
--END CATCH;
--GO
