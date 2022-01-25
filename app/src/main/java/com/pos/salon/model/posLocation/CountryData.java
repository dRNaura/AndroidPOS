package com.pos.salon.model.posLocation;


import java.io.Serializable;

public class CountryData implements Serializable {


    public String getcountry_code() {
        return country_code;
    }

    public void setcountry_code(String country_code) {
        this.country_code = country_code;
    }

    public String getCountryName() {
        return countries_name;
    }

    public void setCountryName(String name) {
        this.countries_name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String countries_name="";
    private String country_code="";
    private String id="";



}
