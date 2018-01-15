import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class Book extends JFrame{
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
        JList<String> contactsList = new JList<>();
        contactsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        //Tabbed Pane: One is "All Contacts", the other one is "Contact"
        JTabbedPane tabbedPane = new JTabbedPane();

        //"All Contacts" Panels
        JPanel mainPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        JScrollPane scrollPane = new JScrollPane(contactsList);
        scrollPane.setPreferredSize(new Dimension(360, 270));

        //"All Contacts" Buttons
        JButton buttonNew = new JButton("New");
        JButton buttonOpen = new JButton("Open");
        JButton buttonRemove = new JButton("Remove");

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
        JTextField nameField = new JTextField(15);
        JTextField addressField = new JTextField(15);
        JTextField cityField = new JTextField(15);
        JTextField stateField = new JTextField(15);
        JTextField zipField = new JTextField(15);
        JTextField phoneField = new JTextField(15);
        JTextArea noteArea = new JTextArea(10, 20);
        JScrollPane notePane = new JScrollPane(noteArea);
        noteArea.setLineWrap(true);
        noteArea.setWrapStyleWord(true);
        notePane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        notePane.setPreferredSize(new Dimension(10, 20));

        //"Contact" Buttons
        JButton buttonCancel = new JButton("Cancel");
        JButton buttonDone = new JButton("Done");

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

    public static void main(String[] args) {
        JFrame frame = new Book();
        frame.setVisible(true);
        frame.pack();
    }
}
