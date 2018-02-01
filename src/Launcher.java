import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.*;
import java.util.ArrayList;
import java.nio.file.*;

public class Launcher extends JFrame{
    public static ArrayList<AdressBook> addressBooks;
    private static ArrayList<AdressBook> deletedBooks;
    private static ArrayList<String> openedBooks;
    public static AdressBook newBook;
    private static JList<String> booksList;
    private static Boolean isModified;
    public static ArrayList<Book> openBooks;
    private TSV tsv;
//Constructor
    private Launcher() {
        openBooks = new ArrayList<>();

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
        itemOpen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openBookView();
            }
        });
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
        itemSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveBooksList();
                isModified = false;
                getAllBooksFromDB();
                deletedBooks.clear();
            }
        });
        menuFile.add(itemSave);
        /*
        JMenuItem itemSaveAs = new JMenuItem("Save As ...");
        menuFile.add(itemSaveAs);
        */

        JMenuItem itemImport = new JMenuItem("Import");
        itemImport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                importAction();
            }
        });
        menuFile.add(itemImport);

        JMenuItem itemExport = new JMenuItem("Export");
        itemExport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportAction();
            }
        });
        menuFile.add(itemExport);

        menuFile.addSeparator();

        JMenuItem itemQuit = new JMenuItem("Quit");
        itemQuit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeApp();
            }
        });
        menuFile.add(itemQuit);

        setJMenuBar(menuBar);

        //List of all address books
        booksList = new JList<>();
        booksList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //DefaultListModel<String> model = (DefaultListModel<String>) booksList.getModel();

        JPanel searchPanel = new JPanel();
        JTextField searchField = new JTextField(20);
        JLabel searchLabel = new JLabel("Search:");
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                //model.clear();
                ArrayList<String> tempList = new ArrayList<>();
                String s = searchField.getText();
                for (AdressBook ab : addressBooks) {
                    if (ab.getBookName().contains(s)) {
                        //model.addElement(ab.getBookName());
                        tempList.add(ab.getBookName());
                    }
                }
                booksList.setListData(tempList.toArray(new String[tempList.size()]));
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                ArrayList<String> tempList = new ArrayList<>();
                String s = searchField.getText();
                for (AdressBook ab : addressBooks) {
                    if (ab.getBookName().contains(s)) {
                        //model.addElement(ab.getBookName());
                        tempList.add(ab.getBookName());
                    }
                }
                booksList.setListData(tempList.toArray(new String[tempList.size()]));
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                ArrayList<String> tempList = new ArrayList<>();
                String s = searchField.getText();
                for (AdressBook ab : addressBooks) {
                    if (ab.getBookName().contains(s)) {
                        //model.addElement(ab.getBookName());
                        tempList.add(ab.getBookName());
                    }
                }
                booksList.setListData(tempList.toArray(new String[tempList.size()]));
            }
        });


        if (!Files.exists(Paths.get("./AllBooks.db"))) {
            ConnectDB.createBookList();
        } else {
            getAllBooksFromDB();
        }

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
        mainPanel.add(searchPanel, BorderLayout.PAGE_START);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.PAGE_END);

        buttonPanel.add(buttonNew);
        buttonPanel.add(buttonOpen);
        buttonPanel.add(buttonRemove);

        isModified = false;

    }
// call export function from tsv class
    private void exportAction() {
        if (!booksList.isSelectionEmpty()) {
            if (!Files.exists(Paths.get("./tsv"))) {
                try {
                	//create file
                    Files.createDirectories(Paths.get("./tsv"));
                } catch (IOException e1) {
                    System.out.println(e1);
                }
            }
            tsv = new TSV(this, booksList.getSelectedValue());
            tsv.exportTSV();
            JOptionPane.showMessageDialog(null, "Export success. The tsv file is in /tsv directory");
        }
    }
 // call export function from tsv class

    private void importAction() {
        JFileChooser chooser = new JFileChooser();
        //find the file
        FileNameExtensionFilter filter = new FileNameExtensionFilter(".tsv", "tsv");
        chooser.setFileFilter(filter);
        int selection = chooser.showOpenDialog(null);
        if (selection == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            tsv = new TSV(this, null);
            //import
            tsv.importTSV(path);
        }
        isModified = true;
        updateBooksList();
    }

    public void createNewBook() {
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
            //check duplicate book name
            if (dup) {
                JOptionPane.showMessageDialog(this, "An address book with this name exists. Please try again.");
                createNewBook();
            } // check empty book name
            else if (newBookName.equals("")) {
                JOptionPane.showMessageDialog(this, "The name of address book cannot be empty. Please try again.");
                createNewBook();
            } else {
                newBook = new AdressBook(0, newBookName);
                addressBooks.add(newBook);
                //create book in db
                ConnectDB.createNewTable(newBookName);
            }
        }
        isModified = true;
        updateBooksList();
    }

    public String getNewBookName() {
        return addressBooks.get(addressBooks.size()-1).getBookName();
    }

    private void removeBook() {
    	// make confirmation
        int selection = JOptionPane.showConfirmDialog(null,
                "Confirm to remove this adress book",
                "Remove Book",
                JOptionPane.YES_NO_CANCEL_OPTION);
        if (selection == 0) {
        	//select book
            deletedBooks.add(addressBooks.get(booksList.getSelectedIndex()));
            //delete selected book
            addressBooks.remove(booksList.getSelectedIndex());
        }
        isModified = true;
        updateBooksList();
    }

    public void modifyAddressBooks(String oldName, String newName) {
        for (int i = 0; i < addressBooks.size(); i++) {
        	// rename book
            if (addressBooks.get(i).getBookName().equals(oldName)) {
                deletedBooks.add(addressBooks.get(i));
                addressBooks.remove(i);
            }
        }
        saveBooksList();
        isModified = false;
        updateBooksList();
    }

    public static void getAllBooksFromDB() {
    	//import all db form db
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
                //push all data into arraylist
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
        if (!booksList.isSelectionEmpty()) {
            boolean opened = false;
            String bookName = booksList.getModel().getElementAt(booksList.getSelectedIndex());
            //String bookName = addressBooks.get(booksList.getSelectedIndex()).getBookName();
            //ConnectDB.createNewTable(bookName);
            for (Book b : openBooks) {
                if (b.bookName.equals(bookName)) {
                    opened = true;
                }
            }
            if (!opened) {
                Book frame = new Book(this, bookName);
                openBooks.add(frame);
                frame.setPreferredSize(new Dimension(450, 530));
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                frame.pack();
            } else {
                JOptionPane.showMessageDialog(null, "Selected book has opened");
            }
        }
    }

    private static void updateBooksList() {
        ArrayList<String> tempList = new ArrayList<>();
        for (AdressBook ab: addressBooks) {
        	//add all changes and update db
            tempList.add(ab.getBookName());
        }
        booksList.setListData(tempList.toArray(new String[tempList.size()]));
        booksList.setSelectedIndex(0);
    }

    public void closeApp() {
    	//close directly if user do not open any book
        if (!openBooks.isEmpty()) {
            int size = openBooks.size();
            for (int i = 0; i < size; i++) {
                openBooks.get(i).closeBook();
            }
        }
        if (isModified) {
        	//give save option if user modified any data
            int choice = JOptionPane.showConfirmDialog(null, "Save the change?", "Save Change",
                    JOptionPane.YES_NO_CANCEL_OPTION);
            if (choice == 0) {
            	//save
                saveBooksList();
                dispose();
            } else if (choice == 1) {
            	//unsave
                deleteFile();
                dispose();
            }
        } else {
            dispose();
        }
    }

    public void deleteFile() {
    	// sql if user choose to unsave
        for (AdressBook ab:addressBooks) {
            if (ab.getId() == 0) {
                try {
                    String p = "./"+ ab.getBookName()+".db";
                    Files.delete(Paths.get(p));
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        for (AdressBook db:deletedBooks) {
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
            	//sql if user choose to save
                String sql = "INSERT INTO BookList(BookName) VALUES(?)";
                try (
                		//connect db
                        Connection conn = DriverManager.getConnection("jdbc:sqlite:./AllBooks.db");
                        PreparedStatement pstmt = conn.prepareStatement(sql)) {
                	//push book data into booklist db
                    pstmt.setString(1, ab.getBookName());
                    pstmt.executeUpdate();
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        // sql delete the original book
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

        frame.setPreferredSize(new Dimension(450, 500));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        updateBooksList();
    }
}
