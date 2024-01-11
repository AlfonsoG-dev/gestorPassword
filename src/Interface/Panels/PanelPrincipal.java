package Interface.Panels;

import java.awt.GridLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.TransferHandler;
import javax.swing.table.DefaultTableModel;

import Conexion.Query.QueryDAO;
import Config.DbConfig;
import Mundo.Cuentas.Cuenta;
import Mundo.Cuentas.CuentaBuilder;
import Utils.QueryUtils;



public class PanelPrincipal {

    private JTable mTable;
    private DefaultTableModel tableModel;
    private JFrame myFrame;
    private JLabel headerLabel;
    private JPanel controlPanel;
    private QueryUtils queryUtils;
    private QueryDAO<Cuenta> cuentaDAO;
    private DbConfig myConfig;
    private int loggedUser;

    public PanelPrincipal(DbConfig mConfig, int pLoggedUser) {
        myConfig = mConfig;
        loggedUser = pLoggedUser;
        queryUtils = new QueryUtils();
        cuentaDAO = new QueryDAO<>("cuenta", myConfig);
        if(misCuentas().size() > 0) {
            CreateUI("table example", "Gestor Password", 1100, 540);
        } else {
            new PanelRegistro("Register", 400, 900, myConfig, loggedUser);
        }
    }
    private ArrayList<Cuenta> misCuentas() {
        ArrayList<Cuenta> nuevas = new ArrayList<>();
        ArrayList<Cuenta> nCuentas = cuentaDAO.ReadAll(new CuentaBuilder());
        for(Cuenta c: nCuentas) {
            if(c.getUser_id_fk() == loggedUser) {
                nuevas.add(c);
            }
        }
        return nuevas;
    }
    private Object[][] TableContent(String[] columns) {
        ArrayList<Cuenta> cuentaList = misCuentas();
        String results = "";
        for(Cuenta miCuenta: cuentaList) {
            if(miCuenta.getUpdate_at() != null && miCuenta.getUpdate_at().isEmpty() == false) {
                results += queryUtils.GetModelType(miCuenta.GetAllProperties(), true).replace("'", "") + "\n";
            } else if(miCuenta.getUpdate_at() == null) {
                results += queryUtils.GetModelType(miCuenta.GetAllProperties(), true).replace("'", "") + ",null\n";
            }
        }
        String[] datos = results.split("\n");
        Object[][] data = new String[datos.length][columns.length];
        for(int i=0; i<datos.length; ++i) {
            String[] mData = datos[i].split(",");
            data[i] = mData;
        }
        return data;
    }
    private JPanel TableComponent(String tableText) {

        headerLabel.setText(tableText);
        String modelColumns = queryUtils.GetModelColumns(new Cuenta().InitModel(), true);

        String[] columns = modelColumns.split(",");
        
        tableModel = new DefaultTableModel(TableContent(columns), columns);
        mTable = new JTable(tableModel);
        mTable.setDragEnabled(true);
        mTable.setDropMode(DropMode.INSERT_ROWS);
        mTable.setTransferHandler(new TableTransferable());
        /** 
         * disable the PK and FK columns for edition.
         * 1. id_pk
         * 2. user_id_fk
         * 3. create_at
         * 4. update_at
         */
        mTable.getColumnModel().getColumn(0).setCellEditor(new NonEditableCell());
        mTable.getColumnModel().getColumn(3).setCellEditor(new NonEditableCell());
        mTable.getColumnModel().getColumn(5).setCellEditor(new NonEditableCell());
        mTable.getColumnModel().getColumn(6).setCellEditor(new NonEditableCell());

        JScrollPane scroll = new JScrollPane(mTable);
        scroll.setSize(300, 300);
        mTable.setFillsViewportHeight(true);
        controlPanel.add(scroll);
        return controlPanel;
    }
    private void setNewDataTableModel() {
        String modelColumns = queryUtils.GetModelColumns(new Cuenta().InitModel(), true);
        String[] columns = modelColumns.split(",");
        Object[][] contenido = TableContent(columns);
        tableModel = new DefaultTableModel(contenido, columns);
        mTable.setModel(tableModel);

        /** 
         * disable the PK and FK columns for edition.
         * 0. id_pk
         * 3. user_id_fk
         * 5. create_at
         * 6. update_at
         */
        mTable.getColumnModel().getColumn(0).setCellEditor(new NonEditableCell());
        mTable.getColumnModel().getColumn(3).setCellEditor(new NonEditableCell());
        mTable.getColumnModel().getColumn(5).setCellEditor(new NonEditableCell());
        mTable.getColumnModel().getColumn(6).setCellEditor(new NonEditableCell());
    }
    private JPanel TableOptionComponents() {
        JPanel tableOptions = new JPanel();
        tableOptions.setLayout(new GridLayout(3, 1));
         
        JButton reloadButton = new JButton("R");
        tableOptions.add(reloadButton);
        reloadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setNewDataTableModel();
            }
        });

        JButton agregarButton = new JButton("+");
        tableOptions.add(agregarButton);
        agregarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String[] data = {"", "", "", String.valueOf(loggedUser), "", "", ""};
                tableModel.addRow(data);
            }
        });


        JButton eliminarButton = new JButton("-");
        tableOptions.add(eliminarButton);
        eliminarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int tableSize = mTable.getRowCount()-1;
                String cNombre = mTable.getValueAt(tableSize, 1).toString();
                if(cNombre.isEmpty() || cNombre.isBlank()) {
                    tableModel.removeRow(tableSize);
                } else {
                    JOptionPane.showMessageDialog(myFrame, "Cannot remove!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return tableOptions;
    }
    private Cuenta BuildCuentaFromTable(int row, int column) {
        String columName = mTable.getColumnName(column);
        String options = columName + ": " + mTable.getValueAt(row, column).toString() + ", user_id_fk: " + loggedUser;
        Cuenta myCuenta = cuentaDAO.FindByColumnName(options, "and", new CuentaBuilder());
        return myCuenta;
    }
    private ArrayList<Cuenta> ListaFaltantes() {
        ArrayList<Cuenta> faltante = new ArrayList<>();
        int rows = mTable.getRowCount();
        ArrayList<Cuenta> nCuentas = misCuentas();
        Cuenta mia = null;
        if(nCuentas.size() < rows) {
        outter: for(int i=0; i<rows; ++i) {
                String cNombre = mTable.getValueAt(i, 1).toString();
                String cEmail = mTable.getValueAt(i, 2).toString();
                String cUserFk = mTable.getValueAt(i, 3).toString();
                String cPassword = mTable.getValueAt(i, 4).toString();
                if(cNombre.isEmpty() || cEmail.isEmpty() || cPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(myFrame, "invalid empty fields", "Error", JOptionPane.ERROR_MESSAGE);
                    break outter;
                }
                if(cNombre == null || cEmail == null || cPassword == null) {
                    JOptionPane.showMessageDialog(myFrame, "invalid empty fields", "Error", JOptionPane.ERROR_MESSAGE);
                    break outter;
                }
                String condition = "nombre: " + cNombre + ", user_id_fk: " + cUserFk;
                Cuenta buscada = cuentaDAO.FindByColumnName(condition, "and", new CuentaBuilder());
                if(buscada == null) {
                    mia = new Cuenta(cNombre, cEmail, Integer.parseInt(cUserFk), cPassword);
                    mia.setCreate_at();
                    faltante.add(mia);
                }
            }
        }
        return faltante;
    }
    private void DeleteButtonHandler(JButton deleteButton) {
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = mTable.getSelectedRow();
                int column = mTable.getSelectedColumn();
                String columName = mTable.getColumnName(column);
                try {
                    if(columName.equals("create_at") || columName.equals("update_at") || columName.equals("password")) {
                        JOptionPane.showMessageDialog(myFrame, "to delete use 'ID' or 'nombre' or 'email' or 'FK' ", "Error", JOptionPane.ERROR_MESSAGE);
                    } else if(mTable.getSelectedRow() != -1 && JOptionPane.showConfirmDialog(myFrame, "Do you want to remove?", "Remove operation",
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION && row != -1 && column != -1) {
                        String options = columName + ": " + mTable.getValueAt(row, column).toString() + ", user_id_fk: " + mTable.getValueAt(row, 3);
                        tableModel.removeRow(row);
                        cuentaDAO.EliminarRegistro(options, "and", new CuentaBuilder());
                    } else {
                        JOptionPane.showMessageDialog(myFrame, "NO TABLE ELEMENT SELECTED");
                    }
                } catch(Exception er) {
                    System.err.println(er);
                }
            }
        });
    }
    private JPanel OptionsComponent(int hight, int height) {
        JButton insertButton = new JButton("Insert");
        insertButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(ListaFaltantes().size() > 0) {
                    try {
                        for(Cuenta c: ListaFaltantes()) {
                            String condition = "nombre: " + c.getNombre() + ", user_id_fk: " + c.getUser_id_fk();
                            if(JOptionPane.showConfirmDialog(myFrame, "Do you want to register?", "Register operation", 
                                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION) {
                                cuentaDAO.InsertNewRegister(c, condition, "and", new CuentaBuilder());
                            }
                        }
                    } catch(Exception er) {
                        System.err.println(er);
                    } finally {
                        JOptionPane.showMessageDialog(myFrame, "reload the window to see the changes", "INFO", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    new PanelRegistro("Register", hight, height, myConfig, loggedUser);
                    myFrame.setVisible(false);
                }
            }
        });

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = mTable.getSelectedRow();
                int column = mTable.getSelectedColumn();
                String columName = mTable.getColumnName(column);
                if(columName.equals("create_at") || columName.equals("update_at") || columName.equals("password")) {
                        JOptionPane.showMessageDialog(myFrame, "to update use 'ID' or 'nombre' or 'email' or 'FK' ", "Error", JOptionPane.ERROR_MESSAGE);
                } else if(row != -1 || column != -1) {
                    Cuenta updateCuenta = BuildCuentaFromTable(row, column);
                    new PanelUpdate("Update", hight, height, updateCuenta, myConfig);
                    myFrame.setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(myFrame, "NO TABLE ELEMENT SELECTED");
                }
            }
        });


        JButton deleteButton = new JButton("Delete");
        DeleteButtonHandler(deleteButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(JOptionPane.showConfirmDialog(myFrame, "Do you want to go back to login?", "Cancel operation",
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION) {
                    new PanelLogin(myConfig);
                    myFrame.setVisible(false);
                }
            }
        });

        JPanel optionPanel = new JPanel();
        optionPanel.setLayout(new FlowLayout());
        optionPanel.add(insertButton);
        optionPanel.add(updateButton);
        optionPanel.add(deleteButton);
        optionPanel.add(cancelButton);
        return optionPanel;
    }
    public void CreateUI(String frameTitle, String tableTitle, int hight, int height) {
        myFrame = new JFrame(frameTitle);
        myFrame.setSize(hight, height);
        myFrame.setLayout(new GridLayout(3, 1));

        myFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
        
        headerLabel = new JLabel("", JLabel.CENTER);

        controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout());
        controlPanel.add(TableOptionComponents(), BorderLayout.EAST);

        myFrame.add(headerLabel);
        myFrame.add(TableComponent(tableTitle), BorderLayout.CENTER);
        myFrame.add(OptionsComponent(600, 700));
        myFrame.setVisible(true);
        myFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        myFrame.setResizable(true);
    }
}

// to disable cell edition
class NonEditableCell extends DefaultCellEditor {

    public NonEditableCell() {
        super(new JTextField());
    }

    @Override
    public boolean isCellEditable(EventObject e) {
        return false;
    }
}
class TableTransferable extends TransferHandler {
    @Override
    public boolean canImport(TransferSupport support) {
        return support.isDataFlavorSupported(DataFlavor.stringFlavor);
    }
    @Override
    public boolean importData(TransferHandler.TransferSupport support) {
        if (!canImport(support)) {
            return false;
        }

        JTable table = (JTable) support.getComponent();
        Transferable transferable = support.getTransferable();

        try {
            String data = (String) transferable.getTransferData(DataFlavor.stringFlavor);
            ((DefaultTableModel) table.getModel()).addRow(data.split(","));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
