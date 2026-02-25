package application.interfaces.panels;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import application.interfaces.utils.PanelUtils;
import application.models.user.UserModel;
import application.models.user.UserODM;

import javax.swing.JPanel;

import orm.utils.formats.DbConfig;
import orm.utils.formats.ParamValue;

public class PanelLoginUser {

    private JFrame myFrame;
    private JTextField txtNombre;
    private JTextField txtEmail;
    private JTextField txtPassword;
    private JTextField txtRol;
    private DbConfig myConfig;
    private Connection cursor;
    private PanelUtils<UserModel> userUtils;

    public PanelLoginUser(DbConfig nConfig, Connection nCursor, PanelUtils<UserModel> nUserUtils) {
        cursor = nCursor;
        myConfig = nConfig;
        userUtils = nUserUtils;
        createUI(500, 600);
    }
    private JPanel optionsComponent() {
        JPanel pOptions = new JPanel();
        pOptions.setLayout(new GridLayout(4, 2));

        pOptions.add(new JLabel(" Nombre"));
        txtNombre = new JTextField();
        pOptions.add(txtNombre);

        pOptions.add(new JLabel(" Email"));
        txtEmail = new JTextField();
        pOptions.add(txtEmail);

        pOptions.add(new JLabel(" Password"));
        txtPassword = new JTextField();
        pOptions.add(txtPassword);

        pOptions.add(new JLabel(" Rol"));
        txtRol = new JTextField();
        pOptions.add(txtRol);

        return pOptions;
    }
    private void appendOKOptionAction(String[] column, String[] value, UserODM user) throws SQLException {
        ParamValue condition = new ParamValue(column, value, "and");
        userUtils.insertOperation(user, condition);
        myFrame.dispose();
        new PanelLogin(myConfig, cursor);
    }
    private String[] getTextFieldValues() {
        String nombre   = !txtNombre.getText().isBlank() ? txtNombre.getText() : null;
        String email    = !txtEmail.getText().isBlank() ? txtEmail.getText() : null;
        String password = !txtPassword.getText().isBlank() ? txtPassword.getText() : null;
        String rol      = !txtRol.getText().isBlank() ? txtRol.getText() : null;
        return new String[] {nombre, email, password, rol};
    }
    private void okButtonHandler(JButton okButton) {
        okButton.setMnemonic(KeyEvent.VK_ENTER);
        okButton.addActionListener(e -> {
            try {
                String nombre = getTextFieldValues()[0];
                String email = getTextFieldValues()[1];
                String password = getTextFieldValues()[2];
                String rol = getTextFieldValues()[3];

                if(nombre == null || email == null || password == null || rol == null) {
                    JOptionPane.showMessageDialog(myFrame,
                            "Invalid data", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    UserODM newUser = new UserODM(nombre, email, password, rol);
                    newUser.makeCreate_at();
                    String[] c = {"nombre", "email"};
                    String[] v = {nombre, email};
                    int options = JOptionPane.showConfirmDialog(myFrame,
                            "Do you want to register?", "Register operation",
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(options == JOptionPane.OK_OPTION) {
                        appendOKOptionAction(c, v, newUser);
                    }
                }
            } catch(Exception er) {
                er.printStackTrace();
            }
        });
    }
    private void cancelButtonHandler(JButton cancelButton) {
        cancelButton.addActionListener(e -> {
            int options = JOptionPane.showConfirmDialog(myFrame,
                    "Do you want to cancel?", "Register operation",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if(options == JOptionPane.OK_OPTION) {
                System.exit(0);
            }
        });
    }

    private JPanel operationOptions() {

        JPanel options = new JPanel();
        options.setLayout(new FlowLayout());

        JButton btnOK = new JButton("OK");
        options.add(btnOK);
        okButtonHandler(btnOK);

        JButton cancelButton = new JButton("cancel");
        options.add(cancelButton);
        cancelButtonHandler(cancelButton);
        return options;
    }

    public void createUI(int width, int height) {
        myFrame = new JFrame("Register User");
        myFrame.setSize(width, height);
        myFrame.setLayout(new GridLayout(3, 1));
        myFrame.addWindowListener(new WindowAdapter() {
            // changes the close operation
            @Override
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });

        JLabel header = new JLabel("Register user", SwingConstants.CENTER);

        JPanel pPrincipal = new JPanel();
        pPrincipal.setLayout(new GridLayout(2, 1));

        pPrincipal.add(optionsComponent());
        pPrincipal.add(operationOptions());

        myFrame.add(header);
        myFrame.add(pPrincipal);

        myFrame.setVisible(true);
        myFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}
