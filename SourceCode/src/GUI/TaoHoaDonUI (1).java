
package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
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
import javax.swing.JComponent;
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
	private JLabel lblNhanGiamGia;

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

		JPanel panel = new JPanel(new BorderLayout(0, 14));
		panel.setOpaque(false);

		// ----- Tiêu đề + bộ lọc -----
		JPanel top = new JPanel(new BorderLayout(20, 0));
		top.setOpaque(false);

		JPanel tieuDe = new JPanel();
		tieuDe.setOpaque(false);
		tieuDe.setLayout(new BoxLayout(tieuDe, BoxLayout.Y_AXIS));

		JLabel title = UiKit.tieuDeTrang("Quản lý hóa đơn");
		JLabel phuDe = UiKit.phuDe("Tra cứu, xem chi tiết và lập hóa đơn bán hàng");
		title.setAlignmentX(Component.LEFT_ALIGNMENT);
		phuDe.setAlignmentX(Component.LEFT_ALIGNMENT);

		tieuDe.add(title);
		tieuDe.add(Box.createVerticalStrut(2));
		tieuDe.add(phuDe);

		top.add(tieuDe, BorderLayout.WEST);

		JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
		right.setOpaque(false);

		txtLocHoaDon = UiKit.truongGoiY("Nhập mã hóa đơn / tên khách hàng");
		txtLocHoaDon.setPreferredSize(new Dimension(300, 38));

		JButton btnLoc = UiKit.nutPhu("Lọc");
		JButton btnXoaLoc = UiKit.nutPhu("Xóa lọc");
		JButton btnTao = UiKit.nutChinh("+ Tạo hóa đơn");

		right.add(txtLocHoaDon);
		right.add(btnLoc);
		right.add(btnXoaLoc);
		right.add(btnTao);

		top.add(right, BorderLayout.EAST);

		panel.add(top, BorderLayout.NORTH);

		// ----- Bảng hóa đơn -----
		modelHoaDon = new DefaultTableModel(
				new String[] { "Mã HD", "Ngày", "Khách", "Nhân viên", "Tổng tiền", "Trạng thái" }, 0) {

			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};

		tblHoaDon = new JTable(modelHoaDon);
		UiKit.styleBang(tblHoaDon);
		tblHoaDon.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		dinhDangCotTien(tblHoaDon, 4);

		// Trạng thái: xanh nếu đã thanh toán, cam nếu trạng thái khác
		tblHoaDon.getColumnModel().getColumn(5).setCellRenderer(new UiKit.RenderBang() {
			@Override
			public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r,
					int c) {
				super.getTableCellRendererComponent(t, v, s, f, r, c);
				String tt = v == null ? "" : v.toString();
				setForeground(tt.contains("thanh toán") ? UiKit.XANH : UiKit.CAM);
				setFont(UiKit.F_DAM);
				return this;
			}
		});

		int[] doRong = { 150, 140, 190, 190, 120, 120 };
		for (int i = 0; i < doRong.length; i++) {
			tblHoaDon.getColumnModel().getColumn(i).setPreferredWidth(doRong[i]);
		}

		// ----- Xem chi tiết -----
		Runnable xemChiTiet = () -> {
			int selectedRow = tblHoaDon.getSelectedRow();

			// Kiểm tra đã chọn hóa đơn chưa
			if (selectedRow == -1) {
				JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn cần xem chi tiết!", "Thông báo",
						JOptionPane.WARNING_MESSAGE);
				return;
			}

			// Lấy dòng tương ứng trong model
			int modelRow = tblHoaDon.convertRowIndexToModel(selectedRow);

			// Lấy mã hóa đơn ở cột đầu tiên
			String maHD = tblHoaDon.getModel().getValueAt(modelRow, 0).toString();

			// Mở trang chi tiết hóa đơn
			ChiTietHoaDonUI dialog = new ChiTietHoaDonUI(this, maHD);
			dialog.setVisible(true);
		};

		// Nhấp đúp vào dòng cũng mở chi tiết
		tblHoaDon.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					xemChiTiet.run();
				}
			}
		});

		JButton btnXemChiTiet = UiKit.nutXanhDuong("Xem chi tiết");
		btnXemChiTiet.addActionListener(e -> xemChiTiet.run());

		JPanel pnlNut = new JPanel(new BorderLayout());
		pnlNut.setOpaque(false);

		JPanel nutPhai = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		nutPhai.setOpaque(false);
		nutPhai.add(btnXemChiTiet);

		pnlNut.add(UiKit.nhanNho("Mẹo: nhấp đúp vào hóa đơn để xem chi tiết"), BorderLayout.WEST);
		pnlNut.add(nutPhai, BorderLayout.EAST);

		UiKit.TheBo card = new UiKit.TheBo(new BorderLayout(0, 12), 14);
		card.add(UiKit.cuon(tblHoaDon), BorderLayout.CENTER);
		card.add(pnlNut, BorderLayout.SOUTH);

		panel.add(card, BorderLayout.CENTER);

		btnLoc.addActionListener(e -> loadHoaDon(txtLocHoaDon.getText()));

		// Nhấn Enter trong ô lọc cũng lọc luôn
		txtLocHoaDon.addActionListener(e -> loadHoaDon(txtLocHoaDon.getText()));

		btnXoaLoc.addActionListener(e -> {
			txtLocHoaDon.setText("");
			loadHoaDon("");
		});

		btnTao.addActionListener(e -> hienTaoHoaDon());

		loadHoaDon("");

		mainContent.add(panel, BorderLayout.CENTER);

		mainContent.revalidate();
		mainContent.repaint();

	}

	private void loadHoaDon(String tuKhoa) {

		modelHoaDon.setRowCount(0);

		try (Connection con = getConnection()) {

			String sql = """
					SELECT h.maHD,h.ngayLap,k.hoTen,n.hoTen,
					       h.tongTien,h.trangThai
					FROM HoaDon h
					LEFT JOIN KhachHang k ON h.maKH=k.maKH
					LEFT JOIN NhanVien n ON h.maNV=n.maNV
					WHERE h.maHD LIKE ? OR k.hoTen LIKE ?
					ORDER BY h.ngayLap DESC
					""";

			PreparedStatement ps = con.prepareStatement(sql);

			ps.setString(1, "%" + tuKhoa + "%");
			ps.setString(2, "%" + tuKhoa + "%");

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				String khach = rs.getString(3);

				modelHoaDon.addRow(new Object[] { rs.getString(1), rs.getTimestamp(2),
						khach == null ? "Khách lẻ" : khach, rs.getString(4), rs.getDouble(5), rs.getString(6) });

			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	// ================= TẠO HÓA ĐƠN =================

	private void hienTaoHoaDon() {

		mainContent.removeAll();

		JPanel root = new JPanel(new BorderLayout(0, 14));
		root.setOpaque(false);

		// ----- Tiêu đề + nút quay lại danh sách -----
		JPanel top = new JPanel(new BorderLayout(20, 0));
		top.setOpaque(false);

		JPanel tieuDe = new JPanel();
		tieuDe.setOpaque(false);
		tieuDe.setLayout(new BoxLayout(tieuDe, BoxLayout.Y_AXIS));

		JLabel title = UiKit.tieuDeTrang("Tạo hóa đơn");
		JLabel phuDe = UiKit.phuDe("Chọn sản phẩm, khách hàng và ưu đãi rồi tiến hành thanh toán");
		title.setAlignmentX(Component.LEFT_ALIGNMENT);
		phuDe.setAlignmentX(Component.LEFT_ALIGNMENT);

		tieuDe.add(title);
		tieuDe.add(Box.createVerticalStrut(2));
		tieuDe.add(phuDe);

		JButton btnQuayLai = UiKit.nutPhu("← Danh sách hóa đơn");
		btnQuayLai.addActionListener(e -> hienDanhSachHoaDon());

		JPanel nutQuayLai = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		nutQuayLai.setOpaque(false);
		nutQuayLai.add(btnQuayLai);

		top.add(tieuDe, BorderLayout.WEST);
		top.add(nutQuayLai, BorderLayout.EAST);

		root.add(top, BorderLayout.NORTH);

		// ----- Hai cột: bên trái chọn hàng, bên phải hóa đơn -----
		JPanel trai = taoPanelSanPham();
		JPanel phai = taoPanelHoaDon();

		trai.setMinimumSize(new Dimension(420, 0));
		phai.setMinimumSize(new Dimension(400, 0));
		phai.setPreferredSize(new Dimension(470, 0));

		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		UiKit.lamDongChiaTrong(split, 14);
		split.setLeftComponent(trai);
		split.setRightComponent(phai);
		split.setResizeWeight(0.58);
		UiKit.datTyLeLanDau(split, 0.58);

		root.add(split, BorderLayout.CENTER);

		mainContent.add(root);

		mainContent.revalidate();
		mainContent.repaint();

	}

	// ---------- Bên trái: danh sách sản phẩm để chọn ----------

	
	private JPanel taoPanelSanPham() {

		JPanel p = new JPanel(new BorderLayout());
		p.setOpaque(false);

		// ===== 1. KHU VỰC SẢN PHẨM =====
		UiKit.TheBo pnlSanPham = new UiKit.TheBo(new BorderLayout(0, 10), 14);
		pnlSanPham.setMinimumSize(new Dimension(300, 230));

		JPanel dauSanPham = new JPanel(new BorderLayout(0, 8));
		dauSanPham.setOpaque(false);

		txtTimSanPham = UiKit.truongGoiY("Tìm sản phẩm theo mã hoặc tên...");
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

		dauSanPham.add(UiKit.tieuMuc("Chọn sản phẩm"), BorderLayout.NORTH);
		dauSanPham.add(txtTimSanPham, BorderLayout.CENTER);

		modelSanPham = new DefaultTableModel(new String[] { "Mã SP", "Tên sản phẩm", "Giá bán", "Tồn kho" }, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};

		tblSanPham = new JTable(modelSanPham);
		UiKit.styleBang(tblSanPham);
		tblSanPham.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Giá bán: chữ đậm màu cam
		tblSanPham.getColumnModel().getColumn(2).setCellRenderer(renderTien(true, UiKit.CAM));

		// Tồn kho: xanh (còn nhiều), cam (sắp hết), đỏ (hết hàng)
		tblSanPham.getColumnModel().getColumn(3).setCellRenderer(new UiKit.RenderBang() {
			@Override
			public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r,
					int c) {
				super.getTableCellRendererComponent(t, v, s, f, r, c);
				setHorizontalAlignment(SwingConstants.CENTER);
				int ton = v instanceof Number ? ((Number) v).intValue() : 0;
				setForeground(ton <= 0 ? UiKit.DO : (ton <= 5 ? UiKit.CAM : UiKit.XANH));
				setFont(UiKit.F_DAM);
				return this;
			}
		});

		int[] doRong = { 90, 320, 110, 80 };
		for (int i = 0; i < doRong.length; i++) {
			tblSanPham.getColumnModel().getColumn(i).setPreferredWidth(doRong[i]);
		}

		tblSanPham.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					themSanPham();
				}
			}
		});

		loadSanPham("");

		// Số lượng + nút thêm
		JPanel southSanPham = new JPanel(new BorderLayout());
		southSanPham.setOpaque(false);

		JPanel nutPhai = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
		nutPhai.setOpaque(false);

		spnSoLuong = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
		spnSoLuong.setPreferredSize(new Dimension(80, 36));
		((JSpinner.DefaultEditor) spnSoLuong.getEditor()).getTextField().setFont(UiKit.F_DAM);

		JLabel lblSL = new JLabel("Số lượng:");
		lblSL.setFont(UiKit.F_DAM);

		JButton btnThem = UiKit.nutChinh("+ Thêm vào hóa đơn");
		btnThem.addActionListener(e -> themSanPham());

		nutPhai.add(lblSL);
		nutPhai.add(spnSoLuong);
		nutPhai.add(btnThem);

		southSanPham.add(UiKit.nhanNho("Mẹo: nhấp đúp vào sản phẩm để thêm nhanh"), BorderLayout.WEST);
		southSanPham.add(nutPhai, BorderLayout.EAST);

		pnlSanPham.add(dauSanPham, BorderLayout.NORTH);
		pnlSanPham.add(UiKit.cuon(tblSanPham), BorderLayout.CENTER);
		pnlSanPham.add(southSanPham, BorderLayout.SOUTH);

		// ===== 2. KHU VỰC TRA CỨU KHÁCH HÀNG =====
		JPanel pnlTraCuuKH = taoPanelTraCuuKhachHang();
		pnlTraCuuKH.setMinimumSize(new Dimension(300, 200));

		// Chia thành hai vùng theo chiều dọc
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		UiKit.lamDongChiaTrong(splitPane, 14);
		splitPane.setTopComponent(pnlSanPham);
		splitPane.setBottomComponent(pnlTraCuuKH);
		splitPane.setResizeWeight(0.58);
		UiKit.datTyLeLanDau(splitPane, 0.58);

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

		UiKit.TheBo panel = new UiKit.TheBo(new BorderLayout(0, 10), 14);

		// Tiêu đề + thanh tìm kiếm
		JPanel dau = new JPanel(new BorderLayout(0, 8));
		dau.setOpaque(false);

		JPanel pnlTimKiem = new JPanel(new BorderLayout(8, 0));
		pnlTimKiem.setOpaque(false);

		txtTimKiemKH = UiKit.truongGoiY("Nhập mã, tên hoặc số điện thoại khách hàng...");
		txtTimKiemKH.setToolTipText("Nhập mã khách hàng, họ tên, số điện thoại hoặc thông tin khác");

		JButton btnLamMoi = UiKit.nutPhu("Làm mới");

		pnlTimKiem.add(txtTimKiemKH, BorderLayout.CENTER);
		pnlTimKiem.add(btnLamMoi, BorderLayout.EAST);

		dau.add(UiKit.tieuMuc("Tra cứu khách hàng"), BorderLayout.NORTH);
		dau.add(pnlTimKiem, BorderLayout.CENTER);

		// Model tự lấy tên cột từ cơ sở dữ liệu
		modelTraCuuKH = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		tblTraCuuKH = new JTable(modelTraCuuKH);
		UiKit.styleBang(tblTraCuuKH);
		tblTraCuuKH.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tblTraCuuKH.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tblTraCuuKH.setRowHeight(30);

		sorterKH = new TableRowSorter<>(modelTraCuuKH);
		tblTraCuuKH.setRowSorter(sorterKH);

		// Lọc khách hàng theo nội dung nhập
		txtTimKiemKH.getDocument().addDocumentListener(new DocumentListener() {
			private void locKhachHang() {
				String tuKhoa = txtTimKiemKH.getText().trim();

				if (tuKhoa.isEmpty()) {
					sorterKH.setRowFilter(null);
				} else {
					sorterKH.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(tuKhoa)));
				}
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				locKhachHang();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				locKhachHang();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				locKhachHang();
			}
		});

		// Chọn khách hàng bằng cách nhấp đúp vào dòng
		tblTraCuuKH.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					chonKhachHangTuBang();
				}
			}
		});

		// Nút thao tác
		JPanel pnlThaoTac = new JPanel(new BorderLayout());
		pnlThaoTac.setOpaque(false);

		JPanel nutPhai = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		nutPhai.setOpaque(false);

		JButton btnChonKH = UiKit.nutChinh("Chọn khách hàng");
		btnChonKH.addActionListener(e -> chonKhachHangTuBang());
		nutPhai.add(btnChonKH);

		btnLamMoi.addActionListener(e -> {
			txtTimKiemKH.setText("");
			taiDanhSachTraCuuKH();
		});

		pnlThaoTac.add(UiKit.nhanNho("Mẹo: nhấp đúp vào dòng để chọn khách hàng"), BorderLayout.WEST);
		pnlThaoTac.add(nutPhai, BorderLayout.EAST);

		panel.add(dau, BorderLayout.NORTH);
		panel.add(UiKit.cuon(tblTraCuuKH), BorderLayout.CENTER);
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

			UiKit.vuaCot(tblTraCuuKH);

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

    // Combobox "Khách hàng" bên phải đã cập nhật ngay nên không cần hộp thoại.
}

	// ---------- Bên phải: thông tin hóa đơn + sản phẩm đã chọn ----------

	private JPanel taoPanelHoaDon() {

		UiKit.TheBo p = new UiKit.TheBo(new BorderLayout(0, 12), 16);

		// ===== 1. THÔNG TIN HÓA ĐƠN =====
		JPanel info = new JPanel();
		info.setOpaque(false);
		info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

		lblMaHD = UiKit.chip("Mã hóa đơn: " + taoMaHoaDon());

		JPanel chipBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		chipBox.setOpaque(false);
		chipBox.add(lblMaHD);

		JPanel hangTieuDe = new JPanel(new BorderLayout());
		hangTieuDe.setOpaque(false);
		hangTieuDe.add(UiKit.tieuMuc("Thông tin hóa đơn"), BorderLayout.WEST);
		hangTieuDe.add(chipBox, BorderLayout.EAST);

		JPanel hangNgay = new JPanel(new GridLayout(1, 2, 12, 0));
		hangNgay.setOpaque(false);
		hangNgay.add(thongTinNho("Ngày lập", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
		hangNgay.add(thongTinNho("Nhân viên", MA_NV_DANG_DANG_NHAP));

		cboKhachHang = new JComboBox<>();
		UiKit.styleCombo(cboKhachHang);
		cboKhachHang.addItem(new KhachHangItem(null, "Khách lẻ"));
		loadKhachHang();

		cboUuDai = new JComboBox<>();
		UiKit.styleCombo(cboUuDai);
		cboUuDai.addItem(new UuDaiItem(null, "Không áp dụng ưu đãi", 0));
		loadUuDai();

		info.add(trai(hangTieuDe));
		info.add(Box.createVerticalStrut(12));
		info.add(trai(hangNgay));
		info.add(Box.createVerticalStrut(12));
		info.add(trai(dongNhan("Khách hàng", cboKhachHang)));
		info.add(Box.createVerticalStrut(10));
		info.add(trai(dongNhan("Ưu đãi", cboUuDai)));

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
		UiKit.styleBang(tblDaChon);

		// Số lượng căn giữa, tiền căn phải
		UiKit.RenderBang giua = new UiKit.RenderBang();
		giua.setHorizontalAlignment(SwingConstants.CENTER);
		tblDaChon.getColumnModel().getColumn(2).setCellRenderer(giua);

		dinhDangCotTien(tblDaChon, 3);
		tblDaChon.getColumnModel().getColumn(4).setCellRenderer(renderTien(true, null));

		int[] doRong = { 80, 190, 50, 90, 100 };
		for (int i = 0; i < doRong.length; i++) {
			tblDaChon.getColumnModel().getColumn(i).setPreferredWidth(doRong[i]);
		}

		modelDaChon.addTableModelListener(e -> {
			if (dangCapNhatBang)
				return;

			if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == 2 && e.getFirstRow() >= 0) {
				xuLySuaSoLuong(e.getFirstRow());
			}
		});

		JPanel center = new JPanel(new BorderLayout(0, 8));
		center.setOpaque(false);

		center.add(UiKit.tieuMuc("Sản phẩm trong hóa đơn"), BorderLayout.NORTH);
		center.add(UiKit.cuon(tblDaChon), BorderLayout.CENTER);

		JPanel tools = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
		tools.setOpaque(false);

		JButton btnGiam = UiKit.nutNho("−");
		JButton btnTang = UiKit.nutNho("+");
		JButton btnXoa = UiKit.nutNguy("Xóa sản phẩm");
		JButton btnXoaHet = UiKit.nutPhu("Xóa tất cả");

		btnGiam.setToolTipText("Giảm 1 số lượng");
		btnTang.setToolTipText("Tăng 1 số lượng");

		btnGiam.addActionListener(e -> doiSoLuong(-1));
		btnTang.addActionListener(e -> doiSoLuong(1));
		btnXoa.addActionListener(e -> xoaSanPhamDaChon());
		btnXoaHet.addActionListener(e -> xoaTatCa());

		tools.add(UiKit.nhanNho("Số lượng:"));
		tools.add(btnGiam);
		tools.add(btnTang);
		tools.add(Box.createHorizontalStrut(8));
		tools.add(btnXoa);
		tools.add(btnXoaHet);

		center.add(tools, BorderLayout.SOUTH);
		p.add(center, BorderLayout.CENTER);

		// ===== 3. TỔNG TIỀN VÀ THANH TOÁN =====
		JLabel nhanTam = new JLabel("Tạm tính");
		nhanTam.setFont(UiKit.F_THUONG);
		nhanTam.setForeground(UiKit.CHU_PHU);

		lblNhanGiamGia = new JLabel("Ưu đãi");
		lblNhanGiamGia.setFont(UiKit.F_THUONG);
		lblNhanGiamGia.setForeground(UiKit.CHU_PHU);

		JLabel nhanTong = new JLabel("Tổng thanh toán");
		nhanTong.setFont(new Font("Segoe UI", Font.BOLD, 15));
		nhanTong.setForeground(UiKit.CHU);

		lblTamTinh = new JLabel("0 VNĐ");
		lblGiamGia = new JLabel("-0 VNĐ");
		lblTongTien = new JLabel("0 VNĐ");

		lblTamTinh.setFont(UiKit.F_DAM);
		lblTamTinh.setForeground(UiKit.CHU);

		lblGiamGia.setFont(UiKit.F_DAM);
		lblGiamGia.setForeground(UiKit.DO);

		lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 22));
		lblTongTien.setForeground(UiKit.CAM);

		javax.swing.JSeparator ngan = new javax.swing.JSeparator();
		ngan.setForeground(UiKit.VANG_VIEN);

		JPanel dong = new JPanel();
		dong.setOpaque(false);
		dong.setLayout(new BoxLayout(dong, BoxLayout.Y_AXIS));

		dong.add(trai(dongTien(nhanTam, lblTamTinh)));
		dong.add(Box.createVerticalStrut(6));
		dong.add(trai(dongTien(lblNhanGiamGia, lblGiamGia)));
		dong.add(Box.createVerticalStrut(8));
		dong.add(trai(ngan));
		dong.add(Box.createVerticalStrut(8));
		dong.add(trai(dongTien(nhanTong, lblTongTien)));

		UiKit.TheBo khungTong = new UiKit.TheBo(new BorderLayout(), 14, UiKit.VANG_NHAT, UiKit.VANG_VIEN);
		khungTong.add(dong, BorderLayout.CENTER);

		JButton btnThanhToan = UiKit.nutXanh("THANH TOÁN");
		btnThanhToan.setFont(new Font("Segoe UI", Font.BOLD, 16));
		btnThanhToan.setPreferredSize(new Dimension(180, 48));

		btnThanhToan.addActionListener(e -> {
			if (tblDaChon.isEditing()) {
				if (!tblDaChon.getCellEditor().stopCellEditing()) {
					return;
				}
			}

			if (modelDaChon.getRowCount() == 0) {
				JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm trước khi thanh toán!", "Thông báo",
						JOptionPane.WARNING_MESSAGE);
				return;
			}

			capNhatTong();

			long tongTien = Math.round(tongTienGoc - soTienGiam);

			if (tongTien <= 0) {
				JOptionPane.showMessageDialog(this, "Tổng tiền thanh toán không hợp lệ!", "Thông báo",
						JOptionPane.WARNING_MESSAGE);
				return;
			}

			ThanhToanUI trangThanhToan = new ThanhToanUI(this, tongTien, () -> luuHoaDon());

			trangThanhToan.setVisible(true);
		});

		JPanel bottom = new JPanel(new BorderLayout(0, 12));
		bottom.setOpaque(false);
		bottom.add(khungTong, BorderLayout.CENTER);
		bottom.add(btnThanhToan, BorderLayout.SOUTH);

		p.add(bottom, BorderLayout.SOUTH);

		// Cập nhật tổng tiền khi đổi khách hàng hoặc ưu đãi
		cboUuDai.addActionListener(e -> capNhatTong());
		cboKhachHang.addActionListener(e -> capNhatTong());

		return p;
	}

	// Cặp "nhãn nhỏ phía trên + giá trị đậm phía dưới"
	private JPanel thongTinNho(String nhan, String giaTri) {
		JPanel d = new JPanel(new BorderLayout(0, 2));
		d.setOpaque(false);

		JLabel v = new JLabel(giaTri);
		v.setFont(UiKit.F_DAM);
		v.setForeground(UiKit.CHU);

		d.add(UiKit.nhanNho(nhan), BorderLayout.NORTH);
		d.add(v, BorderLayout.CENTER);
		return d;
	}

	// Nhãn phía trên + ô nhập / combobox phía dưới
	private JPanel dongNhan(String nhan, JComponent c) {
		JPanel d = new JPanel(new BorderLayout(0, 4));
		d.setOpaque(false);
		d.add(UiKit.nhanNho(nhan), BorderLayout.NORTH);
		d.add(c, BorderLayout.CENTER);
		return d;
	}

	// Một dòng: nhãn bên trái, giá trị bên phải
	private JPanel dongTien(JLabel nhan, JLabel giaTri) {
		JPanel d = new JPanel(new BorderLayout());
		d.setOpaque(false);
		d.add(nhan, BorderLayout.WEST);
		d.add(giaTri, BorderLayout.EAST);
		return d;
	}

	// Căn trái thành phần trong BoxLayout
	private <T extends JComponent> T trai(T c) {
		c.setAlignmentX(Component.LEFT_ALIGNMENT);
		return c;
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
			lblTamTinh.setText(String.format("%,.0f VNĐ", tongTienGoc));
		}

		if (lblNhanGiamGia != null) {
			lblNhanGiamGia.setText("Ưu đãi (" + String.format("%.0f", phanTram) + "%)");
		}

		if (lblGiamGia != null) {
			lblGiamGia.setText("-" + String.format("%,.0f VNĐ", soTienGiam));
		}

		if (lblTongTien != null) {
			lblTongTien.setText(String.format("%,.0f VNĐ", tongThanhToan));
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
	// Hiển thị cột tiền dạng 1,290,000
	private void dinhDangCotTien(JTable table, int... cols) {

		UiKit.RenderBang r = renderTien(false, null);

		for (int c : cols) {
			table.getColumnModel().getColumn(c).setCellRenderer(r);
		}
	}

	// Renderer cột tiền: căn phải, có thể in đậm và đổi màu
	private UiKit.RenderBang renderTien(boolean dam, Color mau) {

		UiKit.RenderBang r = new UiKit.RenderBang() {
			@Override
			public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int row,
					int col) {
				super.getTableCellRendererComponent(t, v, s, f, row, col);
				if (dam) {
					setFont(UiKit.F_DAM);
				}
				if (mau != null) {
					setForeground(mau);
				}
				return this;
			}

			@Override
			protected void setValue(Object value) {
				if (value instanceof Number)
					setText(String.format("%,.0f", ((Number) value).doubleValue()));
				else
					super.setValue(value);
			}
		};

		r.setHorizontalAlignment(SwingConstants.RIGHT);

		return r;
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
