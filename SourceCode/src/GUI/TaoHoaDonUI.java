
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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import java.sql.Date;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;

import java.sql.ResultSetMetaData;
public class TaoHoaDonUI extends JFrame {

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

	private JPanel mainContent;

	private JTable tblHoaDon;
	private JTable tblSanPham;
	private JTable tblDaChon;

	private DefaultTableModel modelHoaDon;
	private DefaultTableModel modelSanPham;
	private DefaultTableModel modelDaChon;

	private JTextField txtLocHoaDon;
	private JTextField txtNgayTu;
	private JTextField txtNgayDen;
	private JCheckBox chkHDThanhToan;
	private JCheckBox chkHDChoThanhToan;
	private JCheckBox chkHDDaHuy;
	private JCheckBox chkHDTienMat;
	private JCheckBox chkHDNganHang;
	private JCheckBox chkHDHomNay;
	private JCheckBox chkHD7Ngay;
	private JCheckBox chkHDTatCa;
	private JTextField txtTimSanPham;
	private JSpinner spnSoLuong;
	private JLabel lblMaHD;
	private JLabel lblTongTien;

	// Cờ chặn vòng lặp khi code tự cập nhật bảng "Sản phẩm đã chọn"
	private boolean dangCapNhatBang = false;

	private final String URL_DB = "jdbc:sqlserver://localhost:1433;databaseName=QL_SieuThiDienMay;encrypt=true;trustServerCertificate=true";

	private final String USER = "sa";
	private final String PASS = "sapassword";
	private static final String MA_NV_DANG_DANG_NHAP = "NV260002";

	private Connection getConnection() throws Exception {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		return DriverManager.getConnection(URL_DB, USER, PASS);
	}

	private JLabel logo;
	private JButton menuDangChon;

	private boolean thuGon = false;
	private JComboBox<KhachHangItem> cboKhachHang;
	private JComboBox<UuDaiItem> cboUuDai;

	private JLabel lblTamTinh;
	private JLabel lblGiamGia;

	private double tongTienGoc = 0;
	private double soTienGiam = 0;
	// Bảng và ô tìm kiếm khách hàng
	private JTable tblTraCuuKH;
	private DefaultTableModel modelTraCuuKH;
	private TableRowSorter<DefaultTableModel> sorterKH;
	private JTextField txtTimKiemKH;

	// Lưu mã khách hàng được chọn
	private String maKHDaChon = null;

	// ================= KHỞI TẠO =================

	public TaoHoaDonUI() {
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

	private static class KhachHangItem {
		String maKH;
		String hoTen;

		KhachHangItem(String maKH, String hoTen) {
			this.maKH = maKH;
			this.hoTen = hoTen;
		}

		@Override
		public String toString() {
			return maKH == null ? "Khách lẻ" : maKH + " - " + hoTen;
		}
	}

	private static class UuDaiItem {
		String maUD;
		String tenUD;
		double phanTramGiam;

		UuDaiItem(String maUD, String tenUD, double phanTramGiam) {
			this.maUD = maUD;
			this.tenUD = tenUD;
			this.phanTramGiam = phanTramGiam;
		}

		@Override
		public String toString() {
			return tenUD + " (" + phanTramGiam + "%)";
		}
	}

	// ================= ĐỌC ẢNH =================

	/*
	 * Ưu tiên đọc ảnh từ thư mục images nằm ở thư mục gốc project. Nếu không tìm
	 * thấy thì thử đọc ảnh từ classpath.
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
	private ImageIcon doiKichThuocAnh(String tenAnh, int rongToiDa, int caoToiDa) {

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
		double tyLe = Math.min((double) rongToiDa / rongGoc, (double) caoToiDa / caoGoc);

		int rongMoi = Math.max(1, (int) (rongGoc * tyLe));
		int caoMoi = Math.max(1, (int) (caoGoc * tyLe));

		Image image = icon.getImage().getScaledInstance(rongMoi, caoMoi, Image.SCALE_SMOOTH);

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

		ImageIcon logoIcon = doiKichThuocAnh("logo.png", 110, 100);

		if (logoIcon != null) {
			logo = new JLabel(logoIcon);
		} else {
			logo = new JLabel("<html><center>" + "<font color='#FF8C00' size='6'><b>U</b></font>"
					+ "<br><b>Ultramart</b>" + "</center></html>", SwingConstants.CENTER);
		}

		logo.setHorizontalAlignment(SwingConstants.CENTER);
		header.add(logo, BorderLayout.CENTER);

		// ---------- Nút thu gọn menu ----------

		JButton btnThuGon = new JButton();

		ImageIcon menuIcon = doiKichThuocAnh("menu.png", 24, 24);

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
		btnThuGon.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnThuGon.setPreferredSize(new Dimension(35, 40));

		header.add(btnThuGon, BorderLayout.EAST);

		// ---------- Danh sách menu ----------

		menuPanel = new JPanel();
		menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
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
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
		bottomPanel.setBackground(NEN_MENU);
		bottomPanel.setBorder(new EmptyBorder(0, 0, 35, 0));

		JButton btnHuongDan = taoNutMenu("huongdan.png", "Hướng dẫn");

		JButton btnDangXuat = taoNutMenu("dangxuat.png", "Đăng xuất");

		bottomPanel.add(btnHuongDan);
		bottomPanel.add(btnDangXuat);

		// Sự kiện hướng dẫn
		btnHuongDan.addActionListener(e -> {
			JOptionPane.showMessageDialog(this, "Chọn chức năng trên thanh menu " + "để sử dụng hệ thống UltraMart.",
					"Hướng dẫn", JOptionPane.INFORMATION_MESSAGE);
		});

		// Sự kiện đăng xuất
		btnDangXuat.addActionListener(e -> {
			int luaChon = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đăng xuất?", "Xác nhận đăng xuất",
					JOptionPane.YES_NO_OPTION);

			if (luaChon == JOptionPane.YES_OPTION) {
				JOptionPane.showMessageDialog(this, "Bạn đã xác nhận đăng xuất.");
			}
		});

		sidebar.add(header, BorderLayout.NORTH);
		sidebar.add(menuPanel, BorderLayout.CENTER);
		sidebar.add(bottomPanel, BorderLayout.SOUTH);

		// ---------- Xử lý thu gọn menu ----------

		btnThuGon.addActionListener(e -> {
			thuGon = !thuGon;

			sidebar.setPreferredSize(new Dimension(thuGon ? 65 : 153, 0));

			header.setPreferredSize(new Dimension(thuGon ? 65 : 153, 105));

			// Ẩn logo khi thu gọn
			logo.setVisible(!thuGon);

			// Thu gọn các menu chính
			for (Component c : menuPanel.getComponents()) {
				if (c instanceof JButton) {
					JButton b = (JButton) c;

					String ten = (String) b.getClientProperty("ten");

					b.setText(thuGon ? "" : ten);

					b.setHorizontalAlignment(thuGon ? SwingConstants.CENTER : SwingConstants.LEFT);

					b.setBorder(new EmptyBorder(0, thuGon ? 0 : 25, 0, 5));

					b.setPreferredSize(new Dimension(thuGon ? 65 : 153, 39));

					b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 39));
				}
			}

			// Thu gọn Hướng dẫn và Đăng xuất
			for (Component c : bottomPanel.getComponents()) {
				if (c instanceof JButton) {
					JButton b = (JButton) c;

					String ten = (String) b.getClientProperty("ten");

					b.setText(thuGon ? "" : ten);

					b.setHorizontalAlignment(thuGon ? SwingConstants.CENTER : SwingConstants.LEFT);

					b.setBorder(new EmptyBorder(0, thuGon ? 0 : 25, 0, 5));

					b.setPreferredSize(new Dimension(thuGon ? 65 : 153, 39));

					b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 39));
				}
			}

			sidebar.revalidate();
			sidebar.repaint();
		});
	}

	// ================= TẠO MENU =================

	private void taoMenu(String tenAnh, String ten) {

		JButton btn = taoNutMenu(tenAnh, ten);

		btn.addActionListener(e -> {

			chonMenu(btn);

			switch (ten) {

			case "Trang chủ":
				hienTrangChu();
				break;

			case "Hóa đơn":
				hienDanhSachHoaDon();
				break;
			}

		});
		menuPanel.add(btn);
	}

	// ================= TẠO NÚT MENU CÓ ẢNH =================

	private JButton taoNutMenu(String tenAnh, String ten) {

		JButton btn = new JButton(ten);

		// Lưu tên để sử dụng khi thu gọn
		btn.putClientProperty("ten", ten);

		// Đọc ảnh biểu tượng
		ImageIcon icon = doiKichThuocAnh(tenAnh, 18, 18);

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
		btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 39));

		btn.setBorder(new EmptyBorder(0, 25, 0, 5));

		btn.setFocusPainted(false);
		btn.setBorderPainted(false);
		btn.setContentAreaFilled(true);

		btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

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
		contentPanel = new JPanel(new BorderLayout(0, 0));

		contentPanel.setBackground(NEN_CHINH);
		contentPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

		// ---------- Thanh phía trên ----------

		JPanel topBar = new JPanel(new BorderLayout(20, 0));

		topBar.setOpaque(false);
		topBar.setPreferredSize(new Dimension(0, 65));

		// ---------- Ô tìm kiếm ----------

		JPanel searchPanel = new JPanel(new BorderLayout());

		searchPanel.setBackground(Color.WHITE);
		searchPanel.setPreferredSize(new Dimension(263, 35));

		searchPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(225, 228, 232)), new EmptyBorder(0, 12, 0, 5)));

		JTextField txtTimKiem = new JTextField();
		txtTimKiem.setText("Tra cứu sản phẩm / hóa đơn");

		txtTimKiem.setFont(new Font("Arial", Font.PLAIN, 12));

		txtTimKiem.setForeground(new Color(105, 110, 120));

		txtTimKiem.setBorder(null);
		txtTimKiem.setOpaque(false);

		// ---------- Nút tìm kiếm bằng hình ảnh ----------

		JButton btnTim = new JButton();

		// Đọc ảnh search.png và giữ nguyên tỷ lệ ảnh
		ImageIcon searchIcon = doiKichThuocAnh("search.png", 20, 20);

		if (searchIcon != null) {
			btnTim.setIcon(searchIcon);
		} else {
			// Dùng ký tự thay thế nếu chưa có ảnh search.png
			btnTim.setText("⌕");
			btnTim.setFont(new Font("Arial", Font.PLAIN, 27));
		}

		btnTim.setPreferredSize(new Dimension(35, 35));
		btnTim.setBorderPainted(false);
		btnTim.setContentAreaFilled(false);
		btnTim.setFocusPainted(false);
		btnTim.setCursor(new Cursor(Cursor.HAND_CURSOR));

		searchPanel.add(txtTimKiem, BorderLayout.CENTER);

		searchPanel.add(btnTim, BorderLayout.EAST);

		// ---------- Thông tin tài khoản ----------

		JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));

		userPanel.setBackground(new Color(238, 240, 243));

		userPanel.setPreferredSize(new Dimension(155, 63));

		// Đọc ảnh đại diện nếu có
		ImageIcon avatarIcon = doiKichThuocAnh("Thụy.png", 24, 24);

		JLabel avatar;

		if (avatarIcon != null) {
			avatar = new JLabel(avatarIcon);
		} else {
			avatar = new JLabel("👨🏻");
			avatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
		}

		JLabel userInfo = new JLabel("<html><b>Phạm Nguyễn<br>" + "Vĩnh Thụy</b>"
				+ "<br><font color='#777777' size='3'>" + "Quản lý</font></html>");

		userInfo.setFont(new Font("Arial", Font.PLAIN, 12));

		userPanel.add(avatar);
		userPanel.add(userInfo);

		topBar.add(searchPanel, BorderLayout.WEST);
		topBar.add(userPanel, BorderLayout.EAST);

		// ---------- Khu vực nội dung ----------

//        JPanel mainContent = new JPanel();
		mainContent = new JPanel(new BorderLayout());
		hienTrangChu();
		mainContent.setOpaque(false);

		contentPanel.add(topBar, BorderLayout.NORTH);

		contentPanel.add(mainContent, BorderLayout.CENTER);
	}

	private void hienTrangChu() {

		mainContent.removeAll();

		JPanel p = new JPanel();

		p.setOpaque(false);

		p.add(new JLabel("Trang chủ UltraMart"));

		mainContent.add(p, BorderLayout.CENTER);

		mainContent.revalidate();
		mainContent.repaint();

	}

	// ================= DANH SÁCH HÓA ĐƠN =================

	private void hienDanhSachHoaDon() {
		mainContent.removeAll();
		JPanel page = new JPanel(new BorderLayout(14, 14));
		page.setOpaque(false);

		JPanel filterCard = new JPanel(new BorderLayout(10, 10));
		filterCard.setBackground(Color.WHITE);
		filterCard.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(224, 228, 233)),
				new EmptyBorder(14, 16, 12, 16)));

		JPanel filterHeader = new JPanel(new BorderLayout(16, 0));
		filterHeader.setOpaque(false);
		JLabel filterTitle = new JLabel("Bộ lọc");
		filterTitle.setFont(new Font("Arial", Font.BOLD, 19));
		txtLocHoaDon = new JTextField();
		txtLocHoaDon.setFont(new Font("Arial", Font.PLAIN, 13));
		txtLocHoaDon.setPreferredSize(new Dimension(300, 36));
		txtLocHoaDon.setToolTipText("Tìm theo mã hóa đơn hoặc tên khách hàng");
		filterHeader.add(filterTitle, BorderLayout.WEST);
		filterHeader.add(txtLocHoaDon, BorderLayout.CENTER);
		filterCard.add(filterHeader, BorderLayout.NORTH);

		chkHDThanhToan = new JCheckBox("Đã thanh toán");
		chkHDChoThanhToan = new JCheckBox("Chờ thanh toán");
		chkHDDaHuy = new JCheckBox("Đã hủy");
		chkHDTienMat = new JCheckBox("Tiền mặt");
		chkHDNganHang = new JCheckBox("Ngân hàng");
		chkHDHomNay = new JCheckBox("Hôm nay");
		chkHD7Ngay = new JCheckBox("7 ngày qua");
		chkHDTatCa = new JCheckBox("Tất cả", true);

		JPanel groups = new JPanel(new GridLayout(1, 3, 14, 0));
		groups.setOpaque(false);
		groups.add(taoNhomLoc("Trạng thái", chkHDThanhToan, chkHDChoThanhToan, chkHDDaHuy));
		groups.add(taoNhomLoc("Hình thức", chkHDTienMat, chkHDNganHang));
		groups.add(taoNhomLoc("Thời gian", chkHDHomNay, chkHD7Ngay, chkHDTatCa));
		filterCard.add(groups, BorderLayout.CENTER);

		JPanel filterActions = new JPanel(new BorderLayout());
		filterActions.setOpaque(false);
		JLabel activeFilters = new JLabel("Chọn bộ lọc rồi nhấn Áp dụng");
		activeFilters.setForeground(new Color(105, 112, 120));
		JPanel filterButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
		filterButtons.setOpaque(false);
		JButton btnXoaLoc = taoNutPhu("Xóa lọc");
		JButton btnLoc = taoNutChinh("Áp dụng", new Color(255, 211, 76));
		filterButtons.add(btnXoaLoc);
		filterButtons.add(btnLoc);
		filterActions.add(activeFilters, BorderLayout.WEST);
		filterActions.add(filterButtons, BorderLayout.EAST);
		filterCard.add(filterActions, BorderLayout.SOUTH);
		page.add(filterCard, BorderLayout.NORTH);

		JPanel results = new JPanel(new BorderLayout(10, 10));
		results.setOpaque(false);
		JPanel resultsHeader = new JPanel(new BorderLayout(10, 8));
		resultsHeader.setOpaque(false);
		JLabel title = new JLabel("Quản lý hóa đơn");
		title.setFont(new Font("Arial", Font.BOLD, 23));
		resultsHeader.add(title, BorderLayout.WEST);

		JPanel dateAndActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
		dateAndActions.setOpaque(false);
		dateAndActions.add(new JLabel("Từ ngày:"));
		txtNgayTu = new JTextField(9);
		txtNgayTu.setToolTipText("dd/MM/yyyy");
		dateAndActions.add(txtNgayTu);
		dateAndActions.add(new JLabel("Đến ngày:"));
		txtNgayDen = new JTextField(9);
		txtNgayDen.setToolTipText("dd/MM/yyyy");
		dateAndActions.add(txtNgayDen);
		JButton btnTao = taoNutChinh("+ Tạo hóa đơn", new Color(255, 211, 76));
		dateAndActions.add(btnTao);
		resultsHeader.add(dateAndActions, BorderLayout.EAST);
		results.add(resultsHeader, BorderLayout.NORTH);

		modelHoaDon = new DefaultTableModel(new String[] {
				"Mã HD", "Thời gian", "Khách hàng", "Tổng tiền", "Ưu đãi", "Thành tiền", "PTTT", "Trạng thái"
		}, 0) {
			@Override public boolean isCellEditable(int row, int column) { return false; }
		};
		tblHoaDon = new JTable(modelHoaDon);
		tblHoaDon.setRowHeight(34);
		tblHoaDon.setFont(new Font("Arial", Font.PLAIN, 12));
		tblHoaDon.setFillsViewportHeight(true);
		tblHoaDon.setShowVerticalLines(false);
		tblHoaDon.setGridColor(new Color(231, 234, 238));
		tblHoaDon.setSelectionBackground(new Color(255, 241, 194));
		tblHoaDon.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
		tblHoaDon.getTableHeader().setBackground(new Color(225, 247, 250));
		tblHoaDon.getTableHeader().setReorderingAllowed(false);
		tblHoaDon.setAutoCreateRowSorter(true);
		dinhDangCotTien(tblHoaDon, 3, 4, 5);
		JScrollPane scrollHoaDon = new JScrollPane(tblHoaDon);
		scrollHoaDon.setBorder(BorderFactory.createLineBorder(new Color(224, 228, 233)));
		results.add(scrollHoaDon, BorderLayout.CENTER);

		JPanel resultActions = new JPanel(new BorderLayout());
		resultActions.setOpaque(false);
		JLabel dateHint = new JLabel("Ngày nhập theo định dạng dd/MM/yyyy");
		dateHint.setForeground(new Color(110, 116, 124));
		resultActions.add(dateHint, BorderLayout.WEST);
		JButton btnXemChiTiet = taoNutChinh("Xem chi tiết", new Color(43, 112, 177));
		btnXemChiTiet.setForeground(Color.WHITE);
		resultActions.add(btnXemChiTiet, BorderLayout.EAST);
		results.add(resultActions, BorderLayout.SOUTH);
		page.add(results, BorderLayout.CENTER);

		btnLoc.addActionListener(e -> loadHoaDon(txtLocHoaDon.getText()));
		txtLocHoaDon.addActionListener(e -> loadHoaDon(txtLocHoaDon.getText()));
		txtNgayTu.addActionListener(e -> loadHoaDon(txtLocHoaDon.getText()));
		txtNgayDen.addActionListener(e -> loadHoaDon(txtLocHoaDon.getText()));
		btnXoaLoc.addActionListener(e -> {
			txtLocHoaDon.setText("");
			txtNgayTu.setText("");
			txtNgayDen.setText("");
			chkHDThanhToan.setSelected(false);
			chkHDChoThanhToan.setSelected(false);
			chkHDDaHuy.setSelected(false);
			chkHDTienMat.setSelected(false);
			chkHDNganHang.setSelected(false);
			chkHDHomNay.setSelected(false);
			chkHD7Ngay.setSelected(false);
			chkHDTatCa.setSelected(true);
			loadHoaDon("");
		});
		chkHDTatCa.addActionListener(e -> {
			if (chkHDTatCa.isSelected()) {
				chkHDHomNay.setSelected(false);
				chkHD7Ngay.setSelected(false);
			}
		});
		chkHDHomNay.addActionListener(e -> { if (chkHDHomNay.isSelected()) chkHDTatCa.setSelected(false); });
		chkHD7Ngay.addActionListener(e -> { if (chkHD7Ngay.isSelected()) chkHDTatCa.setSelected(false); });
		btnTao.addActionListener(e -> hienTaoHoaDon());
		btnXemChiTiet.addActionListener(e -> {
			int selectedRow = tblHoaDon.getSelectedRow();
			if (selectedRow < 0) {
				JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn cần xem chi tiết!", "Thông báo", JOptionPane.WARNING_MESSAGE);
				return;
			}
			int modelRow = tblHoaDon.convertRowIndexToModel(selectedRow);
			String maHD = modelHoaDon.getValueAt(modelRow, 0).toString();
			new ChiTietHoaDonUI(this, maHD).setVisible(true);
		});

		loadHoaDon("");
		mainContent.add(page, BorderLayout.CENTER);
		mainContent.revalidate();
		mainContent.repaint();
	}

	private JPanel taoNhomLoc(String tieuDe, JCheckBox... checkboxes) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBackground(Color.WHITE);
		JLabel title = new JLabel(tieuDe);
		title.setFont(new Font("Arial", Font.BOLD, 12));
		title.setForeground(new Color(100, 107, 116));
		panel.add(title);
		for (JCheckBox checkbox : checkboxes) {
			checkbox.setOpaque(false);
			checkbox.setFont(new Font("Arial", Font.PLAIN, 12));
			panel.add(checkbox);
		}
		return panel;
	}

	private JButton taoNutPhu(String text) {
		JButton button = new JButton(text);
		button.setFont(new Font("Arial", Font.PLAIN, 12));
		button.setFocusPainted(false);
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		return button;
	}

	private JButton taoNutChinh(String text, Color background) {
		JButton button = new JButton(text);
		button.setFont(new Font("Arial", Font.BOLD, 12));
		button.setBackground(background);
		button.setForeground(new Color(43, 48, 54));
		button.setFocusPainted(false);
		button.setBorderPainted(false);
		button.setOpaque(true);
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		return button;
	}

	private void loadHoaDon(String tuKhoa) {
		if (modelHoaDon == null) return;
		modelHoaDon.setRowCount(0);
		try {
			java.time.LocalDate tuNgay = docNgay(txtNgayTu == null ? "" : txtNgayTu.getText());
			java.time.LocalDate denNgay = docNgay(txtNgayDen == null ? "" : txtNgayDen.getText());
			if (tuNgay != null && denNgay != null && tuNgay.isAfter(denNgay)) {
				throw new IllegalArgumentException("Ngày bắt đầu phải trước hoặc bằng ngày kết thúc.");
			}
			StringBuilder sql = new StringBuilder("""
				SELECT h.maHD, h.ngayLap, COALESCE(k.hoTen, N'Khách lẻ'),
				       COALESCE(d.tongTruocGiam, h.tongTien),
				       CASE WHEN d.tongTruocGiam IS NULL THEN 0
				            WHEN d.tongTruocGiam - h.tongTien > COALESCE(d.giamTheoDong, 0)
				                 THEN d.tongTruocGiam - h.tongTien
				            ELSE COALESCE(d.giamTheoDong, 0) END,
				       h.tongTien, COALESCE(h.phuongThucTT, N'—'), h.trangThai
				FROM HoaDon h
				LEFT JOIN KhachHang k ON h.maKH = k.maKH
				LEFT JOIN (
				    SELECT maHD,
				           SUM(COALESCE(thanhTien, soLuong * donGia) + COALESCE(tienThue, 0)) AS tongTruocGiam,
				           SUM(COALESCE(tienChietKhau, 0)) AS giamTheoDong
				    FROM ChiTietHoaDon GROUP BY maHD
				) d ON h.maHD = d.maHD
				WHERE (h.maHD LIKE ? OR k.hoTen LIKE ?)
				""");
			java.util.List<Object> params = new java.util.ArrayList<>();
			String keyword = tuKhoa == null ? "" : tuKhoa.trim();
			params.add("%" + keyword + "%");
			params.add("%" + keyword + "%");

			java.util.List<String> statuses = new java.util.ArrayList<>();
			if (chkHDThanhToan != null && chkHDThanhToan.isSelected()) statuses.add("Đã thanh toán");
			if (chkHDChoThanhToan != null && chkHDChoThanhToan.isSelected()) {
				statuses.add("Chờ thanh toán");
				statuses.add("Đã đặt cọc");
			}
			if (chkHDDaHuy != null && chkHDDaHuy.isSelected()) statuses.add("Đã hủy");
			if (!statuses.isEmpty()) {
				sql.append(" AND h.trangThai IN (").append(String.join(",", java.util.Collections.nCopies(statuses.size(), "?"))).append(")");
				params.addAll(statuses);
			}

			java.util.List<String> methods = new java.util.ArrayList<>();
			if (chkHDTienMat != null && chkHDTienMat.isSelected()) methods.add("Tiền mặt");
			if (chkHDNganHang != null && chkHDNganHang.isSelected()) {
				methods.add("Chuyển khoản");
				methods.add("Ngân hàng");
			}
			if (!methods.isEmpty()) {
				sql.append(" AND h.phuongThucTT IN (")
						.append(String.join(",", java.util.Collections.nCopies(methods.size(), "?")))
						.append(")");
				params.addAll(methods);
			}

			java.time.LocalDate today = java.time.LocalDate.now();
			if (chkHDHomNay != null && chkHDHomNay.isSelected()) {
				tuNgay = today;
				denNgay = today;
			} else if (chkHD7Ngay != null && chkHD7Ngay.isSelected()) {
				tuNgay = today.minusDays(6);
				denNgay = today;
			}
			if (tuNgay != null) {
				sql.append(" AND h.ngayLap >= ?");
				params.add(java.sql.Date.valueOf(tuNgay));
			}
			if (denNgay != null) {
				sql.append(" AND h.ngayLap < DATEADD(day, 1, ?)");
				params.add(java.sql.Date.valueOf(denNgay));
			}
			sql.append(" ORDER BY h.ngayLap DESC");

			try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql.toString())) {
				for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						java.sql.Timestamp timestamp = rs.getTimestamp(2);
						String thoiGian = timestamp == null ? "—" : timestamp.toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
						modelHoaDon.addRow(new Object[] { rs.getString(1), thoiGian, rs.getString(3),
								rs.getDouble(4), rs.getDouble(5), rs.getDouble(6), rs.getString(7),
								rs.getString(8) == null ? "—" : rs.getString(8) });
					}
				}
			}
		} catch (IllegalArgumentException ex) {
			JOptionPane.showMessageDialog(this, ex.getMessage(), "Ngày lọc không hợp lệ", JOptionPane.WARNING_MESSAGE);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Không thể tải danh sách hóa đơn.\n" + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}

	private java.time.LocalDate docNgay(String value) {
		if (value == null || value.trim().isEmpty()) return null;
		try {
			return java.time.LocalDate.parse(value.trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		} catch (java.time.format.DateTimeParseException ex) {
			throw new IllegalArgumentException("Ngày phải theo định dạng dd/MM/yyyy.");
		}
	}

	// ================= TẠO HÓA ĐƠN =================

	private void hienTaoHoaDon() {

		mainContent.removeAll();

		JPanel root = new JPanel(new BorderLayout(15, 15));

		root.setOpaque(false);

		// Tiêu đề + nút quay lại danh sách
		JPanel top = new JPanel(new BorderLayout());
		top.setOpaque(false);

		JLabel title = new JLabel("Tạo hóa đơn");

		title.setFont(new Font("Arial", Font.BOLD, 24));

		JButton btnQuayLai = new JButton("← Danh sách hóa đơn");

		btnQuayLai.addActionListener(e -> hienDanhSachHoaDon());

		top.add(title, BorderLayout.WEST);
		top.add(btnQuayLai, BorderLayout.EAST);

		root.add(top, BorderLayout.NORTH);

		JSplitPane split = new JSplitPane();
		split.setDividerSize(8);
		split.setContinuousLayout(true);
		split.setBackground(NEN_CHINH);

		split.setResizeWeight(.60);

		split.setBorder(null);

		split.setLeftComponent(taoPanelSanPham());

		split.setRightComponent(taoPanelHoaDon());

		root.add(split, BorderLayout.CENTER);

		mainContent.add(root);

		mainContent.revalidate();
		mainContent.repaint();

	}

	// ---------- Bên trái: danh sách sản phẩm để chọn ----------

	
private JPanel taoPanelSanPham() {

    JPanel p = new JPanel(new BorderLayout(10, 10));
    p.setBackground(Color.WHITE);
    p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(225, 228, 232)),
            new EmptyBorder(12, 12, 12, 12)));

    // ===== 1. KHU VỰC SẢN PHẨM =====
    JPanel pnlSanPham = new JPanel(new BorderLayout(8, 8));
    pnlSanPham.setBackground(Color.WHITE);

    txtTimSanPham = new JTextField();
    txtTimSanPham.setToolTipText("Nhập mã hoặc tên sản phẩm để tìm");

    txtTimSanPham.getDocument().addDocumentListener(new DocumentListener() {
        public void insertUpdate(DocumentEvent e) {
            loadSanPham(txtTimSanPham.getText());
        }

        public void removeUpdate(DocumentEvent e) {
            loadSanPham(txtTimSanPham.getText());
        }

        public void changedUpdate(DocumentEvent e) {
            loadSanPham(txtTimSanPham.getText());
        }
    });

    modelSanPham = new DefaultTableModel(
            new String[]{"Mã SP", "Tên sản phẩm", "Giá bán", "Tồn kho"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };

    tblSanPham = new JTable(modelSanPham);
    tblSanPham.setFont(new Font("Arial", Font.PLAIN, 13));
    tblSanPham.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
    tblSanPham.getTableHeader().setBackground(new Color(245, 246, 248));
    tblSanPham.setSelectionBackground(new Color(255, 239, 190));
    tblSanPham.setRowHeight(26);
    tblSanPham.setShowVerticalLines(false);
    tblSanPham.setGridColor(new Color(232, 235, 239));
    tblSanPham.setFillsViewportHeight(true);
    tblSanPham.getTableHeader().setForeground(new Color(48, 61, 75));
    tblSanPham.getTableHeader().setReorderingAllowed(false);

    dinhDangCotTien(tblSanPham, 2);

    tblSanPham.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                themSanPham();
            }
        }
    });

    JScrollPane scrollPaneSanPham = new JScrollPane(tblSanPham);

    loadSanPham("");

    JPanel southSanPham = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 10, 5));
    southSanPham.setBackground(Color.WHITE);

    spnSoLuong = new JSpinner(
            new SpinnerNumberModel(1, 1, 9999, 1));
    spnSoLuong.setPreferredSize(new Dimension(70, 28));

    JButton btnThem = new JButton("Thêm +");
    btnThem.setBackground(VANG);
    btnThem.addActionListener(e -> themSanPham());

    southSanPham.add(new JLabel("Số lượng:"));
    southSanPham.add(spnSoLuong);
    southSanPham.add(btnThem);

    pnlSanPham.add(txtTimSanPham, BorderLayout.NORTH);
    pnlSanPham.add(scrollPaneSanPham, BorderLayout.CENTER);
    pnlSanPham.add(southSanPham, BorderLayout.SOUTH);

    // ===== 2. KHU VỰC TRA CỨU KHÁCH HÀNG =====
    JPanel pnlTraCuuKH = taoPanelTraCuuKhachHang();

    // Chia thành hai vùng theo chiều dọc
    JSplitPane splitPane = new JSplitPane(
            JSplitPane.VERTICAL_SPLIT,
            pnlSanPham,
            pnlTraCuuKH);

    splitPane.setResizeWeight(0.55);
    splitPane.setDividerLocation(350);
    splitPane.setContinuousLayout(true);
    splitPane.setOneTouchExpandable(true);

    p.add(splitPane, BorderLayout.CENTER);

    return p;
}


	// Thêm sản phẩm đang chọn (bảng trái) vào hóa đơn (bảng phải)
	private void themSanPham() {

		int row = tblSanPham.getSelectedRow();

		if (row < 0) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn một sản phẩm!");
			return;
		}

		// Ép giá trị đang gõ trong spinner (nếu chưa Enter)
		try {
			spnSoLuong.commitEdit();
		} catch (Exception ex) {
			spnSoLuong.setValue(1);
		}

		int sl = (Integer) spnSoLuong.getValue();

		String ma = modelSanPham.getValueAt(row, 0).toString();
		String ten = modelSanPham.getValueAt(row, 1).toString();
		double gia = ((Number) modelSanPham.getValueAt(row, 2)).doubleValue();
		int ton = ((Number) modelSanPham.getValueAt(row, 3)).intValue();

		// Nếu đã có trong hóa đơn thì cộng dồn số lượng
		int viTri = timDongDaChon(ma);

		int slMoi = sl;

		if (viTri >= 0) {
			slMoi += ((Number) modelDaChon.getValueAt(viTri, 2)).intValue();
		}

		if (slMoi > ton) {
			JOptionPane.showMessageDialog(this, "Số lượng vượt quá tồn kho (còn " + ton + ")!");
			return;
		}

		dangCapNhatBang = true;

		if (viTri >= 0) {
			modelDaChon.setValueAt(slMoi, viTri, 2);
			modelDaChon.setValueAt(slMoi * gia, viTri, 4);
		} else {
			modelDaChon.addRow(new Object[] { ma, ten, slMoi, gia, slMoi * gia });
		}

		dangCapNhatBang = false;

		capNhatTong();

		spnSoLuong.setValue(1);
	}

	private int timDongDaChon(String maSP) {

		for (int i = 0; i < modelDaChon.getRowCount(); i++) {
			if (modelDaChon.getValueAt(i, 0).toString().equals(maSP))
				return i;
		}

		return -1;
	}

	private int layTonKho(String maSP) {

		for (int i = 0; i < modelSanPham.getRowCount(); i++) {
			if (modelSanPham.getValueAt(i, 0).toString().equals(maSP))
				return ((Number) modelSanPham.getValueAt(i, 3)).intValue();
		}

		return Integer.MAX_VALUE;
	}

	private void loadSanPham(String tuKhoa) {

		modelSanPham.setRowCount(0);

		try (Connection con = getConnection()) {

			PreparedStatement ps = con.prepareStatement("""
					SELECT maSP,tenSP,giaBan,soLuongTon
					FROM SanPham
					WHERE maSP LIKE ? OR tenSP LIKE ?
					""");

			ps.setString(1, "%" + tuKhoa + "%");
			ps.setString(2, "%" + tuKhoa + "%");

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				modelSanPham.addRow(new Object[] { rs.getString(1), rs.getString(2), rs.getDouble(3), rs.getInt(4) });

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private JPanel taoPanelTraCuuKhachHang() {
	    JPanel panel = new JPanel(new BorderLayout(8, 8));
	    panel.setBorder(BorderFactory.createTitledBorder(
	            "TRA CỨU KHÁCH HÀNG"));
	    panel.setBackground(Color.WHITE);

	    // Thanh tìm kiếm
	    JPanel pnlTimKiem = new JPanel(new BorderLayout(8, 0));
	    pnlTimKiem.setBackground(Color.WHITE);

	    JLabel lblTimKiem = new JLabel("Tìm khách hàng:");
	    txtTimKiemKH = new JTextField();
	    txtTimKiemKH.setToolTipText(
	            "Nhập mã khách hàng, họ tên, số điện thoại hoặc thông tin khác");

	    JButton btnLamMoi = new JButton("Làm mới");

	    pnlTimKiem.add(lblTimKiem, BorderLayout.WEST);
	    pnlTimKiem.add(txtTimKiemKH, BorderLayout.CENTER);
	    pnlTimKiem.add(btnLamMoi, BorderLayout.EAST);

	    // Model tự lấy tên cột từ cơ sở dữ liệu
	    modelTraCuuKH = new DefaultTableModel() {
	        @Override
	        public boolean isCellEditable(int row, int column) {
	            return false;
	        }
	    };

	    tblTraCuuKH = new JTable(modelTraCuuKH);
	    tblTraCuuKH.setSelectionMode(
	            ListSelectionModel.SINGLE_SELECTION);
	    tblTraCuuKH.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	    tblTraCuuKH.setRowHeight(25);
	    tblTraCuuKH.getTableHeader().setReorderingAllowed(false);

	    sorterKH = new TableRowSorter<>(modelTraCuuKH);
	    tblTraCuuKH.setRowSorter(sorterKH);

	    JScrollPane scrollKH = new JScrollPane(tblTraCuuKH);

	    // Lọc khách hàng theo nội dung nhập
	    txtTimKiemKH.getDocument().addDocumentListener(
	            new javax.swing.event.DocumentListener() {
	        private void locKhachHang() {
	            String tuKhoa = txtTimKiemKH.getText().trim();

	            if (tuKhoa.isEmpty()) {
	                sorterKH.setRowFilter(null);
	            } else {
	                sorterKH.setRowFilter(
	                        RowFilter.regexFilter(
	                                "(?i)" + java.util.regex.Pattern.quote(tuKhoa)));
	            }
	        }

	        @Override
	        public void insertUpdate(
	                javax.swing.event.DocumentEvent e) {
	            locKhachHang();
	        }

	        @Override
	        public void removeUpdate(
	                javax.swing.event.DocumentEvent e) {
	            locKhachHang();
	        }

	        @Override
	        public void changedUpdate(
	                javax.swing.event.DocumentEvent e) {
	            locKhachHang();
	        }
	    });

	    // Chọn khách hàng bằng cách nhấp đúp vào dòng
	    tblTraCuuKH.addMouseListener(new java.awt.event.MouseAdapter() {
	        @Override
	        public void mouseClicked(java.awt.event.MouseEvent e) {
	            if (e.getClickCount() == 2) {
	                chonKhachHangTuBang();
	            }
	        }
	    });

	    // Nút thao tác
	    JPanel pnlThaoTac = new JPanel(
	            new FlowLayout(FlowLayout.RIGHT));
	    pnlThaoTac.setBackground(Color.WHITE);

	    JButton btnChonKH = new JButton("Chọn khách hàng");
	    btnChonKH.addActionListener(e -> chonKhachHangTuBang());

	    btnLamMoi.addActionListener(e -> {
	        txtTimKiemKH.setText("");
	        taiDanhSachTraCuuKH();
	    });

	    pnlThaoTac.add(btnChonKH);

	    panel.add(pnlTimKiem, BorderLayout.NORTH);
	    panel.add(scrollKH, BorderLayout.CENTER);
	    panel.add(pnlThaoTac, BorderLayout.SOUTH);

	    taiDanhSachTraCuuKH();

	    return panel;
	}
	private void taiDanhSachTraCuuKH() {
	    if (modelTraCuuKH == null) {
	        return;
	    }

	    modelTraCuuKH.setRowCount(0);
	    modelTraCuuKH.setColumnCount(0);

	    String sql = "SELECT * FROM KhachHang";

	    try (Connection con = getConnection();
	         PreparedStatement ps = con.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {

	        ResultSetMetaData meta = rs.getMetaData();
	        int soCot = meta.getColumnCount();

	        // Tạo tiêu đề từ tên cột trong SQL Server
	        for (int i = 1; i <= soCot; i++) {
	            modelTraCuuKH.addColumn(meta.getColumnLabel(i));
	        }

	        // Nạp tất cả dữ liệu khách hàng
	        while (rs.next()) {
	            Object[] row = new Object[soCot];

	            for (int i = 1; i <= soCot; i++) {
	                row[i - 1] = rs.getObject(i);
	            }

	            modelTraCuuKH.addRow(row);
	        }

	        if (sorterKH != null) {
	            sorterKH.setModel(modelTraCuuKH);
	        }

	    } catch (Exception ex) {
	        ex.printStackTrace();

	        JOptionPane.showMessageDialog(
	                this,
	                "Không tải được danh sách khách hàng:\n"
	                        + ex.getMessage(),
	                "Lỗi",
	                JOptionPane.ERROR_MESSAGE
	        );
	    }
	}
	
	private void chonKhachHangTuBang() {
    int selectedRow = tblTraCuuKH.getSelectedRow();

    // Kiểm tra đã chọn khách hàng trên bảng chưa
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(
                this,
                "Vui lòng chọn một khách hàng trong danh sách.",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    // Chuyển chỉ số dòng khi bảng đang được sắp xếp hoặc lọc
    selectedRow = tblTraCuuKH.convertRowIndexToModel(selectedRow);

    // Lấy vị trí các cột theo tên
    int colMaKH = -1;
    int colHoTen = -1;

    for (int i = 0; i < modelTraCuuKH.getColumnCount(); i++) {
        String tenCot = modelTraCuuKH.getColumnName(i);

        if (tenCot.equalsIgnoreCase("maKH")) {
            colMaKH = i;
        } else if (tenCot.equalsIgnoreCase("hoTen")) {
            colHoTen = i;
        }
    }

    // Kiểm tra bảng có đủ cột cần thiết không
    if (colMaKH == -1 || colHoTen == -1) {
        JOptionPane.showMessageDialog(
                this,
                "Không tìm thấy cột mã khách hàng hoặc họ tên.",
                "Lỗi dữ liệu",
                JOptionPane.ERROR_MESSAGE
        );
        return;
    }

    // Lấy thông tin khách hàng được chọn
    Object maKH = modelTraCuuKH.getValueAt(selectedRow, colMaKH);
    Object tenKH = modelTraCuuKH.getValueAt(selectedRow, colHoTen);

    if (maKH == null || tenKH == null) {
        JOptionPane.showMessageDialog(
                this,
                "Thông tin khách hàng không hợp lệ.",
                "Lỗi dữ liệu",
                JOptionPane.ERROR_MESSAGE
        );
        return;
    }

    maKHDaChon = maKH.toString();
    String hoTen = tenKH.toString();

    // Tìm khách hàng trong ComboBox của hóa đơn
    boolean daTimThay = false;

    if (cboKhachHang != null) {
        for (int i = 0; i < cboKhachHang.getItemCount(); i++) {
            KhachHangItem kh = cboKhachHang.getItemAt(i);

            if (kh != null && maKHDaChon.equals(kh.maKH)) {
                cboKhachHang.setSelectedIndex(i);
                daTimThay = true;
                break;
            }
        }
    }

    // Thông báo nếu không tìm thấy khách hàng trong ComboBox
    if (!daTimThay) {
        JOptionPane.showMessageDialog(
                this,
                "Không tìm thấy khách hàng trong danh sách hóa đơn.",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    // Thông báo chọn khách hàng thành công
    JOptionPane.showMessageDialog(
            this,
            "Đã chọn khách hàng: " + hoTen
                    + "\nMã khách hàng: " + maKHDaChon,
            "Thông báo",
            JOptionPane.INFORMATION_MESSAGE
    );
}

	// ---------- Bên phải: thông tin hóa đơn + sản phẩm đã chọn ----------

	private JPanel taoPanelHoaDon() {

		JPanel p = new JPanel(new BorderLayout(10, 12));
		p.setBackground(Color.WHITE);
		p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(225, 228, 232)),
				new EmptyBorder(18, 18, 18, 18)));

		// ===== 1. THÔNG TIN HÓA ĐƠN =====
		JPanel info = new JPanel();
		info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
		info.setBackground(Color.WHITE);

		JLabel title = new JLabel("Thông tin hóa đơn");
		title.setFont(new Font("Arial", Font.BOLD, 19));
		title.setForeground(CHU);

		lblMaHD = new JLabel("Mã hóa đơn: " + taoMaHoaDon());
		lblMaHD.setFont(new Font("Arial", Font.BOLD, 13));
		lblMaHD.setForeground(new Color(37, 99, 155));

		JLabel ngay = new JLabel("Ngày lập: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

		JLabel nv = new JLabel("Nhân viên: " + MA_NV_DANG_DANG_NHAP);

		cboKhachHang = new JComboBox<>();
		cboKhachHang.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
		cboKhachHang.setBackground(Color.WHITE);
		cboKhachHang.addItem(new KhachHangItem(null, "Khách lẻ"));
		loadKhachHang();

		cboUuDai = new JComboBox<>();
		cboUuDai.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
		cboUuDai.setBackground(Color.WHITE);
		cboUuDai.addItem(new UuDaiItem(null, "Không áp dụng ưu đãi", 0));
		loadUuDai();

		info.add(title);
		info.add(Box.createVerticalStrut(10));
		info.add(lblMaHD);
		info.add(Box.createVerticalStrut(5));
		info.add(ngay);
		info.add(Box.createVerticalStrut(5));
		info.add(nv);
		info.add(Box.createVerticalStrut(12));
		info.add(new JLabel("Khách hàng"));
		info.add(Box.createVerticalStrut(4));
		info.add(cboKhachHang);
		info.add(Box.createVerticalStrut(9));
		info.add(new JLabel("Ưu đãi"));
		info.add(Box.createVerticalStrut(4));
		info.add(cboUuDai);

		p.add(info, BorderLayout.NORTH);

		// ===== 2. BẢNG SẢN PHẨM ĐÃ CHỌN =====
		modelDaChon = new DefaultTableModel(new String[] { "Mã SP", "Tên sản phẩm", "SL", "Đơn giá", "Thành tiền" },
				0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return c == 2;
			}
		};

		tblDaChon = new JTable(modelDaChon);
		tblDaChon.setRowHeight(28);
		tblDaChon.setFont(new Font("Arial", Font.PLAIN, 12));
		tblDaChon.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
		tblDaChon.getTableHeader().setBackground(new Color(245, 246, 248));
		tblDaChon.setSelectionBackground(new Color(255, 239, 190));
		tblDaChon.setShowVerticalLines(false);
		tblDaChon.setGridColor(new Color(232, 235, 239));
		tblDaChon.setFillsViewportHeight(true);
		tblDaChon.getTableHeader().setReorderingAllowed(false);

		dinhDangCotTien(tblDaChon, 3, 4);

		modelDaChon.addTableModelListener(e -> {
			if (dangCapNhatBang)
				return;

			if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == 2 && e.getFirstRow() >= 0) {
				xuLySuaSoLuong(e.getFirstRow());
			}
		});

		JPanel center = new JPanel(new BorderLayout(5, 8));
		center.setBackground(Color.WHITE);

		JLabel lblSanPham = new JLabel("Sản phẩm trong hóa đơn");
		lblSanPham.setFont(new Font("Arial", Font.BOLD, 14));

		center.add(lblSanPham, BorderLayout.NORTH);
		center.add(new JScrollPane(tblDaChon), BorderLayout.CENTER);

		JPanel tools = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 4));
		tools.setBackground(Color.WHITE);

		JButton btnGiam = new JButton("−");
		JButton btnTang = new JButton("+");
		JButton btnXoa = new JButton("Xóa sản phẩm");
		JButton btnXoaHet = new JButton("Xóa tất cả");

		btnGiam.addActionListener(e -> doiSoLuong(-1));
		btnTang.addActionListener(e -> doiSoLuong(1));
		btnXoa.addActionListener(e -> xoaSanPhamDaChon());
		btnXoaHet.addActionListener(e -> xoaTatCa());

		tools.add(btnGiam);
		tools.add(btnTang);
		tools.add(btnXoa);
		tools.add(btnXoaHet);

		center.add(tools, BorderLayout.SOUTH);
		p.add(center, BorderLayout.CENTER);

		// ===== 3. TỔNG TIỀN VÀ THANH TOÁN =====
		JPanel bottom = new JPanel();
		bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
		bottom.setBackground(new Color(248, 250, 252));
		bottom.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(225, 228, 232)),
				new EmptyBorder(14, 12, 8, 12)));

		lblTamTinh = new JLabel("Tạm tính: 0 VNĐ");
		lblGiamGia = new JLabel("Ưu đãi: 0 VNĐ");
		lblTongTien = new JLabel("Tổng thanh toán: 0 VNĐ");

		lblTamTinh.setFont(new Font("Arial", Font.PLAIN, 13));
		lblGiamGia.setFont(new Font("Arial", Font.PLAIN, 13));
		lblGiamGia.setForeground(new Color(220, 80, 50));

		lblTongTien.setFont(new Font("Arial", Font.BOLD, 20));
		lblTongTien.setForeground(new Color(21, 101, 66));

		JButton btnThanhToan = new JButton("THANH TOÁN");
		btnThanhToan.setFont(new Font("Arial", Font.BOLD, 14));
		btnThanhToan.setForeground(Color.WHITE);
		btnThanhToan.setBackground(new Color(29, 128, 78));
		btnThanhToan.setFocusPainted(false);
		btnThanhToan.setPreferredSize(new Dimension(180, 42));
		btnThanhToan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
		btnThanhToan.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnThanhToan.setOpaque(true);
		btnThanhToan.setContentAreaFilled(true);
		btnThanhToan.setBorderPainted(false);
		
		btnThanhToan.addActionListener(e -> {
		    if (tblDaChon.isEditing()) {
		        if (!tblDaChon.getCellEditor().stopCellEditing()) {
		            return;
		        }
		    }

		    if (modelDaChon.getRowCount() == 0) {
		        JOptionPane.showMessageDialog(
		                this,
		                "Vui lòng chọn sản phẩm trước khi thanh toán!",
		                "Thông báo",
		                JOptionPane.WARNING_MESSAGE
		        );
		        return;
		    }

		    capNhatTong();

		    long tongTien = Math.round(tongTienGoc - soTienGiam);

		    if (tongTien <= 0) {
		        JOptionPane.showMessageDialog(
		                this,
		                "Tổng tiền thanh toán không hợp lệ!",
		                "Thông báo",
		                JOptionPane.WARNING_MESSAGE
		        );
		        return;
		    }

		    ThanhToanUI trangThanhToan = new ThanhToanUI(
		            this,
		            tongTien,
		            () -> luuHoaDon()
		    );

		    trangThanhToan.setVisible(true);
		});

		   
		bottom.add(lblTamTinh);
		bottom.add(Box.createVerticalStrut(5));
		bottom.add(lblGiamGia);
		bottom.add(Box.createVerticalStrut(7));
		bottom.add(new javax.swing.JSeparator());
		bottom.add(Box.createVerticalStrut(8));
		bottom.add(lblTongTien);
		bottom.add(Box.createVerticalStrut(12));
		bottom.add(btnThanhToan);

		p.add(bottom, BorderLayout.SOUTH);

		// Cập nhật tổng tiền khi đổi khách hàng hoặc ưu đãi
		cboUuDai.addActionListener(e -> capNhatTong());
		cboKhachHang.addActionListener(e -> capNhatTong());

		return p;
	}

	private void loadKhachHang() {
		try (Connection con = getConnection();
				PreparedStatement ps = con.prepareStatement("SELECT maKH, hoTen FROM KhachHang ORDER BY hoTen");
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				cboKhachHang.addItem(new KhachHangItem(rs.getString("maKH"), rs.getString("hoTen")));
			}

		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Không tải được danh sách khách hàng:\n" + e.getMessage());
		}
	}

	private void loadUuDai() {
		cboUuDai.removeAllItems();

		// Luôn có lựa chọn không áp dụng ưu đãi
		cboUuDai.addItem(new UuDaiItem(null, "Không áp dụng ưu đãi", 0));

		String sql = """
				SELECT maUD, tenUD, chietKhau
				FROM UuDai
				WHERE (ngayBD IS NULL OR ngayBD <= GETDATE())
				  AND (ngayKT IS NULL OR ngayKT >= GETDATE())
				  AND trangThai = N'Đang áp dụng'
				ORDER BY tenUD
				""";

		try (Connection con = getConnection();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				String maUD = rs.getString("maUD");
				String tenUD = rs.getString("tenUD");
				double chietKhau = rs.getDouble("chietKhau");

				cboUuDai.addItem(new UuDaiItem(maUD, tenUD, chietKhau));
			}

		} catch (Exception ex) {
			ex.printStackTrace();

			JOptionPane.showMessageDialog(this, "Không thể tải danh sách ưu đãi:\n" + ex.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void capNhatTong() {

		tongTienGoc = 0;

		if (modelDaChon != null) {
			for (int i = 0; i < modelDaChon.getRowCount(); i++) {
				tongTienGoc += ((Number) modelDaChon.getValueAt(i, 4)).doubleValue();
			}
		}

		double phanTram = 0;

		if (cboUuDai != null && cboUuDai.getSelectedItem() instanceof UuDaiItem) {
			UuDaiItem uuDai = (UuDaiItem) cboUuDai.getSelectedItem();
			phanTram = uuDai.phanTramGiam;
		}

		soTienGiam = tongTienGoc * phanTram / 100.0;
		double tongThanhToan = tongTienGoc - soTienGiam;

		if (lblTamTinh != null) {
			lblTamTinh.setText("Tạm tính: " + String.format("%,.0f VNĐ", tongTienGoc));
		}

		if (lblGiamGia != null) {
			lblGiamGia.setText(
					"Ưu đãi (" + String.format("%.0f", phanTram) + "%): -" + String.format("%,.0f VNĐ", soTienGiam));
		}

		if (lblTongTien != null) {
			lblTongTien.setText("Tổng thanh toán: " + String.format("%,.0f VNĐ", tongThanhToan));
		}
	}


	// Tăng / giảm số lượng dòng đang chọn bằng nút + / −
	private void doiSoLuong(int delta) {

		int row = tblDaChon.getSelectedRow();

		if (row < 0) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm trong hóa đơn!");
			return;
		}

		if (tblDaChon.isEditing())
			tblDaChon.getCellEditor().stopCellEditing();

		String ma = modelDaChon.getValueAt(row, 0).toString();

		int sl = ((Number) modelDaChon.getValueAt(row, 2)).intValue() + delta;

		if (sl < 1)
			return; // muốn bỏ hẳn thì dùng nút Xóa

		int ton = layTonKho(ma);

		if (sl > ton) {
			JOptionPane.showMessageDialog(this, "Số lượng vượt quá tồn kho (còn " + ton + ")!");
			return;
		}

		datSoLuong(row, sl);

		tblDaChon.setRowSelectionInterval(row, row);
	}

	// Người dùng gõ tay số lượng vào bảng -> kiểm tra hợp lệ
	private void xuLySuaSoLuong(int row) {

		Object v = modelDaChon.getValueAt(row, 2);

		String ma = modelDaChon.getValueAt(row, 0).toString();

		int ton = layTonKho(ma);

		int sl;

		try {
			sl = Integer.parseInt(v.toString().trim());
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Số lượng phải là số nguyên!");
			sl = 1;
		}

		if (sl < 1) {
			JOptionPane.showMessageDialog(this, "Số lượng phải lớn hơn 0!");
			sl = 1;
		}

		if (sl > ton) {
			JOptionPane.showMessageDialog(this, "Số lượng vượt quá tồn kho (còn " + ton + ")!");
			sl = ton;
		}

		SwingUtilities.invokeLater(() -> {
			if (row < modelDaChon.getRowCount())
				datSoLuong(row, sl_(v, ton));
		});
	}

	// Tính lại số lượng hợp lệ cho invokeLater (tránh sửa model trong lúc đang nhận
	// sự kiện)
	private int sl_(Object v, int ton) {

		int sl;

		try {
			sl = Integer.parseInt(v.toString().trim());
		} catch (Exception ex) {
			sl = 1;
		}

		if (sl < 1)
			sl = 1;

		if (sl > ton)
			sl = ton;

		return sl;
	}

	// Gán số lượng cho dòng và tính lại thành tiền
	private void datSoLuong(int row, int sl) {

		double gia = ((Number) modelDaChon.getValueAt(row, 3)).doubleValue();

		dangCapNhatBang = true;

		modelDaChon.setValueAt(sl, row, 2);
		modelDaChon.setValueAt(sl * gia, row, 4);

		dangCapNhatBang = false;

		capNhatTong();
	}

	private void xoaSanPhamDaChon() {

		if (tblDaChon.isEditing())
			tblDaChon.getCellEditor().cancelCellEditing();

		int[] rows = tblDaChon.getSelectedRows();

		if (rows.length == 0) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần xóa!");
			return;
		}

		// Xóa từ dưới lên để không lệch chỉ số
		for (int i = rows.length - 1; i >= 0; i--) {
			modelDaChon.removeRow(rows[i]);
		}

		capNhatTong();
	}

	private void xoaTatCa() {

		if (modelDaChon.getRowCount() == 0)
			return;

		if (tblDaChon.isEditing())
			tblDaChon.getCellEditor().cancelCellEditing();

		int ok = JOptionPane.showConfirmDialog(this, "Xóa toàn bộ sản phẩm đã chọn?", "Xác nhận",
				JOptionPane.YES_NO_OPTION);

		if (ok == JOptionPane.YES_OPTION) {
			modelDaChon.setRowCount(0);
			capNhatTong();
		}
	}

	// Hiển thị cột tiền dạng 1,290,000
	private void dinhDangCotTien(JTable table, int... cols) {

		DefaultTableCellRenderer r = new DefaultTableCellRenderer() {
			@Override
			protected void setValue(Object value) {
				if (value instanceof Number)
					setText(String.format("%,.0f", ((Number) value).doubleValue()));
				else
					super.setValue(value);
			}
		};

		r.setHorizontalAlignment(SwingConstants.RIGHT);

		for (int c : cols) {
			table.getColumnModel().getColumn(c).setCellRenderer(r);
		}
	}

	// ================= MÃ HÓA ĐƠN TỰ SINH =================
	// Quy tắc: HD + ddMMyyyy + số thứ tự 4 chữ số trong ngày
	// Ví dụ: HD231020260002

	private String taoMaHoaDon() {

		try (Connection con = getConnection()) {

			return taoMaHoaDon(con);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "HD" + LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyyyy")) + "0001";

	}

	private String taoMaHoaDon(Connection con) throws Exception {

		String ngay = LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyyyy"));

		String tienTo = "HD" + ngay;

		int stt = 1;

		// Lấy số thứ tự lớn nhất của hôm nay rồi +1
		// (an toàn hơn COUNT nếu có hóa đơn bị xóa)
		PreparedStatement ps = con.prepareStatement("""
				SELECT ISNULL(MAX(CAST(RIGHT(maHD,4) AS INT)),0)
				FROM HoaDon
				WHERE maHD LIKE ?
				""");

		ps.setString(1, tienTo + "%");

		ResultSet rs = ps.executeQuery();

		if (rs.next())
			stt = rs.getInt(1) + 1;

		return tienTo + String.format("%04d", stt);

	}

	// ================= LƯU HÓA ĐƠN =================

	// ================= LƯU HÓA ĐƠN =================

private boolean luuHoaDon() {

    if (tblDaChon.isEditing()) {
        if (!tblDaChon.getCellEditor().stopCellEditing()) {
            return false;
        }
    }

    if (modelDaChon == null || modelDaChon.getRowCount() == 0) {
        JOptionPane.showMessageDialog(
                this,
                "Chưa có sản phẩm trong hóa đơn!",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE
        );
        return false;
    }

    String loiChiTiet = kiemTraChiTietTruocKhiLuu();
    if (loiChiTiet != null) {
        JOptionPane.showMessageDialog(
                this, loiChiTiet, "Dữ liệu hóa đơn chưa hợp lệ",
                JOptionPane.WARNING_MESSAGE
        );
        return false;
    }

    Connection con = null;

    try {
        // Cập nhật lại tiền trước khi lưu
        capNhatTong();

        final double tong = Math.round(tongTienGoc - soTienGiam);

        if (tong <= 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Tổng tiền thanh toán không hợp lệ!",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE
            );
            return false;
        }

        con = getConnection();
        con.setAutoCommit(false);

        // Sinh mã hóa đơn tại thời điểm lưu
        String maHD = taoMaHoaDon(con);

        KhachHangItem khachHang =
                (KhachHangItem) cboKhachHang.getSelectedItem();

        String maKH = khachHang == null ? null : khachHang.maKH;

        // 1. Lưu thông tin hóa đơn
        String sqlHoaDon = """
                INSERT INTO HoaDon
                    (maHD, ngayLap, maKH, maNV, tongTien, trangThai)
                VALUES (?, GETDATE(), ?, ?, ?, N'Đã thanh toán')
                """;

        try (PreparedStatement psHD = con.prepareStatement(sqlHoaDon)) {

            psHD.setString(1, maHD);

            if (maKH == null) {
                psHD.setNull(2, java.sql.Types.VARCHAR);
            } else {
                psHD.setString(2, maKH);
            }

            psHD.setString(3, MA_NV_DANG_DANG_NHAP);
            psHD.setDouble(4, tong);

            psHD.executeUpdate();
        }

        // 2. Lưu chi tiết hóa đơn
        String sqlChiTiet = """
                INSERT INTO ChiTietHoaDon
                    (maHD, maSP, soLuong, donGia, thanhTien)
                VALUES (?, ?, ?, ?, ?)
                """;

        // 3. Trừ tồn kho nếu số lượng còn đủ
        String sqlTonKho = """
                UPDATE SanPham
                SET soLuongTon = soLuongTon - ?
                WHERE maSP = ? AND soLuongTon >= ?
                """;

        try (
                PreparedStatement psCT = con.prepareStatement(sqlChiTiet);
                PreparedStatement psTon = con.prepareStatement(sqlTonKho)
        ) {
            for (int i = 0; i < modelDaChon.getRowCount(); i++) {

                String maSP =
                        modelDaChon.getValueAt(i, 0).toString();

                int sl = ((Number)
                        modelDaChon.getValueAt(i, 2)).intValue();

                double gia = ((Number)
                        modelDaChon.getValueAt(i, 3)).doubleValue();

                double thanhTien = ((Number)
                        modelDaChon.getValueAt(i, 4)).doubleValue();

                if (sl <= 0) {
                    throw new Exception(
                            "Số lượng sản phẩm không hợp lệ: " + maSP
                    );
                }

                // Thêm chi tiết hóa đơn
                psCT.setString(1, maHD);
                psCT.setString(2, maSP);
                psCT.setInt(3, sl);
                psCT.setDouble(4, gia);
                psCT.setDouble(5, thanhTien);
                psCT.executeUpdate();

                // Kiểm tra và trừ tồn kho
                psTon.setInt(1, sl);
                psTon.setString(2, maSP);
                psTon.setInt(3, sl);

                int soDongCapNhat = psTon.executeUpdate();

                if (soDongCapNhat == 0) {
                    throw new Exception(
                            "Sản phẩm " + maSP
                            + " không đủ tồn kho hoặc không còn tồn tại!"
                    );
                }
            }
        }

        // Chỉ xác nhận khi toàn bộ giao dịch thành công
        con.commit();

        JOptionPane.showMessageDialog(
                this,
                "Thanh toán thành công!\nMã hóa đơn: " + maHD,
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE
        );

        // Làm mới danh sách hóa đơn
        hienDanhSachHoaDon();

        return true;

    } catch (Exception ex) {

        // Hoàn tác cả hóa đơn, chi tiết và cập nhật tồn kho
        if (con != null) {
            try {
                con.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        }

        ex.printStackTrace();

        JOptionPane.showMessageDialog(
                this,
                "Lưu hóa đơn thất bại:\n" + ex.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE
        );

        return false;

    } finally {

        if (con != null) {
            try {
                con.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}

private String kiemTraChiTietTruocKhiLuu() {
    java.util.Set<String> maSanPhamDaChon = new java.util.HashSet<>();
    for (int i = 0; i < modelDaChon.getRowCount(); i++) {
        Object maSPValue = modelDaChon.getValueAt(i, 0);
        if (maSPValue == null || maSPValue.toString().isBlank()) {
            return "Mã sản phẩm không được để trống (dòng " + (i + 1) + ").";
        }
        String maSP = maSPValue.toString().trim();
        if (!maSanPhamDaChon.add(maSP)) {
            return "Sản phẩm " + maSP + " đang bị lặp trong hóa đơn.";
        }

        try {
            int soLuong = ((Number) modelDaChon.getValueAt(i, 2)).intValue();
            double donGia = ((Number) modelDaChon.getValueAt(i, 3)).doubleValue();
            double thanhTien = ((Number) modelDaChon.getValueAt(i, 4)).doubleValue();
            if (soLuong <= 0) {
                return "Số lượng sản phẩm " + maSP + " phải lớn hơn 0.";
            }
            if (!Double.isFinite(donGia) || donGia < 0
                    || !Double.isFinite(thanhTien) || thanhTien < 0) {
                return "Đơn giá và thành tiền của " + maSP + " phải là số không âm hợp lệ.";
            }
            if (Math.abs(thanhTien - soLuong * donGia) > 0.01) {
                return "Thành tiền của " + maSP + " không khớp với số lượng × đơn giá.";
            }
        } catch (RuntimeException ex) {
            return "Số lượng, đơn giá hoặc thành tiền của " + maSP + " không hợp lệ.";
        }
    }
    return null;
}
	// ================= CHẠY CHƯƠNG TRÌNH =================

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				e.printStackTrace();
			}

			TaoHoaDonUI frame = new TaoHoaDonUI();
			frame.setVisible(true);
		});
	}
}
