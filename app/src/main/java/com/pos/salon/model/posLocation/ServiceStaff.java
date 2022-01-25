package com.pos.salon.model.posLocation;

import com.pos.salon.R;

public class ServiceStaff {


    public String first_name="",last_name="";
    public String id="",name="";
    private int imageId = R.drawable.customer;


    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }


    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}
