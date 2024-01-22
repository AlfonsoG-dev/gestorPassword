package Interface.Panels;

import java.sql.Connection;

import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Config.DbConfig;
import Interface.Utils.PanelUtils;
import Mundo.Cuentas.Cuenta;

public class PanelRegistro {
    /**
     * the panel frame
     */
    private JFrame myFrame;
    /**
     * the logged user
     */
    private int loggedUser;
    /**
     * the label for the panel name
     */
    private JLabel headerLabel;
    /**
     * the nombre text field
     */
    private JTextField txtNombre;
    /**
     * the email text field
     */
    private JTextField txtEmail;
    /**
     * logged user text field 
     */
    private JTextField txtLoggedUser;
    /**
     * the password text field
     */
    private JTextField txtPassword;
    /**
     * the current principal panel
     */
    private JPanel pPrincipal;
    /**
     * the cursor to allow transaction for commit or rollback
     */
    private Connection cursor;
    /**
     * the frame of the main panel
     */
    private JFrame mainFrame;
    /**
     * DAO class for account
     */
    private PanelUtils<Cuenta> cuentaUtils;
    /**
     * database configuration
     */
    private DbConfig myConfig;
    /**
     * constructor
     */
    public PanelRegistro(String frameTitle, int width, int height, DbConfig nConfig, int pLoggedUser, Connection miCursor, JFrame nMainFrame, PanelUtils<Cuenta> nCuentaUtils) {
        loggedUser = pLoggedUser;
        myConfig = nConfig;
        cursor = miCursor;
        mainFrame = nMainFrame;
        cuentaUtils = nCuentaUtils;
        CreateUI(frameTitle, width, height);
    }
    /**
     * implements the OKButton handler
     * <br> pre: </br> disable the main frame to insert the new register
     * <br> post: </br> enables the main frame and close the current frame
     * @param OKButton: panel OKButton to insert a new register to the database
     */
    private void OkButtonHandler(JButton OKButton) {
        OKButton.setMnemonic(KeyEvent.VK_ENTER);
            OKButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        String nombre = txtNombre.getText();
                        String email = txtEmail.getText();
                        String password = txtPassword.getText();
                        Cuenta nueva = new Cuenta(nombre, email, loggedUser, password);
                        nueva.setCreate_at();
                        String condition = "nombre: "  + nueva.getNombre() + ", user_id_fk" + nueva.getUser_id_fk();
                        if(JOptionPane.showConfirmDialog(myFrame, "Do you want to register?", "Register operation",
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION) {
                            cuentaUtils.InsertOperation(nueva, condition, "and");
                            if(mainFrame != null) {
                                mainFrame.setEnabled(true);
                                myFrame.dispose();
                            } else {
                                new PanelPrincipal(myConfig, loggedUser, myFrame, cursor, cuentaUtils);
                                myFrame.dispose();
                            }
                        }
                    } catch(Exception ex) {
                        ex.printStackTrace();
                        cuentaUtils.ErrorMessage(myFrame, "error while trying to insert registers", "Insert Error");
                    } finally {
                        cuentaUtils.InfoMessage(myFrame, "reload the window to see the changes","Cuenta Error"); 
                    }
                }
            });
    }
    /**
     * implements the cancelButton handler
     * <br> post: </br> close the current frame and enables the main frame
     * @param cancelButton: panel cancelButton to go back to the mainFrame
     */
    private void CancelButtonHandler(JButton cancelButton) {
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(JOptionPane.showConfirmDialog(myFrame, "Do you want to cancel?", "Register operation",
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION) {
                    if(mainFrame != null) {
                        mainFrame.setEnabled(true);
                        myFrame.dispose();
                    } else {
                        System.exit(0);
                    }
                }
            }
        });
    }
    private void GenerateButtonHandler(JButton generateButton) {
        generateButton.setMnemonic(KeyEvent.VK_G);
        generateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(txtPassword.getText().isEmpty()) {
                    new PanelPassword(myFrame, txtPassword, cuentaUtils);
                    myFrame.setEnabled(false);
                } else {
                    txtPassword.setText(cuentaUtils.GeneratePassword(cuentaUtils.getPasswrodOptions()).toString());
                }
            }
        });
    }
    /**
     * set the panel components and its content
     * @return the panel with the content setted
     */
    private JPanel OptionsComponent() {
        JPanel pOptions = new JPanel();
        pOptions.setLayout(new GridLayout(4, 2));

        pOptions.add(new JLabel(" Nombre"));
        pOptions.add(txtNombre = new JTextField());
        pOptions.add(new JLabel(" Email"));
        pOptions.add(txtEmail = new JTextField());
        pOptions.add(new JLabel(" User_id"));

        pOptions.add(txtLoggedUser = new JTextField(String.valueOf(loggedUser)));
        txtLoggedUser.setEnabled(false);

        pOptions.add(new JLabel(" Password"));
        JPanel mip = new JPanel();
        mip.setLayout(new GridLayout(1, 2));
        mip.add(txtPassword = new JTextField());
        JButton btnGenerate = new JButton("generate");
        mip.add(btnGenerate);
        GenerateButtonHandler(btnGenerate);
        pOptions.add(mip);

        return pOptions;
    }
    /**
     * sets the panel content for the options
     * <br> post: </br> ok button insert data cancel redirects to main frame
     */
    private JPanel OperationOptions() {

        JPanel options = new JPanel();
        options.setLayout(new GridLayout(1, 2));

        JButton OKButton = new JButton("OK");
        options.add(OKButton);
        OkButtonHandler(OKButton);

        JButton cancelButton = new JButton("cancel");
        options.add(cancelButton);
        CancelButtonHandler(cancelButton);
        return options;
    }
    /**
     * creates the ui for the current frame
     * @param frameTitle: frame title
     * @param width: frame width
     * @param height: frame height
     */
    public void CreateUI(String frameTitle, int width, int height) {
        myFrame = new JFrame(frameTitle);
        myFrame.setSize(width, height);
        myFrame.setLayout(new GridLayout(3, 1));

        myFrame.addWindowListener(new WindowAdapter() {
            // changes the close operation
            public void windowClosing(WindowEvent we) {
                if(mainFrame != null) {
                    mainFrame.setEnabled(true);
                    myFrame.dispose();
                } else {
                    System.exit(0);
                }
            }
        });

        headerLabel = new JLabel("Register", JLabel.CENTER);

        pPrincipal = new JPanel();
        pPrincipal.setLayout(new BorderLayout());

        pPrincipal.add(OptionsComponent(), BorderLayout.NORTH);

        pPrincipal.add(OperationOptions(), BorderLayout.SOUTH);

        myFrame.add(headerLabel);
        myFrame.add(pPrincipal);
        myFrame.setVisible(true);
        myFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
}
