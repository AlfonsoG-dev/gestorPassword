package Interface.Panels;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.awt.event.WindowAdapter;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;

import Interface.Utils.FileUtils;

import Mundo.Cuentas.Cuenta;

public class ImportPanel {

    private JFrame myFrame;
    private JFrame mainFrame;

    private JPanel pPrincipal;
    private JFileChooser fileChooser;
    
    private DefaultTableModel tableModel;

    private String filePaht;
    private int loggedUser;


    public ImportPanel(JFrame nMainFrame, int width, int height, int nLoggedUser, DefaultTableModel nModel) {
        mainFrame = nMainFrame;
        loggedUser = nLoggedUser;
        tableModel = nModel;
        createUI(width, height);
    }

    /**
     * build table data from a file that is import from the system
     * @param imporData: data from file
     * <br> post: </br> set the table content to add imported data
     */
    private void buildTableDataFromImportFile(ArrayList<Cuenta> imporData) {
        ArrayList<String> tableContent = new ArrayList<>();
        imporData
            .parallelStream()
            .forEach(e -> {
                Cuenta data = e;
                int id = 0;
                String 
                    nombre = data.getNombre(),
                    email = data.getEmail(),
                    password = data.getPassword(),
                    create_at = data.getCreate_at(),
                    update_at = "";
                tableContent.add(
                        id + "," +
                        nombre + "," +
                        email + "," +
                        loggedUser + "," +
                        password + "," +
                        create_at + "," +
                        update_at
                );
            });
        for(String d: tableContent) {
            tableModel.addRow(d.split(","));
        }
    }
    private JPanel contentPanel() {
        pPrincipal = new JPanel();
        pPrincipal.setLayout(new GridLayout(1, 1));
        pPrincipal.add(fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory()));
        fileChooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int r = fileChooser.showOpenDialog(myFrame);
                if(r == JFileChooser.APPROVE_OPTION) {
                    filePaht = fileChooser.getSelectedFile().getPath();
                }
            }
        });
        return pPrincipal;
    }
    private void okButtonHandler(JButton okButton) {
        okButton.setMnemonic(KeyEvent.VK_ENTER);
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ArrayList<Cuenta> importData = FileUtils.getData(filePaht);
                buildTableDataFromImportFile(importData);
                mainFrame.setEnabled(true);
                myFrame.dispose();
            }
        });
    }

    private void cancelButtonHandler(JButton cancelButton) {
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int condition = JOptionPane.showConfirmDialog(
                        mainFrame,
                        "the frame will be closed!",
                        "Close",
                        JOptionPane.YES_NO_OPTION
                );
                if(condition == JOptionPane.YES_OPTION) {
                    mainFrame.setEnabled(true);
                    myFrame.dispose();
                } else {
                    myFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                }
            }
        });
    }
    private JPanel optionsPanel() {
        JPanel options = new JPanel();
        options.setLayout(new FlowLayout());

        JButton okButton = new JButton("OK");
        options.add(okButton);
        okButtonHandler(okButton);


        JButton cancelButton = new JButton("cancel");
        options.add(cancelButton);
        cancelButtonHandler(cancelButton);

        return options;
    }

    public void createUI(int width, int height) {
        myFrame = new JFrame("Import panel");
        myFrame.setLayout(new BorderLayout());
        myFrame.setSize(width, height);

        myFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                int condition = JOptionPane.showConfirmDialog(
                        mainFrame,
                        "the frame will be closed!",
                        "Close",
                        JOptionPane.YES_NO_OPTION
                );
                if(condition == JOptionPane.YES_OPTION) {
                    mainFrame.setEnabled(true);
                    myFrame.dispose();
                } else {
                    myFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                }
            }
        });

        myFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        myFrame.add(contentPanel(), BorderLayout.CENTER);
        myFrame.add(optionsPanel(), BorderLayout.SOUTH);
        myFrame.setVisible(true);
    }
}
