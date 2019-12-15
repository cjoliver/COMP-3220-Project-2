package com.company;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.*;

public class SQLiteDataAccess {

    public static final int PRODUCT_SAVED_OK = 0;
    public static final int PRODUCT_DUPLICATE_ERROR = 1;
    private Dynamic2DArray ProductMatrix = new Dynamic2DArray();
    private Dynamic2DArray CustomerMatrix = new Dynamic2DArray();
    private Dynamic2DArray TransactionMatrix = new Dynamic2DArray();
    private Connection conn = null;
    private static ServerSocket server;
    private static ArrayList<Integer> connections = new ArrayList<Integer>();
    public static void main(String[] args) {
        SQLiteDataAccess db = new SQLiteDataAccess();
        db.connect("/Users/cj/IdeaProjects/Activity9_/data/store.db");
        db.loadProduct();
        db.loadCustomer();
        db.loadTransaction();
        try {
            server = new ServerSocket(9000);
            while(true){
                System.out.println("Waiting for the client request");
                //creating socket and waiting for client connection
                Socket socket = server.accept();
                //read from socket to ObjectInputStream object
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                //convert ObjectInputStream object to String
                String message = (String) ois.readObject();
                if(message.equals("login"))
                {
                    String username = (String) ois.readObject();
                    String password = (String) ois.readObject();
                    String[] auth = db.loadUsers(username, password);
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(auth);
                    System.out.println(username + ": " + password);
                }
                if(message.equals("product"))
                {
                    String message2 = (String) ois.readObject();
                    if(message2.equals("load")) {
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                        oos.writeObject(db.getProductMatrix().getMatrix());
                        System.out.println("Sent data");
                    }
                    if(message2.equals("edit")) {
                        int row = (int) ois.readObject();
                        String col = ois.readObject().toString();
                        String value = ois.readObject().toString();
                        db.editProduct(row, col, value);
                        System.out.println("Edit data " + row + " " + col + " " + value);
                    }
                    if(message2.equals("delete")) {
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                        int row = (int) ois.readObject();
                        db.deleteProduct(row+1);
                        System.out.println("Delete data at row "+row);
                    }
                    if(message2.equals("save")) {
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                        String row = (String) ois.readObject();
                        db.saveP(row);
                    }
                }
                else if(message.equals("customer"))
                {
                    String message2 = (String) ois.readObject();
                    if(message2.equals("load")) {
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                        oos.writeObject(db.getCustomerMatrix().getMatrix());
                        System.out.println("Sent customer data");
                    }
                    if(message2.equals("edit")) {
                        int row = (int) ois.readObject();
                        String col = ois.readObject().toString();
                        String value = ois.readObject().toString();
                        db.editCustomer(row, col, value);
                        System.out.println("Edit data " + row + " " + col + " " + value);
                    }
                    if(message2.equals("delete")) {
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                        int row = (int) ois.readObject();
                        db.deleteCustomer(row+1);
                        System.out.println("Delete data at row "+row);
                    }
                    if(message2.equals("save")) {
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                        String row = (String) ois.readObject();
                        db.saveC(row);
                    }
                }
                else if(message.equals("transaction"))
                {
                    String message2 = (String) ois.readObject();
                    if(message2.equals("load")) {
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                        oos.writeObject(db.getTransactionMatrix().getMatrix());
                        System.out.println("Sent data");
                    }
                    if(message2.equals("edit")) {
                        int row = (int) ois.readObject();
                        String col = ois.readObject().toString();
                        String value = ois.readObject().toString();
                        db.editTransaction(row, col, value);
                        System.out.println("Edit data " + row + " " + col + " " + value);
                    }
                    if(message2.equals("delete")) {
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                        int row = (int) ois.readObject();
                        db.deleteTransaction(row+1);
                        System.out.println("Delete data at row "+row);
                    }
                    if(message2.equals("save")) {
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                        String row = (String) ois.readObject();
                        db.saveT(row);
                    }
                }
                ois.close();
                socket.close();
                //terminate the server if client sends exit request
                if(message.equalsIgnoreCase("exit")) break;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void saveP(String row){
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(row);

        } catch (Exception e) {
            String msg = e.getMessage();
            System.out.println(msg);
        }
    }
    public void saveC(String row){
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(row);

        } catch (Exception e) {
            String msg = e.getMessage();
            System.out.println(msg);
        }
    }
    public void saveT(String row){
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(row);
            System.out.println(row);
        } catch (Exception e) {
            String msg = e.getMessage();
            System.out.println(msg);
        }
    }
    public void deleteCustomer(int row)
    {
        String sql = "DELETE FROM Customer WHERE CustomerID = ?";

        try{
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, row);
            pstmt.executeUpdate();
            System.out.println("Database updated successfully ");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void deleteTransaction(int row)
    {
        String sql = "DELETE FROM Trans WHERE PurchaseID = ?";

        try{
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, row);
            pstmt.executeUpdate();
            System.out.println("Database updated successfully ");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void deleteProduct(int row)
    {
        String sql = "DELETE FROM Product WHERE ProductID = ?";

        try{
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, row);
            pstmt.executeUpdate();
            System.out.println("Database updated successfully ");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void editTransaction(int row, String col, String value)
    {
        row = row + 1;
        String sqlUpdate = "UPDATE Trans "
                + "SET "+col+" = ? "
                + "WHERE PurchaseID = ?";
        try{
            PreparedStatement pstmt = conn.prepareStatement(sqlUpdate);
            pstmt.setString(1, value);
            pstmt.setInt(2, row);
            pstmt.executeUpdate();
            System.out.println("Database updated successfully ");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void editCustomer(int row, String col, String value)
    {
        row = row + 1;
        String sqlUpdate = "UPDATE Customer "
                + "SET "+col+" = ? "
                + "WHERE CustomerID = ?";
        try{
            PreparedStatement pstmt = conn.prepareStatement(sqlUpdate);
            pstmt.setString(1, value);
            pstmt.setInt(2, row);
            pstmt.executeUpdate();
            System.out.println("Database updated successfully ");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void editProduct(int row, String col, String value)
    {
        row = row + 1;
        String sqlUpdate = "UPDATE Product "
                + "SET "+col+" = ? "
                + "WHERE ProductID = ?";
        try{
            PreparedStatement pstmt = conn.prepareStatement(sqlUpdate);
            pstmt.setString(1, value);
            pstmt.setInt(2, row);
            pstmt.executeUpdate();
            System.out.println("Database updated successfully ");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void connect() {
        try {
            // db parameters
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:store.db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            DatabaseMetaData md = conn.getMetaData();


        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.toString());
            System.out.println("Not work");
        }
    }
    public void connect(String file) {
        try {
            // db parameters
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:" + file;
            conn = DriverManager.getConnection(url);
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.toString());
        }
    }
    public Dynamic2DArray getProductMatrix() {
        return ProductMatrix;
    }
    public Dynamic2DArray getCustomerMatrix() {
        return CustomerMatrix;
    }
    public Dynamic2DArray getTransactionMatrix() {
        return TransactionMatrix;
    }
    public void loadProduct() {
        ProductMatrix.reset();
        try{
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Product");
            while (rs.next()) {
                int id = rs.getInt("ProductID");
                String name = rs.getString("Name");
                String job = rs.getString("Price");
                String Quantity = rs.getString("Quantity");
                String Tax = rs.getString("Tax");
                ProductMatrix.set(rs.getRow()-1, 0, id);
                ProductMatrix.set(rs.getRow()-1, 1, name);
                ProductMatrix.set(rs.getRow()-1, 2, job);
                ProductMatrix.set(rs.getRow()-1, 3, Quantity);
                ProductMatrix.set(rs.getRow()-1,4, Tax);
            }
        } catch(SQLException e) {
            System.out.println("SQL exception occured" + e);
        }
    }
    public void loadCustomer() {
        CustomerMatrix.reset();
        try{
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Customer");
            while (rs.next()) {
                int id = rs.getInt("CustomerID");
                String name = rs.getString("Name");
                String job = rs.getString("Email");
                String Quantity = rs.getString("Phone");
                String pass = rs.getString("Password");
                String auth = rs.getString("Authentication");
                CustomerMatrix.set(rs.getRow()-1, 0, id);
                CustomerMatrix.set(rs.getRow()-1, 1, name);
                CustomerMatrix.set(rs.getRow()-1, 2, job);
                CustomerMatrix.set(rs.getRow()-1, 3, Quantity);
                CustomerMatrix.set(rs.getRow()-1, 4, pass);
                CustomerMatrix.set(rs.getRow()-1, 5, auth);
            }
        } catch(SQLException e) {
            System.out.println("SQL exception occured" + e);
        }
    }
    public String[] loadUsers(String username, String password) {
        String[] auth = new String[2];
        try{
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Customer");
            while (rs.next()) {
                int id = rs.getInt("CustomerID");
                String pass = rs.getString("Password");
                auth[0] = rs.getString("Authentication");
                auth[1] = rs.getString("Name");
                if(id == Integer.parseInt(username))
                {
                    System.out.println("Good ID" + password);
                    if(pass.equals(password))
                    {
                        System.out.println("Good Pass");
                        return auth;
                    }
                }
            }
            auth[0] = "BadLogin";
            return auth;
        } catch(SQLException e) {
            System.out.println("SQL exception occured" + e);
        }
        auth[0] = "BadLogin";
        return auth;
    }
    public void loadTransaction() {
        TransactionMatrix.reset();
        try{
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Trans");
            System.out.println("Here1");
            while (rs.next()) {
                System.out.println("Here1");
                String purID = rs.getString("PurchaseID");
                String ProID = rs.getString("ProductID");
                String CusID = rs.getString("CustomerID");
                String Quantity = rs.getString("Quantity");
                String Tax = rs.getString("Tax");
                String Total = rs.getString("Total");
                TransactionMatrix.set(rs.getRow()-1, 0, purID);
                TransactionMatrix.set(rs.getRow()-1, 1, ProID);
                TransactionMatrix.set(rs.getRow()-1, 2, CusID);
                TransactionMatrix.set(rs.getRow()-1, 3, Quantity);
                TransactionMatrix.set(rs.getRow()-1, 4, Tax);
                TransactionMatrix.set(rs.getRow()-1, 5, Total);
            }
        } catch(SQLException e) {
            System.out.println("SQL exception occured" + e);
        }
    }


}