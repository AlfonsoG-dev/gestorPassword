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
import javax.swing.JComboBox;
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
    private JComboBox<String> cbxSize; 
    private JCheckBox cbxLetter;
    private JCheckBox cbxSimbol;
    private JCheckBox cbxNumber;
    private PasswordOptions pOptions;
    private PanelUtils<Cuenta> cuentaUtils;

    public PanelPassword(JFrame nMainFrame, JTextField nPassword, PanelUtils<Cuenta> nCuentaUtils) {
        mainFrame = nMainFrame;
        txtPassword = nPassword;
        cuentaUtils = nCuentaUtils;
        createUI();
    }
    private void okButtonHandler(JButton okButton) {
        okButton.setMnemonic(KeyEvent.VK_ENTER);
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int size    = Integer.parseInt(cbxSize.getSelectedItem().toString());
                boolean 
                    letters = cbxLetter.isSelected(),
                    simbols = cbxSimbol.isSelected(),
                    numbers = cbxNumber.isSelected();
                pOptions = new PasswordOptions(size, letters, simbols, numbers);
                if(size != -1 && size > 4 && (letters || simbols || numbers)) {
                    txtPassword.setText(cuentaUtils.generatePassword(pOptions).toString());
                    cuentaUtils.setPasswordValues(pOptions);
                    mainFrame.setEnabled(true);
                    myFrame.dispose();
                } else {
                    JOptionPane.showMessageDialog(
                            myFrame,
                            "invalid password options",
                            "Generator Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });
    }

    private void cancelButtonHandler(JButton cancelButton) {
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                txtPassword.setText("");
                mainFrame.setEnabled(true);
                myFrame.dispose();
            }
        });
    }
    private JPanel optionComponent() {
        JPanel options = new JPanel();
        options.setLayout(new FlowLayout());

        JButton okButton = new JButton("OK");
        okButtonHandler(okButton);
        options.add(okButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButtonHandler(cancelButton);
        options.add(cancelButton);

        return options;
    }
    private JPanel panelContentComponent() {
        pPrincipal = new JPanel();
        pPrincipal.setLayout(new GridLayout(3, 2));
        pPrincipal.add(new JLabel());
        pPrincipal.add(new JLabel());


        String numbers = "";
        for(int i=5; i<=10; ++i) {
            numbers += i + ",";
        }
        JPanel miPanel = new JPanel();
        miPanel.setLayout(new GridLayout(1, 3));
        cbxSize = new JComboBox<String>(numbers.split(","));
        pPrincipal.add(new JLabel("size: "));
        miPanel.add(cbxSize);
        // add empty labels to make the combobox smaller  
        miPanel.add(new JLabel());
        miPanel.add(new JLabel());
        // add panel of combobox
        pPrincipal.add(miPanel);

        pPrincipal.add(new JLabel("Allow: "));

        JPanel checks = new JPanel();
        checks.setLayout(new FlowLayout());

        checks.add(cbxLetter = new JCheckBox("letters"));
        checks.add(cbxSimbol = new JCheckBox("simbols"));
        checks.add(cbxNumber = new JCheckBox("numbers"));

        pPrincipal.add(checks);

        return pPrincipal;
    }

    public void createUI() {
        myFrame = new JFrame("Password Generator");
        myFrame.setSize(500, 300);
        myFrame.setLayout(new GridLayout(2, 1));
        
        myFrame.addWindowListener(new WindowAdapter() {
            // changes the close operation
            public void windowClosing(WindowEvent we) {
                mainFrame.setEnabled(true);
                myFrame.dispose();
            }
        });
        
        myFrame.add(panelContentComponent());
        myFrame.add(optionComponent());

        myFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        myFrame.setVisible(true);
    }

}
