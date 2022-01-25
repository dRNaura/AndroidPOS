package com.pos.salon.model.cashData;

import java.io.Serializable;

public class LineData implements Serializable {

    private String name="";
    private String variation="";
    private String quantity="";
    private String units="";
    private String unit_price="";
    private String tax="";
    private String tax_unformatted="";
    private String  tax_name="";
    private String tax_percent="";
    private String  unit_price_inc_tax="";


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVariation() {
        return variation;
    }

    public void setVariation(String variation) {
        this.variation = variation;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getUnit_price() {
        return unit_price;
    }

    public void setUnit_price(String unit_price) {
        this.unit_price = unit_price;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getTax_unformatted() {
        return tax_unformatted;
    }

    public void setTax_unformatted(String tax_unformatted) {
        this.tax_unformatted = tax_unformatted;
    }

    public String getTax_name() {
        return tax_name;
    }

    public void setTax_name(String tax_name) {
        this.tax_name = tax_name;
    }

    public String getTax_percent() {
        return tax_percent;
    }

    public void setTax_percent(String tax_percent) {
        this.tax_percent = tax_percent;
    }

    public String getUnit_price_inc_tax() {
        return unit_price_inc_tax;
    }

    public void setUnit_price_inc_tax(String unit_price_inc_tax) {
        this.unit_price_inc_tax = unit_price_inc_tax;
    }

    public String getUnit_price_exc_tax() {
        return unit_price_exc_tax;
    }

    public void setUnit_price_exc_tax(String unit_price_exc_tax) {
        this.unit_price_exc_tax = unit_price_exc_tax;
    }

    public String getPrice_exc_tax() {
        return price_exc_tax;
    }

    public void setPrice_exc_tax(String price_exc_tax) {
        this.price_exc_tax = price_exc_tax;
    }

    public String getUnit_price_before_discount() {
        return unit_price_before_discount;
    }

    public void setUnit_price_before_discount(String unit_price_before_discount) {
        this.unit_price_before_discount = unit_price_before_discount;
    }

    public String getLine_total() {
        return line_total;
    }

    public void setLine_total(String line_total) {
        this.line_total = line_total;
    }

    public String getLine_discount() {
        return line_discount;
    }

    public void setLine_discount(String line_discount) {
        this.line_discount = line_discount;
    }

    public String getSub_sku() {
        return sub_sku;
    }

    public void setSub_sku(String sub_sku) {
        this.sub_sku = sub_sku;
    }

    public String getCat_code() {
        return cat_code;
    }

    public void setCat_code(String cat_code) {
        this.cat_code = cat_code;
    }

    private String  unit_price_exc_tax;
    private String price_exc_tax;
    private String unit_price_before_discount;
    private String   line_total;
    private String  line_discount;
    private String  sub_sku;
    private String cat_code;
}
