package com.pos.salon.model.repairEditDetailModel;

import java.util.ArrayList;

public class ProductModel {

public int id=0,business_id=0,brand_id=0;
    public String name="",products_price="",products_model="";

    public String getProducts_model() {
        return products_model;
    }

    public void setProducts_model(String products_model) {
        this.products_model = products_model;
    }

    public ArrayList<VariationModel> variations=new ArrayList<>();


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBusiness_id() {
        return business_id;
    }

    public void setBusiness_id(int business_id) {
        this.business_id = business_id;
    }

    public int getBrand_id() {
        return brand_id;
    }

    public void setBrand_id(int brand_id) {
        this.brand_id = brand_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProducts_price() {
        return products_price;
    }

    public void setProducts_price(String products_price) {
        this.products_price = products_price;
    }

    public ArrayList<VariationModel> getVariations() {
        return variations;
    }

    public void setVariations(ArrayList<VariationModel> variations) {
        this.variations = variations;
    }
}
