package com.pos.salon.model.posLocation;

import com.pos.salon.R;

import java.io.Serializable;

public class CurriencyData implements Serializable {

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

    private String name = "";
    private String id = "0";

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    private int imageId = R.drawable.currency;
}
