package com.pos.salon.model.VariableModel;

import java.util.ArrayList;

public class VariationValueModel {

    public int id=0,variation_template_name_id=0,variation_template_id=0,color_id=0,position=0;
    public String name="",created_at="",updated_at="",variation_sku="",dpp_exc_tax="0.0",dpp_inc_tax="0.0",var_exc_margin="0.0",var_dsp_exc_tax="0.0";


    public ArrayList<AllVariationModel> varialtionValues=new ArrayList<>();

    public ArrayList<AllVariationModel> getVarialtionValues() {
        return varialtionValues;
    }

    public void setVarialtionValues(ArrayList<AllVariationModel> varialtionValues) {
        this.varialtionValues = varialtionValues;
    }
}
