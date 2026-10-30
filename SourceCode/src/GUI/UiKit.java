package GUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * Bộ giao diện dùng chung cho UltraMart:
 * màu sắc, phông chữ, nút bo góc, thẻ (card), bảng, ô nhập có gợi ý...
 */
public final class UiKit {

    private UiKit() {
    }

    // ================= MÀU SẮC =================

    public static final Color NEN = new Color(247, 248, 250);
    public static final Color TRANG = Color.WHITE;
    public static final Color VANG = new Color(255, 226, 122);
    public static final Color VANG_NHAT = new Color(255, 248, 220);
    public static final Color VANG_VIEN = new Color(240, 222, 160);
    public static final Color CAM = new Color(230, 110, 20);
    public static final Color XANH = new Color(46, 160, 67);
    public static final Color XANH_DUONG = new Color(37, 117, 230);
    public static final Color DO = new Color(205, 60, 55);
    public static final Color VIEN = new Color(225, 228, 232);
    public static final Color CHU = new Color(45, 45, 45);
    public static final Color CHU_PHU = new Color(120, 125, 135);
    public static final Color ZEBRA = new Color(250, 251, 252);
    public static final Color CHON = new Color(255, 240, 190);
    public static final Color DAU_BANG = new Color(245, 246, 248);

    // ================= PHÔNG CHỮ =================

    public static final Font F_THUONG = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font F_DAM = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font F_NHO = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font F_MUC = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font F_TIEU_DE = new Font("Segoe UI", Font.BOLD, 24);

    private static Color pha(Color a, Color b, double t) {
        int r = (int) (a.getRed() + (b.getRed() - a.getRed()) * t);
        int g = (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t);
        int bl = (int) (a.getBlue() + (b.getBlue() - a.getBlue()) * t);
        return new Color(r, g, bl);
    }

    private static void matMin(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    // ================= NHÃN =================

    public static JLabel tieuDeTrang(String text) {
        JLabel l = new JLabel(text);
        l.setFont(F_TIEU_DE);
        l.setForeground(CHU);
        return l;
    }

    public static JLabel phuDe(String text) {
        JLabel l = new JLabel(text);
        l.setFont(F_NHO);
        l.setForeground(CHU_PHU);
        return l;
    }

    public static JLabel tieuMuc(String text) {
        JLabel l = new JLabel(text);
        l.setFont(F_MUC);
        l.setForeground(CHU);
        return l;
    }

    public static JLabel nhanNho(String text) {
        JLabel l = new JLabel(text);
        l.setFont(F_NHO);
        l.setForeground(CHU_PHU);
        return l;
    }

    /** Nhãn nổi bật dạng "chip" (ví dụ mã hóa đơn). */
    public static JLabel chip(String text) {
        JLabel l = new JLabel(text);
        l.setFont(F_DAM);
        l.setForeground(CHU);
        l.setOpaque(true);
        l.setBackground(VANG_NHAT);
        l.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(VANG_VIEN),
                new EmptyBorder(5, 12, 5, 12)));
        return l;
    }

    // ================= THẺ (CARD) BO GÓC =================

    public static class TheBo extends JPanel {

        private final int bk = 14;
        private final Color nen;
        private final Color vien;

        public TheBo(LayoutManager lm, int pad) {
            this(lm, pad, TRANG, VIEN);
        }

        public TheBo(LayoutManager lm, int pad, Color nen, Color vien) {
            super(lm);
            this.nen = nen;
            this.vien = vien;
            setOpaque(false);
            setBorder(new EmptyBorder(pad, pad, pad, pad));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            matMin(g2);
            g2.setColor(nen);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, bk, bk);
            g2.setColor(vien);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, bk, bk);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ================= NÚT BO GÓC =================

    public static class NutBo extends JButton {

        private final Color nen;
        private final Color hover;
        private final Color vien;
        private boolean dangRe = false;

        public NutBo(String text, Color nen, Color hover, Color chu, Color vien) {
            super(text);
            this.nen = nen;
            this.hover = hover;
            this.vien = vien;

            setFont(F_DAM);
            setForeground(chu);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(8, 16, 8, 16));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    dangRe = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    dangRe = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            matMin(g2);

            Color c;
            if (!isEnabled()) {
                c = new Color(236, 237, 240);
            } else if (getModel().isPressed()) {
                c = pha(nen, Color.BLACK, 0.12);
            } else if (dangRe) {
                c = hover;
            } else {
                c = nen;
            }

            g2.setColor(c);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);

            if (vien != null) {
                g2.setColor(vien);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static NutBo nutChinh(String text) {
        return new NutBo(text, VANG, new Color(255, 236, 160), CHU, null);
    }

    public static NutBo nutXanh(String text) {
        return new NutBo(text, XANH, new Color(60, 180, 85), Color.WHITE, null);
    }

    public static NutBo nutXanhDuong(String text) {
        return new NutBo(text, XANH_DUONG, new Color(70, 140, 240), Color.WHITE, null);
    }

    public static NutBo nutPhu(String text) {
        return new NutBo(text, TRANG, new Color(244, 245, 247), CHU, VIEN);
    }

    public static NutBo nutNguy(String text) {
        return new NutBo(text, TRANG, new Color(255, 238, 236), DO,
                new Color(240, 190, 185));
    }

    /** Nút vuông nhỏ cho dấu − và +. */
    public static NutBo nutNho(String text) {
        NutBo b = nutPhu(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 16));
        b.setBorder(new EmptyBorder(4, 0, 4, 0));
        b.setPreferredSize(new Dimension(38, 34));
        return b;
    }

    // ================= NÚT CHỌN (DẠNG TAB) =================

    public static class NutChon extends JToggleButton {

        public NutChon(String text) {
            super(text);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setForeground(CHU);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(10, 16, 10, 16));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            matMin(g2);
            g2.setColor(isSelected() ? VANG : TRANG);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            g2.setColor(isSelected() ? VANG_VIEN : VIEN);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ================= Ô NHẬP =================

    public static class TruongGoiY extends JTextField {

        private final String goiY;

        public TruongGoiY(String goiY) {
            this.goiY = goiY;
            setFont(F_THUONG);
            setForeground(CHU);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(VIEN),
                    new EmptyBorder(7, 10, 7, 10)));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (getText().isEmpty()) {
                Graphics2D g2 = (Graphics2D) g.create();
                matMin(g2);
                g2.setColor(new Color(150, 155, 165));
                g2.setFont(getFont());
                Insets in = getInsets();
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(goiY, in.left,
                        (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
                g2.dispose();
            }
        }
    }

    public static TruongGoiY truongGoiY(String goiY) {
        return new TruongGoiY(goiY);
    }

    public static void styleCombo(JComboBox<?> c) {
        c.setFont(F_THUONG);
        c.setBackground(TRANG);
        c.setPreferredSize(new Dimension(c.getPreferredSize().width, 34));
    }

    // ================= BẢNG =================

    public static class RenderBang extends DefaultTableCellRenderer {

        private static final SimpleDateFormat NGAY_GIO =
                new SimpleDateFormat("dd/MM/yyyy HH:mm");

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int r, int c) {
            super.getTableCellRendererComponent(t, v, sel, false, r, c);
            setForeground(CHU);
            setBackground(sel ? CHON : (r % 2 == 0 ? TRANG : ZEBRA));
            setBorder(new EmptyBorder(0, 10, 0, 10));
            setFont(F_THUONG);
            return this;
        }

        @Override
        protected void setValue(Object v) {
            if (v instanceof java.util.Date) {
                setText(NGAY_GIO.format((java.util.Date) v));
            } else {
                super.setValue(v);
            }
        }
    }

    private static class RenderDauBang implements TableCellRenderer {

        private final JLabel lbl = new JLabel();

        RenderDauBang() {
            lbl.setOpaque(true);
            lbl.setBackground(DAU_BANG);
            lbl.setForeground(CHU);
            lbl.setFont(F_DAM);
            lbl.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, VANG),
                    new EmptyBorder(0, 10, 0, 10)));
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v,
                boolean s, boolean f, int r, int c) {
            lbl.setText(v == null ? "" : v.toString());

            int can = SwingConstants.LEFT;

            if (c >= 0 && c < t.getColumnModel().getColumnCount()) {
                TableCellRenderer cr =
                        t.getColumnModel().getColumn(c).getCellRenderer();
                if (cr instanceof JLabel) {
                    can = ((JLabel) cr).getHorizontalAlignment();
                }
            }

            lbl.setHorizontalAlignment(can);
            return lbl;
        }
    }

    public static void styleBang(JTable t) {
        t.setFont(F_THUONG);
        t.setForeground(CHU);
        t.setBackground(TRANG);
        t.setRowHeight(34);
        t.setShowVerticalLines(false);
        t.setShowHorizontalLines(true);
        t.setGridColor(new Color(238, 240, 243));
        t.setIntercellSpacing(new Dimension(0, 1));
        t.setSelectionBackground(CHON);
        t.setSelectionForeground(CHU);
        t.setFillsViewportHeight(true);

        t.setDefaultRenderer(Object.class, new RenderBang());

        RenderBang so = new RenderBang();
        so.setHorizontalAlignment(SwingConstants.RIGHT);
        t.setDefaultRenderer(Number.class, so);

        JTableHeader h = t.getTableHeader();
        h.setReorderingAllowed(false);
        h.setPreferredSize(new Dimension(0, 38));
        h.setDefaultRenderer(new RenderDauBang());
    }

    /** Tự chỉnh độ rộng cột theo nội dung. */
    public static void vuaCot(JTable t) {
        TableColumnModel cm = t.getColumnModel();

        for (int c = 0; c < cm.getColumnCount(); c++) {
            TableColumn col = cm.getColumn(c);

            Component hc = t.getTableHeader().getDefaultRenderer()
                    .getTableCellRendererComponent(
                            t, col.getHeaderValue(), false, false, -1, c);

            int w = hc.getPreferredSize().width;
            int n = Math.min(t.getRowCount(), 60);

            for (int r = 0; r < n; r++) {
                Component cc = t.prepareRenderer(t.getCellRenderer(r, c), r, c);
                w = Math.max(w, cc.getPreferredSize().width);
            }

            col.setPreferredWidth(Math.max(70, Math.min(280, w + 16)));
        }
    }

    public static JScrollPane cuon(Component c) {
        JScrollPane sp = new JScrollPane(c);
        sp.setBorder(BorderFactory.createLineBorder(VIEN));
        sp.getViewport().setBackground(TRANG);
        return sp;
    }

    // ================= THANH CHIA =================

    /** Thanh chia trong suốt (không viền xám) để các thẻ nhìn tách biệt. */
    public static void lamDongChiaTrong(JSplitPane sp, int kichThuoc) {
        sp.setUI(new BasicSplitPaneUI() {
            @Override
            public BasicSplitPaneDivider createDefaultDivider() {
                return new BasicSplitPaneDivider(this) {
                    @Override
                    public void paint(Graphics g) {
                        g.setColor(NEN);
                        g.fillRect(0, 0, getWidth(), getHeight());
                    }
                };
            }
        });
        sp.setBorder(null);
        sp.setDividerSize(kichThuoc);
        sp.setOpaque(false);
        sp.setContinuousLayout(true);
    }

    /** Đặt vị trí thanh chia theo tỷ lệ ngay lần đầu có kích thước. */
    public static void datTyLeLanDau(final JSplitPane sp, final double tyLe) {
        sp.addComponentListener(new ComponentAdapter() {
            private boolean xong = false;

            @Override
            public void componentResized(ComponentEvent e) {
                if (xong || sp.getWidth() <= 0 || sp.getHeight() <= 0) {
                    return;
                }
                xong = true;
                sp.setDividerLocation(tyLe);
                sp.removeComponentListener(this);
            }
        });
    }
}
