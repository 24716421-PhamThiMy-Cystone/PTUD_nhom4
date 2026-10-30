package GUI;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

public class TaoHoaDonUI extends JFrame {
	private final Color YELLOW = new Color(255, 225, 116), PALE = new Color(255, 242, 194),
			BG = new Color(246, 247, 249), GREEN = new Color(58, 153, 22), ORANGE = new Color(255, 112, 0),
			DARK = new Color(48, 52, 58);
	private final JPanel productGrid = new JPanel(new GridLayout(0, 4, 8, 8));
	private final DefaultTableModel invoiceModel = new DefaultTableModel(
			new Object[] { "STT", "Tên sản phẩm", "SL", "Đơn giá", "Thành tiền" }, 0) {
		public boolean isCellEditable(int r, int c) {
			return c == 2;
		}
	};
	private final JTable invoiceTable = new JTable(invoiceModel);
	private final JLabel subtotalLabel = new JLabel("0 VND"), discountLabel = new JLabel("0 VND"),
			totalLabel = new JLabel("0 VND");
	private final JTextField searchProduct = new JTextField(), searchCustomer = new JTextField();
	private final JTextField customerField = new JTextField("Chưa chọn khách hàng");
	private final JComboBox<String> category = new JComboBox<>(
			new String[] { "Tất cả", "Tivi", "Tủ lạnh", "Máy giặt", "Điều hòa", "Điện thoại" });
	private final JComboBox<String> voucher = new JComboBox<>(
			new String[] { "Chọn voucher (nếu có)", "GIAM5 - Giảm 5%", "GIAM10 - Giảm 10%" });
	private final List<Product> products = Arrays.asList(new Product("Tivi SamSung 55 inch 4K", "Tivi", 12990000, "TV"),
			new Product("Tủ lạnh SamSung Inverter 360L", "Tủ lạnh", 12980000, "TL"),
			new Product("Máy giặt LG Inverter 9kg", "Máy giặt", 8990000, "MG"),
			new Product("Điều hòa LG Inverter 1.5HP", "Điều hòa", 7490000, "DH"),
			new Product("iPhone 17 512GB", "Điện thoại", 32990000, "IP"),
			new Product("Tivi Sony 65 inch 4K", "Tivi", 15900000, "TV"),
			new Product("Tủ lạnh Toshiba Inverter 453L", "Tủ lạnh", 14890000, "TL"),
			new Product("Máy giặt Panasonic 10kg", "Máy giặt", 10900000, "MG"));
	private final Map<String, Integer> quantities = new LinkedHashMap<>();
	private final Map<String, Product> chosen = new LinkedHashMap<>();
	private final NumberFormat money = NumberFormat.getInstance(new Locale("vi", "VN"));

	public TaoHoaDonUI() {
		super("UltraMart - Tạo hóa đơn");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(1200, 760));
		setSize(1440, 900);
		setLocationRelativeTo(null);
		getContentPane().setBackground(BG);
		setLayout(new BorderLayout(0, 0));
		add(createSidebar(), BorderLayout.WEST);
		JPanel main = new JPanel(new BorderLayout(14, 10));
		main.setBackground(BG);
		main.setBorder(new EmptyBorder(16, 18, 16, 18));
		add(main, BorderLayout.CENTER);
		main.add(createHeader(), BorderLayout.NORTH);
		JPanel body = new JPanel(new GridBagLayout());
		body.setOpaque(false);
		main.add(body, BorderLayout.CENTER);
		GridBagConstraints g = new GridBagConstraints();
		g.insets = new Insets(0, 0, 0, 14);
		g.fill = GridBagConstraints.BOTH;
		g.weighty = 1;
		g.gridx = 0;
		g.gridy = 0;
		g.weightx = 0.59;
		body.add(createLeftContent(), g);
		g.gridx = 1;
		g.weightx = 0.41;
		g.insets = new Insets(0, 0, 0, 0);
		body.add(createInvoicePanel(), g);
		refreshProducts();
		updateTotals();
		invoiceModel.addTableModelListener(e -> {
			if (e.getType() == javax.swing.event.TableModelEvent.UPDATE)
				updateTotals();
		});
		searchProduct.addActionListener(e -> refreshProducts());
		category.addActionListener(e -> refreshProducts());
		voucher.addActionListener(e -> updateTotals());
	}

	private JPanel createSidebar() {
		JPanel side = new JPanel();
		side.setPreferredSize(new Dimension(190, 0));
		side.setBackground(new Color(238, 241, 245));
		side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
		side.setBorder(new EmptyBorder(18, 12, 14, 12));
		JLabel logo = new JLabel("🛒 UltraMart");
		logo.setFont(new Font("SansSerif", Font.BOLD, 22));
		logo.setForeground(ORANGE);
		logo.setAlignmentX(Component.CENTER_ALIGNMENT);
		side.add(logo);
		side.add(Box.createVerticalStrut(28));
		String[] nav = { "⌂   Trang chủ", "◇   Sản phẩm", "▤   Hóa đơn", "♙   Nhân viên", "◷   Báo cáo", "%   Ưu đãi",
				"♟   Khách hàng", "♧   Đổi Trả" };
		for (String n : nav) {
			JLabel l = new JLabel(n);
			l.setFont(new Font("SansSerif", n.contains("Hóa đơn") ? Font.BOLD : Font.PLAIN, 14));
			l.setForeground(DARK);
			l.setOpaque(true);
			l.setBackground(n.contains("Hóa đơn") ? YELLOW : new Color(238, 241, 245));
			l.setBorder(new EmptyBorder(13, 12, 13, 6));
			l.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
			l.setAlignmentX(Component.LEFT_ALIGNMENT);
			side.add(l);
		}
		side.add(Box.createVerticalGlue());
		JLabel help = new JLabel("ⓘ  Hướng dẫn");
		help.setBorder(new EmptyBorder(12, 8, 12, 4));
		side.add(help);
		JLabel logout = new JLabel("⇥  Đăng xuất");
		logout.setBorder(new EmptyBorder(12, 8, 8, 4));
		side.add(logout);
		return side;
	}

	private JPanel createHeader() {
		JPanel p = new JPanel(new BorderLayout(20, 0));
		p.setOpaque(false);
		JPanel left = new JPanel(new BorderLayout(10, 0));
		left.setOpaque(false);
		JLabel title = new JLabel("Tạo hóa đơn");
		title.setFont(new Font("SansSerif", Font.BOLD, 22));
		left.add(title, BorderLayout.WEST);
		searchProduct.setPreferredSize(new Dimension(310, 38));
		searchProduct.setFont(new Font("SansSerif", Font.PLAIN, 14));
		searchProduct
				.setBorder(new CompoundBorder(new LineBorder(new Color(220, 223, 228)), new EmptyBorder(8, 12, 8, 12)));
		searchProduct.setToolTipText("Nhập tên sản phẩm để tìm kiếm");
		JPanel searchWrap = new JPanel(new BorderLayout());
		searchWrap.setOpaque(false);
		searchWrap.add(searchProduct);
		JLabel icon = new JLabel("  ⚲  ");
		icon.setFont(new Font("SansSerif", Font.BOLD, 20));
		icon.setOpaque(true);
		icon.setBackground(Color.WHITE);
		searchWrap.add(icon, BorderLayout.EAST);
		left.add(searchWrap, BorderLayout.EAST);
		p.add(left, BorderLayout.CENTER);
		JLabel user = new JLabel("👩🏻  Trần Huỳnh Trân\n      Nhân viên bán hàng");
		user.setFont(new Font("SansSerif", Font.BOLD, 13));
		user.setOpaque(true);
		user.setBackground(new Color(238, 241, 245));
		user.setBorder(new EmptyBorder(12, 14, 12, 14));
		p.add(user, BorderLayout.EAST);
		return p;
	}

	private JPanel createLeftContent() {
		JPanel p = new JPanel(new BorderLayout(0, 10));
		p.setOpaque(false);
		JPanel cat = new JPanel(new FlowLayout(FlowLayout.LEFT, 7, 0));
		cat.setOpaque(false);
		category.setFont(new Font("SansSerif", Font.PLAIN, 13));
		category.setBackground(YELLOW);
		category.setBorder(new EmptyBorder(7, 9, 7, 9));
		cat.add(category);
		p.add(cat, BorderLayout.NORTH);
		productGrid.setOpaque(false);
		JScrollPane productScroll = new JScrollPane(productGrid);
		productScroll.setBorder(null);
		productScroll.getViewport().setOpaque(false);
		productScroll.setOpaque(false);
		productScroll.getVerticalScrollBar().setUnitIncrement(18);
		JPanel upper = new JPanel(new BorderLayout());
		upper.setOpaque(false);
		upper.add(productScroll, BorderLayout.CENTER);
		upper.setPreferredSize(new Dimension(600, 390));
		JPanel customerPanel = createCustomerPanel();
		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, upper, customerPanel);
		split.setResizeWeight(.61);
		split.setDividerSize(8);
		split.setBorder(null);
		split.setOpaque(false);
		split.setContinuousLayout(true);
		p.add(split, BorderLayout.CENTER);
		return p;
	}

	private JPanel createCustomerPanel() {
		JPanel p = new JPanel(new BorderLayout(7, 8));
		p.setBackground(PALE);
		p.setBorder(
				new CompoundBorder(new LineBorder(new Color(255, 226, 135), 1, true), new EmptyBorder(10, 12, 10, 12)));
		JPanel top = new JPanel(new BorderLayout());
		top.setOpaque(false);
		JLabel title = new JLabel("Tra cứu khách hàng");
		title.setFont(new Font("SansSerif", Font.BOLD, 20));
		top.add(title, BorderLayout.WEST);
		JButton add = new JButton("+ Thêm khách hàng");
		styleButton(add, YELLOW, DARK);
		add.addActionListener(e -> JOptionPane.showMessageDialog(this, "Mở chức năng thêm khách hàng."));
		top.add(add, BorderLayout.EAST);
		p.add(top, BorderLayout.NORTH);
		JPanel mid = new JPanel(new BorderLayout(0, 5));
		mid.setOpaque(false);
		searchCustomer
				.setBorder(new CompoundBorder(new LineBorder(new Color(225, 225, 225)), new EmptyBorder(8, 10, 8, 10)));
		searchCustomer.setToolTipText("Nhập SĐT hoặc tên khách hàng");
		mid.add(searchCustomer, BorderLayout.NORTH);
		String[] cols = { "Mã KH", "Tên khách hàng", "Số điện thoại", "Địa chỉ", "Hạng thành viên", "Chọn" };
		Object[][] rows = { { "KH12340001", "Nguyễn Văn Trí", "0348679124", "Q.Bình Thạnh, HCM", "Vàng", "Chọn" },
				{ "KH98760002", "Bùi Thị Thảo", "0987657245", "Q.Gò Vấp, HCM", "Bạc", "Chọn" },
				{ "KH01140003", "Trần Thanh Thúy", "0345098965", "Q.Gò Vấp, HCM", "Đồng", "Chọn" } };
		JTable table = new JTable(new DefaultTableModel(rows, cols) {
			public boolean isCellEditable(int r, int c) {
				return c == 5;
			}
		});
		table.setRowHeight(28);
		table.setFont(new Font("SansSerif", Font.PLAIN, 11));
		table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 10));
		table.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
		table.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox(), row -> {
			String name = String.valueOf(table.getValueAt(row, 1));
			customerField.setText(name);
			JOptionPane.showMessageDialog(this, "Đã chọn khách hàng: " + name);
		}));
		mid.add(new JScrollPane(table), BorderLayout.CENTER);
		p.add(mid, BorderLayout.CENTER);
		searchCustomer.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				String q = searchCustomer.getText().toLowerCase();
				for (int r = 0; r < table.getRowCount(); r++) {
					boolean found = false;
					for (int c = 0; c < 4; c++)
						if (String.valueOf(table.getValueAt(r, c)).toLowerCase().contains(q))
							found = true;
					table.setRowSelectionAllowed(true);
					if (found)
						table.setRowSelectionInterval(r, r);
				}
			}
		});
		return p;
	}

	private JPanel createInvoicePanel() {
		JPanel p = new JPanel(new BorderLayout(0, 10));
		p.setOpaque(false);
		JLabel title = new JLabel("Thông tin hóa đơn");
		title.setFont(new Font("SansSerif", Font.BOLD, 20));
		p.add(title, BorderLayout.NORTH);
		JPanel content = new JPanel();
		content.setOpaque(false);
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		JPanel ids = new JPanel(new GridLayout(1, 2, 10, 0));
		ids.setOpaque(false);
		ids.add(fieldBlock("Mã hóa đơn", new JTextField("HD161020260001")));
		ids.add(fieldBlock("Ngày tạo", new JTextField(
				java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")))));
		content.add(ids);
		content.add(Box.createVerticalStrut(8));
		content.add(fieldBlock("♟ Khách hàng", customerField));
		customerField
				.setBorder(new CompoundBorder(new LineBorder(new Color(205, 209, 215)), new EmptyBorder(7, 8, 7, 8)));
		content.add(Box.createVerticalStrut(8));
		content.add(fieldBlock("♧ Voucher", voucher));
		content.add(Box.createVerticalStrut(10));
		JLabel listTitle = new JLabel("▤ Danh sách sản phẩm");
		listTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
		content.add(listTitle);
		content.add(Box.createVerticalStrut(4));
		invoiceTable.setRowHeight(35);
		invoiceTable.setFont(new Font("SansSerif", Font.PLAIN, 11));
		invoiceTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 10));
		invoiceTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		invoiceTable.getColumnModel().getColumn(0).setPreferredWidth(30);
		invoiceTable.getColumnModel().getColumn(1).setPreferredWidth(170);
		invoiceTable.getColumnModel().getColumn(2).setPreferredWidth(35);
		invoiceTable.getColumnModel().getColumn(3).setPreferredWidth(75);
		invoiceTable.getColumnModel().getColumn(4).setPreferredWidth(95);
		JScrollPane tableScroll = new JScrollPane(invoiceTable);
		tableScroll.setPreferredSize(new Dimension(500, 250));
		tableScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 270));
		content.add(tableScroll);
		content.add(Box.createVerticalGlue());
		p.add(content, BorderLayout.CENTER);
		JPanel bottom = new JPanel();
		bottom.setOpaque(false);
		bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
		bottom.add(summaryRow("Tạm tính", subtotalLabel));
		bottom.add(Box.createVerticalStrut(9));
		bottom.add(summaryRow("Giảm giá", discountLabel));
		bottom.add(Box.createVerticalStrut(12));
		JPanel total = new JPanel(new BorderLayout());
		total.setBackground(PALE);
		total.setBorder(new EmptyBorder(10, 12, 10, 12));
		JLabel tl = new JLabel("Tổng tiền:");
		tl.setForeground(ORANGE);
		tl.setFont(new Font("SansSerif", Font.BOLD, 16));
		totalLabel.setForeground(ORANGE);
		totalLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
		total.add(tl, BorderLayout.WEST);
		total.add(totalLabel, BorderLayout.EAST);
		bottom.add(total);
		bottom.add(Box.createVerticalStrut(12));
		JButton pay = new JButton("Thanh toán");
		pay.setFont(new Font("SansSerif", Font.BOLD, 16));
		pay.setForeground(Color.WHITE);
		pay.setBackground(GREEN);
		pay.setFocusPainted(false);
		pay.setBorder(new EmptyBorder(12, 25, 12, 25));
		pay.setAlignmentX(Component.CENTER_ALIGNMENT);
		pay.addActionListener(e -> payment());
		bottom.add(pay);
		p.add(bottom, BorderLayout.SOUTH);
		return p;
	}

	private JPanel fieldBlock(String label, Component input) {
		JPanel p = new JPanel(new BorderLayout(0, 3));
		p.setOpaque(false);
		JLabel l = new JLabel(label);
		l.setFont(new Font("SansSerif", Font.PLAIN, 12));
		p.add(l, BorderLayout.NORTH);
		input.setFont(new Font("SansSerif", Font.PLAIN, 12));
		input.setPreferredSize(new Dimension(100, 32));
		p.add(input, BorderLayout.CENTER);
		return p;
	}

	private JPanel summaryRow(String name, JLabel val) {
		JPanel p = new JPanel(new BorderLayout());
		p.setOpaque(false);
		JLabel l = new JLabel(name);
		l.setFont(new Font("SansSerif", Font.PLAIN, 14));
		val.setFont(new Font("SansSerif", Font.PLAIN, 14));
		p.add(l, BorderLayout.WEST);
		p.add(val, BorderLayout.EAST);
		return p;
	}

	private void refreshProducts() {
		productGrid.removeAll();
		String q = searchProduct.getText().trim().toLowerCase();
		String cat = String.valueOf(category.getSelectedItem());
		for (Product pr : products) {
			if (!cat.equals("Tất cả") && !pr.category.equals(cat))
				continue;
			if (!pr.name.toLowerCase().contains(q))
				continue;
			productGrid.add(productCard(pr));
		}
		productGrid.revalidate();
		productGrid.repaint();
	}

	private JPanel productCard(Product pr) {
		JPanel p = new JPanel(new BorderLayout(3, 5));
		p.setBackground(Color.WHITE);
		p.setBorder(new CompoundBorder(new LineBorder(new Color(228, 230, 233)), new EmptyBorder(7, 7, 7, 7)));
		JLabel image = new JLabel(productEmoji(pr.category), SwingConstants.CENTER);
		image.setFont(new Font("SansSerif", Font.PLAIN, 48));
		image.setOpaque(true);
		image.setBackground(new Color(248, 249, 250));
		image.setPreferredSize(new Dimension(120, 85));
		p.add(image, BorderLayout.NORTH);
		JLabel name = new JLabel("<html><div style='width:120px'>" + pr.name + "</div></html>");
		name.setFont(new Font("SansSerif", Font.PLAIN, 11));
		p.add(name, BorderLayout.CENTER);
		JPanel b = new JPanel();
		b.setOpaque(false);
		b.setLayout(new BoxLayout(b, BoxLayout.Y_AXIS));
		JLabel price = new JLabel(money.format(pr.price) + " VND");
		price.setForeground(ORANGE);
		price.setFont(new Font("SansSerif", Font.BOLD, 11));
		b.add(price);
		JPanel row = new JPanel(new BorderLayout());
		row.setOpaque(false);
		JLabel stock = new JLabel("• Số lượng: 15");
		stock.setForeground(GREEN);
		stock.setFont(new Font("SansSerif", Font.PLAIN, 10));
		row.add(stock, BorderLayout.CENTER);
		JButton add = new JButton("Thêm +");
		styleButton(add, new Color(255, 245, 140), DARK);
		add.setFont(new Font("SansSerif", Font.BOLD, 10));
		add.addActionListener(e -> addProduct(pr));
		row.add(add, BorderLayout.EAST);
		b.add(row);
		p.add(b, BorderLayout.SOUTH);
		return p;
	}

	private String productEmoji(String cat) {
		switch (cat) {
		case "Tivi":
			return "📺";
		case "Tủ lạnh":
			return "🧊";
		case "Máy giặt":
			return "🧺";
		case "Điều hòa":
			return "❄";
		default:
			return "📱";
		}
	}

	private void addProduct(Product p) {
		if (!chosen.containsKey(p.name)) {
			chosen.put(p.name, p);
			quantities.put(p.name, 1);
		} else
			quantities.put(p.name, quantities.get(p.name) + 1);
		rebuildInvoice();
	}

	private void rebuildInvoice() {
		invoiceModel.setRowCount(0);
		int i = 1;
		for (String name : chosen.keySet()) {
			Product p = chosen.get(name);
			int qty = quantities.get(name);
			invoiceModel.addRow(new Object[] { i++, name, qty, money.format(p.price), money.format(p.price * qty) });
		}
		updateTotals();
	}

	private void updateTotals() {
		double subtotal = 0;
		for (String name : chosen.keySet())
			subtotal += chosen.get(name).price * quantities.get(name);
		double rate = voucher.getSelectedIndex() == 1 ? .05 : voucher.getSelectedIndex() == 2 ? .10 : 0;
		double discount = subtotal * rate;
		subtotalLabel.setText(money.format(subtotal) + " VND");
		discountLabel.setText(money.format(discount) + " VND");
		totalLabel.setText(money.format(subtotal - discount) + " VND");
	}

	private void payment() {
		if (chosen.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng thêm ít nhất một sản phẩm vào hóa đơn.", "Chưa có sản phẩm",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		if (customerField.getText().trim().isEmpty() || customerField.getText().equals("Chưa chọn khách hàng")) {
			int c = JOptionPane.showConfirmDialog(this, "Bạn chưa chọn khách hàng. Tiếp tục thanh toán?", "Xác nhận",
					JOptionPane.YES_NO_OPTION);
			if (c != JOptionPane.YES_OPTION)
				return;
		}
		int ok = JOptionPane.showConfirmDialog(this,
				"Tổng tiền cần thanh toán: " + totalLabel.getText() + "\nXác nhận thanh toán hóa đơn?", "Thanh toán",
				JOptionPane.YES_NO_OPTION);
		if (ok == JOptionPane.YES_OPTION)
			JOptionPane.showMessageDialog(this,
					"Đã xác nhận giao dịch trên giao diện mẫu.\nCần kết nối DAO/cơ sở dữ liệu để lưu hóa đơn thực tế.",
					"Thông báo", JOptionPane.INFORMATION_MESSAGE);
	}

	private void styleButton(JButton b, Color bg, Color fg) {
		b.setBackground(bg);
		b.setForeground(fg);
		b.setFocusPainted(false);
		b.setBorder(new EmptyBorder(6, 8, 6, 8));
	}

	private static class Product {
		String name, category, sku;
		double price;

		Product(String n, String c, double p, String s) {
			name = n;
			category = c;
			price = p;
			sku = s;
		}
	}

	private static class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
		ButtonRenderer() {
			setText("Chọn");
			setOpaque(true);
		}

		public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
			setText(v == null ? "Chọn" : v.toString());
			setBackground(new Color(232, 235, 239));
			return this;
		}
	}

	private static class ButtonEditor extends DefaultCellEditor {
		private final JButton button = new JButton("Chọn");
		private int row;
		private final java.util.function.IntConsumer action;

		ButtonEditor(JCheckBox cb, java.util.function.IntConsumer a) {
			super(cb);
			action = a;
			button.addActionListener(e -> {
				fireEditingStopped();
				action.accept(row);
			});
		}

		public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int c) {
			row = r;
			button.setText("Chọn");
			return button;
		}

		public Object getCellEditorValue() {
			return "Chọn";
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception ignored) {
			}
			new TaoHoaDonUI().setVisible(true);
		});
	}
}
