package com.pos.salon.model.customerData;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class FetchPartialCustomer implements Serializable {
    private boolean success;
    private ArrayList<PartialCustomerList> sell_details=new ArrayList<>();
    public Transaction_details transaction_details;
    private String paid_amount = "";


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }


    public ArrayList<PartialCustomerList> getSell_details() {
        return sell_details;
    }

    public void setSell_details(ArrayList<PartialCustomerList> sell_details) {
        this.sell_details = sell_details;
    }

    public String getPaid_amount() {
        return paid_amount;
    }

    public void setPaid_amount(String paid_amount) {
        this.paid_amount = paid_amount;
    }

    public Transaction_details getTransaction() {
        return transaction_details;
    }

    public void setTransaction(Transaction_details transaction_details) {
        this.transaction_details = transaction_details;
    }

    public class Transaction_details {

        @SerializedName("id")
        private String id;

        @SerializedName("transaction_id")
        private String transaction_id;

        @SerializedName("shipping_charges")
        private String shippingcharges;


        @SerializedName("final_total")
        private String final_total;

        @SerializedName("total_before_tax")
        private String total_before_tax;

        @SerializedName("currency")
        private String currency;

        @SerializedName("discount_amount")
        private String discount_amount;

        @SerializedName("tax_amount")
        private String tax_amount;

        @SerializedName("shipping_details")
        private String shipping_details;

        @SerializedName("discount_type")
        private String discount_type;

        @SerializedName("location_id")
        private String location_id;

        public String getTotal_before_tax() {
            return total_before_tax;
        }

        public void setTotal_before_tax(String total_before_tax) {
            this.total_before_tax = total_before_tax;
        }

        public String getLocation_id() {
            return location_id;
        }

        public void setLocation_id(String location_id) {
            this.location_id = location_id;
        }

        public String getShippingcharges() {
            return shippingcharges;
        }

        public void setShippingcharges(String shippingcharges) {
            this.shippingcharges = shippingcharges;
        }

        public String getFinal_total() {
            return final_total;
        }

        public void setFinal_total(String final_total) {
            this.final_total = final_total;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getDiscount_amount() {
            return discount_amount;
        }

        public void setDiscount_amount(String discount_amount) {
            this.discount_amount = discount_amount;
        }

        public String getTax_amount() {
            return tax_amount;
        }

        public void setTax_amount(String tax_amount) {
            this.tax_amount = tax_amount;
        }

        public String getShipping_details() {
            return shipping_details;
        }

        public void setShipping_details(String shipping_details) {
            this.shipping_details = shipping_details;
        }

        public String getDiscount_type() {
            return discount_type;
        }

        public void setDiscount_type(String discount_type) {
            this.discount_type = discount_type;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTransaction_id() {
            return transaction_id;
        }

        public void setTransaction_id(String transaction_id) {
            this.transaction_id = transaction_id;
        }
    }

}




