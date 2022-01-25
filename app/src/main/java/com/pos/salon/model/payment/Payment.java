package com.pos.salon.model.payment;

import java.io.Serializable;

public class Payment implements Serializable {

    private String amount="";
    private String method="";

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getCard_number() {
        return card_number;
    }

    public void setCard_number(String card_number) {
        this.card_number = card_number;
    }

    public String getCard_holder_name() {
        return card_holder_name;
    }

    public void setCard_holder_name(String card_holder_name) {
        this.card_holder_name = card_holder_name;
    }

    public String getCard_type() {
        return card_type;
    }

    public void setCard_type(String card_type) {
        this.card_type = card_type;
    }

    public String getCard_month() {
        return card_month;
    }

    public void setCard_month(String card_month) {
        this.card_month = card_month;
    }

    public String getCard_year() {
        return card_year;
    }

    public void setCard_year(String card_year) {
        this.card_year = card_year;
    }

    public String getCard_security() {
        return card_security;
    }

    public void setCard_security(String card_security) {
        this.card_security = card_security;
    }

    private String card_number="";
    private String card_holder_name="";
    private String card_type="";
    private String card_month="";
    private String card_year="";
    private String card_security="";
}
