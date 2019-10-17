package com.company;
public class ProductModel {
    public int mProductID;
    public String mName;
    public double mPrice, mQuantity, mTax;

    public String toString() {
        StringBuilder sb = new StringBuilder("(");
        sb.append(mProductID).append(",");
        sb.append("\"").append(mName).append("\"").append(",");
        sb.append(mPrice).append(",");
        sb.append(mQuantity).append(",");
        sb.append(mTax).append(")");
        return sb.toString();
    }
}
