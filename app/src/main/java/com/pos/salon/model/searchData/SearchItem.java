package com.pos.salon.model.searchData;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class SearchItem implements Serializable {


    public SearchItem()
    {

    }
    public SearchItem(SearchItem objAlready) {
        this.setProduct_id(objAlready.getProduct_id());
        this.setVariation_name(objAlready.getVariation_name());
        this.setVariation(objAlready.getVariation());
        this.setVariationPrice(objAlready.getVariationPrice());
        this.setUnitFinalPrice(objAlready.getUnitFinalPrice());
        this.setModifier_sets(objAlready.getModifier_sets());
        this.setQty_available(objAlready.getQty_available());
        this.setDiscountt(objAlready.getDiscountt());
        this.setDiscountAmt(objAlready.getDiscountAmt());
        this.setTaxCalculateAmt(objAlready.getTaxCalculateAmt());
        this.setTaxAmount(objAlready.getTaxAmount());
        this.setSellLineId(objAlready.getSellLineId());
        this.setSub_sku(objAlready.getSub_sku());
        this.setEnable_stock(objAlready.getEnable_stock());
        this.setSelling_price(objAlready.getSelling_price());
        this.setVariation_id(objAlready.getVariation_id());
        this.setName(objAlready.getName());
        this.setActiveProduct(objAlready.activeProduct);
        this.setCalculationDone(objAlready.calculationDone);
        this.setColor(objAlready.getColor());
        this.setDiscountType(objAlready.getDiscountType());
        this.setFinalPrice(objAlready.getFinalPrice());
        this.setImage(objAlready.getImage());
        this.setProduct_columnid(objAlready.getProduct_columnid());
        this.setQuantity(objAlready.getQuantity());
        this.setTaxSelectId(objAlready.getTaxSelectId());
        this.setType(objAlready.getType());
        this.setTaxType(objAlready.getTaxType());
        this.setTaxCalculationDone(objAlready.taxCalculationDone);
        this.setselectedQuantity(objAlready.getselectedQuantity());
        this.setTaxCalculationAmt(objAlready.getTaxCalculationAmt());
    }

    public void setSellLineId(String sellLineId){
        this.sellLineId = sellLineId;
    }
    public String  getSellLineId() {
        return sellLineId;
    }

    public String  getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
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

    public void setType(String type){
        this.type = type;
    }

    public String getEnable_stock() {
        return enable_stock;
    }

    public void setEnable_stock(String enable_stock) {
        this.enable_stock = enable_stock;
    }

    public String getVariation_id() {
        return variation_id;
    }

    public void setselectedQuantity(String updateQuantity){
        this.updateQuantity=updateQuantity;
    }
    public String getselectedQuantity() {
        return updateQuantity;
    }

    public void setVariation_id(String variation_id) {
        this.variation_id = variation_id;
    }

    public String getVariation() {
        return variation;
    }

    public void setVariation(String variation) {
        this.variation = variation;
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

    public String getSelling_price() {
        return selling_price;
    }

    public void setVariationPrice(double varPrice) {
        this.variationPrice = varPrice;
    }

    public double getVariationPrice() {
        return variationPrice;
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

    private String product_id="0";
     private String name = "";
    private String type = "";
    private String enable_stock="";
    private String variation_id="";
    private String variation="";
    private String variation_name = "";
    private String color="";
    private String qty_available="0";
    private String selling_price = "0";
    private String sub_sku="";
    private String image="";
    private String product_columnid="";
    private String updateQuantity = "";
    private String sellLineId = "";
    public ProductDiscountModel discount;
    public  String style_no="";
    private ArrayList<SearchItem> details=new ArrayList<>();

    public ArrayList<SearchItem> getDetails() {
        return details;
    }

    public void setDetails(ArrayList<SearchItem> details) {
        this.details = details;
    }

    public String getStyle_no() {
        return style_no;
    }

    public void setStyle_no(String style_no) {
        this.style_no = style_no;
    }

    public String getSellLineNote() {
        return sellLineNote;
    }

    public void setSellLineNote(String sellLineNote) {
        this.sellLineNote = sellLineNote;
    }

    private String sellLineNote = "";
    private double variationPrice = 0.0;

    public String getProduct_columnid() {
        return product_columnid;
    }

    public void setProduct_columnid(String product_columnid) {
        this.product_columnid = product_columnid;
    }

    private ArrayList<Modifierrlist> modifier_sets = new ArrayList<Modifierrlist>();

    public String getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(String finalPrice) {
        this.finalPrice = finalPrice;
    }

    private String finalPrice = "0";


    public String getUnitFinalPrice() {
        return unitFinalPrice;
    }
    public void setUnitFinalPrice(String unitFinalPrice) {
        this.unitFinalPrice = unitFinalPrice;
    }

    private String unitFinalPrice = "0";


    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    private int  quantity = 1;
    public boolean isActiveProduct() {
        return activeProduct;
    }

    public void setActiveProduct(boolean activeProduct) {
        this.activeProduct = activeProduct;
    }

    private boolean activeProduct;

    public String getDiscountAmt() {
        return discountAmt;
    }

    public void setDiscountAmt(String discountAmt) {
        this.discountAmt = discountAmt;
    }

    private String discountAmt="0.00";

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    private String discountType="Select Discount Type";

    public boolean isCalculationDone() {
        return calculationDone;
    }

    public void setCalculationDone(boolean calculationDone) {
        this.calculationDone = calculationDone;
    }

    private boolean calculationDone = false;

    private String taxAmount="0.0";
    private String taxType="Select Tax Type";

    public String getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(String taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getTaxType() {
        return taxType;
    }

    public void setTaxType(String taxType) {
        this.taxType = taxType;
    }

    public boolean isTaxCalculationDone() {
        return taxCalculationDone;
    }

    public void setTaxCalculationDone(boolean taxCalculationDone) {
        this.taxCalculationDone = taxCalculationDone;
    }

    private boolean taxCalculationDone=false;

    public String getDiscountt() {
        return discountt;
    }

    public void setDiscountt(String discountt) {
        this.discountt = discountt;
    }

    private String discountt = "0.0";

    public String getTaxCalculationAmt() {
        return taxCalculationAmt;
    }

    public void setTaxCalculationAmt(String taxCalculationAmt) {
        this.taxCalculationAmt = taxCalculationAmt;
    }

    private String taxCalculationAmt="0.00";

    public String getTaxSelectId() {
        return taxSelectId;
    }

    public void setTaxSelectId(String taxSelectId) {
        this.taxSelectId = taxSelectId;
    }

    private String taxSelectId="";

    public double getTaxCalculateAmt() {
        return taxCalculateAmt;
    }

    public void setTaxCalculateAmt(double taxCalculateAmt) {
        this.taxCalculateAmt = taxCalculateAmt;
    }

    private double taxCalculateAmt=0.0;

    public String getVariation_name() {
        return variation_name;
    }

    public void setVariation_name(String variation_name) {
        this.variation_name = variation_name;
    }

    public ArrayList<Modifierrlist> getModifier_sets() {
        return modifier_sets;
    }

    public void setModifier_sets(ArrayList<Modifierrlist> modifier_sets) {
        this.modifier_sets = modifier_sets;
    }


    public class Modifierrlist{
        @SerializedName("id")
        private String id;

        @SerializedName("name")
        private String name;

        @SerializedName("enable_stock")
        private String enable_stock;

        @SerializedName("weight")
        private String weight;

        @SerializedName("products_price")
        private String products_price;

        @SerializedName("variations")
        private ArrayList<Variations> variations = new ArrayList<Variations>();

        private boolean visibility;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEnable_stock() {
            return enable_stock;
        }

        public void setEnable_stock(String enable_stock) {
            this.enable_stock = enable_stock;
        }

        public String getWeight() {
            return weight;
        }

        public void setWeight(String weight) {
            this.weight = weight;
        }

        public String getProducts_price() {
            return products_price;
        }

        public void setProducts_price(String products_price) {
            this.products_price = products_price;
        }

        public ArrayList<Variations> getVariations() {
            return variations;
        }

        public void setVariations(ArrayList<Variations> variations) {
            this.variations = variations;
        }

        public boolean isVisibility() {
            return visibility;
        }

        public void setVisibility(boolean visibility) {
            this.visibility = visibility;
        }
    }

    public class Variations{
        @SerializedName("id")
        @Expose
        private String id;

        @SerializedName("name")
        @Expose
        private String name = "";

        @SerializedName("sell_price_inc_tax")
        @Expose
        private String sell_price_inc_tax = "0.0";

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSell_price_inc_tax() {
            return sell_price_inc_tax;
        }

        public void setSell_price_inc_tax(String sell_price_inc_tax) {
            this.sell_price_inc_tax = sell_price_inc_tax;
        }
    }
}
