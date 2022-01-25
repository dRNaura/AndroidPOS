package com.pos.salon.model.posLocation;

import com.pos.salon.model.repairModel.SpinnerSelectionModel;
import com.pos.salon.model.repairModel.RepairItemTypesModel;
import com.pos.salon.model.customerData.CustomerListData;
import java.util.ArrayList;

public class PosLocationResponse {

    private boolean success;
    private ArrayList<BusinessLocationData> business_locations=new ArrayList<>();
    private ArrayList<CurriencyData> currencies=new ArrayList<>();
    private boolean is_service_staff;
    private ArrayList<ServiceStaff> service_staff=new ArrayList<>();
    private ArrayList<RepairItemTypesModel> repair_item_types=new ArrayList<>();
    private ArrayList<ResTables> res_tables =new ArrayList<>();
    private String default_currency="";
    private ArrayList<CountryData> countries =new ArrayList();
    private ArrayList<SpinnerSelectionModel> brands=new ArrayList<>();

    public ArrayList<SpinnerSelectionModel> getBrands() {
        return brands;
    }

    public void setBrands(ArrayList<SpinnerSelectionModel> brands) {
        this.brands = brands;
    }

    public ArrayList<RepairItemTypesModel> getRepair_item_types() {
        return repair_item_types;
    }

    public void setRepair_item_types(ArrayList<RepairItemTypesModel> repair_item_types) {
        this.repair_item_types = repair_item_types;
    }


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public ArrayList<BusinessLocationData> getBusiness_locations() {
        return business_locations;
    }

    public void setBusiness_locations(ArrayList<BusinessLocationData> business_locations) {
        this.business_locations = business_locations;
    }

    public ArrayList<CurriencyData> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(ArrayList<CurriencyData> currencies) {
        this.currencies = currencies;
    }

    public boolean isIs_service_staff() {
        return is_service_staff;
    }

    public void setIs_service_staff(boolean is_service_staff) {
        this.is_service_staff = is_service_staff;
    }

    public ArrayList<ServiceStaff> getService_staff() {
        return service_staff;
    }

    public void setService_staff(ArrayList<ServiceStaff> service_staff) {
        this.service_staff = service_staff;
    }

    public ArrayList<CustomerListData> getWalk_in_customer() {
        return walk_in_customer;
    }

    public void setWalk_in_customer(ArrayList<CustomerListData> walk_in_customer) {
        this.walk_in_customer = walk_in_customer;
    }


    public ArrayList<ResTables> getRes_tables() {
        return res_tables;
    }

    public void setRes_tables(ArrayList<ResTables> res_tables) {
        this.res_tables = res_tables;
    }


    private ArrayList<CustomerListData> walk_in_customer;

    public ArrayList<Taxes> getTaxes() {
        return taxes;
    }

    public void setTaxes(ArrayList<Taxes> taxes) {
        this.taxes = taxes;
    }

    private ArrayList<Taxes> taxes;

    public String getDefault_currency() {
        return default_currency;
    }

    public void setDefault_currency(String default_currency) {
        this.default_currency = default_currency;
    }


    public ArrayList<CountryData> getCountryLists() {
        return countries;
    }
    public void setCountryList(ArrayList<CountryData> list){
        this.countries = list;
    }
}
