import javax.swing.*;
import javax.swing.border.Border;
import javax.xml.ws.soap.Addressing;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.*;
import java.util.ArrayList;
import java.nio.file.*;

public class Launcher extends JFrame{
    private static ArrayList<AdressBook> addressBooks;
    private static ArrayList<AdressBook> deletedBooks;
    private static AdressBook newBook;
    //private static EditContact editContact = new EditContact("AllBooks");
    private static JList<String> booksList;
    private static Boolean isModified;

    private Launcher() {
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        //Main Window
        setTitle("Address Books");
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeApp();
            }
        });
        //setSize(450, 450);
        setLocationRelativeTo(null);

        //Menu Bar
        JMenuBar menuBar = new JMenuBar();
        JMenu menuFile = new JMenu("File");
        menuBar.add(menuFile);

        JMenuItem itemNew = new JMenuItem("New");
        itemNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createNewBook();
            }
        });
        menuFile.add(itemNew);

        JMenuItem itemOpen = new JMenuItem("Open");
        menuFile.add(itemOpen);

        JMenuItem itemDelete = new JMenuItem("Delete");
        itemDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeBook();
            }
        });
        menuFile.add(itemDelete);

        menuFile.addSeparator();

        JMenuItem itemSave = new JMenuItem("Save");
        menuFile.add(itemSave);

        JMenuItem itemSaveAs = new JMenuItem("Save As ...");
        menuFile.add(itemSaveAs);

        menuFile.addSeparator();

        JMenuItem itemQuit = new JMenuItem("Quit");
        menuFile.add(itemQuit);

        setJMenuBar(menuBar);

        //List of all address books
        booksList = new JList<>();
        booksList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        //Panels
        JPanel mainPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        JScrollPane scrollPane = new JScrollPane(booksList);
        scrollPane.setPreferredSize(new Dimension(350, 340));

        //Buttons
        JButton buttonNew = new JButton("New");
        buttonNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createNewBook();
            }
        });
        JButton buttonOpen = new JButton("Open");
        buttonOpen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openBookView();
            }
        });
        JButton buttonRemove = new JButton("Remove");
        buttonRemove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeBook();
            }
        });


        //Layout
        Border border = BorderFactory.createEmptyBorder(5, 5, 5, 5);
        add(mainPanel);
        mainPanel.setBorder(border);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.PAGE_END);

        buttonPanel.add(buttonNew);
        buttonPanel.add(buttonOpen);
        buttonPanel.add(buttonRemove);

        isModified = false;

    }

    private void createNewBook() {
        boolean dup = false;
        String newBookName = "";
        newBookName = JOptionPane.showInputDialog(null, "Please input the name of the new book:");
        if (newBookName != null) {
            if (addressBooks.size() != 0) {
                for (AdressBook ab : addressBooks) {
                    if (ab.getBookName().equals(newBookName)) {
                        dup = true;
                    }
                }
            }
            if (dup) {
                JOptionPane.showMessageDialog(this, "An address book with this name exists. Please try again.");
                createNewBook();
            } else if (newBookName.equals("")) {
                JOptionPane.showMessageDialog(this, "The name of address book cannot be empty. Please try again.");
                createNewBook();
            } else {
                newBook = new AdressBook(0, newBookName);
                addressBooks.add(newBook);
                ConnectDB.createNewTable(newBookName);
            }
        }
        isModified = true;
        updateBooksList();
    }

    private void removeBook() {
        int selection = JOptionPane.showConfirmDialog(null,
                "Confirm to remove this adress book",
                "Remove Book",
                JOptionPane.YES_NO_CANCEL_OPTION);
        if (selection == 0) {
            deletedBooks.add(addressBooks.get(booksList.getSelectedIndex()));
            addressBooks.remove(booksList.getSelectedIndex());
        }
        isModified = true;
        updateBooksList();
    }

    public static void getAllBooksFromDB() {
        String sql = "SELECT * FROM BookList";
        addressBooks = new ArrayList<>();
        try (
                Statement stmt  = DriverManager.getConnection("jdbc:sqlite:./AllBooks.db").createStatement();
                ResultSet rs    = stmt.executeQuery(sql)){

            // loop through the result set
            while (rs.next()) {
                newBook = new AdressBook(rs.getInt("id"), rs.getString("BookName"));
                addressBooks.add(newBook);
            }
            if (addressBooks.size() != 0) {
                ArrayList<String> tempList = new ArrayList<>();
                for (AdressBook ab : addressBooks) {
                    tempList.add(ab.getBookName());
                }
                booksList.setListData(tempList.toArray(new String[tempList.size()]));
                booksList.setSelectedIndex(0);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void openBookView() {
        String bookName = addressBooks.get(booksList.getSelectedIndex()).getBookName();
        ConnectDB.createNewTable(bookName);

        JFrame frame = new Book(bookName);
        frame.setVisible(true);
        frame.setPreferredSize(new Dimension(450, 450));
        frame.pack();
    }

    private static void updateBooksList() {
        ArrayList<String> tempList = new ArrayList<>();
        for (AdressBook ab: addressBooks) {
            tempList.add(ab.getBookName());
        }
        booksList.setListData(tempList.toArray(new String[tempList.size()]));
        booksList.setSelectedIndex(0);
    }

    public void closeApp() {
        if (isModified) {
            int choice = JOptionPane.showConfirmDialog(null, "Save the change?", "Save Change",
                    JOptionPane.YES_NO_CANCEL_OPTION);
            if (choice == 0) {
                saveBooksList();
                dispose();
            } else if (choice == 1) {
                deleteFile();
                dispose();
            }
        } else {
            dispose();
        }
    }

    public void deleteFile() {
        File file;
        for (AdressBook ab:addressBooks) {
            if (ab.getId() == 0) {
                /*
                try (Connection conn = DriverManager.getConnection("jdbc:sqlite:./" + ab.getBookName() + ".db");
                     Statement stmt = conn.createStatement()) {
                    if (conn != null) {
                        stmt.close();
                        conn.close();
                    }
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
                */
                try {
                    String p = "./"+ ab.getBookName()+".db";
                    Files.delete(Paths.get(p));
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        for (AdressBook db:deletedBooks) {
            /*(
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:./" + db.getBookName() + ".db");
                 Statement stmt = conn.createStatement()) {
                if (conn != null) {
                    stmt.close();
                    conn.close();
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
            */
            try {
                String p = "./"+ db.getBookName()+".db";
                Files.delete(Paths.get(p));
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void saveBooksList() {
        for (AdressBook ab:addressBooks) {
            if (ab.getId() == 0) {
                String sql = "INSERT INTO BookList(BookName) VALUES(?)";
                try (
                        Connection conn = DriverManager.getConnection("jdbc:sqlite:./AllBooks.db");
                        PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, ab.getBookName());
                    pstmt.executeUpdate();
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        for (AdressBook db:deletedBooks) {
            if (db.getId() != 0) {
                String sql = "DELETE FROM BookList WHERE id = ?";

                try (
                        PreparedStatement pstmt = DriverManager.getConnection("jdbc:sqlite:./AllBooks.db").prepareStatement(sql)) {

                    // set the corresponding param
                    pstmt.setInt(1, db.getId());
                    // execute the delete statement
                    pstmt.executeUpdate();

                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }

            }
            /*
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:./" + db.getBookName() + ".db");
                 Statement stmt = conn.createStatement()) {
                if (conn != null) {
                    stmt.close();
                    conn.close();
                }
            } catch (SQLException e) {
                System.out.println("11");
                System.out.println(e.getMessage());
            }
            */
            try {
                String p = "./"+ db.getBookName()+".db";
                Files.delete(Paths.get(p));
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

        }

        getRootPane().putClientProperty("Window.documentModified", Boolean.FALSE);
    }

    public static void main(String[] args) {
        addressBooks = new ArrayList<>();
        deletedBooks = new ArrayList<>();
        JFrame frame = new Launcher();

        //File f = new File("./AllBooks.db");
        if (Files.exists(Paths.get("./AllBooks.db"))) {
            getAllBooksFromDB();
        } else {
            ConnectDB.createBookList();
        }

        frame.setPreferredSize(new Dimension(450, 460));
        frame.setVisible(true);
        frame.pack();
        updateBooksList();
    }
}
