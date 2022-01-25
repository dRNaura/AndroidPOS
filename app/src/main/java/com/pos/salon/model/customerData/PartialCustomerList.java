package com.pos.salon.model.customerData;

import java.io.Serializable;

public class PartialCustomerList implements Serializable {

    private String product_name="";
    private String product_id="";
    private String unit="";
    private String unit_price_before_discount="";
    private String sell_price_inc_tax="";
    private String quantity_ordered="";
    private String line_discount_amount="";
    private String line_discount_type="";
    private String item_tax="";
    private String variation_id="";
    private String enable_stock="";



    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }


    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }


    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getUnit_price_before_discount() {
        return unit_price_before_discount;
    }

    public void setUnit_price_before_discount(String unit_price_before_discount) {
        this.unit_price_before_discount = unit_price_before_discount;
    }

    public String getSell_price_inc_tax() {
        return sell_price_inc_tax;
    }

    public void setSell_price_inc_tax(String sell_price_inc_tax) {
        this.sell_price_inc_tax = sell_price_inc_tax;
    }


    public String getQuantity_ordered() {
        return quantity_ordered;
    }

    public void setQuantity_ordered(String quantity_ordered) {
        this.quantity_ordered = quantity_ordered;
    }

    public String getLine_discount_amount() {
        return line_discount_amount;
    }

    public void setLine_discount_amount(String line_discount_amount) {
        this.line_discount_amount = line_discount_amount;
    }

    public String getLine_discount_type() {
        return line_discount_type;
    }

    public void setLine_discount_type(String line_discount_type) {
        this.line_discount_type = line_discount_type;
    }

    public String getItem_tax() {
        return item_tax;
    }

    public void setItem_tax(String item_tax) {
        this.item_tax = item_tax;
    }

    public String getVariation_id() {
        return variation_id;
    }

    public void setVariation_id(String variation_id) {
        this.variation_id = variation_id;
    }

    public String getEnable_stock() {
        return enable_stock;
    }

    public void setEnable_stock(String enable_stock) {
        this.enable_stock = enable_stock;
    }
}
