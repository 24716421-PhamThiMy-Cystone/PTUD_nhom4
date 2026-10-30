
package GUI;

import DAO.ChiTietHoaDonDAO;
import Entity.ChiTietHoaDon;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class ChiTietHoaDonUI extends JDialog {

    private final String maHD;

    private JTable tblChiTiet;
    private DefaultTableModel model;

    private JLabel lblMaHD;
    private JLabel lblSoSanPham;
    private JLabel lblTongTienHang;
    private JLabel lblTongThue;
    private JLabel lblTongChietKhau;
    private JLabel lblTongThanhToan;

    private final ChiTietHoaDonDAO dao = new ChiTietHoaDonDAO();

    private final NumberFormat dinhDangTien =
            NumberFormat.getNumberInstance(new Locale("vi", "VN"));

    public ChiTietHoaDonUI(Frame parent, String maHD) {
        super(parent, "Chi tiết hóa đơn", true);

        this.maHD = maHD;

        setSize(1000, 660);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        taoGiaoDien();
        loadChiTietHoaDon();
    }

    // Tạo giao diện
    private void taoGiaoDien() {

        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setBackground(UiKit.NEN);
        root.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // ----- Tiêu đề + mã hóa đơn -----
        JPanel pnlTieuDe = new JPanel(new BorderLayout(20, 0));
        pnlTieuDe.setOpaque(false);

        JPanel tieuDe = new JPanel();
        tieuDe.setOpaque(false);
        tieuDe.setLayout(new BoxLayout(tieuDe, BoxLayout.Y_AXIS));

        JLabel lblTieuDe = UiKit.tieuDeTrang("Chi tiết hóa đơn");
        lblTieuDe.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

        lblSoSanPham = UiKit.nhanNho("Số dòng sản phẩm: 0");
        lblSoSanPham.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

        tieuDe.add(lblTieuDe);
        tieuDe.add(Box.createVerticalStrut(2));
        tieuDe.add(lblSoSanPham);

        lblMaHD = UiKit.chip("Mã hóa đơn: " + maHD);

        JPanel chipBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        chipBox.setOpaque(false);
        chipBox.add(lblMaHD);

        pnlTieuDe.add(tieuDe, BorderLayout.WEST);
        pnlTieuDe.add(chipBox, BorderLayout.EAST);

        // ----- Bảng chi tiết hóa đơn -----
        String[] columns = {
            "STT",
            "Mã sản phẩm",
            "Số lượng",
            "Đơn giá (VNĐ)",
            "Thành tiền (VNĐ)",
            "Thuế (%)",
            "Tiền thuế (VNĐ)",
            "Chiết khấu (VNĐ)",
            "Thanh toán (VNĐ)"
        };

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblChiTiet = new JTable(model);
        UiKit.styleBang(tblChiTiet);
        tblChiTiet.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        int[] doRong = {
            45, 110, 75, 120, 135, 75, 125, 130, 145
        };

        for (int i = 0; i < doRong.length; i++) {
            tblChiTiet.getColumnModel()
                    .getColumn(i)
                    .setPreferredWidth(doRong[i]);
        }

        // Căn lề: STT và số lượng ở giữa, các cột tiền căn phải
        UiKit.RenderBang giua = new UiKit.RenderBang();
        giua.setHorizontalAlignment(SwingConstants.CENTER);

        UiKit.RenderBang phai = new UiKit.RenderBang();
        phai.setHorizontalAlignment(SwingConstants.RIGHT);

        UiKit.RenderBang phaiDam = new UiKit.RenderBang() {
            @Override
            public java.awt.Component getTableCellRendererComponent(
                    JTable t, Object v, boolean s, boolean f, int r, int c) {
                super.getTableCellRendererComponent(t, v, s, f, r, c);
                setFont(UiKit.F_DAM);
                return this;
            }
        };
        phaiDam.setHorizontalAlignment(SwingConstants.RIGHT);

        tblChiTiet.getColumnModel().getColumn(0).setCellRenderer(giua);
        tblChiTiet.getColumnModel().getColumn(2).setCellRenderer(giua);

        for (int i = 3; i <= 7; i++) {
            tblChiTiet.getColumnModel().getColumn(i).setCellRenderer(phai);
        }

        tblChiTiet.getColumnModel().getColumn(8).setCellRenderer(phaiDam);

        UiKit.TheBo theBang = new UiKit.TheBo(new BorderLayout(0, 10), 14);
        theBang.add(UiKit.tieuMuc("Danh sách sản phẩm"), BorderLayout.NORTH);
        theBang.add(UiKit.cuon(tblChiTiet), BorderLayout.CENTER);

        // ----- Tổng kết thanh toán -----
        lblTongTienHang = new JLabel("0 VNĐ");
        lblTongThue = new JLabel("0 VNĐ");
        lblTongChietKhau = new JLabel("0 VNĐ");
        lblTongThanhToan = new JLabel("0 VNĐ");

        lblTongTienHang.setFont(UiKit.F_DAM);
        lblTongThue.setFont(UiKit.F_DAM);

        lblTongChietKhau.setFont(UiKit.F_DAM);
        lblTongChietKhau.setForeground(UiKit.DO);

        lblTongThanhToan.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTongThanhToan.setForeground(UiKit.CAM);

        JPanel dong = new JPanel(new GridLayout(4, 1, 0, 8));
        dong.setOpaque(false);
        dong.add(dongTong("Tổng tiền hàng", lblTongTienHang, false));
        dong.add(dongTong("Tổng tiền thuế", lblTongThue, false));
        dong.add(dongTong("Tổng chiết khấu", lblTongChietKhau, false));
        dong.add(dongTong("Tổng thanh toán", lblTongThanhToan, true));

        UiKit.TheBo khungTong = new UiKit.TheBo(
                new BorderLayout(), 14, UiKit.VANG_NHAT, UiKit.VANG_VIEN);
        khungTong.add(dong, BorderLayout.CENTER);
        khungTong.setPreferredSize(
                new java.awt.Dimension(380, khungTong.getPreferredSize().height));

        // ----- Nút đóng -----
        JButton btnDong = UiKit.nutPhu("Đóng");
        btnDong.addActionListener(e -> dispose());

        JPanel nutTrai = new JPanel(new BorderLayout());
        nutTrai.setOpaque(false);
        nutTrai.add(btnDong, BorderLayout.SOUTH);

        JPanel pnlBottom = new JPanel(new BorderLayout(14, 0));
        pnlBottom.setOpaque(false);
        pnlBottom.add(nutTrai, BorderLayout.WEST);
        pnlBottom.add(khungTong, BorderLayout.EAST);

        root.add(pnlTieuDe, BorderLayout.NORTH);
        root.add(theBang, BorderLayout.CENTER);
        root.add(pnlBottom, BorderLayout.SOUTH);

        setContentPane(root);
    }

    // Một dòng tổng kết: nhãn bên trái, giá trị bên phải
    private JPanel dongTong(String nhan, JLabel giaTri, boolean noiBat) {
        JLabel lbl = new JLabel(nhan);
        lbl.setFont(noiBat
                ? new Font("Segoe UI", Font.BOLD, 15)
                : UiKit.F_THUONG);
        lbl.setForeground(noiBat ? UiKit.CHU : UiKit.CHU_PHU);

        JPanel d = new JPanel(new BorderLayout());
        d.setOpaque(false);
        d.add(lbl, BorderLayout.WEST);
        d.add(giaTri, BorderLayout.EAST);
        return d;
    }

    // Đọc chi tiết hóa đơn từ DAO
    private void loadChiTietHoaDon() {
        model.setRowCount(0);

        try {
            List<ChiTietHoaDon> dsChiTiet = dao.getByMaHD(maHD);

            if (dsChiTiet == null || dsChiTiet.isEmpty()) {
                lblSoSanPham.setText("Số dòng sản phẩm: 0");

                JOptionPane.showMessageDialog(
                        this,
                        "Hóa đơn chưa có chi tiết hoặc không tìm thấy dữ liệu.",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE
                );

                return;
            }

            double tongTienHang = 0;
            double tongThue = 0;
            double tongChietKhau = 0;
            double tongThanhToan = 0;

            int stt = 1;

            for (ChiTietHoaDon ct : dsChiTiet) {
                model.addRow(new Object[]{
                    stt++,
                    ct.getMaSP(),
                    ct.getSoLuong(),
                    dinhDangTien(ct.getDonGia()),
                    dinhDangTien(ct.getThanhTien()),
                    dinhDangTien(ct.getPhanTramThue()) + "%",
                    dinhDangTien(ct.getTienThue()),
                    dinhDangTien(ct.getTienChietKhau()),
                    dinhDangTien(ct.getThanhToan())
                });

                tongTienHang += ct.getThanhTien();
                tongThue += ct.getTienThue();
                tongChietKhau += ct.getTienChietKhau();
                tongThanhToan += ct.getThanhToan();
            }

            lblSoSanPham.setText(
                    "Số dòng sản phẩm: " + dsChiTiet.size()
            );

            lblTongTienHang.setText(
                    dinhDangTien(tongTienHang) + " VNĐ"
            );

            lblTongThue.setText(
                    dinhDangTien(tongThue) + " VNĐ"
            );

            lblTongChietKhau.setText(
                    dinhDangTien(tongChietKhau) + " VNĐ"
            );

            lblTongThanhToan.setText(
                    dinhDangTien(tongThanhToan) + " VNĐ"
            );

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Không thể tải chi tiết hóa đơn.\nChi tiết lỗi: "
                            + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );

            ex.printStackTrace();
        }
    }

    // Định dạng tiền Việt Nam
    private String dinhDangTien(double tien) {
        return dinhDangTien.format(tien);
    }
}
