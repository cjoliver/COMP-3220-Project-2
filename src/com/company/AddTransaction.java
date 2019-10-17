package com.company;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddTransaction extends JFrame {

    public JButton btnAdd = new JButton("Add");
    public JButton btnCancel = new JButton("Cancel");
    public JButton btnprint = new JButton("Print Receipt");

    public SQLiteDataAccess adapter;
    public JTextField txtPurchaseID = new JTextField(20);
    public JTextField txtProductID = new JTextField(20);
    public JTextField txtCustomerID = new JTextField(20);
    public JTextField txtQuantity = new JTextField(20);
    private DefaultTableModel model = new DefaultTableModel();
    private JTable table = new JTable(model);

    private double taxRate;
    private double taxTotal;
    private double cost;
    private double tax;
    private double total;
    private double amount;
    private String name;
    private String productName;

    public AddTransaction(SQLiteDataAccess db) {
        adapter = db;
        this.setTitle("Add Transaction");
        this.setSize(600, 500);
        this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));
        String[] labels = {"PurchaseID ", "ProductID ", "CustomerID ", "Quantity", "Tax", "Total"};
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
        panelButtons.add(btnCancel);
        panelButtons.add(btnprint);
        btnprint.setVisible(false);
        addPanel.add(panelButtons);
        this.getContentPane().add(addPanel);
        model.setDataVector(db.getTransactionMatrixMatrix().getMatrix(), labels);
        //add the table to the frame
        table.setEnabled(false);
        this.add(new JScrollPane(table));
        btnAdd.addActionListener(new AddButtonListener());
        btnCancel.addActionListener(new CancelButtonListener());
        btnprint.addActionListener(new PrintListener());
        setVisible(true);
        pack();
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
    class AddButtonListener implements ActionListener {

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
            String s = (String) adapter.getProductMatrix().get(ModelT.mProductID - 1,2);
            cost = Double.parseDouble(s);
            s = (String) adapter.getProductMatrix().get(ModelT.mProductID - 1, 4);
            taxRate = Double.parseDouble(s);
            amount = ModelT.mQuantity;
            tax = cost * taxRate;
            taxTotal = tax * amount;
            total = (amount * cost) + taxTotal;
            s = (String) adapter.getCustomerMatrix().get(ModelT.mCustomerID - 1, 1);
            name = s;
            s = (String) adapter.getProductMatrix().get(ModelT.mProductID - 1, 1);
            productName = s;
            System.out.println(s);

            DecimalFormat df2 = new DecimalFormat("#.##");
            Object[] temp = {ModelT.mPurchaseID, ModelT.mProductID, ModelT.mCustomerID, df2.format(ModelT.mQuantity), df2.format(taxTotal) , df2.format(total)};
            ModelT.mTax = taxTotal;
            ModelT.mTotal = total;
            switch (adapter.saveProduct(ModelT)) {
                case SQLiteDataAccess.PRODUCT_DUPLICATE_ERROR:
                    JOptionPane.showMessageDialog(null, "Transaction NOT added successfully! Duplicate product ID!");
                default:

                    addRow(temp);
                    btnprint.setVisible(true);
            }
        }
    }

    class CancelButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            dispose();
        }
    }


}
