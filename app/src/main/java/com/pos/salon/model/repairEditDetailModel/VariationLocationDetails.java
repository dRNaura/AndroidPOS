package com.pos.salon.model.repairEditDetailModel;

public class VariationLocationDetails {

    public int id=0,product_id=0,product_variation_id=0,variation_id=0,location_id=0;
    public String qty_available="0",created_at="",updated_at="";


    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

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

    public int getProduct_variation_id() {
        return product_variation_id;
    }

    public void setProduct_variation_id(int product_variation_id) {
        this.product_variation_id = product_variation_id;
    }

    public int getVariation_id() {
        return variation_id;
    }

    public void setVariation_id(int variation_id) {
        this.variation_id = variation_id;
    }

    public int getLocation_id() {
        return location_id;
    }

    public void setLocation_id(int location_id) {
        this.location_id = location_id;
    }

    public String getQty_available() {
        return qty_available;
    }

    public void setQty_available(String qty_available) {
        this.qty_available = qty_available;
    }
}
