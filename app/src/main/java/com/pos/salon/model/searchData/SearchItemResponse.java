package com.pos.salon.model.searchData;

import java.io.Serializable;
import java.util.ArrayList;

public class SearchItemResponse implements Serializable {

    private boolean success;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public ArrayList<SearchItem> getProduct_list() {
        return product_list;
    }

    public void setProduct_list(ArrayList<SearchItem> product_list) {
        this.product_list = product_list;
    }

    private ArrayList<SearchItem>product_list=new ArrayList<>();
}
