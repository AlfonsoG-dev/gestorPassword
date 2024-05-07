package Interface.Panels;

import Interface.Utils.PanelUtils;

import java.sql.Connection;

import java.util.ArrayList;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Conexion.Query.QueryDAO;

import Config.DbConfig;
import Mundo.Cuentas.Cuenta;
import Mundo.Cuentas.CuentaBuilder;
import Mundo.Users.User;
import Mundo.Users.UserBuilder;
import Utils.Formats.ParamValue;

public class PanelLogin {

    /**
     * login frame
     */
    private JFrame myFrame;
    /**
     * principal panel of the frame
     */
    private JPanel pPrincipal;
    /**
     * user name options to log in
     */
    private JComboBox<String> cbxUserName;
    /**
     * field to digit user password
     */
    private JTextField txtUserPassword;
    /**
     * button to log in
     */
    private JButton btnIngreso;
    /**
     * button to cancel log in
     */
    private JButton btnCancel;
    /** 
     * database connection
    */
    private Connection cursor;
    /**
     * database configuration
     */
    private DbConfig dbConfig;
    /**
     * utils for the user DAO
     */
    private PanelUtils<User> userUtils;
    /**
     * utils for the account DAO
     */
    private PanelUtils<Cuenta> cuentaUtils;
    /**
     * Constructor
     */
    public PanelLogin(DbConfig myConfig, Connection miConector) {
        cursor = miConector;
        dbConfig = myConfig;
        userUtils = new PanelUtils<User>(
                new QueryDAO<User>(
                    "user",
                    cursor,
                    new UserBuilder()
                )
        );
        cuentaUtils = new PanelUtils<Cuenta>(
                new QueryDAO<Cuenta>(
                    "cuenta",
                    cursor,
                    new CuentaBuilder()
                )
        );
        if(userUtils.myDataList().size() > 0) {
            createUI("Loggin");
        } else {
            new PanelLoginUser(
                    dbConfig,
                    cursor,
                    userUtils
            );
        }
    }
    /**
     * set the users to select in the comboBox
     * @return the comboBox with the users names
     */
    private String[] comboBoxUsers() {
        StringBuilder res = new StringBuilder();
        res.append("Select user...,");
        ArrayList<User> myUsers = userUtils.myDataList();
        if(myUsers.size() > 0) {
            for(User u: myUsers) {
                res.append(u.getNombre() + ",");
            }
        }
        return res.toString().split(",");
    }
    /**
     * set the content of the principal panel 
     * @return the panel with its content
     */
    private JPanel loginContent() {
        pPrincipal = new JPanel();
        pPrincipal.setLayout(new GridLayout(2, 2));
        
        cbxUserName = new JComboBox<String>(comboBoxUsers());
        pPrincipal.add(new JLabel("name"));
        pPrincipal.add(cbxUserName);

        txtUserPassword = new JTextField();
        pPrincipal.add(new JLabel("password"));
        pPrincipal.add(txtUserPassword);

        return pPrincipal;
    }
    /**
     * the login options that correspond to ingreso and calcel
     * @return the options panel with ist logic
     */
    private JPanel loginOptions() {

        JPanel option = new JPanel();
        option.setLayout(new FlowLayout());
        btnIngreso = new JButton("OK");
        option.add(btnIngreso);
        btnIngreso.setMnemonic(KeyEvent.VK_ENTER);

        btnIngreso.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String userName = "";
                String userPassword = "";
                if(cbxUserName.getSelectedIndex() == 0 || txtUserPassword.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(
                            myFrame,
                            "invalid user or password",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                } else {
                    userName = cbxUserName.getSelectedItem().toString();
                    userPassword = txtUserPassword.getText();
                    String[] 
                        c = {"nombre", "password"},
                        v = {userName, userPassword};
                    ParamValue condition = new ParamValue(c, v, "and");
                    User mio = userUtils.findOperation(condition);
                    if(mio != null) {
                        new PanelPrincipal(
                                dbConfig,
                                mio.getId_pk(),
                                myFrame,
                                cursor,
                                cuentaUtils
                        );
                        myFrame.dispose();
                    } else {
                        JOptionPane.showMessageDialog(
                                myFrame,
                                "invalid credentials",
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            }
        });

        btnCancel = new JButton("Cancel");
        option.add(btnCancel);

        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(
                        myFrame,
                        "are you sure?",
                        "Exit",
                        JOptionPane.OK_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );
                if(option == JOptionPane.OK_OPTION) {
                    System.exit(0);
                }
            }
        });
        return option;
    }
    /**
     * initialize the panel frame with its content 
     * @param frameTitle: title of the frame
     */
    public void createUI(String frameTitle) {
        myFrame = new JFrame(frameTitle);
        myFrame.setSize(400, 200);
        myFrame.setLayout(new BorderLayout());

        myFrame.add(loginContent(), BorderLayout.CENTER);
        myFrame.add(loginOptions(), BorderLayout.SOUTH);


        myFrame.setVisible(true);
        myFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        myFrame.setResizable(false);
    }
}
