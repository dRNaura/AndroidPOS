package com.pos.salon.model.payment;

import java.io.Serializable;

public class ProductDataSend implements Serializable {

    private String unit_price="";
    private String line_discount_type="";
    private String line_discount_amount="0.00";
    private String item_tax="";
    private String tax_id="";
    private String product_id="";
    private String variation_id="";
    private String enable_stock="";
    private String quantity="";
    private String product_type="";
    private String unit_price_inc_tax="";


    public String getProduct_type() {
        return product_type;
    }

    public void setProduct_type(String product_type) {
        this.product_type = product_type;
    }

    public String getUnit_price() {
        return unit_price;
    }

    public void setUnit_price(String unit_price) {
        this.unit_price = unit_price;
    }

    public String getLine_discount_type() {
        return line_discount_type;
    }

    public void setLine_discount_type(String line_discount_type) {
        this.line_discount_type = line_discount_type;
    }

    public String getLine_discount_amount() {
        return line_discount_amount;
    }

    public void setLine_discount_amount(String line_discount_amount) {
        this.line_discount_amount = line_discount_amount;
    }

    public String getItem_tax() {
        return item_tax;
    }

    public void setItem_tax(String item_tax) {
        this.item_tax = item_tax;
    }

    public String getTax_id() {
        return tax_id;
    }

    public void setTax_id(String tax_id) {
        this.tax_id = tax_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
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

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getUnit_price_inc_tax() {
        return unit_price_inc_tax;
    }

    public void setUnit_price_inc_tax(String unit_price_inc_tax) {
        this.unit_price_inc_tax = unit_price_inc_tax;
    }

}
