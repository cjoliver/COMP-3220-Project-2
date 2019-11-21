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

public class AddProduct extends JFrame {

    public JButton btnAdd = new JButton("Add");
    public JButton btnCancel = new JButton("Cancel");
    public JButton btnload = new JButton("Load");
    public JButton btnsave = new JButton("Save");

    public SQLiteDataAccess adapter;
    public JTextField txtProductID = new JTextField(20);
    public JTextField txtName = new JTextField(20);
    public JTextField txtPrice = new JTextField(20);
    public JTextField txtQuantity = new JTextField(20);
    public JTextField txtTax = new JTextField(20);
    private DefaultTableModel model = new DefaultTableModel();
    private JTable table = new JTable(model);

    String[] cols = {"ProductID", "Name", "Price", "Quantity", "Tax"};
    InetAddress host = InetAddress.getLocalHost();
    Socket socket = null;
    ObjectOutputStream oos = null;
    ObjectInputStream ois = null;

    public AddProduct() throws IOException, ClassNotFoundException {
        this.setTitle("Add Product");
        this.setSize(700, 500);
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
        panelButtons.add(btnload);
        panelButtons.add(btnsave);
        addPanel.add(panelButtons);
        this.getContentPane().add(addPanel);
        table.setEnabled(true);
        this.add(new JScrollPane(table));
        table.getModel().addTableModelListener(new TableModelListener() {

            public void tableChanged(TableModelEvent e) {
                if(e.getFirstRow() == -1|| e.getColumn() == -1)
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
                        editValue(e.getFirstRow(), cols[e.getColumn()], table.getValueAt(e.getFirstRow(), e.getColumn()).toString());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        //table.setVisible(false);
        btnAdd.addActionListener(new AddButtonListener());
        btnCancel.addActionListener(new CancelButtonListener());
        btnload.addActionListener(new LoadButtonListener());
        btnsave.addActionListener(new saveButtonListener());
        setVisible(true);
        pack();
        //getData();
    }
    public void deleteRow(int row) throws IOException {
        socket = new Socket(host.getHostName(), 9000);
        //write to socket using ObjectOutputStream
        oos = new ObjectOutputStream(socket.getOutputStream());
        System.out.println("Sending request to Socket Server");
        oos.writeObject("product");
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
        oos.writeObject("product");
        oos.writeObject("load");
        //read the server response message
        ois = new ObjectInputStream(socket.getInputStream());
        Object[][] message = (Object[][]) ois.readObject();
        model.setDataVector(message, cols);

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
        oos.writeObject("product");
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
    class saveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae) {
//            table.setEnabled(false);
        }
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

            try {
                String sql = "INSERT INTO Product(ProductId, Name, Price, Quantity, Tax) VALUES " + product;
                System.out.println(sql);
                socket = new Socket(host.getHostName(), 9000);
                oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject("product");
                oos.writeObject("save");
                oos.writeObject(sql);
                oos.close();
                ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
                    Object[] temp = {product.mProductID, product.mName, product.mPrice, product.mQuantity, product.mTax};
                    addRow(temp);
        }
    }

    class CancelButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            dispose();
        }
    }


}
