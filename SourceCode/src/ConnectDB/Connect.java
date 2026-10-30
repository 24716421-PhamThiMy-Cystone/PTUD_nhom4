package ConnectDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connect {
	private static final Connect INSTANCE = new Connect();
    private static Connection con;

    private Connect() {}

    public static Connect getInstance() {
        return INSTANCE;
    }

    // DAO tự đóng kết nối nên mỗi thao tác cần một Connection riêng.
    public static Connection moKetNoiMoi() throws SQLException {
        String url = "jdbc:sqlserver://localhost:1433;databaseName=QL_SieuThiDienMay;encrypt=false;trustServerCertificate=true";
        String user = "sa";
        String password = "123456";

        return DriverManager.getConnection(url, user, password);
    }

    public static void connect() {
        try {
            con = moKetNoiMoi();
            System.out.println("Ket noi thanh cong!");
        } catch (SQLException e) {
            con = null;
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        try {
            if (con == null || con.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con;
    }

    public void disconnect() {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
