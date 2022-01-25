package com.pos.salon.model.repairModel;

public class SpinnerSelectionModel {
    public String name="",description="",barcode_id="",order_status_id="",idString="";
    public int  id=0,brand_id=0,unit_id=0,model_id=0,condition_id=0,transmission_id=0,fuel_id=0,color_id=0,inteColor_id=0,extColor_id=0;

   public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
