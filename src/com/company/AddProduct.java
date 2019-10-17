package com.company;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddProduct extends JFrame {

    public JButton btnAdd = new JButton("Add");
    public JButton btnCancel = new JButton("Cancel");

    public SQLiteDataAccess adapter;
    public JTextField txtProductID = new JTextField(20);
    public JTextField txtName = new JTextField(20);
    public JTextField txtPrice = new JTextField(20);
    public JTextField txtQuantity = new JTextField(20);
    public JTextField txtTax = new JTextField(20);
    private DefaultTableModel model = new DefaultTableModel();
    private JTable table = new JTable(model);

    public AddProduct(SQLiteDataAccess db) {
        adapter = db;
        this.setTitle("Add Product");
        this.setSize(600, 500);
        this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));
        String[] labels = {"ProductID ", "Name ", "Price ", "Quantity "};
        JPanel addPanel = new JPanel();
        addPanel.setLayout(new BoxLayout(addPanel, BoxLayout.PAGE_AXIS));
        addPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
        JPanel line1 = new JPanel();
        line1.add(new JLabel("ProductID "));
        line1.add(txtProductID);
        line1.setAlignmentX( Component.RIGHT_ALIGNMENT );
        addPanel.add(line1, BOTTOM_ALIGNMENT);

        JPanel line2 = new JPanel(new FlowLayout());
        line2.add(new JLabel("Name "));
        line2.add(txtName);
        line2.setAlignmentX( Component.RIGHT_ALIGNMENT );

        addPanel.add(line2);

        JPanel line3 = new JPanel(new FlowLayout());
        line3.add(new JLabel("Price "));
        line3.add(txtPrice);
        line3.setAlignmentX( Component.RIGHT_ALIGNMENT );
        addPanel.add(line3);

        JPanel line4 = new JPanel(new FlowLayout());
        line4.add(new JLabel("Quantity "));
        line4.add(txtQuantity);
        line4.setAlignmentX( Component.RIGHT_ALIGNMENT );
        addPanel.add(line4);

        JPanel line5 = new JPanel(new FlowLayout());
        line5.add(new JLabel("Tax "));
        line5.add(txtTax);
        line5.setAlignmentX( Component.RIGHT_ALIGNMENT );
        addPanel.add(line5);

        JPanel panelButtons = new JPanel(new FlowLayout());
        panelButtons.add(btnAdd);
        panelButtons.add(btnCancel);
        addPanel.add(panelButtons);
        this.getContentPane().add(addPanel);
        String[] cols = {"ProductID", "Name", "Price", "Quantity", "Tax"};
        model.setDataVector(db.getProductMatrix().getMatrix(), cols);
        //add the table to the frame
        table.setEnabled(false);
        this.add(new JScrollPane(table));
        btnAdd.addActionListener(new AddButtonListener());
        btnCancel.addActionListener(new CancelButtonListener());
        setVisible(true);
        pack();
    }
    public void addRow(Object[] row)
    {
        model.addRow(row);
    }
    class AddButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            ProductModel product = new ProductModel();

            String id = txtProductID.getText();

            if (id.length() == 0) {
                JOptionPane.showMessageDialog(null, "ProductID cannot be null!");
                return;
            }

            try {
                product.mProductID = Integer.parseInt(id);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "ProductID is invalid!");
                return;
            }

            String name = txtName.getText();
            if (name.length() == 0) {
                JOptionPane.showMessageDialog(null, "Product name cannot be empty!");
                return;
            }

            product.mName = name;

            String price = txtPrice.getText();
            try {
                product.mPrice = Double.parseDouble(price);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Price is invalid!");
                return;
            }

            String quant = txtQuantity.getText();
            try {
                product.mQuantity = Double.parseDouble(quant);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Quantity is invalid!");
                return;
            }

            String tax = txtTax.getText();
            try {
                product.mTax = Double.parseDouble(tax);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Tax is invalid!");
                return;
            }

            switch (adapter.saveProduct(product)) {
                case SQLiteDataAccess.PRODUCT_DUPLICATE_ERROR:
                    JOptionPane.showMessageDialog(null, "Product NOT added successfully! Duplicate product ID!");
                default:
                    Object[] temp = {product.mProductID, product.mName, product.mPrice, product.mQuantity, product.mTax};
                    addRow(temp);
                    JOptionPane.showMessageDialog(null, "Product added successfully!" + product);
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
