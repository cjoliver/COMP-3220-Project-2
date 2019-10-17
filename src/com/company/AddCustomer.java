package com.company;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddCustomer extends JFrame {

    public JButton btnAdd = new JButton("Add");
    public JButton btnCancel = new JButton("Cancel");

    public SQLiteDataAccess adapter;
    public JTextField txtCustomerID = new JTextField(20);
    public JTextField txtName = new JTextField(20);
    public JTextField txtPhone = new JTextField(20);
    public JTextField txtEmail = new JTextField(20);
    private DefaultTableModel model = new DefaultTableModel();
    private JTable table = new JTable(model);

    public AddCustomer(SQLiteDataAccess db) {
        adapter = db;
        this.setTitle("Add Customer");
        this.setSize(600, 400);
        this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));

        String[] labels = {"CustomerID ", "Name ", "PhoneNumber ", "ProductID"};

        JPanel line1 = new JPanel(new FlowLayout());
        line1.add(new JLabel("CustomerID "));
        line1.add(txtCustomerID);
        this.getContentPane().add(line1);

        JPanel line2 = new JPanel(new FlowLayout());
        line2.add(new JLabel("Name "));
        line2.add(txtName);
        this.getContentPane().add(line2);

        JPanel line3 = new JPanel(new FlowLayout());
        line3.add(new JLabel("Phone "));
        line3.add(txtPhone);
        this.getContentPane().add(line3);

        JPanel line4 = new JPanel(new FlowLayout());
        line4.add(new JLabel("Email "));
        line4.add(txtEmail);
        this.getContentPane().add(line4);

        JPanel panelButtons = new JPanel(new FlowLayout());
        panelButtons.add(btnAdd);
        panelButtons.add(btnCancel);
        this.getContentPane().add(panelButtons);
        String[] cols = {"CustomerID", "Name", "Email", "Phone"};
        model.setDataVector(db.getCustomerMatrix().getMatrix(), cols);
        //add the table to the frame
        this.add(new JScrollPane(table));
        btnAdd.addActionListener(new AddButtonListener());
        btnCancel.addActionListener(new CancelButtonCListener());
        setVisible(true);

    }
    public void addRow(Object[] row)
    {
        model.addRow(row);
    }

    class AddButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            CustomerModel customer = new CustomerModel();

            String id = txtCustomerID.getText();

            if (id.length() == 0) {
                JOptionPane.showMessageDialog(null, "CustomerID cannot be null!");
                return;
            }

            try {
                customer.mCustomerID = Integer.parseInt(id);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "ProductID is invalid!");
                return;
            }

            String name = txtName.getText();
            if (name.length() == 0) {
                JOptionPane.showMessageDialog(null, "Product name cannot be empty!");
                return;
            }

            customer.mName = name;

            String phone = txtPhone.getText();
            try {
                customer.mPhone = phone;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Phone is invalid!");
                return;
            }

            String email = txtEmail.getText();
            if (email.length() == 0) {
                JOptionPane.showMessageDialog(null, "Email cannot be null!");
                return;
            }
            customer.mEmail = email;
            switch (adapter.saveProduct(customer)) {
                case SQLiteDataAccess.PRODUCT_DUPLICATE_ERROR:
                    JOptionPane.showMessageDialog(null, "customer NOT added successfully! Duplicate product ID!");
                    break;
                default:
                    Object[] temp = {customer.mCustomerID, customer.mName, customer.mEmail, customer.mPhone};
                    addRow(temp);
            }
        }
    }

    class CancelButtonCListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            dispose();
        }
    }


}
