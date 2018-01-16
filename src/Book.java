import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

public class Book extends JFrame{
    private JTabbedPane tabbedPane;
    private JTextField nameField;
    private JTextField addressField;
    private JTextField cityField;
    private JTextField stateField;
    private JTextField zipField;
    private JTextField phoneField;
    private JTextArea noteArea;

    private static JList<String> contactsList;
    private static EditContact editContact = new EditContact("test.db");

    private Book() {
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        //Main Window
        setTitle("Address Book");
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(450, 450);
        setLocationRelativeTo(null);

        //Menu Bar
        JMenuBar menuBar = new JMenuBar();
        JMenu menuFile = new JMenu("File");
        menuBar.add(menuFile);

        JMenuItem itemNew = new JMenuItem("New");
        //need action listener here
        menuFile.add(itemNew);

        JMenuItem itemOpen = new JMenuItem("Open");
        menuFile.add(itemOpen);

        JMenuItem itemClose = new JMenuItem("Close");
        menuFile.add(itemClose);

        menuFile.addSeparator();

        JMenuItem itemSave = new JMenuItem("Save");
        menuFile.add(itemSave);

        JMenuItem itemSaveAs = new JMenuItem("Save As ...");
        menuFile.add(itemSaveAs);

        menuFile.addSeparator();

        JMenuItem itemQuit = new JMenuItem("Quit");
        menuFile.add(itemQuit);

        setJMenuBar(menuBar);

        //List of all contacts
        contactsList = new JList<>();
        contactsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        //Tabbed Pane: One is "All Contacts", the other one is "Contact"
        tabbedPane = new JTabbedPane();

        //"All Contacts" Panels
        JPanel mainPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        JScrollPane scrollPane = new JScrollPane(contactsList);
        scrollPane.setPreferredSize(new Dimension(360, 270));

        //"All Contacts" Buttons
        JButton buttonNew = new JButton("New");
        buttonNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tabbedPane.setEnabledAt(0, false);
                newContact();
            }
        });
        JButton buttonOpen = new JButton("Open");
        JButton buttonRemove = new JButton("Remove");
        buttonRemove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeContact();
            }
        });

        //"Contact" Panels
        JPanel contactPanel = new JPanel();
        //JPanel contactButtonPanel = new JPanel();
        JPanel informationPanel = new JPanel();

        //"Contact" Labels
        JLabel nameLabel = new JLabel("Name:");
        JLabel addressLabel = new JLabel("Address:");
        JLabel cityLabel = new JLabel("City:");
        JLabel stateLabel = new JLabel("State:");
        JLabel zipLabel = new JLabel("Zip:");
        JLabel phoneLabel = new JLabel("Phone:");
        JLabel noteLabel = new JLabel("Note:");

        //"Contact" Text Fields
        nameField = new JTextField(15);
        addressField = new JTextField(15);
        cityField = new JTextField(15);
        stateField = new JTextField(15);
        zipField = new JTextField(15);
        phoneField = new JTextField(15);
        noteArea = new JTextArea(10, 20);
        JScrollPane notePane = new JScrollPane(noteArea);
        noteArea.setLineWrap(true);
        noteArea.setWrapStyleWord(true);
        notePane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        notePane.setPreferredSize(new Dimension(10, 20));

        //"Contact" Buttons
        JButton buttonCancel = new JButton("Cancel");
        buttonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelEdit();
            }
        });
        JButton buttonDone = new JButton("Done");
        buttonDone.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveContact();
            }
        });

        //Layouts
        add(tabbedPane);
        tabbedPane.addTab("All Contacts", mainPanel);
        tabbedPane.addTab("Contact", contactPanel);

        Border border = BorderFactory.createEmptyBorder(0, 10, 10, 10);

        //"All Contacts" pane layout
        mainPanel.setBorder(border);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.PAGE_END);
        buttonPanel.add(buttonNew);
        buttonPanel.add(buttonOpen);
        buttonPanel.add(buttonRemove);

        //"Contact" pane layout
        contactPanel.add(informationPanel, BorderLayout.CENTER);
        //contactPanel.add(contactButtonPanel, BorderLayout.PAGE_END);

        GridLayout informationPanelLayout = new GridLayout(8, 2);

        informationPanel.setLayout(informationPanelLayout);
        informationPanel.setBorder(border);
        informationPanel.add(nameLabel);
        informationPanel.add(nameField);
        informationPanel.add(addressLabel);
        informationPanel.add(addressField);
        informationPanel.add(cityLabel);
        informationPanel.add(cityField);
        informationPanel.add(stateLabel);
        informationPanel.add(stateField);
        informationPanel.add(zipLabel);
        informationPanel.add(zipField);
        informationPanel.add(phoneLabel);
        informationPanel.add(phoneField);
        informationPanel.add(noteLabel);
        informationPanel.add(notePane);
        informationPanel.add(buttonCancel);
        informationPanel.add(buttonDone);
    }

    private void newContact() {
        clearFields();
        tabbedPane.setSelectedIndex(1);
    }

    private void cancelEdit() {
        tabbedPane.setEnabledAt(0, true);
        tabbedPane.setSelectedIndex(0);
        clearFields();
    }
    private void saveContact() {
        String name = nameField.getText();
        String address = addressField.getText();
        String city = addressField.getText();
        String state = stateField.getText();
        String zip = zipField.getText();
        String phone = phoneField.getText();
        String note = noteArea.getText();

        InsertData(name, phone, address, city, state, zip, note);

        clearFields();
        tabbedPane.setSelectedIndex(0);
        updateContactsList();
    }

    private void removeContact() {
        int selection = JOptionPane.showConfirmDialog(null,
                "Confirm to remove this contact",
                "Remove Contact",
                JOptionPane.YES_NO_CANCEL_OPTION);
        if (selection == 0) {
            ArrayList<Integer> list = new ArrayList<>();
            String sql = "SELECT id FROM AddressBook";
            try (
                    Statement idstmt  = editContact.conn.createStatement();
                    ResultSet idrs = idstmt.executeQuery(sql)){

                // loop through the result set
                while (idrs.next()) {
                    list.add(idrs.getInt("id"));
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
            Integer[] idset = list.toArray(new Integer[list.size()]);
            DeleteData(idset[contactsList.getSelectedIndex()]);
        }
        updateContactsList();
    }

    private void clearFields() {
        nameField.setText("");
        addressField.setText("");
        cityField.setText("");
        stateField.setText("");
        zipField.setText("");
        phoneField.setText("");
        noteArea.setText("");
    }

    private static void updateContactsList() {
        ArrayList<String> list = new ArrayList<>();
        String sql = "SELECT name FROM AddressBook";
        try (
                Statement stmt  = editContact.conn.createStatement();
                ResultSet rs    = stmt.executeQuery(sql)){

            // loop through the result set
            while (rs.next()) {
                list.add(rs.getString("name"));
            }
            contactsList.setListData(list.toArray(new String[list.size()]));
            contactsList.setSelectedIndex(0);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void InsertData(String name, String email, String street, String city, String state, String zip, String note) {
        String sql = "INSERT INTO AddressBook(name,email,street,city,state,zip,note) VALUES(?,?,?,?,?,?,?)";

        try (
                PreparedStatement pstmt = editContact.conn.prepareStatement(sql)) {
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

    public void DeleteData(int id) {
        String sql = "DELETE FROM AddressBook WHERE id = ?";

        try (
                PreparedStatement pstmt = editContact.conn.prepareStatement(sql)) {

            // set the corresponding param
            pstmt.setInt(1, id);
            // execute the delete statement
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        ConnectDB.connect();
        ConnectDB.createNewTable();

        JFrame frame = new Book();
        frame.setVisible(true);
        frame.pack();
        updateContactsList();
    }
}
