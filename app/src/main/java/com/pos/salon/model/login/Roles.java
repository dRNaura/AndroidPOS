package com.pos.salon.model.login;

import com.pos.salon.model.LoginPermissionsData;

import java.util.ArrayList;

public class Roles {

    public ArrayList<LoginPermissionsData> permissions =new ArrayList<LoginPermissionsData>();

    public ArrayList<LoginPermissionsData> getPermissions() {
        return permissions;
    }

    public void setPermissions(ArrayList<LoginPermissionsData> permissions) {
        this.permissions = permissions;
    }
}
