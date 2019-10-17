package com.company;

import java.sql.*;
import java.util.*;

public class SQLiteDataAccess {

    public static final int PRODUCT_SAVED_OK = 0;
    public static final int PRODUCT_DUPLICATE_ERROR = 1;
    private Dynamic2DArray ProductMatrix = new Dynamic2DArray();
    private Dynamic2DArray CustomerMatrix = new Dynamic2DArray();
    private Dynamic2DArray TransactionMatrix = new Dynamic2DArray();
    private Connection conn = null;

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
    public Dynamic2DArray getTransactionMatrixMatrix() {
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
                CustomerMatrix.set(rs.getRow()-1, 0, id);
                CustomerMatrix.set(rs.getRow()-1, 1, name);
                CustomerMatrix.set(rs.getRow()-1, 2, job);
                CustomerMatrix.set(rs.getRow()-1, 3, Quantity);
            }
        } catch(SQLException e) {
            System.out.println("SQL exception occured" + e);
        }
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
    public int saveProduct(ProductModel product) {
        try {
            String sql = "INSERT INTO Product(ProductId, Name, Price, Quantity, Tax) VALUES " + product;
            System.out.println(sql);
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);

        } catch (Exception e) {
            String msg = e.getMessage();
            System.out.println(msg);
            if (msg.contains("UNIQUE constraint failed"))
                return PRODUCT_DUPLICATE_ERROR;
        }

        return PRODUCT_SAVED_OK;
    }
    public int saveProduct(TransactionModel product) {
        try {
            String sql = "INSERT INTO Trans(PurchaseID, ProductID, CustomerID, Quantity, Tax, Total) VALUES " + product;
            System.out.println(sql);
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);

        } catch (Exception e) {
            String msg = e.getMessage();
            System.out.println(msg);
            if (msg.contains("UNIQUE constraint failed"))
                return PRODUCT_DUPLICATE_ERROR;
        }

        return PRODUCT_SAVED_OK;
    }
    public int saveProduct(CustomerModel model) {
        try {
            String sql = "INSERT INTO Customer(CustomerID, Name, Email, Phone) VALUES " + model;
            System.out.println(sql);
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);

        } catch (Exception e) {
            String msg = e.getMessage();
            System.out.println(msg);
            if (msg.contains("UNIQUE constraint failed"))
                return PRODUCT_DUPLICATE_ERROR;
        }

        return PRODUCT_SAVED_OK;
    }

}