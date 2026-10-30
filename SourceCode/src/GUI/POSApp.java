package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class POSApp extends JFrame {

    public POSApp() {
        setTitle("Hệ Thống Quản Lý Bán Hàng - Ultramart");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ==========================================
        // 1. SIDEBAR NAVIGATION (MENU BÊN TRÁI)
        // ==========================================
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(30, 41, 59)); // Màu xám xanh tối
        sidebar.setPreferredSize(new Dimension(220, 0));

        // Header Sidebar: Logo (logo.png) & Nút Menu (menu.png)
        JPanel sidebarHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 15));
        sidebarHeader.setOpaque(false);
        JLabel lblLogo = new JLabel(createScaledIcon("logo.png", 40, 40));
        JLabel lblTitle = new JLabel("Ultramart");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        JLabel lblMenuBtn = new JLabel(createScaledIcon("menu.png", 20, 20));

        sidebarHeader.add(lblLogo);
        sidebarHeader.add(lblTitle);
        sidebarHeader.add(lblMenuBtn);
        sidebar.add(sidebarHeader);
        sidebar.add(createSeparator());

        // Danh mục điều hướng với 8 Icon chức năng
        sidebar.add(createNavButton("Trang chủ", "trangchu.png"));
        sidebar.add(createNavButton("Sản phẩm", "sanpham.png"));
        sidebar.add(createNavButton("Hóa đơn", "hoadon.png"));
        sidebar.add(createNavButton("Khách hàng", "khachhang.png"));
        sidebar.add(createNavButton("Nhân viên", "nhanvien.png"));
        sidebar.add(createNavButton("Khuyến mãi", "voucher.png"));
        sidebar.add(createNavButton("Đổi trả hàng", "doitra.png"));
        sidebar.add(createNavButton("Thống kê", "thongke.png"));

        sidebar.add(Box.createVerticalGlue()); // Tự động đẩy nút Đăng xuất xuống đáy

        // Nút Đăng xuất (dangxuat.png)
        JButton btnLogout = createNavButton("Đăng xuất", "dangxuat.png");
        btnLogout.setForeground(new Color(248, 113, 113));
        sidebar.add(btnLogout);

        add(sidebar, BorderLayout.WEST);

        // ==========================================
        // 2. KHU VỰC CHÍNH (MAIN CONTENT AREA)
        // ==========================================
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Thanh Topbar: Tìm kiếm (search.png), User Profile (user.png), Hiện/Ẩn mật khẩu (hienmk.png, hidemk.png)
        JPanel topbar = new JPanel(new BorderLayout(10, 0));
        topbar.setBackground(Color.WHITE);
        topbar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        // Ô tìm kiếm
        JPanel searchBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        searchBox.setOpaque(false);
        searchBox.add(new JLabel(createScaledIcon("search.png", 20, 20)));
        JTextField txtSearch = new JTextField(25);
        txtSearch.setText("Tìm kiếm sản phẩm, đơn hàng...");
        searchBox.add(txtSearch);
        topbar.add(searchBox, BorderLayout.WEST);

        // Thông tin người dùng
        JPanel userBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userBox.setOpaque(false);
        JLabel lblUser = new JLabel(" Thu ngân: Nguyễn Văn A", createScaledIcon("user.png", 20, 20), JLabel.LEFT);
        JToggleButton btnTogglePass = new JToggleButton(createScaledIcon("hienmk.png", 18, 18));
        btnTogglePass.setSelectedIcon(createScaledIcon("hidemk.png", 18, 18));
        btnTogglePass.setToolTipText("Hiện/Ẩn thông tin mật khẩu");

        userBox.add(lblUser);
        userBox.add(btnTogglePass);
        topbar.add(userBox, BorderLayout.EAST);

        mainPanel.add(topbar, BorderLayout.NORTH);

        // Bảng dữ liệu trung tâm & Thanh công cụ
        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));

        // Thanh công cụ thao tác (lưu.png, capnhat.png, xoa.png, xemchitiet.png, in.png)
        JToolBar actionToolbar = new JToolBar();
        actionToolbar.setFloatable(false);

        JButton btnSave = new JButton("Lưu", createScaledIcon("lưu.png", 20, 20));
        JButton btnUpdate = new JButton("Cập nhật", createScaledIcon("capnhat.png", 20, 20));
        JButton btnDelete = new JButton("Xóa", createScaledIcon("xoa.png", 20, 20));
        JButton btnDetail = new JButton("Xem chi tiết", createScaledIcon("xemchitiet.png", 20, 20));
        JButton btnPrint = new JButton("In hóa đơn", createScaledIcon("in.png", 20, 20));

        actionToolbar.add(btnSave);
        actionToolbar.add(btnUpdate);
        actionToolbar.add(btnDelete);
        actionToolbar.addSeparator();
        actionToolbar.add(btnDetail);
        actionToolbar.add(btnPrint);

        centerPanel.add(actionToolbar, BorderLayout.NORTH);

        // Bảng danh sách hàng hóa trong giỏ
        String[] columnNames = {"Mã SP", "Tên Sản Phẩm", "Đơn Giá", "Số Lượng", "Thành Tiền"};
        Object[][] data = {
                {"SP01", "Sữa tươi Vinamilk 1L", "32,000", "2", "64,000"},
                {"SP02", "Bánh mì Sandwich", "25,000", "1", "25,000"},
                {"SP03", "Cà phê hòa tan G7", "55,000", "3", "165,000"}
        };
        JTable table = new JTable(new DefaultTableModel(data, columnNames));
        JScrollPane scrollPane = new JScrollPane(table);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // ==========================================
        // 3. PANEL THANH TOÁN POS BÊN PHẢI
        // ==========================================
        JPanel payPanel = new JPanel();
        payPanel.setLayout(new BoxLayout(payPanel, BoxLayout.Y_AXIS));
        payPanel.setPreferredSize(new Dimension(260, 0));
        payPanel.setBorder(BorderFactory.createTitledBorder("Thanh Toán (POS)"));

        // Giỏ hàng (giohang.png)
        JLabel lblCart = new JLabel(" Giỏ hàng (3 sản phẩm)", createScaledIcon("giohang.png", 24, 24), JLabel.LEFT);
        lblCart.setFont(new Font("Arial", Font.BOLD, 14));
        lblCart.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTotal = new JLabel("Tổng tiền: 254,000 VNĐ");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 16));
        lblTotal.setForeground(new Color(220, 38, 38));
        lblTotal.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Nút Tiền mặt (tienmat.png) & Chuyển khoản (nganhang.png)
        JButton btnCash = new JButton("Thanh toán Tiền mặt", createScaledIcon("tienmat.png", 22, 22));
        btnCash.setMaximumSize(new Dimension(240, 40));
        btnCash.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnBank = new JButton("Chuyển khoản Ngân hàng", createScaledIcon("nganhang.png", 22, 22));
        btnBank.setMaximumSize(new Dimension(240, 40));
        btnBank.setAlignmentX(Component.CENTER_ALIGNMENT);

        payPanel.add(Box.createVerticalStrut(10));
        payPanel.add(lblCart);
        payPanel.add(Box.createVerticalStrut(15));
        payPanel.add(lblTotal);
        payPanel.add(Box.createVerticalStrut(20));
        payPanel.add(btnCash);
        payPanel.add(Box.createVerticalStrut(10));
        payPanel.add(btnBank);

        mainPanel.add(payPanel, BorderLayout.EAST);

        add(mainPanel, BorderLayout.CENTER);
    }

    // Hàm hỗ trợ nạp và thu nhỏ/phóng to ảnh tự động (Dùng Image.SCALE_SMOOTH)
    private ImageIcon createScaledIcon(String filename, int width, int height) {
        try {
            ImageIcon icon = new ImageIcon(filename);
            // Tự động kiểm tra và căn chỉnh tỷ lệ
            Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            System.err.println("Không thể nạp tệp ảnh: " + filename);
            return null;
        }
    }

    // Hàm hỗ trợ tạo Nút Menu Sidebar
    private JButton createNavButton(String text, String iconPath) {
        JButton btn = new JButton(text, createScaledIcon(iconPath, 20, 20));
        btn.setMaximumSize(new Dimension(220, 40));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setForeground(new Color(203, 213, 225));
        btn.setBackground(new Color(30, 41, 59));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Arial", Font.PLAIN, 14));
        return btn;
    }

    private JSeparator createSeparator() {
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(220, 1));
        sep.setForeground(new Color(51, 65, 85));
        return sep;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new POSApp().setVisible(true);
        });
    }
}