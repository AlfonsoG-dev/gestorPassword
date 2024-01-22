package Interface.Panels;

import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Interface.Utils.PanelUtils;
import Interface.Utils.PasswordOptions;
import Mundo.Cuentas.Cuenta;

public final class PanelPassword {

    private JFrame myFrame;
    private JFrame mainFrame;
    private JPanel pPrincipal;
    private JTextField txtPassword;
    private JTextField txtSize; 
    private JCheckBox cbxLetter;
    private JCheckBox cbxSimbol;
    private JCheckBox cbxNumber;
    private PasswordOptions pOptions;
    private PanelUtils<Cuenta> cuentaUtils;

    public PanelPassword(JFrame nMainFrame, JTextField nPassword, PanelUtils<Cuenta> nCuentaUtils) {
        mainFrame = nMainFrame;
        txtPassword = nPassword;
        cuentaUtils = nCuentaUtils;
        CreateUI();
    }
    private void OkButtonHandler(JButton okButton) {
        okButton.setMnemonic(KeyEvent.VK_ENTER);
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int size = !txtSize.getText().isEmpty() ? Integer.parseInt(txtSize.getText()) : -1; 
                boolean letters = cbxLetter.isSelected();
                boolean simbols = cbxSimbol.isSelected();
                boolean numbers = cbxNumber.isSelected();
                pOptions = new PasswordOptions(size, letters, simbols, numbers);
                if(size != -1 && size > 4 && (letters || simbols || numbers)) {
                    txtPassword.setText(cuentaUtils.GeneratePassword(pOptions).toString());
                    cuentaUtils.SetPasswordValues(pOptions);
                    mainFrame.setEnabled(true);
                    myFrame.dispose();
                } else {
                    JOptionPane.showMessageDialog(myFrame, "invalid password options", "Generator Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void CancelButtonHandler(JButton cancelButton) {
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                txtPassword.setText("");
                mainFrame.setEnabled(true);
                myFrame.dispose();
            }
        });
    }
    private JPanel OptionComponent() {
        JPanel options = new JPanel();
        options.setLayout(new FlowLayout());

        JButton okButton = new JButton("OK");
        OkButtonHandler(okButton);
        options.add(okButton);

        JButton cancelButton = new JButton("Cancel");
        CancelButtonHandler(cancelButton);
        options.add(cancelButton);

        return options;
    }
    private JPanel PanelContentComponent() {
        pPrincipal = new JPanel();
        pPrincipal.setLayout(new GridLayout(2, 2));


        pPrincipal.add(new JLabel("size: "));
        pPrincipal.add( txtSize = new JTextField());

        pPrincipal.add(new JLabel("Allow: "));

        JPanel checks = new JPanel();
        checks.setLayout(new FlowLayout());

        checks.add(cbxLetter = new JCheckBox("letters"));
        checks.add(cbxSimbol = new JCheckBox("simbols"));
        checks.add(cbxNumber = new JCheckBox("numbers"));

        pPrincipal.add(checks);

        return pPrincipal;
    }

    public void CreateUI() {
        myFrame = new JFrame("Password Generator");
        myFrame.setSize(400, 300);
        myFrame.setLayout(new GridLayout(2, 1));
        
        myFrame.addWindowListener(new WindowAdapter() {
            // changes the close operation
            public void windowClosing(WindowEvent we) {
                myFrame.dispose();
                mainFrame.setEnabled(true);
            }
        });
        
        myFrame.add(PanelContentComponent());
        myFrame.add(OptionComponent());

        myFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        myFrame.setVisible(true);
    }

}
