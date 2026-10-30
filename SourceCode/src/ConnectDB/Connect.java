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

    public static void connect() {
        String url = "jdbc:sqlserver://localhost:1433;databaseName=QL_SieuThiDienMay;encrypt=false;trustServerCertificate=true";
        String user = "sa";
        String password = "sapassword";

        try {
            con = DriverManager.getConnection(url, user, password);
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
