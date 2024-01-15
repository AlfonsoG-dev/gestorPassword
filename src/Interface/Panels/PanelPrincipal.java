package Interface.Panels;

import java.sql.Connection;
import java.sql.Savepoint;
import java.sql.Statement;
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

    /**
     * the data table for the panel
     */
    private JTable mTable;
    /**
     * the model for the table
     */
    private DefaultTableModel tableModel;
    /**
     * the panel frame
     */
    private JFrame myFrame;
    /**
     * the main frame of login
     */
    private JFrame mainFrame;
    /**
     * label that describes the panel
     */
    private JLabel headerLabel;
    /**
     * panel of options
     */
    private JPanel controlPanel;
    /**
     * utils that uses the queryUtils for build DAO operations
     */
    private QueryUtils queryUtils;
    /**
     * DAO class for cuenta
     */
    private QueryDAO<Cuenta> cuentaDAO;
    /**
     * database configuration
     */
    private DbConfig myConfig;
    /**
     * user that had been logged
     */
    private int loggedUser;
    /**
     * Connection instance for transaction use
     */
    private Connection cursor;
    /**
     * point to rollback or commit changes
     */
    private Savepoint miSave;
    /**
     * constructor
     */
    public PanelPrincipal(DbConfig mConfig, int pLoggedUser, JFrame nMainFrame, Connection miConnection, QueryDAO<Cuenta> nCuentaDAO) {
        myConfig = mConfig;
        loggedUser = pLoggedUser;
        queryUtils = new QueryUtils();
        cuentaDAO = nCuentaDAO;
        cursor = miConnection;
        mainFrame = nMainFrame;
        // set the save point to rollback or commit changes
        try {
            cursor.setAutoCommit(false);
            miSave = cursor.setSavepoint();
        } catch(Exception e) {
            System.err.println(e);
        }

        if(misCuentas().size() > 0) {
            CreateUI("table example", "Gestor Password", 1100, 540);
        } else {
            new PanelRegistro("Register", 400, 900, myConfig, loggedUser, cursor, myFrame, cuentaDAO);
        }
    }
    private void SetAutoImcrement() throws Exception {
        int tableSize = cuentaDAO.ReadAll(new CuentaBuilder()).size()+1;
        String sql = "alter table cuenta AUTO_INCREMENT=" + tableSize;
        Statement stm = cursor.createStatement();
        stm.executeUpdate(sql);
    }
    /**
     * list of cuentas that verify the user_id_fk with the loggedUser
     * @return the cuentas with the same user_id_fk
     */
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
    /**
     * creates the table content from database
     * @param columns: the columns of the table cuenta
     * @return the table content like Object[][]
     */
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
    /**
     * build the cuenta using the table row and column
     * @param row: table row
     * @param column: table column
     * @return the cuenta fron the table using row and column
     */
    private Cuenta BuildCuentaFromTable(int row, int column) {
        String columName = mTable.getColumnName(column);
        String options = columName + ": " + mTable.getValueAt(row, column).toString() + ", user_id_fk: " + loggedUser;
        Cuenta myCuenta = cuentaDAO.FindByColumnName(options, "and", new CuentaBuilder());
        if(myCuenta == null) {
            JOptionPane.showMessageDialog(myFrame, "invalid value of field", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        } else {
            return myCuenta;
        }
    }
    /**
     * list the created cuentas from table that are not present in the database
     * the list of cuentas only works when you insert a new row with its content directly in the table
     * @return the list of cuentas that are not present in the database
     */
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
    /**
     * sets the panel with the table component and its content
     * @param tableText: table component title
     * @param the panel with the table
     */
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
    /**
     * changes the data for the table model, making a request to the database
     */
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
    /**
     * set the panel with the options to manipulate the table like add rows, delete rows or reload
     * @return the panel with the table options
     */
    private JPanel TableOptionComponents() {
        JPanel tableOptions = new JPanel();
        tableOptions.setLayout(new GridLayout(3, 1));
         
        JButton reloadButton = new JButton("R");
        tableOptions.add(reloadButton);
        // reload the table content
        reloadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    int option = JOptionPane.showConfirmDialog(myFrame, "apply changes before reload?", "REALOAD", JOptionPane.YES_NO_CANCEL_OPTION);
                    if(option == JOptionPane.YES_OPTION) {
                        cursor.commit();
                        setNewDataTableModel();
                    } else if(option == JOptionPane.NO_OPTION) {
                        cursor.rollback();
                        SetAutoImcrement();
                        setNewDataTableModel();
                    } else if(option == JOptionPane.CANCEL_OPTION) {
                        // do nothing
                    }
                } catch(Exception er) {
                    System.err.println(er);
                }
            }
        });

        JButton agregarButton = new JButton("+");
        tableOptions.add(agregarButton);
        // add a new row for the table
        agregarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String[] data = {"", "", "", String.valueOf(loggedUser), "", "", ""};
                tableModel.addRow(data);
            }
        });


        JButton eliminarButton = new JButton("-");
        tableOptions.add(eliminarButton);
        // delete the row from the table
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
    /**
     * implements the action handler for the delete button.
     * <br> pre: </br> only works if the selected table column is not: create_at, update_at or password
     * @param deleteButton: panel deleteButton to delte or truncate the cuenta for the database
     */
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
                        String valueOfColumn = mTable.getValueAt(row, column).toString();
                        String options = columName + ": " + valueOfColumn + ", user_id_fk: " + mTable.getValueAt(row, 3);
                        boolean eliminado = cuentaDAO.EliminarRegistro(options, "and", new CuentaBuilder());
                        if(eliminado == true) {
                            tableModel.removeRow(row);
                        } else {
                            JOptionPane.showMessageDialog(myFrame, String.format("Column: %s with value of: %s not found", columName, valueOfColumn),
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(myFrame, "NO TABLE ELEMENT SELECTED");
                    }
                } catch(Exception er) {
                    System.err.println(er);
                }
            }
        });
    }
    /**
     * implements the action handler for the insert button.
     * <br> post: </br> when the table have ListaFaltantes.size() > 0 insert the data, otherwise redirects to PanelRegistro
     * @param insertButton: panel insertButton to register a new cuenta for the database
     * @param weight: weight of the PanelRegistro
     * @param height: height of the PanelRegistro
     */
    private void InsertButtonHandler(JButton insertButton, int weight, int height) {
        insertButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(ListaFaltantes().size() == 0) {
                    new PanelRegistro("Register", weight, height, myConfig, loggedUser, cursor, myFrame, cuentaDAO);
                    myFrame.setEnabled(false);
                } else {
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
                }
            }
        });
    }
    /**
     * implements the action handler for the updateButton.
     * <br> post: </br> redirects to PanelUpdate
     * @param updateButton: panel button to update the cuenta
     * @param weight: weight of the PanelUpdate
     * @param height: height of the PanelUpdate
     */
    private void UpdateButtonHandler(JButton updateButton, int weight, int height) {
        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = mTable.getSelectedRow();
                int column = mTable.getSelectedColumn();
                String columName = mTable.getColumnName(column);
                if(columName.equals("create_at") || columName.equals("update_at") || columName.equals("password")) {
                        JOptionPane.showMessageDialog(myFrame, "to update use 'ID' or 'nombre' or 'email' or 'FK' ", "Error", JOptionPane.ERROR_MESSAGE);
                } else if(row != -1 || column != -1) {
                    Cuenta updateCuenta = BuildCuentaFromTable(row, column);
                    if(updateCuenta != null) {
                        new PanelUpdate("Update", weight, height, updateCuenta, myConfig, myFrame, cuentaDAO);
                        myFrame.setEnabled(false);
                    }
                } else {
                    JOptionPane.showMessageDialog(myFrame, "NO TABLE ELEMENT SELECTED");
                }
            }
        });
    }
    private void CancelButtonHandler(JButton cancelButton) {
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(JOptionPane.showConfirmDialog(myFrame, "Go back to login", "Cancel op", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                    mainFrame.setVisible(true);;
                    myFrame.dispose();
                }
            }
        });
    }
    /**
     * panel that its content if the panel buttons and its action handlers
     * @param hight: hight of the panel
     * @param height: height of the panel
     * @return the panel with the content setted
     */
    private JPanel OptionsComponent(int hight, int height) {
        JPanel optionPanel = new JPanel();
        optionPanel.setLayout(new FlowLayout());

        JButton insertButton = new JButton("Insert");
        InsertButtonHandler(insertButton, height, height);
        optionPanel.add(insertButton);

        JButton updateButton = new JButton("Update");
        UpdateButtonHandler(updateButton, height, height);
        optionPanel.add(updateButton);

        JButton deleteButton = new JButton("Delete");
        DeleteButtonHandler(deleteButton);
        optionPanel.add(deleteButton);

        JButton cancelButton = new JButton("Cancel");
        CancelButtonHandler(cancelButton);
        optionPanel.add(cancelButton);

        return optionPanel;
    }
    /**
     * creates the ui for the current frame
     * @param frameTitle: the frame title
     * @param tableTitle: the table name
     * @param hight: the frame hight
     * @param height: the frame height
     */
    public void CreateUI(String frameTitle, String tableTitle, int hight, int height) {
        myFrame = new JFrame(frameTitle);
        myFrame.setSize(hight, height);
        myFrame.setLayout(new GridLayout(3, 1));

        myFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                try {
                    int option = JOptionPane.showConfirmDialog(myFrame, "save changes before exit?", "save changes", JOptionPane.YES_NO_CANCEL_OPTION);
                    if(option == JOptionPane.YES_OPTION) {
                        cursor.commit();
                        cursor.releaseSavepoint(miSave);
                        System.exit(0);
                    } else if(option == JOptionPane.NO_OPTION) {
                        cursor.rollback();
                        SetAutoImcrement();
                        cursor.releaseSavepoint(miSave);
                        System.exit(0);
                    } else if(option == JOptionPane.CANCEL_OPTION) {
                        myFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                    }
                } catch(Exception e) {
                    System.err.println(e);
                }
            }
        });

        headerLabel = new JLabel("", JLabel.CENTER);

        controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout());
        controlPanel.add(TableOptionComponents(), BorderLayout.EAST);

        myFrame.add(headerLabel);
        myFrame.add(TableComponent(tableTitle), BorderLayout.CENTER);
        myFrame.add(OptionsComponent(600, 700));
        myFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        myFrame.setVisible(true);
        myFrame.setLocationRelativeTo(mainFrame);
        myFrame.setResizable(true);
    }
}

/**
 * helper class to disable cell edition
 */
class NonEditableCell extends DefaultCellEditor {

    public NonEditableCell() {
        super(new JTextField());
    }

    @Override
    public boolean isCellEditable(EventObject e) {
        return false;
    }
}
/**
 * helper class to enable drag and drop for the table
 */
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
            // the data must have this format: id,nombre,email,user_id_fk,password,null,null
            ((DefaultTableModel) table.getModel()).addRow(data.split(","));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
