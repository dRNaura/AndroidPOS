package com.pos.salon.model.posLocation;

import com.pos.salon.R;

import java.io.Serializable;

public class BusinessLocationData implements Serializable {

    public String name="";
    public String id="";
    private int imageId = R.drawable.location;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }


}
