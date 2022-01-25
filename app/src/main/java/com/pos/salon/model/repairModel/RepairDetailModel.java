package com.pos.salon.model.repairModel;

import com.pos.salon.model.posLocation.BusinessLocationData;
import com.pos.salon.model.posLocation.ServiceStaff;
import com.pos.salon.model.repairEditDetailModel.DiagnosticDetailModel;
import com.pos.salon.model.repairEditDetailModel.OrderStatusesModel;
import com.pos.salon.model.repairEditDetailModel.RepairDetailsModel;
import com.pos.salon.model.repairEditDetailModel.RepairPartsDetailModel;

import java.util.ArrayList;

public class RepairDetailModel {
    private boolean success;
    private ArrayList<BusinessLocationData> business_locations=new ArrayList<>();
    private ArrayList<ServiceStaff> service_staff=new ArrayList<>();
    private ArrayList<RepairItemTypesModel> repair_item_types =new ArrayList<>();
    private ArrayList<SpinnerSelectionModel> brands =new ArrayList<>();
    private ArrayList<RepairPartsDetailModel> repair_parts_details=new ArrayList<>();
    private RepairDetailsModel repair_details;
    private OrderStatusesModel orderStatuses;
    private DiagnosticDetailModel diagnostics_product_details;

    public ArrayList<RepairPartsDetailModel> getRepair_parts_details() {
        return repair_parts_details;
    }

    public void setRepair_parts_details(ArrayList<RepairPartsDetailModel> repair_parts_details) {
        this.repair_parts_details = repair_parts_details;
    }

    public OrderStatusesModel getOrderStatuses() {
        return orderStatuses;
    }

    public void setOrderStatuses(OrderStatusesModel orderStatuses) {
        this.orderStatuses = orderStatuses;
    }

    public DiagnosticDetailModel getDiagnostics_product_details() {
        return diagnostics_product_details;
    }

    public void setDiagnostics_product_details(DiagnosticDetailModel diagnostics_product_details) {
        this.diagnostics_product_details = diagnostics_product_details;
    }


    public RepairDetailsModel getRepairDetailsModel() {
        return repair_details;
    }

    public void setRepairDetailsModel(RepairDetailsModel repairDetailsModel) {
        this.repair_details = repairDetailsModel;
    }

    public ArrayList<BusinessLocationData> getBusiness_locations() {
        return business_locations;
    }

    public void setBusiness_locations(ArrayList<BusinessLocationData> business_locations) {
        this.business_locations = business_locations;
    }

    public ArrayList<ServiceStaff> getService_staff() {
        return service_staff;
    }

    public void setService_staff(ArrayList<ServiceStaff> service_staff) {
        this.service_staff = service_staff;
    }
    public ArrayList<RepairItemTypesModel> getRepair_item_types() {
        return repair_item_types;
    }

    public void setRepair_item_types(ArrayList<RepairItemTypesModel> repair_item_types) {
        this.repair_item_types = repair_item_types;
    }
    public ArrayList<SpinnerSelectionModel> getBrands() {
        return brands;
    }

    public void setBrands(ArrayList<SpinnerSelectionModel> brands) {
        this.brands = brands;
    }
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
