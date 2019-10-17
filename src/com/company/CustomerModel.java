package com.company;

public class CustomerModel {
        public int mCustomerID;
        public String mPhone, mName, mEmail;

        public String toString() {
            StringBuilder sb = new StringBuilder("(");
            sb.append(mCustomerID).append(",");
            sb.append("\"").append(mName).append("\"").append(",");
            sb.append("\"").append(mEmail).append("\"").append(",");
            sb.append("\"").append(mPhone).append("\"").append(")");
            return sb.toString();
        }

}
