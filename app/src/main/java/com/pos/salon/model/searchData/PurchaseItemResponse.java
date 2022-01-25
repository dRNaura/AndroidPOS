package com.pos.salon.model.searchData;

import com.pos.salon.model.PurchaseModel.PurchaseProductSearchModel;

import java.io.Serializable;
import java.util.ArrayList;

public class PurchaseItemResponse implements Serializable {

    private boolean success;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public ArrayList<PurchaseProductSearchModel> getProduct_list() {
        return product_list;
    }

    public void setProduct_list(ArrayList<PurchaseProductSearchModel> product_list) {
        this.product_list = product_list;
    }

    private ArrayList<PurchaseProductSearchModel>product_list;
}

