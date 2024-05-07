package Interface.Panels;

import Interface.Utils.PanelUtils;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JPanel;

import Config.DbConfig;

import Mundo.Users.User;
import Utils.Formats.ParamValue;

public class PanelLoginUser {

    private JFrame myFrame;
    private JPanel pPrincipal;
    private JTextField txtNombre;
    private JTextField txtEmail;
    private JTextField txtPassword;
    private JTextField txtRol;
    private DbConfig myConfig;
    private Connection cursor;
    private PanelUtils<User> userUtils;

    public PanelLoginUser(DbConfig nConfig, Connection nCursor, PanelUtils<User> nUserUtils) {
        cursor = nCursor;
        myConfig = nConfig;
        userUtils = nUserUtils;
        createUI(500, 600);
    }
    private JPanel optionsComponent() {
        JPanel pOptions = new JPanel();
        pOptions.setLayout(new GridLayout(4, 2));

        pOptions.add(new JLabel(" Nombre"));
        pOptions.add(txtNombre = new JTextField());

        pOptions.add(new JLabel(" Email"));
        pOptions.add(txtEmail = new JTextField());

        pOptions.add(new JLabel(" Password"));
        pOptions.add(txtPassword = new JTextField());

        pOptions.add(new JLabel(" Rol"));
        pOptions.add(txtRol = new JTextField());

        return pOptions;
    }
    private void okButtonHandler(JButton okButton) {
        okButton.setMnemonic(KeyEvent.VK_ENTER);
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String 
                        nombre   = txtNombre.getText().isEmpty() == false ? txtNombre.getText() : "",
                        email    = txtEmail.getText().isEmpty() == false ? txtEmail.getText() : "",
                        password = txtPassword.getText().isEmpty() == false ? txtPassword.getText() : "",
                        rol      = txtRol.getText().isEmpty() == false ? txtRol.getText() : "";
                    if(nombre == "" || email == "" || password == "" || rol == "") {
                        JOptionPane.showMessageDialog(
                                myFrame,
                                "Invalid data",
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    } else {
                        User newUser = new User(nombre, email, password, rol);
                        newUser.setCreate_at();
                        String[]
                            c = {"nombre", "email"},
                            v = {nombre, email};
                        int options = JOptionPane.showConfirmDialog(
                                myFrame,
                                "Do you want to register?",
                                "Register operation",
                                JOptionPane.OK_CANCEL_OPTION,
                                JOptionPane.QUESTION_MESSAGE
                        );
                        if(options == JOptionPane.OK_OPTION) {
                            ParamValue condition = new ParamValue(c, v, "and");
                            userUtils.insertOperation(newUser, condition);
                            myFrame.dispose();
                            new PanelLogin(myConfig, cursor);
                        }
                    }
                } catch(Exception er) {
                    er.printStackTrace();
                }
            }
        });
    }
    private void cancelButtonHandler(JButton cancelButton) {
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int options = JOptionPane.showConfirmDialog(
                        myFrame,
                        "Do you want to cancel?",
                        "Register operation",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );
                if(options == JOptionPane.OK_OPTION) {
                    System.exit(0);
                }
            }
        });
    }

    private JPanel operationOptions() {

        JPanel options = new JPanel();
        options.setLayout(new FlowLayout());

        JButton OKButton = new JButton("OK");
        options.add(OKButton);
        okButtonHandler(OKButton);

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
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });

        JLabel header = new JLabel("Register user", JLabel.CENTER);

        pPrincipal = new JPanel();
        pPrincipal.setLayout(new GridLayout(2, 1));

        pPrincipal.add(optionsComponent());
        pPrincipal.add(operationOptions());

        myFrame.add(header);
        myFrame.add(pPrincipal);

        myFrame.setVisible(true);
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
