package com.pos.salon.model.PurchaseModel;

import com.pos.salon.model.searchData.ProductDiscountModel;

import java.io.Serializable;

public class PurchaseProductSearchModel implements Serializable {

    public int product_id=0;
    public int variation_id=0;
    public int enable_stock=0;
    public int quantity = 1;;
    public String name="",type="",style_no="",variation="",variation_name="",color="",qty_available="",default_purchase_price="0.00",profit_percent="0.00",selling_price="0.00";
    public String sub_sku="",image="", unitFinalPrice = "0",discount_percent="0",unitCostBeforTax="0",lineTotal="0";
    public ProductDiscountModel discount;
    private boolean activeProduct;

    public String getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(String lineTotal) {
        this.lineTotal = lineTotal;
    }

    public String getUnitCostBeforTax() {
        return unitCostBeforTax;
    }

    public void setUnitCostBeforTax(String unitCostBeforTax) {
        this.unitCostBeforTax = unitCostBeforTax;
    }

    public String getDiscount_percent() {
        return discount_percent;
    }

    public void setDiscount_percent(String discount_percent) {
        this.discount_percent = discount_percent;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getUnitFinalPrice() {
        return unitFinalPrice;
    }

    public void setUnitFinalPrice(String unitFinalPrice) {
        this.unitFinalPrice = unitFinalPrice;
    }

    public boolean isActiveProduct() {
        return activeProduct;
    }

    public void setActiveProduct(boolean activeProduct) {
        this.activeProduct = activeProduct;
    }

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

    public int getEnable_stock() {
        return enable_stock;
    }

    public void setEnable_stock(int enable_stock) {
        this.enable_stock = enable_stock;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStyle_no() {
        return style_no;
    }

    public void setStyle_no(String style_no) {
        this.style_no = style_no;
    }

    public String getVariation() {
        return variation;
    }

    public void setVariation(String variation) {
        this.variation = variation;
    }

    public String getVariation_name() {
        return variation_name;
    }

    public void setVariation_name(String variation_name) {
        this.variation_name = variation_name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getQty_available() {
        return qty_available;
    }

    public void setQty_available(String qty_available) {
        this.qty_available = qty_available;
    }

    public String getDefault_purchase_price() {
        return default_purchase_price;
    }

    public void setDefault_purchase_price(String default_purchase_price) {
        this.default_purchase_price = default_purchase_price;
    }

    public String getProfit_percent() {
        return profit_percent;
    }

    public void setProfit_percent(String profit_percent) {
        this.profit_percent = profit_percent;
    }

    public String getSelling_price() {
        return selling_price;
    }

    public void setSelling_price(String selling_price) {
        this.selling_price = selling_price;
    }

    public String getSub_sku() {
        return sub_sku;
    }

    public void setSub_sku(String sub_sku) {
        this.sub_sku = sub_sku;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ProductDiscountModel getDiscount() {
        return discount;
    }

    public void setDiscount(ProductDiscountModel discount) {
        this.discount = discount;
    }
}