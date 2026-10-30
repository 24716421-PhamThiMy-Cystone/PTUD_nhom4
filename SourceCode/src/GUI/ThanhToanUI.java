
package GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.File;
import java.text.NumberFormat;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.function.BooleanSupplier;

public class ThanhToanUI extends JDialog {

    private final long tongTien;
    private final BooleanSupplier xuLyLuuHoaDon;

    private JTextField txtTienKhachDua;
    private JLabel lblTienThua;
    private JLabel lblQR;

    private JPanel pnlTienMat;
    private JPanel pnlChuyenKhoan;

    private JToggleButton rdoTienMat;
    private JToggleButton rdoChuyenKhoan;

    private final NumberFormat dinhDangTien =
            NumberFormat.getInstance(new Locale("vi", "VN"));

    // Đường dẫn ảnh QR thanh toán
    private static final String DUONG_DAN_QR =
            "images/qr.png";

    public ThanhToanUI(
            JFrame parent,
            long tongTien,
            BooleanSupplier xuLyLuuHoaDon) {

        super(parent, "Thanh toán hóa đơn", true);

        this.tongTien = tongTien;
        this.xuLyLuuHoaDon = xuLyLuuHoaDon;

        setSize(640, 780);
        setLocationRelativeTo(parent);
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        khoiTaoGiaoDien();
    }

    private void khoiTaoGiaoDien() {
        JPanel pnlChinh = new JPanel(new BorderLayout(0, 14));
        pnlChinh.setBackground(UiKit.NEN);
        pnlChinh.setBorder(new EmptyBorder(20, 24, 20, 24));

        // ================= TIÊU ĐỀ + TỔNG TIỀN =================
        JLabel lblTieuDe = UiKit.tieuDeTrang("Thanh toán hóa đơn");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 22));

        UiKit.TheBo pnlThongTin = new UiKit.TheBo(
                new BorderLayout(0, 2), 16, UiKit.VANG_NHAT, UiKit.VANG_VIEN);

        JLabel lblTongTien = new JLabel(dinhDangTien(tongTien));
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTongTien.setForeground(UiKit.CAM);

        pnlThongTin.add(UiKit.nhanNho("Tổng thanh toán"), BorderLayout.NORTH);
        pnlThongTin.add(lblTongTien, BorderLayout.CENTER);

        // ================= PHƯƠNG THỨC THANH TOÁN =================
        rdoTienMat = new UiKit.NutChon("Tiền mặt");
        rdoChuyenKhoan = new UiKit.NutChon("Chuyển khoản");
        rdoTienMat.setSelected(true);

        ButtonGroup nhomPhuongThuc = new ButtonGroup();
        nhomPhuongThuc.add(rdoTienMat);
        nhomPhuongThuc.add(rdoChuyenKhoan);

        JPanel pnlPhuongThuc = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlPhuongThuc.setOpaque(false);
        pnlPhuongThuc.add(rdoTienMat);
        pnlPhuongThuc.add(rdoChuyenKhoan);

        JPanel khuPhuongThuc = new JPanel(new BorderLayout(0, 6));
        khuPhuongThuc.setOpaque(false);
        khuPhuongThuc.add(UiKit.nhanNho("Chọn phương thức thanh toán"),
                BorderLayout.NORTH);
        khuPhuongThuc.add(pnlPhuongThuc, BorderLayout.CENTER);

        // ================= NỘI DUNG THANH TOÁN =================
        pnlTienMat = taoPanelTienMat();
        pnlChuyenKhoan = taoPanelChuyenKhoan();

        JPanel pnlNoiDung = new JPanel(new CardLayout());
        pnlNoiDung.setOpaque(false);
        pnlNoiDung.add(pnlTienMat, "TIEN_MAT");
        pnlNoiDung.add(pnlChuyenKhoan, "CHUYEN_KHOAN");

        rdoTienMat.addActionListener(e ->
                ((CardLayout) pnlNoiDung.getLayout())
                        .show(pnlNoiDung, "TIEN_MAT")
        );

        rdoChuyenKhoan.addActionListener(e ->
                ((CardLayout) pnlNoiDung.getLayout())
                        .show(pnlNoiDung, "CHUYEN_KHOAN")
        );

        UiKit.TheBo theNoiDung = new UiKit.TheBo(new BorderLayout(), 14);
        theNoiDung.add(pnlNoiDung, BorderLayout.CENTER);

        // ================= NÚT CHỨC NĂNG =================
        JButton btnXacNhan = UiKit.nutXanh("Xác nhận thanh toán");
        btnXacNhan.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnXacNhan.setPreferredSize(new Dimension(0, 46));

        JButton btnHuy = UiKit.nutNguy("Hủy");
        btnHuy.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnHuy.setPreferredSize(new Dimension(110, 46));

        btnXacNhan.addActionListener(e -> {
            if (rdoTienMat.isSelected()) {
                xacNhanTienMat();
            } else {
                xacNhanChuyenKhoan();
            }
        });

        btnHuy.addActionListener(e -> dispose());

        JPanel pnlNut = new JPanel(new BorderLayout(10, 0));
        pnlNut.setOpaque(false);
        pnlNut.add(btnXacNhan, BorderLayout.CENTER);
        pnlNut.add(btnHuy, BorderLayout.EAST);

        // ================= GHÉP GIAO DIỆN =================
        JPanel pnlDau = new JPanel(new BorderLayout(0, 12));
        pnlDau.setOpaque(false);
        pnlDau.add(lblTieuDe, BorderLayout.NORTH);
        pnlDau.add(pnlThongTin, BorderLayout.CENTER);
        pnlDau.add(khuPhuongThuc, BorderLayout.SOUTH);

        pnlChinh.add(pnlDau, BorderLayout.NORTH);
        pnlChinh.add(theNoiDung, BorderLayout.CENTER);
        pnlChinh.add(pnlNut, BorderLayout.SOUTH);

        setContentPane(pnlChinh);
    }

    // =====================================================
    // PANEL THANH TOÁN TIỀN MẶT
    // =====================================================
    private JPanel taoPanelTienMat() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel lblTienKhach = new JLabel("Tiền khách đưa (VNĐ)");
        lblTienKhach.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTienKhach.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtTienKhachDua = new JTextField();
        txtTienKhachDua.setFont(new Font("Segoe UI", Font.BOLD, 20));
        txtTienKhachDua.setHorizontalAlignment(SwingConstants.RIGHT);
        txtTienKhachDua.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiKit.VANG_VIEN, 2),
                new EmptyBorder(6, 12, 6, 12)
        ));
        txtTienKhachDua.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 48)
        );
        txtTienKhachDua.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblTienThua = new JLabel("Tiền thừa: 0 đ");
        lblTienThua.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTienThua.setForeground(new Color(0, 128, 0));
        lblTienThua.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtTienKhachDua.getDocument().addDocumentListener(
                new DocumentListener() {
                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        capNhatTienThua();
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        capNhatTienThua();
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        capNhatTienThua();
                    }
                }
        );

        JPanel pnlGoiY = taoPanelGoiYTien();
        JPanel pnlBanPhim = taoBanPhimSo();

        panel.add(lblTienKhach);
        panel.add(Box.createVerticalStrut(6));
        panel.add(txtTienKhachDua);
        panel.add(Box.createVerticalStrut(8));
        panel.add(lblTienThua);
        panel.add(Box.createVerticalStrut(12));
        panel.add(pnlGoiY);
        panel.add(Box.createVerticalStrut(12));
        panel.add(pnlBanPhim);

        return panel;
    }

    // =====================================================
    // GỢI Ý TIỀN KHÁCH TRẢ
    // =====================================================
    private JPanel taoPanelGoiYTien() {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel luoi = new JPanel(new GridLayout(0, 3, 8, 8));
        luoi.setOpaque(false);

        long[] cacMucTien = tinhCacMucTienGoiY();

        for (int i = 0; i < cacMucTien.length; i++) {
            final long soTien = cacMucTien[i];

            // Mức đầu tiên là "vừa đủ" nên được làm nổi bật
            JButton btn = (i == 0)
                    ? UiKit.nutChinh(dinhDangTien(soTien))
                    : UiKit.nutPhu(dinhDangTien(soTien));

            btn.addActionListener(e -> {
                txtTienKhachDua.setText(String.valueOf(soTien));
                txtTienKhachDua.requestFocusInWindow();
                txtTienKhachDua.setCaretPosition(
                        txtTienKhachDua.getText().length()
                );
            });

            luoi.add(btn);
        }

        panel.add(UiKit.nhanNho("Gợi ý tiền khách trả"), BorderLayout.NORTH);
        panel.add(luoi, BorderLayout.CENTER);

        return panel;
    }

    private long[] tinhCacMucTienGoiY() {
        Set<Long> cacMuc = new LinkedHashSet<>();

        cacMuc.add(tongTien);
        cacMuc.add(lamTronLen(tongTien, 10_000));
        cacMuc.add(lamTronLen(tongTien, 50_000));
        cacMuc.add(lamTronLen(tongTien, 100_000));
        cacMuc.add(lamTronLen(tongTien, 200_000));
        cacMuc.add(lamTronLen(tongTien, 500_000));
        cacMuc.add(lamTronLen(tongTien, 1_000_000));

        return cacMuc.stream()
                .filter(soTien -> soTien >= tongTien && soTien > 0)
                .mapToLong(Long::longValue)
                .toArray();
    }

    private long lamTronLen(long soTien, long donVi) {
        if (soTien <= 0) {
            return 0;
        }

        long phanDu = soTien % donVi;

        if (phanDu == 0) {
            return soTien;
        }

        long phanCanCong = donVi - phanDu;

        if (soTien > Long.MAX_VALUE - phanCanCong) {
            return Long.MAX_VALUE;
        }

        return soTien + phanCanCong;
    }

    // =====================================================
    // BÀN PHÍM SỐ
    // =====================================================
    private JPanel taoBanPhimSo() {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel luoi = new JPanel(new GridLayout(4, 3, 8, 8));
        luoi.setOpaque(false);

        String[] cacPhim = {
                "7", "8", "9",
                "4", "5", "6",
                "1", "2", "3",
                "C", "0", "←"
        };

        for (String phim : cacPhim) {
            JButton btn = phim.equals("C")
                    ? UiKit.nutNguy(phim)
                    : UiKit.nutPhu(phim);

            btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
            btn.setPreferredSize(new Dimension(70, 40));

            btn.addActionListener(e -> xuLyPhimSo(phim));
            luoi.add(btn);
        }

        panel.add(UiKit.nhanNho("Bàn phím nhập tiền"), BorderLayout.NORTH);
        panel.add(luoi, BorderLayout.CENTER);

        return panel;
    }

    private void xuLyPhimSo(String phim) {
        String hienTai = txtTienKhachDua.getText();

        if ("C".equals(phim)) {
            txtTienKhachDua.setText("");
        } else if ("←".equals(phim)) {
            if (!hienTai.isEmpty()) {
                txtTienKhachDua.setText(
                        hienTai.substring(0, hienTai.length() - 1)
                );
            }
        } else {
            txtTienKhachDua.setText(hienTai + phim);
        }

        txtTienKhachDua.requestFocusInWindow();
    }

    // =====================================================
    // TÍNH TIỀN THỪA
    // =====================================================
    private void capNhatTienThua() {
        try {
            String chuoiTien = txtTienKhachDua.getText().trim();

            long tienKhachDua = chuoiTien.isEmpty()
                    ? 0
                    : Long.parseLong(chuoiTien);

            if (tienKhachDua < tongTien) {
                lblTienThua.setText(
                        "Còn thiếu: "
                                + dinhDangTien(tongTien - tienKhachDua)
                );
                lblTienThua.setForeground(Color.RED);
            } else {
                lblTienThua.setText(
                        "Tiền thừa: "
                                + dinhDangTien(tienKhachDua - tongTien)
                );
                lblTienThua.setForeground(new Color(0, 128, 0));
            }
        } catch (NumberFormatException ex) {
            lblTienThua.setText("Số tiền không hợp lệ");
            lblTienThua.setForeground(Color.RED);
        }
    }

    // =====================================================
    // PANEL CHUYỂN KHOẢN
    // =====================================================
    private JPanel taoPanelChuyenKhoan() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel lblHuongDan = new JLabel(
                "Quét mã QR bằng ứng dụng ngân hàng"
        );
        lblHuongDan.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblHuongDan.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblQR = new JLabel("", SwingConstants.CENTER);
        lblQR.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblQR.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiKit.VIEN),
                new EmptyBorder(10, 10, 10, 10)
        ));
        hienThiQR();

        JLabel lblSoTien = new JLabel(
                "Số tiền cần chuyển: " + dinhDangTien(tongTien)
        );
        lblSoTien.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblSoTien.setForeground(UiKit.CAM);
        lblSoTien.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblLuuY = UiKit.nhanNho(
                "Chỉ xác nhận sau khi giao dịch đã thành công."
        );
        lblLuuY.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalGlue());
        panel.add(lblHuongDan);
        panel.add(Box.createVerticalStrut(12));
        panel.add(lblQR);
        panel.add(Box.createVerticalStrut(12));
        panel.add(lblSoTien);
        panel.add(Box.createVerticalStrut(6));
        panel.add(lblLuuY);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private void hienThiQR() {
        File fileQR = new File(DUONG_DAN_QR);

        if (fileQR.exists()) {
            ImageIcon icon = new ImageIcon(DUONG_DAN_QR);

            Image anh = icon.getImage().getScaledInstance(
                    250, 250, Image.SCALE_SMOOTH
            );

            lblQR.setIcon(new ImageIcon(anh));
            lblQR.setText("");
        } else {
            lblQR.setIcon(null);
            lblQR.setText(
                    "<html><div style='text-align:center;'>"
                            + "Chưa tìm thấy ảnh QR.<br>"
                            + "Đặt ảnh tại:<br>"
                            + DUONG_DAN_QR
                            + "</div></html>"
            );
            lblQR.setForeground(UiKit.DO);
        }
    }

    // =====================================================
    // XÁC NHẬN TIỀN MẶT
    // =====================================================
    private void xacNhanTienMat() {
        try {
            String chuoiTien = txtTienKhachDua.getText().trim();

            if (chuoiTien.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Vui lòng nhập số tiền khách đưa!",
                        "Thông báo",
                        JOptionPane.WARNING_MESSAGE
                );
                txtTienKhachDua.requestFocusInWindow();
                return;
            }

            long tienKhachDua = Long.parseLong(chuoiTien);

            if (tienKhachDua < tongTien) {
                JOptionPane.showMessageDialog(
                        this,
                        "Tiền khách đưa chưa đủ thanh toán!",
                        "Thông báo",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            int luaChon = JOptionPane.showConfirmDialog(
                    this,
                    "Tổng tiền: " + dinhDangTien(tongTien)
                            + "\nTiền khách đưa: "
                            + dinhDangTien(tienKhachDua)
                            + "\nTiền thừa: "
                            + dinhDangTien(tienKhachDua - tongTien)
                            + "\n\nXác nhận thanh toán?",
                    "Xác nhận thanh toán",
                    JOptionPane.YES_NO_OPTION
            );

            if (luaChon == JOptionPane.YES_OPTION) {
                luuHoaDon();
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Số tiền nhập không hợp lệ!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =====================================================
    // XÁC NHẬN CHUYỂN KHOẢN
    // =====================================================
    private void xacNhanChuyenKhoan() {
        int luaChon = JOptionPane.showConfirmDialog(
                this,
                "Vui lòng kiểm tra tài khoản ngân hàng đã nhận đủ "
                        + dinhDangTien(tongTien)
                        + " chưa.\n\n"
                        + "Chỉ xác nhận khi giao dịch thành công.",
                "Xác nhận chuyển khoản",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (luaChon == JOptionPane.YES_OPTION) {
            luuHoaDon();
        }
    }

    // =====================================================
    // LƯU HÓA ĐƠN
    // =====================================================
    private void luuHoaDon() {
        if (xuLyLuuHoaDon == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Chưa thiết lập chức năng lưu hóa đơn!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        try {
            boolean thanhCong = xuLyLuuHoaDon.getAsBoolean();

            if (thanhCong) {
                JOptionPane.showMessageDialog(
                        this,
                        "Thanh toán thành công!",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE
                );
                dispose();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Không thể lưu hóa đơn. Vui lòng kiểm tra lại!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Có lỗi khi lưu hóa đơn: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =====================================================
    // ĐỊNH DẠNG TIỀN
    // =====================================================
    private String dinhDangTien(long soTien) {
        return dinhDangTien.format(soTien) + " đ";
    }
}
