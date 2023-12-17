package Interface.Panels;

import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Conexion.Query.QueryDAO;
import Config.DbConfig;
import Mundo.Cuentas.Cuenta;
import Mundo.Cuentas.CuentaBuilder;
import Mundo.Users.User;
import Mundo.Users.UserBuilder;

public class PanelRegistro {
    private JFrame myFrame;
    private QueryDAO<User> userDAO;
    private JLabel headerLabel;
    private JTextField txtNombre;
    private JTextField txtEmail;
    private JTextField txtPassword;
    private JComboBox<String> cbxUser;
    private JPanel pPrincipal;
    public PanelRegistro(String frameTitle, int hight, int height, DbConfig myConfig) {
        userDAO = new QueryDAO<User>("user", myConfig);
        CreateUI(frameTitle, hight, height, myConfig);
    }
    public String[] ComboBoxUsers() {
        String result = "select a user...,";
        ArrayList<User> users = userDAO.ReadAll(new UserBuilder());
        for(User u: users) {
            result += u.getNombre() + ",";
        }
        return result.split(",");
    }
    public JPanel OptionsComponent() {
        JPanel pOptions = new JPanel();
        pOptions.setLayout(new GridLayout(4, 2));
        pOptions.add(new JLabel(" nombre"));
        pOptions.add(txtNombre = new JTextField());
        pOptions.add(new JLabel(" email"));
        pOptions.add(txtEmail = new JTextField());
        pOptions.add(new JLabel(" user_id"));
        pOptions.add(cbxUser = new JComboBox<String>(ComboBoxUsers()));
        pOptions.add(new JLabel(" password"));
        pOptions.add(txtPassword = new JTextField());

        return pOptions;
    }
    // inserts a new element to the database
    public void OkButtonHandler(JButton OKButton, DbConfig myConfig) {
        QueryDAO<Cuenta> cuentaDAO = new QueryDAO<Cuenta>("cuenta", myConfig);
            OKButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        String nombre = txtNombre.getText();
                        String email = txtEmail.getText();
                        String selectedUser = cbxUser.getSelectedItem().toString();
                        int selectedUserId = userDAO.FindByColumnName("nombre: " + selectedUser, "and", new UserBuilder()).getId_pk();
                        String password = txtPassword.getText();
                        Cuenta nueva = new Cuenta(nombre, email, selectedUserId, password);
                        nueva.setCreate_at();
                        String condition = "nombre: "  + nueva.getNombre();
                        cuentaDAO.InsertNewRegister(nueva, condition, "and", new CuentaBuilder());
                        System.exit(0);
                    } catch(Exception ex) {
                        System.out.println(ex);
                    }
                }
            });
    }
    public void CreateUI(String frameTitle,int hight, int height, DbConfig myConfig) {
        myFrame = new JFrame(frameTitle);
        myFrame.setSize(hight, height);
        myFrame.setLayout(new GridLayout(3, 1));


        headerLabel = new JLabel("Register", JLabel.CENTER);

        pPrincipal = new JPanel();
        pPrincipal.setLayout(new GridLayout(2, 1));

        pPrincipal.add(OptionsComponent());

        JPanel options = new JPanel();
        options.setLayout(new FlowLayout());

        JButton OKButton = new JButton("OK");
        options.add(OKButton);
        OkButtonHandler(OKButton, myConfig);
        options.add(new JButton("cancel"));
        pPrincipal.add(options);

        myFrame.add(headerLabel);
        myFrame.add(pPrincipal);
        myFrame.setVisible(true);
        myFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
}
