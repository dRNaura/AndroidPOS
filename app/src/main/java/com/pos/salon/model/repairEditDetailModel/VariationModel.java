package com.pos.salon.model.repairEditDetailModel;

import java.util.ArrayList;

public class VariationModel {

   public int id=0,product_id=0;
   public String name="",sell_price_inc_tax="" ;
    public ArrayList<VariationLocationDetails> variation_location_details=new ArrayList<VariationLocationDetails>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
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



    public ArrayList<VariationLocationDetails> getVariation_location_details() {
        return variation_location_details;
    }

    public void setVariation_location_details(ArrayList<VariationLocationDetails> variation_location_details) {
        this.variation_location_details = variation_location_details;
    }


}
