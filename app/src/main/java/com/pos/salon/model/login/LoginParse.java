package com.pos.salon.model.login;

import com.pos.salon.model.BusinessDetails;
import com.pos.salon.model.LoginPermissionsData;
import com.pos.salon.model.posLocation.CountryData;
import java.util.ArrayList;

public class LoginParse {

    private String access_token="";
    private String token="";
    private String token_type="";
    private UserDetail user;
    private String status="";
    private String status_text="";
    private String is_service_staff_enabled="";
    private  String is_tables_enabled="";
    private String expires_at="";
    public ArrayList<CountryData> countries=new ArrayList<>();
    public ArrayList<String> enabled_modules =new ArrayList<String>();
    public ArrayList<String> departments =new ArrayList<String>();
    public ArrayList<LoginPermissionsData> permissions =new ArrayList<LoginPermissionsData>();
    public BusinessDetails business_details;

    public ArrayList<String> getDepartments() {
        return departments;
    }

    public void setDepartments(ArrayList<String> departments) {
        this.departments = departments;
    }

    public BusinessDetails getBusiness_details() {
        return business_details;
    }

    public String getIs_tables_enabled() {
        return is_tables_enabled;
    }

    public void setIs_tables_enabled(String is_tables_enabled) {
        this.is_tables_enabled = is_tables_enabled;
    }



    public String getIs_service_staff_enabled() {
        return is_service_staff_enabled;
    }

    public void setIs_service_staff_enabled(String is_service_staff_enabled) {
        this.is_service_staff_enabled = is_service_staff_enabled;
    }


    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public UserDetail getUser() {
        return user;
    }

    public void setUser(UserDetail user) {
        this.user = user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus_text() {
        return status_text;
    }

    public void setStatus_text(String status_text) {
        this.status_text = status_text;
    }

    public String getExpires_at() {
        return expires_at;
    }

    public void setExpires_at(String expires_at) {
        this.expires_at = expires_at;
    }


    public ArrayList<CountryData> getCountryLists() {
        return countries;
    }
    public void setCountryList(ArrayList<CountryData> list){
        this.countries = list;
    }


    public ArrayList<LoginPermissionsData> getPermissionsDataLists() {
        return permissions;
    }
    public void setPermissionsDataLists(ArrayList<LoginPermissionsData> list){
        this.permissions = list;
    }


    public ArrayList<String> getenabled_modules() {
        return enabled_modules;
    }
    public void setenabled_modules(ArrayList<String> enabled_modules){
        this.enabled_modules = enabled_modules;
    }
}
