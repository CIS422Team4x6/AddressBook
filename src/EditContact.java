import java.sql.*;


public class EditContact {

    public static Connection conn;

    public EditContact(String fileName) {
        // SQLite connection string
        String url = "jdbc:sqlite:" + fileName;
        conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public Connection getConn() {
        return conn;
    }

    public static void InsertData(String name, String email, String street, String city, String state, String zip, String note) {
        String sql = "INSERT INTO AddressBook(name,email,street,city,state,zip,note) VALUES(?,?,?,?,?,?,?)";

        try (
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, street);
            pstmt.setString(4, city);
            pstmt.setString(5, state);
            pstmt.setString(6, zip);
            pstmt.setString(7, note);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    public static void DeleteData(int id) {
        String sql = "DELETE FROM AddressBook WHERE id = ?";

        try (
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the corresponding param
            pstmt.setInt(1, id);
            // execute the delete statement
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /*

    void InsertData(String name, String email, String street, String city, String state, int zip) {
        String sql = "INSERT INTO AddressBook(name,email,street,city,state,zip) VALUES(?,?,?,?,?,?)";

        try (
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, street);
            pstmt.setString(4, city);
            pstmt.setString(5, state);
            pstmt.setInt(6, zip);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    void EditData(){

    }

    void DeleteData(int id) {
        String sql = "DELETE FROM AddressBook WHERE id = ?";

        try (
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the corresponding param
            pstmt.setInt(1, id);
            // execute the delete statement
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    void SortbyName() {

    }

    void SortbyZip() {

    }

    void SaveasFile() {

    }

    public void SelectAll(){
        String sql = "SELECT id, name, email,street,city,state,zip FROM AddressBook";

        try (
                Statement stmt  = conn.createStatement();
                ResultSet rs    = stmt.executeQuery(sql)){

            // loop through the result set
            while (rs.next()) {
                System.out.println(rs.getInt("id") +  "\t" +
                        rs.getString("name") + "\t" +
                        rs.getString("email") + "\t" +
                        rs.getString("street") + "\t" +
                        rs.getString("city") + "\t" +
                        rs.getString("state") + "\t" +
                        rs.getInt("zip"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    */
}
