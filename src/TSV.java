import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class TSV {
    private Launcher launcher;
    private String bookName;
    private static ArrayList<Contact> book;
    private static EditDB editContact;
    private static Contact newContact;
// Constructor
    public TSV(Launcher launcher, String bookName) {
        this.launcher = launcher;
        if (bookName != null) {
            this.bookName = bookName;
            editContact = new EditDB(this.bookName);
            getContactsFromDB();
        } else {
            book = new ArrayList<>();
        }
    }

    public void exportTSV() {
    	//initial empty string
        String temp;
        if (!Files.exists(Paths.get("./tsv/" + bookName + ".tsv"))) {
            try {
            	//file path
                Files.createFile(Paths.get("./tsv/" + bookName + ".tsv"));
                //get into file
                File file = Paths.get("./tsv/" + bookName + ".tsv").toFile();
                //column space
                String columns = "CITY" + "\tSTATE" + "\tZIP" + "\tDelivery" + "\tSecond" +
                        "\tLastName" + "\tFirstName" + "\tPhone" + "\n";
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                writer.append(columns);
                // export data into each column
                for  (Contact c : book) {
                    temp = c.getCity() + "\t" + c.getState() + "\t" + c.getZip() + "\t"
                            + c.getStreet() + "\t" + c.getSecond() + "\t" + c.getLname() + "\t"
                            + c.getFname() + "\t" + c.getPhone() + "\n";
                    writer.append(temp);
                }
                writer.close();
            } catch (IOException e1) {
                System.out.println(e1);
            }
        } else {
            try {
                Files.delete(Paths.get("./tsv/" + bookName + ".tsv"));
                exportTSV();
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    public void importTSV(String path) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            //get file path
            bookName = path.substring((path.lastIndexOf("/") + 1), (path.lastIndexOf(".")));
            // check legality of each column
            if (reader.readLine().equals("CITY" + "\tSTATE" + "\tZIP" + "\tDelivery" + "\tSecond" +
                    "\tLastName" + "\tFirstName" + "\tPhone")) {
                String[] info;
                String line;
                while ((line = reader.readLine()) != null) {
                    // read each column info
                	info = line.split("\\t");
                    if (info.length == 8) {
                    	//import all data into contact
                        newContact = new Contact(0, info[6], info[5], "", info[3], info[4],
                                info[0], info[1], info[2], "", info[7], "");
                        book.add(newContact);
                    } else {
                        JOptionPane.showMessageDialog(null, "The format of the contact is not match. Import failed");
                        break;
                    }
                }
                boolean dup = false;
                if (launcher.addressBooks.size() != 0) {
                    for (AdressBook ab : launcher.addressBooks) {
                        if (ab.getBookName().equals(bookName)) {
                            dup = true;
                        }
                    }
                }
                if (dup) {
                	//check duplicated bookid
                    JOptionPane.showMessageDialog(null, "An address book with this name exists. Import failed");
                } else {
                	//import data into database
                    AdressBook newBook = new AdressBook(0, bookName);
                    launcher.addressBooks.add(newBook);
                    ConnectDB.createNewTable(bookName);
                }
                Book addressbook = new Book(launcher, bookName);
                addressbook.setAddressBook(book);
                addressbook.saveBook();
            } else {
                JOptionPane.showMessageDialog(null, "The format is not match. Import failed");
            }
        } catch (FileNotFoundException e1) {
            System.out.println(e1);
        } catch (IOException e2) {
            System.out.println(e2);
        }
    }

    private static void getContactsFromDB() {
    	// database reader
        String sql = "SELECT * FROM AddressBook";
        book = new ArrayList<>();
        try (
                Statement stmt  = editContact.getConn().createStatement();
                ResultSet rs    = stmt.executeQuery(sql)){

            // loop through the result set
            while (rs.next()) {
            	//read db into arrarlist
                newContact = new Contact(rs.getInt("id"),
                        rs.getString("fname"), rs.getString("lname"),
                        rs.getString("email"), rs.getString("street"),
                        rs.getString("second"), rs.getString("city"),
                        rs.getString("state"), rs.getString("zip"),
                        rs.getString("note"), rs.getString("phone"),
                        rs.getString("link"));
                book.add(newContact);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
