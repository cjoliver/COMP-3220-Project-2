package com.company;

public class TransactionModel {
    public int mPurchaseID;
    public int mCustomerID, mProductID;
    public double mQuantity, mTax, mTotal;

    public String toString() {
        StringBuilder sb = new StringBuilder("(");
        sb.append(mPurchaseID).append(",");
        sb.append(mProductID).append(",");
        sb.append(mCustomerID).append(",");
        sb.append(mQuantity).append(",");
        sb.append(mTax).append(",");
        sb.append(mTotal).append(")");
        return sb.toString();
    }

}
