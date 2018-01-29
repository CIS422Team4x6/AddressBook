import java.sql.*;


public class EditDB {

    public static Connection conn;

    public EditDB(String fileName) {
        // SQLite connection string
        String url = "jdbc:sqlite:./" + fileName + ".db";
        //System.out.println(url);
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

    public void InsertData(String fname, String lname, String email, String street, String second,
                           String city, String state, String zip, String note, String phone, String link) {
        String sql = "INSERT INTO AddressBook(fname,lname,email,street,second,city,state,zip,note,phone,link) VALUES(?,?,?,?,?,?,?,?,?,?,?)";

        try (
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, fname);
            pstmt.setString(2, lname);
            pstmt.setString(3, email);
            pstmt.setString(4, street);
            pstmt.setString(5, second);
            pstmt.setString(6, city);
            pstmt.setString(7, state);
            pstmt.setString(8, zip);
            pstmt.setString(9, note);
            pstmt.setString(10, phone);
            pstmt.setString(11,link);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    public void DeleteData(int id) {
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

    void EditData(int id, String fname, String lname, String email, String street, String second,
                  String city, String state, String zip, String note, String phone, String link){
        String sql = "UPDATE AddressBook SET fname = ?,  "
                + "lname = ?, "
                + "email = ?,  "
                + "street = ?, "
                + "second = ?, "
                + "city = ?,  "
                + "state = ?,   "
                + "zip = ?, "
                + "note = ?,"
                + "phone = ?,"
                + "link = ?"
                + "WHERE id = ?";

        try (
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the corresponding param
            pstmt.setString(1, fname);
            pstmt.setString(2, lname);
            pstmt.setString(3, email);
            pstmt.setString(4, street);
            pstmt.setString(5, second);
            pstmt.setString(6, city);
            pstmt.setString(7, state);
            pstmt.setString(8, zip);
            pstmt.setString(9, note);
            pstmt.setString(10, phone);
            pstmt.setString(11, link);
            pstmt.setInt(12, id);
            // update
            pstmt.executeUpdate();
            //System.out.println("success");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}
