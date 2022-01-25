package com.pos.salon.model.repairEditDetailModel;

public class RepairDetailsModel {

    public int id = 0, product_id = 0, location_id = 0, service_staff_id, contact_id, diagnostics_product_id, repair_parts_location_id, repair_item_type_id;

    public String item_name = "", expected_close_date = "", defect="", comment = "", car_year = "", chasis_no = "", registration_no = "", vin_no = "", added_date = "", diagnostics = "";

    public String diagnostics_amount_paid = "", repair_payment_status = "", repair_status = "", work_progress = "", created_at = "", updated_at = "", deleted_at, contact_name;


    public ProductModel products;
    public CustomerData contact;


    public CustomerData getContact() {
        return contact;
    }

    public void setContact(CustomerData contact) {
        this.contact = contact;
    }

    public ProductModel getProducts() {
        return products;
    }

    public void setProducts(ProductModel products) {
        this.products = products;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public int getLocation_id() {
        return location_id;
    }

    public void setLocation_id(int location_id) {
        this.location_id = location_id;
    }

    public int getService_staff_id() {
        return service_staff_id;
    }

    public void setService_staff_id(int service_staff_id) {
        this.service_staff_id = service_staff_id;
    }

    public int getContact_id() {
        return contact_id;
    }

    public void setContact_id(int contact_id) {
        this.contact_id = contact_id;
    }

    public int getDiagnostics_product_id() {
        return diagnostics_product_id;
    }

    public void setDiagnostics_product_id(int diagnostics_product_id) {
        this.diagnostics_product_id = diagnostics_product_id;
    }

    public int getRepair_parts_location_id() {
        return repair_parts_location_id;
    }

    public void setRepair_parts_location_id(int repair_parts_location_id) {
        this.repair_parts_location_id = repair_parts_location_id;
    }

    public int getRepair_item_type_id() {
        return repair_item_type_id;
    }

    public void setRepair_item_type_id(int repair_item_type_id) {
        this.repair_item_type_id = repair_item_type_id;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getExpected_close_date() {
        return expected_close_date;
    }

    public void setExpected_close_date(String expected_close_date) {
        this.expected_close_date = expected_close_date;
    }

    public String getDefect() {
        return defect;
    }

    public void setDefect(String defect) {
        this.defect = defect;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCar_year() {
        return car_year;
    }

    public void setCar_year(String car_year) {
        this.car_year = car_year;
    }

    public String getChasis_no() {
        return chasis_no;
    }

    public void setChasis_no(String chasis_no) {
        this.chasis_no = chasis_no;
    }

    public String getRegistration_no() {
        return registration_no;
    }

    public void setRegistration_no(String registration_no) {
        this.registration_no = registration_no;
    }

    public String getVin_no() {
        return vin_no;
    }

    public void setVin_no(String vin_no) {
        this.vin_no = vin_no;
    }

    public String getAdded_date() {
        return added_date;
    }

    public void setAdded_date(String added_date) {
        this.added_date = added_date;
    }

    public String getDiagnostics() {
        return diagnostics;
    }

    public void setDiagnostics(String diagnostics) {
        this.diagnostics = diagnostics;
    }

    public String getDiagnostics_amount_paid() {
        return diagnostics_amount_paid;
    }

    public void setDiagnostics_amount_paid(String diagnostics_amount_paid) {
        this.diagnostics_amount_paid = diagnostics_amount_paid;
    }

    public String getRepair_payment_status() {
        return repair_payment_status;
    }

    public void setRepair_payment_status(String repair_payment_status) {
        this.repair_payment_status = repair_payment_status;
    }

    public String getRepair_status() {
        return repair_status;
    }

    public void setRepair_status(String repair_status) {
        this.repair_status = repair_status;
    }

    public String getWork_progress() {
        return work_progress;
    }

    public void setWork_progress(String work_progress) {
        this.work_progress = work_progress;
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

    public String getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(String deleted_at) {
        this.deleted_at = deleted_at;
    }

    public String getContact_name() {
        return contact_name;
    }

    public void setContact_name(String contact_name) {
        this.contact_name = contact_name;
    }
}
