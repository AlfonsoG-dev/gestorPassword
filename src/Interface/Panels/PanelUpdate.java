package Interface.Panels;

import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Interface.Utils.PanelUtils;
import ORM.Utils.Formats.DbConfig;
import ORM.Utils.Formats.ParamValue;

import Models.Cuenta.CuentaModel;
import Models.Cuenta.CuentaODM;

public class PanelUpdate {
    /**
     * frame of the current class
     */
    private JFrame myFrame;
    /**
     * logged user
     */
    private int loggedUser;
    /**
     * principal panel of the current Class
     */
    private JPanel pPrincipal;
    /**
     * id of the selected cuenta
     */
    private JTextField txtIdPk;
    /**
     * name of the selected cuenta
     */
    private JTextField txtNombre;
    /**
     * email of the selected cuenta
     */
    private JTextField txtEmail;
    /**
     * user_id_fk of the selected cuenta
     * it must be equals to the loggedUser
     */
    private JTextField txtUserId;
    /**
     * password of the selected cuenta
     */
    private JTextField txtPassword;
    /**
     * frame of the main Class
     */
    private JFrame mainFrame;
    /**
     * DAO class for account
     */
    private PanelUtils<CuentaModel> cuentaUtils;
    /**
     * constructor
     */
    public PanelUpdate(String frameTitle, int width, int height, CuentaModel updateCuenta, DbConfig nConfig,
            JFrame nMainFrame, PanelUtils<CuentaModel> nCuentaUtils) {
        loggedUser = updateCuenta.getUser_id_fk();
        mainFrame = nMainFrame;
        cuentaUtils = nCuentaUtils;
        createUI(
                frameTitle,
                width,
                height,
                updateCuenta
        );
    }
    /**
     * set the content for the principal panel of the current frame
     * @param updateCuenta: selected cuenta of the main frame table
     * @return the panel with its content setted
     */
    private JPanel optionsComponent(CuentaModel updateCuenta) {

        txtUserId = new JTextField(String.valueOf(loggedUser));
        txtUserId.setEditable(false);

        txtIdPk = new JTextField(String.valueOf(updateCuenta.getId_pk()));
        txtIdPk.setEditable(false);

        JPanel pOptions = new JPanel();
        pOptions.setLayout(new GridLayout(5, 2));
        pOptions.add(new JLabel("ID_pk"));
        pOptions.add(txtIdPk);
        pOptions.add(new JLabel("Nombre"));
        pOptions.add(txtNombre = new JTextField(updateCuenta.getNombre()));
        pOptions.add(new JLabel("Email"));
        pOptions.add(txtEmail = new JTextField(updateCuenta.getEmail()));
        pOptions.add(new JLabel("User_Id_fk"));
        pOptions.add(txtUserId);
        pOptions.add(new JLabel("Password"));
        pOptions.add(txtPassword = new JTextField(updateCuenta.getPassword()));
        return pOptions;
    }
    /**
     * implements the OKButton handler for the current frame 
     * @param oKButton: panel oKButton to update the cuenta of the database
     * @param myConfig: database configuration
     * @param toUpdate: selected cuenta of the main frame table
     */
    private void okButtonHandler(JButton OKButton, CuentaModel model) {
        OKButton.setMnemonic(KeyEvent.VK_ENTER);
        OKButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CuentaODM toUpdate = new CuentaODM(model);
                if(txtNombre.getText() != null && txtNombre.getText() != toUpdate.getNombre()) {
                    toUpdate.setNombre(txtNombre.getText());
                }
                if(txtEmail.getText() != null && txtEmail.getText() != toUpdate.getEmail()) {
                    toUpdate.setEmail(txtEmail.getText());
                }
                if(txtPassword.getText() != null && txtPassword.getText() != toUpdate.getPassword()) {
                    toUpdate.setPassword(txtPassword.getText());
                }
                toUpdate.makeUpdate_at();
                try {
                    int options = JOptionPane.showConfirmDialog(
                            myFrame,
                            "Do you want to update?",
                            "Update operation",
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.QUESTION_MESSAGE
                    );
                    String[]
                        c = {"id_pk"},
                        v = {txtIdPk.getText()};

                    ParamValue condition = new ParamValue(
                            c, 
                            v,
                            "and"
                    );
                    if(options == JOptionPane.OK_OPTION) {
                        cuentaUtils.updateOperation(toUpdate, condition);
                        mainFrame.setEnabled(true);
                        myFrame.dispose();
                    }
                } catch(Exception er) {
                    er.printStackTrace();
                    cuentaUtils.errorMessage(
                            myFrame,
                            "Error while trying to update register",
                            "UpdateError"
                    );
                } finally {
                        JOptionPane.showMessageDialog(
                                myFrame,
                                "reload the window to see the changes",
                                "INFO",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                }
            }
        });
    }
    /**
     * implements the cancelButton action handler
     * @param cancelButton: panel cancelButton to go back to the mainFrame panel
     * @param myConfig: database configuration
     */
    private void cancelButtonHandler(JButton cancelButton) {
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int options = JOptionPane.showConfirmDialog(
                        myFrame,
                        "Do you want to cancel?",
                        "Update operation",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );
                if(options == JOptionPane.OK_OPTION) {
                    mainFrame.setEnabled(true);
                    myFrame.dispose();
                }
            }
        });
    }
    /**
     * sets a new JPanel with the buttons and its handlers for the current frame
     * @param myConfig: database configuration
     * @param toUpdate: selected cuenta of the main frame table 
     */
    private JPanel operationsComponent(CuentaModel toUpdate) {
        JPanel pOperations = new JPanel();
        pOperations.setLayout(new GridLayout(1, 2));
        JButton oKButton = new JButton("OK");
        pOperations.add(oKButton);
        okButtonHandler(oKButton, toUpdate);


        JButton cancelButton = new JButton("Cancel");
        pOperations.add(cancelButton);
        cancelButtonHandler(cancelButton);
        return pOperations;
    }
    /**
     * creates the ui for the current frame
     * @param frameTitle: frame title
     * @param width: frame width
     * @param height: frame height
     * @param updateCuenta: selected cuenta from the main frame table
     * @param myConfig: database configuration
     */
    public void createUI(String frameTitle, int width, int height, CuentaModel updateCuenta) {
        myFrame = new JFrame(frameTitle);
        myFrame.setSize(width, height);
        myFrame.setLayout(new GridLayout(3, 1));

        myFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                mainFrame.setEnabled(true);
                myFrame.dispose();
            }
        });

        JLabel headerLabel = new JLabel("Update", JLabel.CENTER);

        pPrincipal = new JPanel();
        pPrincipal.setLayout(new BorderLayout());

        pPrincipal.add(optionsComponent(updateCuenta), BorderLayout.NORTH);
        pPrincipal.add(operationsComponent(updateCuenta), BorderLayout.SOUTH);

        myFrame.add(headerLabel);
        myFrame.add(pPrincipal);
        myFrame.setVisible(true);
        myFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
}
