package com.pos.salon.model;

import java.io.Serializable;

public class LoginPermissionsData implements Serializable {

    public String name="",guard_name="",created_at="",updated_at="";
    public int id=0;

    private PivotData pivot;

    public String getPermission_name() {
        return name;
    }

    public void setPermission_name(String permission_name) {
        this.name = permission_name;
    }

    public String getGuard_name() {
        return guard_name;
    }

    public void setGuard_name(String guard_name) {
        this.guard_name = guard_name;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PivotData getPivot() {
        return pivot;
    }

    public void setPivot(PivotData user) {
        this.pivot = user;
    }
}
