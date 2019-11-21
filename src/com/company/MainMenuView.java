package com.company;
import javax.swing.*;
import javax.swing.plaf.basic.BasicLookAndFeel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Arrays;
import com.bulenkov.darcula.*;

public class MainMenuView extends JFrame {

    public JButton pro = new JButton("Add Product");
    public JButton cust = new JButton("Add Customer");
    public JButton trans = new JButton("Add Transaction");
    public JButton btnChangeDB = new JButton("Change DB");

    JPanel panelButtons1 = new JPanel();
    //public SQLiteDataAccess adapter;
    public JTextField username = new JTextField(20);
    public JPasswordField password = new JPasswordField(20);
    public JButton login = new JButton("Login");

    public MainMenuView(SQLiteDataAccess db) {
        //adapter = db;
        Dynamic2DArray thing = new Dynamic2DArray();
        this.setTitle("Store Management");
        this.setSize(600, 400);
        this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));
//        JPanel mainPanel = new JPanel();
//        BoxLayout mainBox = new BoxLayout(mainPanel, BoxLayout.X_AXIS);
//        mainPanel.setLayout(mainBox);
//        mainPanel.add(pro);
//        mainPanel.add(cust);
//        mainPanel.add(trans);
//        mainPanel.add(btnChangeDB);
//        mainPanel.setVisible(false);

        JPanel loginPanel = new JPanel();
        BoxLayout box = new BoxLayout(loginPanel, BoxLayout.Y_AXIS);
        loginPanel.setLayout(box);
        loginPanel.setBorder(BorderFactory.createTitledBorder("Login"));
        JPanel line1 = new JPanel(new FlowLayout());
        line1.add(new JLabel("Username "));
        line1.add(username);
        loginPanel.add(line1);
        JPanel line2 = new JPanel(new FlowLayout());
        line2.add(new JLabel("Password "));
        line2.add(password);
        loginPanel.add(line2);
        JPanel loginPanel1 = new JPanel();
        BoxLayout box1 = new BoxLayout(loginPanel1, BoxLayout.X_AXIS);
        loginPanel1.setLayout(box1);
        loginPanel1.add(Box.createRigidArea(new Dimension(200, 0)));
        loginPanel1.add(login);
        loginPanel.add(loginPanel1);

        BoxLayout box2 = new BoxLayout(panelButtons1, BoxLayout.X_AXIS);
        panelButtons1.setLayout(box2);
        panelButtons1.add(pro);
        panelButtons1.add(cust);
        panelButtons1.add(trans);
//        panelButtons1.add(btnChangeDB);
        panelButtons1.setVisible(false);
        this.getContentPane().add(loginPanel);
        this.getContentPane().add(panelButtons1);

        pro.addActionListener(new ProductButtonListener());
        cust.addActionListener(new CustomerButtonListener());
        trans.addActionListener(new TransactionButtonListener());
//        btnChangeDB.addActionListener(new DBFileListener());
        login.addActionListener(new LoginButtonListener());
        password.addActionListener(new LoginButtonListener());

        setVisible(true);
        pack();
    }
    class LoginButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae) {
            try {
                String s1 = "admin";
                String s2 = "Jarrod";
                String a1 = username.getText();

                if (s1.equals(a1) && isPasswordCorrect(password.getPassword())) {
                    panelButtons1.setVisible(true);
                    pro.setVisible(true);
                    cust.setVisible(true);
                    pack();

                }
                else if(s2.equals(a1) && isPasswordUser(password.getPassword())) {
                    panelButtons1.setVisible(true);
                    cust.setVisible(true);
                    pro.setVisible(false);
                    pack();
                } else {
                    JDialog dialog = new JDialog();
                    dialog.setAlwaysOnTop(true);
                    JOptionPane.showMessageDialog(dialog, "Username or Password is incorrect" , null , JOptionPane.PLAIN_MESSAGE );
                }
            } catch (Exception e) {

            }
        }
    }
//    class DBFileListener implements ActionListener {
//        @Override
//        public void actionPerformed(ActionEvent ae) {
//            JFileChooser j = new JFileChooser("d:");
//            j.showOpenDialog(null);
//            File file = j.getSelectedFile();
//            adapter.connect(file.getAbsolutePath());
//            System.out.print(file.getAbsolutePath());
//        }
//    }

    class ProductButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            //adapter.loadProduct();
            try {
                AddProduct apView = new AddProduct();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    class CustomerButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            try {
                AddCustomer csView = new AddCustomer();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }

    class TransactionButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            AddTransaction tView;
            try {
                tView = new AddTransaction();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }
    private static boolean isPasswordCorrect(char[] input) {
        boolean isCorrect = true;
        char[] correctPassword = { 'a', 'd', 'm', 'i', 'n'};

        if (input.length != correctPassword.length) {
            isCorrect = false;
        } else {
            isCorrect = Arrays.equals (input, correctPassword);
        }

        //Zero out the password.
        Arrays.fill(correctPassword,'0');

        return isCorrect;
    }
    private static boolean isPasswordUser(char[] input) {
        boolean isCorrect = true;
        char[] correctPassword = { 'p', 'a', 's', 's', 'w', 'o', 'r', 'd'};

        if (input.length != correctPassword.length) {
            isCorrect = false;
        } else {
            isCorrect = Arrays.equals (input, correctPassword);
        }

        //Zero out the password.
        Arrays.fill(correctPassword,'0');

        return isCorrect;
    }
    public static void main(String[] args) {
        BasicLookAndFeel darculaLookAndFeel = new DarculaLaf();
        try {
            //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.setLookAndFeel(darculaLookAndFeel);
            /*
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
             */
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        SQLiteDataAccess adapter = new SQLiteDataAccess();
        adapter.connect();
        MainMenuView view = new MainMenuView(adapter);
    }
}

