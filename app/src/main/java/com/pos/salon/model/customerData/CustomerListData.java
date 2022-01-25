package com.pos.salon.model.customerData;

import com.pos.salon.R;

import java.io.Serializable;

public class CustomerListData implements Serializable {

    private int id=0;
    private String text="";
    private String mobile="";

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private String email = "";
    private String landmark="";
    private String city="";
    private String state="";
    private String pay_term_number="";
    private String pay_term_type="";
    private String register_by="";

    public String getRegister_by() {
        return register_by;
    }

    public void setRegister_by(String register_by) {
        this.register_by = register_by;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name = "";
    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    private int imageId = R.drawable.customer;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPay_term_number() {
        return pay_term_number;
    }

    public void setPay_term_number(String pay_term_number) {
        this.pay_term_number = pay_term_number;
    }

    public String getPay_term_type() {
        return pay_term_type;
    }

    public void setPay_term_type(String pay_term_type) {
        this.pay_term_type = pay_term_type;
    }



}
