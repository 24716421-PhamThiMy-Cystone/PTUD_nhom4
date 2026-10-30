
package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class UI extends JFrame {

    // ================= MÀU SẮC =================

    private final Color NEN_MENU = new Color(238, 240, 243);
    private final Color NEN_CHINH = new Color(247, 248, 250);
    private final Color VANG = new Color(255, 226, 122);
    private final Color HOVER = new Color(255, 235, 170);
    private final Color CHU = new Color(45, 45, 45);

    // ================= THÀNH PHẦN GIAO DIỆN =================

    private JPanel sidebar;
    private JPanel header;
    private JPanel contentPanel;
    private JPanel menuPanel;
    private JPanel bottomPanel;

    private JLabel logo;
    private JButton menuDangChon;

    private boolean thuGon = false;

    // ================= KHỞI TẠO =================

    public UI() {
        setTitle("UltraMart - Hệ thống quản lý siêu thị điện máy");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 550));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(NEN_CHINH);

        taoSidebar();
        taoKhuVucChinh();

        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    // ================= ĐỌC ẢNH =================

    /*
     * Ưu tiên đọc ảnh từ thư mục images nằm ở thư mục gốc project.
     * Nếu không tìm thấy thì thử đọc ảnh từ classpath.
     */
    private ImageIcon docAnh(String tenAnh) {
        if (tenAnh == null || tenAnh.isEmpty()) {
            return null;
        }

        // Cách 1: Đọc từ thư mục images bên ngoài src
        File file = new File("images", tenAnh);

        if (file.exists() && file.isFile()) {
            return new ImageIcon(file.getAbsolutePath());
        }

        // Cách 2: Đọc ảnh nếu images nằm trong src
        URL url = getClass().getResource("/images/" + tenAnh);

        if (url != null) {
            return new ImageIcon(url);
        }

        System.out.println("Không tìm thấy ảnh: " + tenAnh);
        return null;
    }

    // Thay đổi kích thước ảnh
    private ImageIcon doiKichThuocAnh(
            String tenAnh, int rongToiDa, int caoToiDa) {

        ImageIcon icon = docAnh(tenAnh);

        if (icon == null) {
            return null;
        }

        int rongGoc = icon.getIconWidth();
        int caoGoc = icon.getIconHeight();

        if (rongGoc <= 0 || caoGoc <= 0) {
            return null;
        }

        // Tính tỷ lệ để ảnh không bị kéo giãn
        double tyLe = Math.min(
            (double) rongToiDa / rongGoc,
            (double) caoToiDa / caoGoc
        );

        int rongMoi = Math.max(1, (int) (rongGoc * tyLe));
        int caoMoi = Math.max(1, (int) (caoGoc * tyLe));

        Image image = icon.getImage().getScaledInstance(
            rongMoi, caoMoi, Image.SCALE_SMOOTH
        );

        return new ImageIcon(image);
    }

    // ================= THANH MENU BÊN TRÁI =================

    private void taoSidebar() {
        sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(153, 0));
        sidebar.setBackground(NEN_MENU);

        // ---------- Phần logo ----------

        header = new JPanel(new BorderLayout());
        header.setPreferredSize(new Dimension(153, 105));
        header.setBackground(NEN_MENU);

        ImageIcon logoIcon = doiKichThuocAnh(
                "logo.png", 110,100);

        if (logoIcon != null) {
            logo = new JLabel(logoIcon);
        } else {
            logo = new JLabel(
                    "<html><center>"
                    + "<font color='#FF8C00' size='6'><b>U</b></font>"
                    + "<br><b>Ultramart</b>"
                    + "</center></html>",
                    SwingConstants.CENTER);
        }

        logo.setHorizontalAlignment(SwingConstants.CENTER);
        header.add(logo, BorderLayout.CENTER);

        // ---------- Nút thu gọn menu ----------

        JButton btnThuGon = new JButton();

        ImageIcon menuIcon = doiKichThuocAnh(
                "menu.png", 24,24);

        if (menuIcon != null) {
            btnThuGon.setIcon(menuIcon);
        } else {
            btnThuGon.setText("☰");
        }

        btnThuGon.setFont(new Font("Arial", Font.PLAIN, 19));
        btnThuGon.setForeground(CHU);
        btnThuGon.setBorderPainted(false);
        btnThuGon.setContentAreaFilled(false);
        btnThuGon.setFocusPainted(false);
        btnThuGon.setCursor(
                new Cursor(Cursor.HAND_CURSOR));
        btnThuGon.setPreferredSize(
                new Dimension(35, 40));

        header.add(btnThuGon, BorderLayout.EAST);

        // ---------- Danh sách menu ----------

        menuPanel = new JPanel();
        menuPanel.setLayout(
                new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(NEN_MENU);

        taoMenu("trangchu.png", "Trang chủ");
        taoMenu("sanpham.png", "Sản phẩm");
        taoMenu("hoadon.png", "Hóa đơn");
        taoMenu("nhanvien.png", "Nhân viên");
        taoMenu("thongke.png", "Báo cáo");
        taoMenu("voucher.png", "Ưu đãi");
        taoMenu("khachhang.png", "Khách hàng");
        taoMenu("doitra.png", "Đổi Trả");

        // Chọn Trang chủ mặc định
        chonMenu((JButton) menuPanel.getComponent(0));

        // ---------- Khu vực dưới cùng ----------

        bottomPanel = new JPanel();
        bottomPanel.setLayout(
                new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBackground(NEN_MENU);
        bottomPanel.setBorder(
                new EmptyBorder(0, 0, 35, 0));

        JButton btnHuongDan = taoNutMenu(
                "huongdan.png", "Hướng dẫn");

        JButton btnDangXuat = taoNutMenu(
                "dangxuat.png", "Đăng xuất");

        bottomPanel.add(btnHuongDan);
        bottomPanel.add(btnDangXuat);

        // Sự kiện hướng dẫn
        btnHuongDan.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Chọn chức năng trên thanh menu "
                    + "để sử dụng hệ thống UltraMart.",
                    "Hướng dẫn",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        // Sự kiện đăng xuất
        btnDangXuat.addActionListener(e -> {
            int luaChon = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có chắc chắn muốn đăng xuất?",
                    "Xác nhận đăng xuất",
                    JOptionPane.YES_NO_OPTION);

            if (luaChon == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(
                        this,
                        "Bạn đã xác nhận đăng xuất.");
            }
        });

        sidebar.add(header, BorderLayout.NORTH);
        sidebar.add(menuPanel, BorderLayout.CENTER);
        sidebar.add(bottomPanel, BorderLayout.SOUTH);

        // ---------- Xử lý thu gọn menu ----------

        btnThuGon.addActionListener(e -> {
            thuGon = !thuGon;

            sidebar.setPreferredSize(
                    new Dimension(thuGon ? 65 : 153, 0));

            header.setPreferredSize(
                    new Dimension(thuGon ? 65 : 153, 105));

            // Ẩn logo khi thu gọn
            logo.setVisible(!thuGon);

            // Thu gọn các menu chính
            for (Component c : menuPanel.getComponents()) {
                if (c instanceof JButton) {
                    JButton b = (JButton) c;

                    String ten = (String)
                            b.getClientProperty("ten");

                    b.setText(thuGon ? "" : ten);

                    b.setHorizontalAlignment(
                            thuGon
                            ? SwingConstants.CENTER
                            : SwingConstants.LEFT);

                    b.setBorder(new EmptyBorder(
                            0, thuGon ? 0 : 25, 0, 5));

                    b.setPreferredSize(
                            new Dimension(
                                    thuGon ? 65 : 153, 39));

                    b.setMaximumSize(
                            new Dimension(
                                    Integer.MAX_VALUE, 39));
                }
            }

            // Thu gọn Hướng dẫn và Đăng xuất
            for (Component c : bottomPanel.getComponents()) {
                if (c instanceof JButton) {
                    JButton b = (JButton) c;

                    String ten = (String)
                            b.getClientProperty("ten");

                    b.setText(thuGon ? "" : ten);

                    b.setHorizontalAlignment(
                            thuGon
                            ? SwingConstants.CENTER
                            : SwingConstants.LEFT);

                    b.setBorder(new EmptyBorder(
                            0, thuGon ? 0 : 25, 0, 5));

                    b.setPreferredSize(
                            new Dimension(
                                    thuGon ? 65 : 153, 39));

                    b.setMaximumSize(
                            new Dimension(
                                    Integer.MAX_VALUE, 39));
                }
            }

            sidebar.revalidate();
            sidebar.repaint();
        });
    }

    // ================= TẠO MENU =================

    private void taoMenu(
            String tenAnh, String ten) {

        JButton btn = taoNutMenu(
                 tenAnh, ten);

        btn.addActionListener(e -> {
            chonMenu(btn);
        });

        menuPanel.add(btn);
    }

    // ================= TẠO NÚT MENU CÓ ẢNH =================

    private JButton taoNutMenu(
           String tenAnh, String ten) {

        JButton btn = new JButton(ten);

        // Lưu tên để sử dụng khi thu gọn
        btn.putClientProperty("ten", ten);

        // Đọc ảnh biểu tượng
        ImageIcon icon = doiKichThuocAnh(
                tenAnh, 18, 18);

        if (icon != null) {
            btn.setIcon(icon);
        } else {
            // Nếu chưa có ảnh thì dùng ký tự thay thế
            btn.setText("    " + ten);
        }

        btn.setFont(new Font("Arial", Font.PLAIN, 13));
        btn.setForeground(CHU);
        btn.setBackground(NEN_MENU);

        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setIconTextGap(12);

        btn.setPreferredSize(new Dimension(153, 39));
        btn.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 39));

        btn.setBorder(
                new EmptyBorder(0, 25, 0, 5));

        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(true);

        btn.setCursor(
                new Cursor(Cursor.HAND_CURSOR));

        // Hiệu ứng khi rê chuột
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn != menuDangChon) {
                    btn.setBackground(HOVER);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (btn != menuDangChon) {
                    btn.setBackground(NEN_MENU);
                }
            }
        });

        return btn;
    }

    // ================= CHỌN MENU =================

    private void chonMenu(JButton btn) {
        if (menuDangChon != null) {
            menuDangChon.setBackground(NEN_MENU);
        }

        menuDangChon = btn;
        menuDangChon.setBackground(VANG);
    }

    // ================= KHU VỰC CHÍNH =================

    private void taoKhuVucChinh() {
        contentPanel = new JPanel(
                new BorderLayout(0, 0));

        contentPanel.setBackground(NEN_CHINH);
        contentPanel.setBorder(
                new EmptyBorder(20, 30, 20, 30));

        // ---------- Thanh phía trên ----------

        JPanel topBar = new JPanel(
                new BorderLayout(20, 0));

        topBar.setOpaque(false);
        topBar.setPreferredSize(
                new Dimension(0, 65));

        // ---------- Ô tìm kiếm ----------

        JPanel searchPanel = new JPanel(
                new BorderLayout());

        searchPanel.setBackground(Color.WHITE);
        searchPanel.setPreferredSize(
                new Dimension(263, 35));

        searchPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(225, 228, 232)),
                        new EmptyBorder(0, 12, 0, 5)));

        JTextField txtTimKiem = new JTextField();
        txtTimKiem.setText(
                "Tra cứu sản phẩm / hóa đơn");

        txtTimKiem.setFont(
                new Font("Arial", Font.PLAIN, 12));

        txtTimKiem.setForeground(
                new Color(105, 110, 120));

        txtTimKiem.setBorder(null);
        txtTimKiem.setOpaque(false);

        // ---------- Nút tìm kiếm bằng hình ảnh ----------

        JButton btnTim = new JButton();

        // Đọc ảnh search.png và giữ nguyên tỷ lệ ảnh
        ImageIcon searchIcon = doiKichThuocAnh(
                "search.png", 20, 20);

        if (searchIcon != null) {
            btnTim.setIcon(searchIcon);
        } else {
            // Dùng ký tự thay thế nếu chưa có ảnh search.png
            btnTim.setText("⌕");
            btnTim.setFont(
                    new Font("Arial", Font.PLAIN, 27));
        }

        btnTim.setPreferredSize(new Dimension(35, 35));
        btnTim.setBorderPainted(false);
        btnTim.setContentAreaFilled(false);
        btnTim.setFocusPainted(false);
        btnTim.setCursor(
                new Cursor(Cursor.HAND_CURSOR));

        searchPanel.add(
                txtTimKiem, BorderLayout.CENTER);

        searchPanel.add(
                btnTim, BorderLayout.EAST);

        // ---------- Thông tin tài khoản ----------

        JPanel userPanel = new JPanel(
                new FlowLayout(
                        FlowLayout.CENTER, 10, 8));

        userPanel.setBackground(
                new Color(238, 240, 243));

        userPanel.setPreferredSize(
                new Dimension(155, 63));

        // Đọc ảnh đại diện nếu có
        ImageIcon avatarIcon = doiKichThuocAnh(
                "Thụy.png", 24,24);
        
        

        JLabel avatar;

        if (avatarIcon != null) {
            avatar = new JLabel(avatarIcon);
        } else {
            avatar = new JLabel("👨🏻");
            avatar.setFont(
                    new Font("Segoe UI Emoji", Font.PLAIN, 24));
        }

        JLabel userInfo = new JLabel(
                "<html><b>Phạm Nguyễn<br>"
                + "Vĩnh Thụy</b>"
                + "<br><font color='#777777' size='3'>"
                + "Quản lý</font></html>");

        userInfo.setFont(
                new Font("Arial", Font.PLAIN, 12));

        userPanel.add(avatar);
        userPanel.add(userInfo);

        topBar.add(searchPanel, BorderLayout.WEST);
        topBar.add(userPanel, BorderLayout.EAST);

        // ---------- Khu vực nội dung ----------

        JPanel mainContent = new JPanel();
        mainContent.setOpaque(false);

        contentPanel.add(
                topBar, BorderLayout.NORTH);

        contentPanel.add(
                mainContent, BorderLayout.CENTER);
    }

    // ================= CHẠY CHƯƠNG TRÌNH =================

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            UI frame = new UI();
            frame.setVisible(true);
        });
    }
}
