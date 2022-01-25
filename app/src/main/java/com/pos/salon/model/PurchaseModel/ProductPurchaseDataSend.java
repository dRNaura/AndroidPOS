package com.pos.salon.model.PurchaseModel;

import java.io.Serializable;

public class ProductPurchaseDataSend implements Serializable {

    public int product_id=0,variation_id=0,quantity=0;
    public String purchase_price="",default_sell_price="",profit_percent="",discount_percent="",item_tax="0.00",purchase_line_tax_id="",pp_without_discount="", purchase_price_inc_tax="";


    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public int getVariation_id() {
        return variation_id;
    }

    public void setVariation_id(int variation_id) {
        this.variation_id = variation_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getPp_without_discount() {
        return pp_without_discount;
    }

    public void setPp_without_discount(String pp_without_discount) {
        this.pp_without_discount = pp_without_discount;
    }

    public String getDiscount_percent() {
        return discount_percent;
    }

    public void setDiscount_percent(String discount_percent) {
        this.discount_percent = discount_percent;
    }

    public String getPurchase_price() {
        return purchase_price;
    }

    public void setPurchase_price(String purchase_price) {
        this.purchase_price = purchase_price;
    }

    public String getPurchase_line_tax_id() {
        return purchase_line_tax_id;
    }

    public void setPurchase_line_tax_id(String purchase_line_tax_id) {
        this.purchase_line_tax_id = purchase_line_tax_id;
    }

    public String getItem_tax() {
        return item_tax;
    }

    public void setItem_tax(String item_tax) {
        this.item_tax = item_tax;
    }

    public String getPurchase_price_inc_tax() {
        return purchase_price_inc_tax;
    }

    public void setPurchase_price_inc_tax(String purchase_price_inc_tax) {
        this.purchase_price_inc_tax = purchase_price_inc_tax;
    }

    public String getProfit_percent() {
        return profit_percent;
    }

    public void setProfit_percent(String profit_percent) {
        this.profit_percent = profit_percent;
    }

    public String getDefault_sell_price() {
        return default_sell_price;
    }

    public void setDefault_sell_price(String default_sell_price) {
        this.default_sell_price = default_sell_price;
    }
}
