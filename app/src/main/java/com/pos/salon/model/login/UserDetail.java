package com.pos.salon.model.login;


import java.util.ArrayList;

public class UserDetail {

    private String id="";
    private String surname="";
    private String first_name="";
    private String last_name="";
    private String username="";
    private String email="";
    private String language="";
    private String contact_no="";
    private String address="";
    private int business_id=0;
    private String department_id="";
    private String status="";
    private String s_cmmsn_agnt="";
    private String cmmsn_percent="";
    private String selected_contacts="";
    private String profile_pic="";
    private String deleted_at="";
    private String created_at="";
    private String updated_at="";
    public ArrayList<Roles> roles =new ArrayList<Roles>();

    public ArrayList<Roles> getRoles() {
        return roles;
    }

    public void setRoles(ArrayList<Roles> roles) {
        this.roles = roles;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getContact_no() {
        return contact_no;
    }

    public void setContact_no(String contact_no) {
        this.contact_no = contact_no;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getBusiness_id() {
        return business_id;
    }

    public void setBusiness_id(int business_id) {
        this.business_id = business_id;
    }

    public String getDepartment_id() {
        return department_id;
    }

    public void setDepartment_id(String department_id) {
        this.department_id = department_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getS_cmmsn_agnt() {
        return s_cmmsn_agnt;
    }

    public void setS_cmmsn_agnt(String s_cmmsn_agnt) {
        this.s_cmmsn_agnt = s_cmmsn_agnt;
    }

    public String getCmmsn_percent() {
        return cmmsn_percent;
    }

    public void setCmmsn_percent(String cmmsn_percent) {
        this.cmmsn_percent = cmmsn_percent;
    }

    public String getSelected_contacts() {
        return selected_contacts;
    }

    public void setSelected_contacts(String selected_contacts) {
        this.selected_contacts = selected_contacts;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public String getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(String deleted_at) {
        this.deleted_at = deleted_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

}
