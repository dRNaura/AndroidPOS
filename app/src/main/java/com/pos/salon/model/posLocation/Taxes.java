package com.pos.salon.model.posLocation;

import java.io.Serializable;

public class Taxes implements Serializable {

    private String id="";
    private String name="";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    private String amount="";
}
