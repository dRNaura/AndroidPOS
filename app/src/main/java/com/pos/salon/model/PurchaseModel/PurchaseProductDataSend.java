package com.pos.salon.model.PurchaseModel;


import java.io.Serializable;
import java.util.ArrayList;

public class PurchaseProductDataSend implements Serializable {
        private int contact_id=0;
        private String ref_no="";
        private String transaction_date = "";
        private String status="";
        private int location_id=0;

        private ArrayList<ProductPurchaseDataSend> purchases = new ArrayList<ProductPurchaseDataSend>();
        public String payment_method="";
        public double final_total=0.0,payment_amount=0.0,total_before_tax=0.0;

    public int getContact_id() {
        return contact_id;
    }

    public void setContact_id(int contact_id) {
        this.contact_id = contact_id;
    }

    public String getRef_no() {
        return ref_no;
    }

    public void setRef_no(String ref_no) {
        this.ref_no = ref_no;
    }

    public String getTransaction_date() {
        return transaction_date;
    }

    public void setTransaction_date(String transaction_date) {
        this.transaction_date = transaction_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getLocation_id() {
        return location_id;
    }

    public void setLocation_id(int location_id) {
        this.location_id = location_id;
    }

    public ArrayList<ProductPurchaseDataSend> getPurchases() {
        return purchases;
    }

    public void setPurchases(ArrayList<ProductPurchaseDataSend> purchases) {
        this.purchases = purchases;
    }

    public double getTotal_before_tax() {
        return total_before_tax;
    }

    public void setTotal_before_tax(double total_before_tax) {
        this.total_before_tax = total_before_tax;
    }

    public double getFinal_total() {
        return final_total;
    }

    public void setFinal_total(double final_total) {
        this.final_total = final_total;
    }

    public double getPayment_amount() {
        return payment_amount;
    }

    public void setPayment_amount(double payment_amount) {
        this.payment_amount = payment_amount;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }
}
