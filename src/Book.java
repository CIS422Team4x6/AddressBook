import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.net.URI;

public class Book extends JFrame{
    private Launcher launcher;
    private ArrayList<Contact> addressBook;
    private ArrayList<Contact> deletedContact;
    private Contact newContact;
    private Contact tempContact;
    private String bookName;
    private String saveAsName;

    private boolean isCreatingContact = true;
    private boolean isSortByLname = true;
    private boolean isModified;
    private int clickedLname = 1;
    private int clickedZip = 0;

    private JTabbedPane tabbedPane;
    private JTextField fnameField;
    private JTextField lnameField;
    private JTextField addressField;
    private JTextField secondField;
    private JTextField cityField;
    private JTextField stateField;
    private JTextField zipField;
    private JTextField phoneField1;
    private JTextField phoneField2;
    private JTextField phoneField3;
    private JTextField emailField;
    private JTextField linkField;
    private JTextArea noteArea;
    private DefaultTableModel model;
    private CheckInput checkInput;

    private JTable contactsTable;
    private EditDB editContact;
//Constructor
    public Book(Launcher launcher, String bookName) {
        this.bookName = bookName;
        this.launcher = launcher;
        editContact = new EditDB(this.bookName);

        System.setProperty("apple.laf.useScreenMenuBar", "true");

        //Main Window
        setTitle(this.bookName);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeBook();
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

        JMenuItem itemDelete = new JMenuItem("Delete");
        itemDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeContact();
            }
        });
        menuFile.add(itemDelete);

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
        itemSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveBook();
                isModified = false;
                model.setRowCount(0);
                getContactsFromDB();
                deletedContact.clear();
            }
        });
        menuFile.add(itemSave);

        JMenuItem itemSaveAs = new JMenuItem("Save As ...");
        itemSaveAs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                launcher.createNewBook();
                saveAsName = launcher.getNewBookName();
                removeOldBookFromLauncher();
                launcher.modifyAddressBooks(bookName, saveAsName);
                setBookName(saveAsName);
                setTitle(saveAsName);
                editContact = new EditDB(saveAsName);
                for (Contact c : addressBook) {
                    c.setId(0);
                }
                saveBook();
                isModified = false;
                model.setRowCount(0);
                getContactsFromDB();
                deletedContact.clear();
                saveNewBookToLauncher();
            }
        });
        menuFile.add(itemSaveAs);

        menuFile.addSeparator();

        JMenuItem itemQuit = new JMenuItem("Quit");
        itemQuit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                launcher.closeApp();
            }
        });
        menuFile.add(itemQuit);

        setJMenuBar(menuBar);

        //Table of all contacts
        String[] columnNames = {"Last Name", "First Name", "ZIP", "Phone"};
        model = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells are non-editable
                return false;
            }
        };
        contactsTable = new JTable(model);
        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(contactsTable.getModel());
        contactsTable.setRowSorter(sorter);

        List<RowSorter.SortKey> sortKeys = new ArrayList<>(25);
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        //sortKeys.add(new RowSorter.SortKey(0, SortOrder.DESCENDING));
        sortKeys.add(new RowSorter.SortKey(2, SortOrder.ASCENDING));
        //sortKeys.add(new RowSorter.SortKey(2, SortOrder.DESCENDING));
        sorter.setSortKeys(sortKeys);
        sorter.setSortable(1, false);
        sorter.setSortable(3, false);
        contactsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contactsTable.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = contactsTable.columnAtPoint(e.getPoint());
                if (col == 0) {
                    clickedLname += 1;
                    clickedZip = 0;
                    isSortByLname = true;
                    sortByLname();
                    updateContactsTable();
                    contactsTable.setRowSelectionInterval(0, 0);
                } else if (col == 2) {
                    clickedZip += 1;
                    clickedLname = 0;
                    isSortByLname = false;
                    sortByZip();
                    updateContactsTable();
                    contactsTable.setRowSelectionInterval(0, 0);
                }
            }
        });
        JPanel searchPanel = new JPanel();
        JTextField searchFilter = new JTextField(20);
        JLabel searchLabel = new JLabel("Search:");
        searchPanel.add(searchLabel);
        searchPanel.add(searchFilter);
        searchFilter.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                String text = searchFilter.getText();
                if (text.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                String text = searchFilter.getText();
                if (text.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });

        //Tabbed Pane: One is "All Contacts", the other one is "Contact"
        tabbedPane = new JTabbedPane();
        //JPanel allPanel = new JPanel();

        //"All Contacts" Panels
        JPanel mainPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        //JPanel sortPanel = new JPanel();
        JScrollPane scrollPane = new JScrollPane(contactsTable);
        scrollPane.setPreferredSize(new Dimension(360, 360));

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
        JButton buttonRemove = new JButton("Delete");
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
        JPanel phonePanel = new JPanel();
        JPanel informationPanel = new JPanel();
        JPanel linkPanel = new JPanel();

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
        JLabel lpLabel = new JLabel("(");
        JLabel rpLabel = new JLabel(")");
        JLabel dashLabel = new JLabel("-");
        JLabel linkLabel = new JLabel("Social Media Link:");

        //"Contact" Text Fields
        fnameField = new JTextField(9);
        lnameField = new JTextField(9);
        addressField = new JTextField(15);
        secondField = new JTextField(15);
        cityField = new JTextField(15);
        stateField = new JTextField(15);
        zipField = new JTextField(15);
        phoneField1 = new JTextField(3);
        phoneField2 = new JTextField(3);
        phoneField3 = new JTextField(4);
        emailField = new JTextField(15);
        linkField = new JTextField(10);
        noteArea = new JTextArea(10, 20);
        JScrollPane notePane = new JScrollPane(noteArea);
        noteArea.setLineWrap(true);
        noteArea.setWrapStyleWord(true);
        notePane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        notePane.setPreferredSize(new Dimension(10, 20));

        phoneField1.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (phoneField1.getText().length() >= 3) {
                    e.consume();
                }
            }
            @Override
            public void keyPressed(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {}
        });
        phoneField2.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (phoneField2.getText().length() >= 3) {
                    e.consume();
                }
            }
            @Override
            public void keyPressed(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {}
        });
        phoneField3.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (phoneField3.getText().length() >= 4) {
                    e.consume();
                }
            }
            @Override
            public void keyPressed(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {}
        });

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
        JButton buttonLink = new JButton("Open");
        buttonLink.setPreferredSize(new Dimension(60, 25));
        buttonLink.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openLink();
            }
        });
        //Layouts
        add(tabbedPane);
        tabbedPane.addTab("All Contacts", mainPanel);
        tabbedPane.addTab("Contact", contactPanel);

        Border border = BorderFactory.createEmptyBorder(0, 10, 10, 10);

        //"All Contacts" pane layout
        mainPanel.setBorder(border);
        mainPanel.add(searchPanel, BorderLayout.PAGE_START);
        //mainPanel.add(sortPanel, BorderLayout.PAGE_START);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.PAGE_END);
        buttonPanel.add(buttonNew);
        buttonPanel.add(buttonOpen);
        buttonPanel.add(buttonRemove);
        linkPanel.add(linkField);
        linkPanel.add(buttonLink);
        //sortPanel.add(sortByLabel);
        //sortPanel.add(sortDropDown);

        //"Contact" pane layout
        Border contactBorder = BorderFactory.createEmptyBorder(0, -5, 0, 0);
        contactPanel.add(informationPanel, BorderLayout.CENTER);
        fnamePanel.setBorder(contactBorder);
        fnamePanel.add(fnameLabel, BorderLayout.LINE_START);
        fnamePanel.add(fnameField);
        lnamePanel.add(lnameLabel);
        lnamePanel.add(lnameField);
        phonePanel.add(lpLabel);
        phonePanel.add(phoneField1);
        phonePanel.add(rpLabel);
        phonePanel.add(phoneField2);
        phonePanel.add(dashLabel);
        phonePanel.add(phoneField3);

        GridLayout informationPanelLayout = new GridLayout(11, 2);

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
        informationPanel.add(phonePanel);
        informationPanel.add(emailLabel);
        informationPanel.add(emailField);
        informationPanel.add(noteLabel);
        informationPanel.add(notePane);
        informationPanel.add(linkLabel);
        informationPanel.add(linkPanel);
        informationPanel.add(buttonCancel);
        informationPanel.add(buttonDone);

        isModified = false;

        getContactsFromDB();
        deletedContact = new ArrayList<>();
        sortByLname();
    }

    public String getBookName() {
        return bookName;
    }

    public void setAddressBook(ArrayList<Contact> book) {
        addressBook = book;
    }
    private void setBookName(String newName) {
        bookName = newName;
    }

    private void removeOldBookFromLauncher() {
        launcher.openBooks.remove(this);
    }
    private void saveNewBookToLauncher() {
        launcher.openBooks.add(this);
    }

    private void newContact() {
        clearFields();
        tabbedPane.setSelectedIndex(1);
        isCreatingContact = true;
    }

    private void openLink() {
    	//open link in browser
        if (!linkField.getText().equals("")) {
            if (Desktop.isDesktopSupported()) {
                try {
                    URI uri = new URI(linkField.getText());
                    Desktop.getDesktop().browse(uri);
                } catch (URISyntaxException e1) {
                    JOptionPane.showMessageDialog(null, "Not a valid link");
                    System.out.println(e1);
                } catch (IOException e2) {
                    JOptionPane.showMessageDialog(null, "Not a valid link");
                    System.out.println(e2);
                }
            }
        }
    }

    private void openContact() {
        if (!contactsTable.getSelectionModel().isSelectionEmpty()) {
            clearFields();
            tabbedPane.setSelectedIndex(1);
            tabbedPane.setEnabledAt(0, false);
            isCreatingContact = false;
            // get data from db
            //reveal in field
            //tempContact = addressBook.get(contactsTable.getSelectedRow());
            tempContact = addressBook.get(contactsTable.getRowSorter().convertRowIndexToModel(contactsTable.getSelectedRow()));
            fnameField.setText(tempContact.getFname());
            lnameField.setText(tempContact.getLname());
            addressField.setText(tempContact.getStreet());
            secondField.setText(tempContact.getSecond());
            cityField.setText(tempContact.getCity());
            stateField.setText(tempContact.getState());
            zipField.setText(tempContact.getZip());
            emailField.setText(tempContact.getEmail());
            if (!tempContact.getPhone().equals("")) {
                if (tempContact.getPhone().length() == 10) {
                    phoneField1.setText(tempContact.getPhone().substring(0, 3));
                    phoneField2.setText(tempContact.getPhone().substring(3, 6));
                    phoneField3.setText(tempContact.getPhone().substring(6));
                } else if (tempContact.getPhone().length() == 7) {
                    phoneField1.setText("");
                    phoneField2.setText(tempContact.getPhone().substring(0, 3));
                    phoneField3.setText(tempContact.getPhone().substring(3));
                }
            }
            noteArea.setText(tempContact.getNote());
            linkField.setText(tempContact.getLink());
        }
    }

    private void cancelEdit() {
        tabbedPane.setEnabledAt(0, true);
        tabbedPane.setSelectedIndex(0);
        clearFields();
    }

    private void saveContact() {
        if (isCreatingContact) {
        	//get input
            String fname = fnameField.getText();
            String lname = lnameField.getText();
            String address = addressField.getText();
            String second = secondField.getText();
            String city = cityField.getText();
            String state = stateField.getText();
            String zip = zipField.getText();
            String phone = phoneField1.getText() + phoneField2.getText() + phoneField3.getText();
            String note = noteArea.getText();
            String email = emailField.getText();
            String link = linkField.getText();
            //check
            checkInput = new CheckInput(this, fname, lname, address,
                    second, note, link, email, city, state, zip, phone);
            //if all legal
            if (checkInput.checkAll()) {
            	//inser values
                newContact = new Contact(0, fname, lname, email, address, second, city, state, zip, note, phone, link);
                addressBook.add(newContact);
                //model.addRow(new Object[]{lname, fname, zip, phone});
                if (isSortByLname) {
                    sortByLname();
                } else {
                    sortByZip();
                }
                updateContactsTable();
                contactsTable.setRowSelectionInterval(0,0);
                clearFields();
                tabbedPane.setSelectedIndex(0);
                //check sort

                isModified = true;
            }
        } else {
            if (!(tempContact.getFname() == fnameField.getText() && tempContact.getLname() == lnameField.getText()
                    && tempContact.getStreet() == addressField.getText() && tempContact.getSecond() == secondField.getText()
                    && tempContact.getCity() == cityField.getText() && tempContact.getState() == stateField.getText()
                    && tempContact.getZip() == zipField.getText() && tempContact.getEmail() == emailField.getText()
                    && tempContact.getPhone() == phoneField1.getText() + phoneField2.getText() + phoneField3.getText()
                    && tempContact.getNote() == noteArea.getText() && tempContact.getLink() == linkField.getText())) {
                checkInput = new CheckInput(this, fnameField.getText(), lnameField.getText(),
                        addressField.getText(), secondField.getText(), noteArea.getText(), linkField.getText(),
                        emailField.getText(),cityField.getText(), stateField.getText(),
                        zipField.getText(), phoneField1.getText() + phoneField2.getText() + phoneField3.getText());
                if (checkInput.checkAll()) {
                    tempContact.setFname(fnameField.getText());
                    tempContact.setLname(lnameField.getText());
                    tempContact.setStreet(addressField.getText());
                    tempContact.setSecond(secondField.getText());
                    tempContact.setCity(cityField.getText());
                    tempContact.setState(stateField.getText());
                    tempContact.setZip(zipField.getText());
                    tempContact.setEmail(emailField.getText());
                    tempContact.setPhone(phoneField1.getText() + phoneField2.getText() + phoneField3.getText());
                    tempContact.setNote(noteArea.getText());
                    tempContact.setLink(linkField.getText());
                    tempContact.setModified(true);
                    if (isSortByLname) {
                        sortByLname();
                    } else {
                        sortByZip();
                    }
                    updateContactsTable();
                    contactsTable.setRowSelectionInterval(0,0);
                    clearFields();
                    tabbedPane.setSelectedIndex(0);
                    tempContact = null;
                    isModified = true;
                }
            }
        }
    }

    private void removeContact() {
        if (!contactsTable.getSelectionModel().isSelectionEmpty()) {
        	//pane to confirm
            int selection = JOptionPane.showConfirmDialog(null,
                    "Confirm to remove this contact",
                    "Remove Contact",
                    JOptionPane.YES_NO_CANCEL_OPTION);
            if (selection == 0) {
            	//remove
                deletedContact.add(addressBook.get(contactsTable.getSelectedRow()));
                addressBook.remove(contactsTable.getSelectedRow());
                updateContactsTable();
            }

            isModified = true;
        }
    }

    private void clearFields() {
    	//set all field to empty
        fnameField.setText("");
        lnameField.setText("");
        addressField.setText("");
        secondField.setText("");
        cityField.setText("");
        stateField.setText("");
        zipField.setText("");
        phoneField1.setText("");
        phoneField2.setText("");
        phoneField3.setText("");
        emailField.setText("");
        noteArea.setText("");
        linkField.setText("");
    }

    //sort the arraylist with last name
    public void sortByLname() {
        if (clickedLname % 2 != 0) {
            Collections.sort(addressBook, new Comparator<Contact>() {
                @Override
                public int compare(Contact o1, Contact o2) {
                    return (o1.getLname().compareTo(o2.getLname()));
                }
            });
        } else {
            Collections.sort(addressBook, new Comparator<Contact>() {
                @Override
                public int compare(Contact o1, Contact o2) {
                    return (o2.getLname().compareTo(o1.getLname()));
                }
            });
        }
    }

    //sort the arraylist with zip code
    public void sortByZip() {
        if (clickedZip % 2 == 1) {
            Collections.sort(addressBook, new Comparator<Contact>() {
                @Override
                public int compare(Contact o1, Contact o2) {
                    return (o1.getZip().compareTo(o2.getZip()));
                }
            });
        } else {
            Collections.sort(addressBook, new Comparator<Contact>() {
                @Override
                public int compare(Contact o1, Contact o2) {
                    return (o2.getZip().compareTo(o1.getZip()));
                }
            });
        }
    }

    public void closeBook() {
        for (int i = 0; i < launcher.getOpenBook().size(); i++) {
            if (bookName.equals(launcher.getOpenBook().get(i).bookName)) {
                Book deleted = launcher.getOpenBook().remove(i);
            }
        }
        if (isModified) {
        	// new pane to ask save option
            int choice = JOptionPane.showConfirmDialog(null, "Save the change?", "Save Change",
                    JOptionPane.YES_NO_CANCEL_OPTION);
            if (choice == 0) {
                saveBook();
                this.dispose();
            } else if (choice == 1) {
                this.dispose();
            }
        } else {
            this.dispose();
        }
    }

    public void saveBook() {
    	//save action listener
        for (Contact c:addressBook) {
            if (c.getId() == 0) {
                editContact.InsertData(c.getFname(), c.getLname(), c.getEmail(), c.getStreet(),
                        c.getSecond(), c.getCity(), c.getState(), c.getZip(), c.getNote(), c.getPhone(), c.getLink());
            } else {
                if (c.getIsModified()) {
                    editContact.EditData(c.getId(), c.getFname(), c.getLname(), c.getEmail(), c.getStreet(), c.getSecond(),
                            c.getCity(), c.getState(), c.getZip(), c.getNote(), c.getPhone(), c.getLink());
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

    private void updateContactsTable() {
        model.setRowCount(0);
        if (addressBook.size() != 0) {
            Object[] temp;
            for (int i = 0; i < addressBook.size(); i++) {
                temp = new Object[] {addressBook.get(i).getLname(), addressBook.get(i).getFname(),
                        addressBook.get(i).getZip(), addressBook.get(i).getPhone()};
                model.addRow(temp);

            }

        }
    }

    private void getContactsFromDB() {
    	// import all data from db into arraylist
        String sql = "SELECT * FROM AddressBook";
        addressBook = new ArrayList<>();
        try (
                Statement stmt  = editContact.getConn().createStatement();
                ResultSet rs    = stmt.executeQuery(sql)){

            // loop through the result set
            while (rs.next()) {
            	//get data
                newContact = new Contact(rs.getInt("id"),
                        rs.getString("fname"), rs.getString("lname"),
                        rs.getString("email"), rs.getString("street"),
                        rs.getString("second"), rs.getString("city"),
                        rs.getString("state"), rs.getString("zip"),
                        rs.getString("note"), rs.getString("phone"),
                        rs.getString("link"));
                addressBook.add(newContact);
            }
            if (addressBook.size() != 0) {
                sortByLname();
                Object[] temp;
                for (int i = 0; i < addressBook.size(); i++) {
                    temp = new Object[] {addressBook.get(i).getLname(), addressBook.get(i).getFname(),
                            addressBook.get(i).getZip(), addressBook.get(i).getPhone()};
                    model.addRow(temp);

                }

            }
            //order = SortOrder.ASCENDING;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
