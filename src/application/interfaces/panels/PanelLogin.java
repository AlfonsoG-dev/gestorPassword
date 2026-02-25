package application.interfaces.panels;

import java.sql.Connection;

import java.util.List;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import application.interfaces.utils.PanelUtils;
import application.models.cuenta.CuentaModel;
import application.models.user.UserModel;
import orm.connection.dao.QueryDAO;
import orm.utils.formats.DbConfig;
import orm.utils.formats.ParamValue;

public class PanelLogin {

    /**
     * login frame
     */
    private JFrame myFrame;
    /**
     * user name options to log in
     */
    private JComboBox<String> cbxUserName;
    /**
     * field to digit user password
     */
    private JTextField txtUserPassword;
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
    private PanelUtils<UserModel> userUtils;
    /**
     * utils for the account DAO
     */
    private PanelUtils<CuentaModel> cuentaUtils;
    /**
     * Constructor
     */
    public PanelLogin(DbConfig myConfig, Connection miConector) {
        cursor = miConector;
        dbConfig = myConfig;
        userUtils = new PanelUtils<>(new QueryDAO<>(cursor, "user", new UserModel()));
        cuentaUtils = new PanelUtils<>(new QueryDAO<>(cursor, "cuenta", new CuentaModel()));
        if(!userUtils.myDataList().isEmpty()) {
            createUI("Loggin");
        } else {
            new PanelLoginUser(dbConfig, cursor, userUtils);
        }
    }
    /**
     * set the users to select in the comboBox
     * @return the comboBox with the users names
     */
    private String[] comboBoxUsers() {
        StringBuilder res = new StringBuilder();
        res.append("Select user...,");
        List<UserModel> myUsers = userUtils.myDataList();
        if(!myUsers.isEmpty()) {
            for(UserModel u: myUsers) {
                res.append(u.getNombre());
                res.append(",");
            }
        }
        return res.toString().split(",");
    }
    /**
     * set the content of the principal panel 
     * @return the panel with its content
     */
    private JPanel loginContent() {
        JPanel pPrincipal = new JPanel();
        pPrincipal.setLayout(new GridLayout(2, 2));
        cbxUserName = new JComboBox<>(comboBoxUsers());
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
        JButton btnIngreso = new JButton("OK");
        option.add(btnIngreso);
        btnIngreso.setMnemonic(KeyEvent.VK_ENTER);

        btnIngreso.addActionListener(e -> {
            String userName = "";
            String userPassword = "";
            if(cbxUserName.getSelectedIndex() == 0 || txtUserPassword.getText().isEmpty()) {
                JOptionPane.showMessageDialog(myFrame,
                        "invalid user or password", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                userName = cbxUserName.getSelectedItem().toString();
                userPassword = txtUserPassword.getText();
                String[] c = {"nombre", "password"};
                String[] v = {userName, userPassword};
                ParamValue condition = new ParamValue(c, v, "and");
                UserModel mio = userUtils.findOperation(condition).get(0);
                if(mio != null) {
                    new PanelPrincipal(dbConfig, mio.getId_pk(), myFrame, cursor, cuentaUtils);
                    myFrame.dispose();
                } else {
                    JOptionPane.showMessageDialog(myFrame,
                            "invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton btnCancel = new JButton("Cancel");
        option.add(btnCancel);

        btnCancel.addActionListener(e -> {
            int option1 = JOptionPane.showConfirmDialog(myFrame,
                    "are you sure?", "Exit", JOptionPane.OK_OPTION, JOptionPane.QUESTION_MESSAGE);
            if(option1 == JOptionPane.OK_OPTION) {
                System.exit(0);
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
        myFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        myFrame.setResizable(false);
    }
}
