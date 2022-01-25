package com.pos.salon.model.payment;

import java.io.Serializable;
import java.util.ArrayList;

public class PaymentDataSend implements Serializable {

    private String location_id;
    private String currency = "0.0";
    private String transaction_id="";
    private String paid_amount = "";
    private String order_type = "";
    private String res_table_id="";
    private String res_waiter_id="";
    private String repair_product_id="";
    private int contact_id=0;
    private ArrayList<ProductDataSend> products = new ArrayList<ProductDataSend>();
    private String discount_type = "";
    private Double discount_amount=0.00;
    private String tax_rate_id="";
    private String tax_calculation_amount="0.00";
    private String shipping_details="";
    private String shipping_charges="0.00";
    private String final_total="0.00";
    private ArrayList<Payment> payment = new ArrayList<Payment>();
    private String change_return = "";
    private String status = "final";
    private String is_suspend="0";
    private String additional_notes = "";
    private String show_final_total= "";
    private String is_quotation= "0";
    private String exchange_rate= "";
    private String product_type= "";
    private String booking_time= "";
    private String booking_date= "";
    private String service_staff_id= "";

    public String getBooking_time() {
        return booking_time;
    }

    public void setBooking_time(String booking_time) {
        this.booking_time = booking_time;
    }

    public String getBooking_date() {
        return booking_date;
    }

    public void setBooking_date(String booking_date) {
        this.booking_date = booking_date;
    }

    public String getService_staff_id() {
        return service_staff_id;
    }

    public void setService_staff_id(String service_staff_id) {
        this.service_staff_id = service_staff_id;
    }

    public String getProduct_type() {
        return product_type;
    }

    public void setProduct_type(String product_type) {
        this.product_type = product_type;
    }

    public String getExchange_rate() {
        return exchange_rate;
    }

    public void setExchange_rate(String exchange_rate) {
        this.exchange_rate = exchange_rate;
    }

    public String getShow_final_total() {
        return show_final_total;
    }

    public void setShow_final_total(String show_final_total) {
        this.show_final_total = show_final_total;
    }

    public String getIs_quotation() {
        return is_quotation;
    }

    public void setIs_quotation(String is_quotation) {
        this.is_quotation = is_quotation;
    }

    public String getRepair_product_id() {
        return repair_product_id;
    }

    public void setRepair_product_id(String repair_product_id) {
        this.repair_product_id = repair_product_id;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getPaid_amount() {
        return paid_amount;
    }

    public void setPaid_amount(String paid_amount) {
        this.paid_amount = paid_amount;
    }

    public String getLocation_id() {
        return location_id;
    }

    public void setLocation_id(String location_id) {
        this.location_id = location_id;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getContact_id() {
        return contact_id;
    }

    public void setContact_id(int contact_id) {
        this.contact_id = contact_id;
    }

    public ArrayList<ProductDataSend> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<ProductDataSend> products) {
        this.products = products;
    }

    public String getDiscount_type() {
        return discount_type;
    }

    public void setDiscount_type(String discount_type) {
        this.discount_type = discount_type;
    }

    public Double getDiscount_amount() {
        return discount_amount;
    }

    public void setDiscount_amount(Double discount_amount) {
        this.discount_amount = discount_amount;
    }

    public String getTax_rate_id() {
        return tax_rate_id;
    }

    public void setTax_rate_id(String tax_rate_id) {
        this.tax_rate_id = tax_rate_id;
    }

    public String getTax_calculation_amount() {
        return tax_calculation_amount;
    }

    public void setTax_calculation_amount(String tax_calculation_amount) {
        this.tax_calculation_amount = tax_calculation_amount;
    }

    public String getShipping_details() {
        return shipping_details;
    }

    public void setShipping_details(String shipping_details) {
        this.shipping_details = shipping_details;
    }

    public String getShipping_charges() {
        return shipping_charges;
    }

    public void setShipping_charges(String shipping_charges) {
        this.shipping_charges = shipping_charges;
    }

    public String getFinal_total() {
        return final_total;
    }

    public void setFinal_total(String final_total) {
        this.final_total = final_total;
    }

    public ArrayList<Payment> getPayment() {
        return payment;
    }

    public void setPayment(ArrayList<Payment> payment) {
        this.payment = payment;
    }

    public String getChange_return() {
        return change_return;
    }

    public void setChange_return(String change_return) {
        this.change_return = change_return;
    }

    public String getOrder_type() {
        return order_type;
    }

    public void setOrder_type(String order_type) {
        this.order_type = order_type;
    }

    public String getRes_table_id() {
        return res_table_id;
    }

    public void setRes_table_id(String res_table_id) {
        this.res_table_id = res_table_id;
    }

    public String getRes_waiter_id() {
        return res_waiter_id;
    }

    public void setRes_waiter_id(String res_waiter_id) {
        this.res_waiter_id = res_waiter_id;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }



    public String getIs_suspend() {
        return is_suspend;
    }

    public void setIs_suspend(String is_suspend) {
        this.is_suspend = is_suspend;
    }

    public String getAdditional_notes() {
        return additional_notes;
    }

    public void setAdditional_notes(String additional_notes) {
        this.additional_notes = additional_notes;
    }


}
