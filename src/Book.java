import sun.plugin2.ipc.windows.WindowsEvent;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Book extends JFrame{
    private static ArrayList<Contact> addressBook;
    private static ArrayList<Contact> deletedContact;
    private static Contact newContact;
    private static Contact tempContact;

    private Boolean isCreatingContact = true;
    private static Boolean isModified;

    private JTabbedPane tabbedPane;
    private JTextField fnameField;
    private JTextField lnameField;
    private JTextField addressField;
    private JTextField secondField;
    private JTextField cityField;
    private JTextField stateField;
    private JTextField zipField;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextArea noteArea;
    private JComboBox<String> sortDropDown;
    private String bookName;

    private static JList<String> contactsList;
    private static EditContact editContact;

    public Book(String bookName) {
        editContact = new EditContact(bookName);

        System.setProperty("apple.laf.useScreenMenuBar", "true");

        //Main Window
        //setTitle("Address Book");
        setTitle(bookName);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeBook();
                //dispose();
            }
        });
        //setSize(450, 600);
        setLocationRelativeTo(null);

        //Menu Bar
        JMenuBar menuBar = new JMenuBar();
        JMenu menuFile = new JMenu("File");
        menuBar.add(menuFile);

        JMenuItem itemNew = new JMenuItem("New");
        itemNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tabbedPane.setEnabledAt(0, false);
                newContact();
            }
        });
        menuFile.add(itemNew);

        JMenuItem itemOpen = new JMenuItem("Open");
        itemOpen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openContact();
            }
        });
        menuFile.add(itemOpen);

        JMenuItem itemClose = new JMenuItem("Close");
        itemClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeBook();
            }
        });
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
        JPanel sortPanel = new JPanel();
        JScrollPane scrollPane = new JScrollPane(contactsList);
        scrollPane.setPreferredSize(new Dimension(360, 270));

        //drop-down
        String[] sortBy = {"Last Name", "ZIP"};
        sortDropDown = new JComboBox<>(sortBy);
        sortDropDown.setSelectedIndex(0);
        sortDropDown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (sortDropDown.getSelectedIndex() == 0) {
                    sortByLname();
                } else {
                    sortByZip();
                }
                updateContactsList();
            }
        });
        //drop-down label
        JLabel sortByLabel = new JLabel("Sort by:");

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
        buttonOpen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openContact();
            }
        });
        JButton buttonRemove = new JButton("Remove");
        buttonRemove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeContact();
            }
        });

        //"Contact" Panels
        JPanel contactPanel = new JPanel();
        JPanel fnamePanel = new JPanel();
        JPanel lnamePanel = new JPanel();
        //JPanel contactButtonPanel = new JPanel();
        JPanel informationPanel = new JPanel();

        //"Contact" Labels
        JLabel fnameLabel = new JLabel("First Name:");
        JLabel lnameLabel = new JLabel("Last Name:");
        JLabel addressLabel = new JLabel("Address:");
        JLabel secondLabel = new JLabel("Second:");
        JLabel cityLabel = new JLabel("City:");
        JLabel stateLabel = new JLabel("State:");
        JLabel zipLabel = new JLabel("Zip:");
        JLabel phoneLabel = new JLabel("Phone:");
        JLabel emailLabel = new JLabel("Email:");
        JLabel noteLabel = new JLabel("Note:");

        //"Contact" Text Fields
        fnameField = new JTextField(9);
        lnameField = new JTextField(9);
        addressField = new JTextField(15);
        secondField = new JTextField(15);
        cityField = new JTextField(15);
        stateField = new JTextField(15);
        zipField = new JTextField(15);
        phoneField = new JTextField(15);
        emailField = new JTextField(15);
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
        mainPanel.add(sortPanel, BorderLayout.PAGE_START);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.PAGE_END);
        buttonPanel.add(buttonNew);
        buttonPanel.add(buttonOpen);
        buttonPanel.add(buttonRemove);
        sortPanel.add(sortByLabel);
        sortPanel.add(sortDropDown);

        //"Contact" pane layout
        Border contactBorder = BorderFactory.createEmptyBorder(0, -5, 0, 0);
        contactPanel.add(informationPanel, BorderLayout.CENTER);
        fnamePanel.setBorder(contactBorder);
        fnamePanel.add(fnameLabel, BorderLayout.LINE_START);
        fnamePanel.add(fnameField);
        lnamePanel.add(lnameLabel);
        lnamePanel.add(lnameField);
        //contactPanel.add(contactButtonPanel, BorderLayout.PAGE_END);

        GridLayout informationPanelLayout = new GridLayout(10, 2);

        informationPanel.setLayout(informationPanelLayout);
        informationPanel.setBorder(border);
        informationPanel.add(fnamePanel);
        informationPanel.add(lnamePanel);
        informationPanel.add(addressLabel);
        informationPanel.add(addressField);
        informationPanel.add(secondLabel);
        informationPanel.add(secondField);
        informationPanel.add(cityLabel);
        informationPanel.add(cityField);
        informationPanel.add(stateLabel);
        informationPanel.add(stateField);
        informationPanel.add(zipLabel);
        informationPanel.add(zipField);
        informationPanel.add(phoneLabel);
        informationPanel.add(phoneField);
        informationPanel.add(emailLabel);
        informationPanel.add(emailField);
        informationPanel.add(noteLabel);
        informationPanel.add(notePane);
        informationPanel.add(buttonCancel);
        informationPanel.add(buttonDone);

        isModified = false;

        getContactsFromDB();
        deletedContact = new ArrayList<>();
        sortByLname();
        updateContactsList();
    }

    private void newContact() {
        clearFields();
        tabbedPane.setSelectedIndex(1);
        isCreatingContact = true;
    }

    private void openContact() {
        clearFields();
        tabbedPane.setSelectedIndex(1);
        isCreatingContact = false;
        tempContact = addressBook.get(contactsList.getSelectedIndex());
        fnameField.setText(tempContact.getFname());
        lnameField.setText(tempContact.getLname());
        addressField.setText(tempContact.getStreet());
        secondField.setText(tempContact.getSecond());
        cityField.setText(tempContact.getCity());
        stateField.setText(tempContact.getState());
        zipField.setText(tempContact.getZip());
        emailField.setText(tempContact.getEmail());
        phoneField.setText(tempContact.getPhone());
        noteArea.setText(tempContact.getNote());
    }

    private void cancelEdit() {
        tabbedPane.setEnabledAt(0, true);
        tabbedPane.setSelectedIndex(0);
        clearFields();
    }

    private void saveContact() {
        if (isCreatingContact) {
            String fname = fnameField.getText();
            String lname = lnameField.getText();
            String address = addressField.getText();
            String second = secondField.getText();
            String city = cityField.getText();
            String state = stateField.getText();
            String zip = zipField.getText();
            String phone = phoneField.getText();
            String note = noteArea.getText();
            String email = emailField.getText();

            newContact = new Contact(0, fname, lname, email, address, second, city, state, zip, note, phone);
            addressBook.add(newContact);

            //EditContact.InsertData(fname, lname, phone, address, second, city, state, zip, note);
        } else {
            //Contact tempContact = addressBook.get(contactsList.getSelectedIndex());
            if (!(tempContact.getFname() == fnameField.getText() && tempContact.getLname() == lnameField.getText()
                    && tempContact.getStreet() == addressField.getText() && tempContact.getSecond() == secondField.getText()
                    && tempContact.getCity() == cityField.getText() && tempContact.getState() == stateField.getText()
                    && tempContact.getZip() == zipField.getText() && tempContact.getEmail() == emailField.getText()
                    && tempContact.getPhone() == phoneField.getText() && tempContact.getNote() == noteArea.getText())) {
                tempContact.setFname(fnameField.getText());
                tempContact.setLname(lnameField.getText());
                tempContact.setStreet(addressField.getText());
                tempContact.setSecond(secondField.getText());
                tempContact.setCity(cityField.getText());
                tempContact.setState(stateField.getText());
                tempContact.setZip(zipField.getText());
                tempContact.setEmail(emailField.getText());
                tempContact.setPhone(phoneField.getText());
                tempContact.setNote(noteArea.getText());
                tempContact.setModified(true);
                //System.out.println("1");
            }
        }

        clearFields();
        tabbedPane.setSelectedIndex(0);
        if (sortDropDown.getSelectedIndex() == 0) {
            sortByLname();
        } else {
            sortByZip();
        }
        updateContactsList();

        isModified = true;
    }

    private void removeContact() {

        int selection = JOptionPane.showConfirmDialog(null,
                "Confirm to remove this contact",
                "Remove Contact",
                JOptionPane.YES_NO_CANCEL_OPTION);
        if (selection == 0) {
            deletedContact.add(addressBook.get(contactsList.getSelectedIndex()));
            addressBook.remove(contactsList.getSelectedIndex());
            /*
            ArrayList<Integer> list = new ArrayList<>();
            String sql = "SELECT id FROM AddressBook";
            try (
                    Statement idstmt  = editContact.getConn().createStatement();
                    ResultSet idrs = idstmt.executeQuery(sql)){

                // loop through the result set
                while (idrs.next()) {
                    list.add(idrs.getInt("id"));
                }
                Integer[] idset = list.toArray(new Integer[list.size()]);
                EditContact.DeleteData(idset[contactsList.getSelectedIndex()]);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
            */
        }

        updateContactsList();
        isModified = true;
    }

    private void clearFields() {
        fnameField.setText("");
        lnameField.setText("");
        addressField.setText("");
        cityField.setText("");
        stateField.setText("");
        zipField.setText("");
        phoneField.setText("");
        noteArea.setText("");
    }

    private static void updateContactsList() {
        ArrayList<String> tempList = new ArrayList<>();
        for (Contact c: addressBook) {
            tempList.add(c.getLname() + ", " + c.getFname());
        }
        contactsList.setListData(tempList.toArray(new String[tempList.size()]));
        contactsList.setSelectedIndex(0);
        /*ArrayList<String> list = new ArrayList<>();
        String sql = "SELECT name FROM AddressBook";
        try (
                Statement stmt  = editContact.getConn().createStatement();
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
        */
    }

    public static void sortByLname() {
        Collections.sort(addressBook, new Comparator<Contact>() {
            @Override
            public int compare(Contact o1, Contact o2) {
                return (o1.getLname().compareTo(o2.getLname()));
            }
        });
    }

    public static void sortByZip() {
        Collections.sort(addressBook, new Comparator<Contact>() {
            @Override
            public int compare(Contact o1, Contact o2) {
                return (o1.getZip().compareTo(o2.getZip()));
            }
        });
    }

    public void closeBook() {
        if (isModified) {
            int choice = JOptionPane.showConfirmDialog(null, "Save the change?", "Save Change",
                    JOptionPane.YES_NO_CANCEL_OPTION);
            if (choice == 0) {
                saveBook();
                dispose();
            } else if (choice == 1) {
                dispose();
            }
        } else {
            dispose();
        }
    }

    public void saveBook() {
        for (Contact c:addressBook) {
            if (c.getId() == 0) {
                editContact.InsertData(c.getFname(), c.getLname(), c.getEmail(), c.getStreet(),
                        c.getSecond(), c.getCity(), c.getState(), c.getZip(), c.getNote(), c.getPhone());
            } else {
                if (c.getIsModified()) {
                    //System.out.println(c.getLname());
                    editContact.EditData(c.getId(), c.getFname(), c.getLname(), c.getEmail(), c.getStreet(), c.getSecond(),
                            c.getCity(), c.getState(), c.getZip(), c.getNote(), c.getPhone());
                }
            }
        }

        for (Contact dc:deletedContact) {
            if (dc.getId() != 0) {
                editContact.DeleteData(dc.getId());
            }
        }

        getRootPane().putClientProperty("Window.documentModified", Boolean.FALSE);
    }

    public static void getContactsFromDB() {
        String sql = "SELECT * FROM AddressBook";
        addressBook = new ArrayList<>();
        try (
                Statement stmt  = editContact.getConn().createStatement();
                ResultSet rs    = stmt.executeQuery(sql)){

            // loop through the result set
            while (rs.next()) {
                newContact = new Contact(rs.getInt("id"),
                        rs.getString("fname"), rs.getString("lname"),
                        rs.getString("email"), rs.getString("street"),
                        rs.getString("second"), rs.getString("city"),
                        rs.getString("state"), rs.getString("zip"),
                        rs.getString("note"), rs.getString("phone"));
                //addressBook.add(rs.getString("name"));
                addressBook.add(newContact);
            }
            //contactsList.setListData(list.toArray(new String[list.size()]));
            if (addressBook.size() != 0) {
                ArrayList<String> tempList = new ArrayList<>();
                for (Contact c : addressBook) {
                    tempList.add(c.getLname() + ", " + c.getFname());
                }
                contactsList.setListData(tempList.toArray(new String[tempList.size()]));
                contactsList.setSelectedIndex(0);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

/*
    public static void main(String[] args) {
        //ConnectDB.connect();
        //ConnectDB.createNewTable();

        JFrame frame = new Book();
        frame.setVisible(true);
        frame.setPreferredSize(new Dimension(450, 450));
        frame.pack();

        getContactsFromDB();
        deletedContact = new ArrayList<>();
        sortByLname();
        updateContactsList();
    }
    */
}
