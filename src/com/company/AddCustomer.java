package com.company;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class AddCustomer extends JFrame {

    public JButton btnAdd = new JButton("Add");
    public JButton btnCancel = new JButton("Cancel");
    public JButton btnload = new JButton("Load");
    public JButton btnsave = new JButton("Save");

    private List<TableModelEvent> edits = new ArrayList<TableModelEvent>();
    public JTextField txtCustomerID = new JTextField(20);
    public JTextField txtName = new JTextField(20);
    public JTextField txtPhone = new JTextField(20);
    public JTextField txtEmail = new JTextField(20);
    private DefaultTableModel model = new DefaultTableModel();
    private JTable table = new JTable(model);
    private TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>(model);

    InetAddress host = InetAddress.getLocalHost();
    Socket socket = null;
    ObjectOutputStream oos = null;
    ObjectInputStream ois = null;
    String[] labels = {"CustomerID", "Name", "Email", "Phone", "Password", "Authentication"};


    public AddCustomer(String[] auth) throws UnknownHostException {
        this.setTitle("Add Customer");
        this.setSize(600, 400);
        this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));


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
        panelButtons.add(btnload);
        panelButtons.add(btnsave);
        panelButtons.add(btnCancel);
        table.setRowSorter(sorter);
        this.getContentPane().add(panelButtons);
        String[] cols = {"CustomerID", "Name", "Email", "Phone", "Password", "Authentication"};
        //model.setDataVector(db.getCustomerMatrix().getMatrix(), cols);
        //add the table to the frame
        this.add(new JScrollPane(table));
        btnAdd.addActionListener(new AddButtonListener());
        btnload.addActionListener(new LoadButtonListener());
        btnCancel.addActionListener(new CancelButtonCListener());
        btnsave.addActionListener(new saveButtonListener());
        table.getModel().addTableModelListener(e -> {
            edits.add(e);
            System.out.println(e.toString());
        });
        setVisible(true);
    }
    public void deleteRow(int row) throws IOException {
        socket = new Socket(host.getHostName(), 9000);
        //write to socket using ObjectOutputStream
        oos = new ObjectOutputStream(socket.getOutputStream());
        System.out.println("Sending request to Socket Server");
        oos.writeObject("customer");
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
        oos.writeObject("customer");
        oos.writeObject("load");
        //read the server response message
        ois = new ObjectInputStream(socket.getInputStream());
        Object[][] message = (Object[][]) ois.readObject();
        model.setDataVector(message, labels);

        //System.out.println("Message: " + message);
        //close resources
        ois.close();
        oos.close();
        sorter.setRowFilter(RowFilter.regexFilter("user"));
    }
    public void editValue(int row, String col, String value) throws IOException {
        socket = new Socket(host.getHostName(), 9000);
        //write to socket using ObjectOutputStream
        oos = new ObjectOutputStream(socket.getOutputStream());
        System.out.println("Sending request to Socket Server");
        oos.writeObject("customer");
        oos.writeObject("edit");
        oos.writeObject(row);
        oos.writeObject(col);
        oos.writeObject(value);
        //read the server response message
        oos.close();
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
            customer.mPass = "pass";
            customer.mAuth = "user";
            try {
                String sql = "INSERT INTO Customer(CustomerID, Name, Email, Phone, Password, Authentication) VALUES " + customer;
                System.out.println(sql);
                socket = new Socket(host.getHostName(), 9000);
                oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject("customer");
                oos.writeObject("save");
                oos.writeObject(sql);
                ois.close();
                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
                    Object[] temp = {customer.mCustomerID, customer.mName, customer.mEmail, customer.mPhone, customer.mPass, customer.mAuth};
                    addRow(temp);
        }
    }

    class CancelButtonCListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            dispose();
        }
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
        public void actionPerformed(ActionEvent actionEvent) {
            System.out.println("save button" + edits.size());
            for (TableModelEvent e : edits) {
                if (e.getFirstRow() == -1 || e.getColumn() == -1) {

                }
//                System.out.println(e.getFirstRow() + " " + e.getColumn());
//                System.out.println(table.getValueAt(e.getFirstRow(), e.getColumn()));
                else if (table.getValueAt(e.getFirstRow(), e.getColumn()).toString().length() == 0 && e.getColumn() == 0) {
                    try {
                        deleteRow(e.getFirstRow());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    try {
                        editValue(e.getFirstRow(), labels[e.getColumn()], table.getValueAt(e.getFirstRow(), e.getColumn()).toString());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

}
