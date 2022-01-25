package com.pos.salon.model;

public class BusinessDetails {

    public String exchange_rate="",currency_code="",pos_settings="";

    public String getExchange_rate() {
        return exchange_rate;
    }

    public void setExchange_rate(String exchange_rate) {
        this.exchange_rate = exchange_rate;
    }

    public String getCurrency_code() {
        return currency_code;
    }

    public void setCurrency_code(String currency_code) {
        this.currency_code = currency_code;
    }

    public String getPos_settings() {
        return pos_settings;
    }

    public void setPos_settings(String pos_settings) {
        this.pos_settings = pos_settings;
    }
}
