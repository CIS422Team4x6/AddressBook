import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectDB {
    public static void connect() {
        Connection conn = null;
        try {
            // db parameters
            String url = "jdbc:sqlite:./test1.db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);

            System.out.println("Connection to SQLite has been established.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }

    }
    public static void createNewTable(String fileName) {
        // SQLite connection string
        String url = "jdbc:sqlite:./" + fileName + ".db";

        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS AddressBook (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	fname text,\n"
                + " lname text NOT NULL,\n"
                + "	email text,\n"
                + " phone text,\n"
                + "  street text,\n"
                + "  second text,\n"
                + "  city text,\n"
                + "  state text,\n"
                + "  zip integer,\n"
                + "  note text"
                + ");";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void createBookList() {
        // SQLite connection string
        String url = "jdbc:sqlite:./AllBooks.db";

        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS BookList (\n"
                + "	id integer PRIMARY KEY,\n"
                + " BookName text NOT NULL"
                + ");";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    /*
    public static void main(String[] args) {
        connect();
        createNewTable();
    }
    */
}
