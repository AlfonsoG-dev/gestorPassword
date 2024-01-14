package Interface.Panels;

import java.sql.Connection;

import java.awt.GridLayout;
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

import Conexion.Query.QueryDAO;
import Config.DbConfig;
import Mundo.Cuentas.Cuenta;
import Mundo.Cuentas.CuentaBuilder;

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
     * constructor
     */
    public PanelRegistro(String frameTitle, int hight, int height, DbConfig myConfig, int pLoggedUser, Connection miCursor, JFrame nMainFrame) {
        loggedUser = pLoggedUser;
        cursor = miCursor;
        mainFrame = nMainFrame;
        CreateUI(frameTitle, hight, height, myConfig);
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
        pOptions.add(txtPassword = new JTextField());

        return pOptions;
    }
    /**
     * implements the OKButton handler
     * <br> pre: </br> disable the main frame to insert the new register
     * <br> post: </br> enables the main frame and close the current frame
     * @param OKButton: panel OKButton to insert a new register to the database
     * @param myConfig: database configuration
     */
    private void OkButtonHandler(JButton OKButton, DbConfig myConfig) {
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
                            QueryDAO<Cuenta> cuentaDAO = new QueryDAO<Cuenta>("cuenta", myConfig);
                            cuentaDAO.InsertNewRegister(nueva, condition, "and", new CuentaBuilder(), cursor);
                            mainFrame.setEnabled(true);
                            myFrame.setVisible(false);
                        }
                    } catch(Exception ex) {
                        System.out.println(ex);
                    } finally {
                        JOptionPane.showMessageDialog(myFrame, "reload the window to see the changes", "INFO", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            });
    }
    /**
     * implements the cancelButton handler
     * <br> post: </br> close the current frame and enables the main frame
     * @param cancelButton: panel cancelButton to go back to the mainFrame
     * @param myConfig: database configuration
     */
    private void CancelButtonHandler(JButton cancelButton, DbConfig myConfig) {
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(JOptionPane.showConfirmDialog(myFrame, "Do you want to cancel?", "Register operation",
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION) {
                    mainFrame.setEnabled(true);
                    myFrame.setVisible(false);
                }
            }
        });
    }
    private JPanel OperationOptions(DbConfig myConfig) {

        JPanel options = new JPanel();
        options.setLayout(new FlowLayout());

        JButton OKButton = new JButton("OK");
        options.add(OKButton);
        OkButtonHandler(OKButton, myConfig);

        JButton cancelButton = new JButton("cancel");
        options.add(cancelButton);
        CancelButtonHandler(cancelButton, myConfig);
        return options;
    }
    /**
     * creates the ui for the current frame
     * @param frameTitle: frame title
     * @param hight: frame hight
     * @param height: frame height
     * @param DbConfig: database configuration
     */
    public void CreateUI(String frameTitle, int hight, int height, DbConfig myConfig) {
        myFrame = new JFrame(frameTitle);
        myFrame.setSize(hight, height);
        myFrame.setLayout(new GridLayout(3, 1));

        myFrame.addWindowListener(new WindowAdapter() {
            // changes the close operation
            public void windowClosing(WindowEvent we) {
                myFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                myFrame.setVisible(false);
                mainFrame.setEnabled(true);
            }
        });

        headerLabel = new JLabel("Register", JLabel.CENTER);

        pPrincipal = new JPanel();
        pPrincipal.setLayout(new GridLayout(2, 1));

        pPrincipal.add(OptionsComponent());

        pPrincipal.add(OperationOptions(myConfig));

        myFrame.add(headerLabel);
        myFrame.add(pPrincipal);
        myFrame.setVisible(true);
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
