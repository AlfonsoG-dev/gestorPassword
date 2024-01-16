package Interface.Panels;

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
import Mundo.Users.User;
import Mundo.Users.UserBuilder;

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
    private DbConfig db_config;
    /**
     * DAO class for user
     */
    private QueryDAO<User> userDAO;
    /**
     * DAO class for account
     */
    private QueryDAO<Cuenta> cuentaDAO;
    /**
     * Constructor
     */
    public PanelLogin(DbConfig myConfig, Connection miConector) {
        cursor = miConector;
        db_config = myConfig;
        userDAO = new QueryDAO<User>("user", cursor);
        cuentaDAO = new QueryDAO<Cuenta>("cuenta", cursor);
        if(userDAO.ReadAll(new UserBuilder()).size() > 0) {
            CreateUI("Loggin");
        } else {
            new PanelLoginUser(db_config, cursor, userDAO);
        }
    }
    /**
     * set the users to select in the comboBox
     * @return the comboBox with the users names
     */
    private String[] ComboBoxUsers() {
        String res = "Select user...,";
        ArrayList<User> myUsers = userDAO.ReadAll(new UserBuilder());
        if(myUsers.size() > 0) {
            for(User u: myUsers) {
                res += u.getNombre() + ",";
            }
        }
        return res.split(",");
    }
    /**
     * set the content of the principal panel 
     * @return the panel with its content
     */
    private JPanel LoginContent() {
        pPrincipal = new JPanel();
        pPrincipal.setLayout(new GridLayout(2, 2));
        
        cbxUserName = new JComboBox<String>(ComboBoxUsers());
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
    private JPanel LoginOptions() {

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
                    JOptionPane.showMessageDialog(myFrame, "invalid user or password", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    userName = cbxUserName.getSelectedItem().toString();
                    userPassword = txtUserPassword.getText();
                    String condition = "nombre: " + userName + ", password: " + userPassword;
                    User mio = userDAO.FindByColumnName(condition, "and", new UserBuilder());
                    if(mio != null) {
                        new PanelPrincipal(db_config, mio.getId_pk(), myFrame, cursor, cuentaDAO);
                        myFrame.dispose();
                    } else {
                        JOptionPane.showMessageDialog(myFrame, "invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        btnCancel = new JButton("Cancel");
        option.add(btnCancel);

        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(JOptionPane.showConfirmDialog(myFrame, "are you sure?", "Exit", JOptionPane.OK_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION) {
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
    public void CreateUI(String frameTitle) {
        myFrame = new JFrame(frameTitle);
        myFrame.setSize(400, 200);
        myFrame.setLayout(new BorderLayout());

        myFrame.add(LoginContent(), BorderLayout.CENTER);
        myFrame.add(LoginOptions(), BorderLayout.SOUTH);


        myFrame.setVisible(true);
        myFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        myFrame.setResizable(false);
    }
}
