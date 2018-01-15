import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class Launcher extends JFrame{
    private Launcher() {
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        //Main Window
        setTitle("Address Books");
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

        //List of all address books
        JList<String> booksList = new JList<>();
        booksList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        //Panels
        JPanel mainPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        JScrollPane scrollPane = new JScrollPane(booksList);
        scrollPane.setPreferredSize(new Dimension(350, 340));

        //Buttons
        JButton buttonNew = new JButton("New");
        JButton buttonOpen = new JButton("Open");
        JButton buttonRemove = new JButton("Remove");


        //Layout
        Border border = BorderFactory.createEmptyBorder(5, 5, 5, 5);
        add(mainPanel);
        mainPanel.setBorder(border);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.PAGE_END);

        buttonPanel.add(buttonNew);
        buttonPanel.add(buttonOpen);
        buttonPanel.add(buttonRemove);

    }

    public static void main(String[] args) {
        JFrame frame = new Launcher();
        frame.setVisible(true);
        frame.pack();
    }
}
