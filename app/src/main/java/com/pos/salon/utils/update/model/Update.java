
package com.pos.salon.utils.update.model;

public class Update {

    private boolean forced;
    private boolean ignore;
    private String updateContent="";
    private String updateUrl="";
    private int versionCode=0;
    private String versionName="";
    private String md5="";
    private String pkg="";


    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }


    public void setForced(boolean forced) {
        this.forced = forced;
    }


    public void setUpdateContent(String updateContent) {
        this.updateContent = updateContent;
    }


    public void setUpdateUrl(String updateUrl) {
        this.updateUrl = updateUrl;
    }


    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }


    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }


    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public void setPkg(String pkg) {
        this.pkg = pkg;
    }

    public boolean isForced() {
        return forced;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public String getUpdateContent() {
        return updateContent;
    }

    public String getUpdateUrl() {
        return updateUrl;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public String getMd5() {
        return md5;
    }

    public String getPkg() {
        return pkg;
    }

    @Override
    public String toString() {
        return "Update{" +
                "forced=" + forced +
                ", ignore=" + ignore +
                ", updateContent='" + updateContent + '\'' +
                ", updateUrl='" + updateUrl + '\'' +
                ", versionCode=" + versionCode +
                ", versionName='" + versionName + '\'' +
                ", md5='" + md5 + '\'' +
                ", pkg='" + pkg + '\'' +
                '}';
    }
}
