package com.company;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddTransaction extends JFrame {

    public JButton btnAdd = new JButton("Add");
    public JButton btnCancel = new JButton("Cancel");
    public JButton btnprint = new JButton("Print Receipt");
    public JButton btnload = new JButton("Load");
    public JButton btnsave = new JButton("Save");

    public SQLiteDataAccess adapter;
    public JTextField txtPurchaseID = new JTextField(20);
    public JTextField txtProductID = new JTextField(20);
    public JTextField txtCustomerID = new JTextField(20);
    public JTextField txtQuantity = new JTextField(20);
    private DefaultTableModel model = new DefaultTableModel();
    String[] labels = {"PurchaseID ", "ProductID ", "CustomerID ", "Quantity", "Tax", "Total"};
    private JTable table = new JTable(model);

    private double taxRate;
    private double taxTotal;
    private double cost;
    private double tax;
    private double total;
    private double amount;
    private String name;
    private String productName;

    InetAddress host = InetAddress.getLocalHost();
    Socket socket = null;
    ObjectOutputStream oos = null;
    ObjectInputStream ois = null;

    public AddTransaction() throws UnknownHostException {
        this.setTitle("Add Transaction");
        this.setSize(600, 500);
        this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));
        JPanel addPanel = new JPanel();
        addPanel.setLayout(new BoxLayout(addPanel, BoxLayout.PAGE_AXIS));
        addPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
        JPanel line1 = new JPanel();
        line1.add(new JLabel("PurchaseID "));
        line1.add(txtPurchaseID);
        addPanel.add(line1, BOTTOM_ALIGNMENT);

        JPanel line2 = new JPanel(new FlowLayout());
        line2.add(new JLabel("ProductID "));
        line2.add(txtProductID);
        addPanel.add(line2);

        JPanel line3 = new JPanel(new FlowLayout());
        line3.add(new JLabel("CustomerID "));
        line3.add(txtCustomerID);
        addPanel.add(line3);

        JPanel line4 = new JPanel(new FlowLayout());
        line4.add(new JLabel("Quantity "));
        line4.add(txtQuantity);
        addPanel.add(line4);

        JPanel panelButtons = new JPanel(new FlowLayout());
        panelButtons.add(btnAdd);
        panelButtons.add(btnload);
        panelButtons.add(btnsave);
        panelButtons.add(btnCancel);
        panelButtons.add(btnprint);
        btnprint.setVisible(false);
        addPanel.add(panelButtons);
        this.getContentPane().add(addPanel);
        //model.setDataVector(db.getTransactionMatrix().getMatrix(), labels);
        //add the table to the frame
        table.setEnabled(true);
        this.add(new JScrollPane(table));
        btnAdd.addActionListener(new AddButtonListener());
        btnCancel.addActionListener(new CancelButtonListener());
        btnprint.addActionListener(new PrintListener());
        btnload.addActionListener(new LoadButtonListener());
        table.getModel().addTableModelListener(new TableModelListener() {

            public void tableChanged(TableModelEvent e) {
                if(e.getFirstRow() == -1 || e.getColumn() == -1)
                    return;
                System.out.println(e.getFirstRow() + " " + e.getColumn());
                System.out.println(table.getValueAt(e.getFirstRow(), e.getColumn()));
                if(table.getValueAt(e.getFirstRow(), e.getColumn()).toString().length() == 0 && e.getColumn() == 0)
                {
                    try {
                        deleteRow(e.getFirstRow());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                else
                {
                    try {
                        editValue(e.getFirstRow(), labels[e.getColumn()], table.getValueAt(e.getFirstRow(), e.getColumn()).toString());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        setVisible(true);
        pack();
    }
    public void deleteRow(int row) throws IOException {
        socket = new Socket(host.getHostName(), 9000);
        //write to socket using ObjectOutputStream
        oos = new ObjectOutputStream(socket.getOutputStream());
        System.out.println("Sending request to Socket Server");
        oos.writeObject("transaction");
        oos.writeObject("delete");
        oos.writeObject(row);
        //read the server response message
        ois = new ObjectInputStream(socket.getInputStream());

        ois.close();
        oos.close();
    }
    public void getData() throws IOException, ClassNotFoundException {
        socket = new Socket(host.getHostName(), 9000);
        //write to socket using ObjectOutputStream
        oos = new ObjectOutputStream(socket.getOutputStream());
        System.out.println("Sending request to Socket Server");
        oos.writeObject("transaction");
        oos.writeObject("load");
        //read the server response message
        ois = new ObjectInputStream(socket.getInputStream());
        Object[][] message = (Object[][]) ois.readObject();
        model.setDataVector(message, labels);

        //System.out.println("Message: " + message);
        //close resources
        ois.close();
        oos.close();
    }
    public void editValue(int row, String col, String value) throws IOException {
        socket = new Socket(host.getHostName(), 9000);
        //write to socket using ObjectOutputStream
        oos = new ObjectOutputStream(socket.getOutputStream());
        System.out.println("Sending request to Socket Server");
        oos.writeObject("transaction");
        oos.writeObject("edit");
        oos.writeObject(row);
        oos.writeObject(col);
        oos.writeObject(value);
        //read the server response message
        ois = new ObjectInputStream(socket.getInputStream());

        ois.close();
        oos.close();
    }

    public void addRow(Object[] row)
    {
        model.addRow(row);
    }
//    public String gs(Object o){
//        try {
//            String s = Integer.parseInt(id);
//        } catch (NumberFormatException e) {
//            JOptionPane.showMessageDialog(null, "PurchaseID is invalid!");
//            return;
//        }
//    }
class LoadButtonListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            getData();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
    class PrintListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        DecimalFormat df2 = new DecimalFormat("#.##");

        Date date = new Date();
        //System.out.println(formatter.format(date));
        System.out.println("_____________________________________");
        System.out.println("|  Thank you for shopping at CJ's   |");
        System.out.println("|               Mart                |");
        System.out.println("| " + name + "                     |");
        System.out.println("| " + formatter.format(date) + "               |");
        System.out.println("|                                   |");
        for(int i = 0; i < amount; i++) {
            System.out.println("| - " + productName + ": $" + df2.format(cost) + "     + Tax: $" + df2.format(tax) + "  |");
        }
        System.out.println("|  _______________________________  |");
        System.out.println("|   Total: $" + df2.format(total) + "  Tax: " + df2.format(taxTotal) + "          |");
        System.out.println("|___________________________________|");
    }
}
    class AddButtonListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            TransactionModel ModelT = new TransactionModel();
            CustomerModel ModelC = new CustomerModel();
            String id = txtPurchaseID.getText();
            if (id.length() == 0) {
                JOptionPane.showMessageDialog(null, "PurchaseID cannot be null!");
                return;
            }
            try {
                ModelT.mPurchaseID = Integer.parseInt(id);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "PurchaseID is invalid!");
                return;
            }

            String Proid = txtProductID.getText();
            if (Proid.length() == 0) {
                JOptionPane.showMessageDialog(null, "ProductID cannot be null!");
                return;
            }
            try {
                ModelT.mProductID = Integer.parseInt(Proid);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "ProductID is invalid!");
                return;
            }

            String Cusid = txtCustomerID.getText();
            if (Proid.length() == 0) {
                JOptionPane.showMessageDialog(null, "CustomerID cannot be null!");
                return;
            }
            try {
                ModelT.mCustomerID = Integer.parseInt(Cusid);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "CustomerID is invalid!");
                return;
            }

            String quant = txtQuantity.getText();
            try {
                ModelT.mQuantity = Double.parseDouble(quant);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Quantity is invalid!");
                return;
            }
            //write to socket using ObjectOutputStream
            System.out.println("Sending request to Socket Server");
            Object[][] message = null, message1 = null;
            try {
                socket = new Socket(host.getHostName(), 9000);
                oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject("product");
                oos.writeObject("load");
                ois = new ObjectInputStream(socket.getInputStream());

                message = (Object[][]) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            try {
                socket = new Socket(host.getHostName(), 9000);
                oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject("customer");
                oos.writeObject("load");
                ois = new ObjectInputStream(socket.getInputStream());
                message1 = (Object[][]) ois.readObject();

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

            String s = (String) message[ModelT.mProductID - 1][2];
            cost = Double.parseDouble(s);
            s = (String) message[ModelT.mProductID - 1][4];
            taxRate = Double.parseDouble(s);
            amount = ModelT.mQuantity;
            tax = cost * taxRate;
            taxTotal = tax * amount;
            total = (amount * cost) + taxTotal;
            s = (String) message1[ModelT.mCustomerID - 1][1];
            name = s;
            s = (String) message[ModelT.mProductID - 1][1];
            productName = s;
            System.out.println(s);

            DecimalFormat df2 = new DecimalFormat("#.##");
            Object[] temp = {ModelT.mPurchaseID, ModelT.mProductID, ModelT.mCustomerID, df2.format(ModelT.mQuantity), df2.format(taxTotal) , df2.format(total)};
            ModelT.mTax = taxTotal;
            ModelT.mTotal = total;
            String sql = "INSERT INTO Trans(PurchaseID, ProductID, CustomerID, Quantity, Tax, Total) VALUES " + ModelT;
            System.out.println(sql);
            try {
                socket = new Socket(host.getHostName(), 9000);
                oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject("transaction");
                oos.writeObject("save");
                oos.writeObject(sql);

            } catch (IOException e) {
                e.printStackTrace();
            }

            addRow(temp);
            btnprint.setVisible(true);
        }

    }

    class CancelButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            dispose();
        }
    }


}
