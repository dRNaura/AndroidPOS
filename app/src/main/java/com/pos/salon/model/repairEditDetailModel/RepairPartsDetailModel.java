package com.pos.salon.model.repairEditDetailModel;


public class RepairPartsDetailModel {

   public int id=0,repairs_id=0,product_id=0,quantity=0,business_id=0,unit_id=0,category_id=0,sub_category_id=0,sub_sub_category_id=0,tax=0,base_unit_id=0;
  public int enable_stock=0,enable_restock=0,alert_quantity=0,enable_sr_no=0,is_inactive=0,created_by=0,discount_status=0,products_status=0,products_tax_class_id=0;
   public int products_ordered=0,products_liked=0,products_viewed=0,show_website=0,show_cart_button=0,low_limit=0,products_type=0,products_min_order=0,products_max_stock=0,allow_decimal=0,is_active=0;

   public String unit_price="",repair_parts_payment_status="",name="",tax_amount="",tax_type="",sku="",barcode_type="",expiry_period="",expiry_period_type="";
   public String weight="",product_custom_field1="",product_custom_field2="",product_custom_field3="",product_custom_field4="",youtub_url="",image="";
   public String product_description="",created_at="",updated_at="",products_model="",products_price="",discount="",discount_type="",discount_start_date="",discount_expires_date="";
   public String applicable_to="",products_weight="",products_weight_unit="",products_slug="",actual_name="",short_name="",base_unit_multiplier="",deleted_at="";

   public ProductModel product;

    public ProductModel getProducts() {
        return product;
    }

    public void setProducts(ProductModel products) {
        this.product = products;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRepairs_id() {
        return repairs_id;
    }

    public void setRepairs_id(int repairs_id) {
        this.repairs_id = repairs_id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getBusiness_id() {
        return business_id;
    }

    public void setBusiness_id(int business_id) {
        this.business_id = business_id;
    }

    public int getUnit_id() {
        return unit_id;
    }

    public void setUnit_id(int unit_id) {
        this.unit_id = unit_id;
    }


    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public int getSub_category_id() {
        return sub_category_id;
    }

    public void setSub_category_id(int sub_category_id) {
        this.sub_category_id = sub_category_id;
    }

    public int getSub_sub_category_id() {
        return sub_sub_category_id;
    }

    public void setSub_sub_category_id(int sub_sub_category_id) {
        this.sub_sub_category_id = sub_sub_category_id;
    }

    public int getTax() {
        return tax;
    }

    public void setTax(int tax) {
        this.tax = tax;
    }

    public int getBase_unit_id() {
        return base_unit_id;
    }

    public void setBase_unit_id(int base_unit_id) {
        this.base_unit_id = base_unit_id;
    }

    public int getEnable_stock() {
        return enable_stock;
    }

    public void setEnable_stock(int enable_stock) {
        this.enable_stock = enable_stock;
    }

    public int getEnable_restock() {
        return enable_restock;
    }

    public void setEnable_restock(int enable_restock) {
        this.enable_restock = enable_restock;
    }

    public int getAlert_quantity() {
        return alert_quantity;
    }

    public void setAlert_quantity(int alert_quantity) {
        this.alert_quantity = alert_quantity;
    }

    public int getEnable_sr_no() {
        return enable_sr_no;
    }

    public void setEnable_sr_no(int enable_sr_no) {
        this.enable_sr_no = enable_sr_no;
    }

    public int getIs_inactive() {
        return is_inactive;
    }

    public void setIs_inactive(int is_inactive) {
        this.is_inactive = is_inactive;
    }

    public int getCreated_by() {
        return created_by;
    }

    public void setCreated_by(int created_by) {
        this.created_by = created_by;
    }

    public int getDiscount_status() {
        return discount_status;
    }

    public void setDiscount_status(int discount_status) {
        this.discount_status = discount_status;
    }

    public int getProducts_status() {
        return products_status;
    }

    public void setProducts_status(int products_status) {
        this.products_status = products_status;
    }

    public int getProducts_tax_class_id() {
        return products_tax_class_id;
    }

    public void setProducts_tax_class_id(int products_tax_class_id) {
        this.products_tax_class_id = products_tax_class_id;
    }

    public int getProducts_ordered() {
        return products_ordered;
    }

    public void setProducts_ordered(int products_ordered) {
        this.products_ordered = products_ordered;
    }

    public int getProducts_liked() {
        return products_liked;
    }

    public void setProducts_liked(int products_liked) {
        this.products_liked = products_liked;
    }

    public int getProducts_viewed() {
        return products_viewed;
    }

    public void setProducts_viewed(int products_viewed) {
        this.products_viewed = products_viewed;
    }

    public int getShow_website() {
        return show_website;
    }

    public void setShow_website(int show_website) {
        this.show_website = show_website;
    }

    public int getShow_cart_button() {
        return show_cart_button;
    }

    public void setShow_cart_button(int show_cart_button) {
        this.show_cart_button = show_cart_button;
    }

    public int getLow_limit() {
        return low_limit;
    }

    public void setLow_limit(int low_limit) {
        this.low_limit = low_limit;
    }

    public int getProducts_type() {
        return products_type;
    }

    public void setProducts_type(int products_type) {
        this.products_type = products_type;
    }

    public int getProducts_min_order() {
        return products_min_order;
    }

    public void setProducts_min_order(int products_min_order) {
        this.products_min_order = products_min_order;
    }

    public int getProducts_max_stock() {
        return products_max_stock;
    }

    public void setProducts_max_stock(int products_max_stock) {
        this.products_max_stock = products_max_stock;
    }

    public int getAllow_decimal() {
        return allow_decimal;
    }

    public void setAllow_decimal(int allow_decimal) {
        this.allow_decimal = allow_decimal;
    }

    public int getIs_active() {
        return is_active;
    }

    public void setIs_active(int is_active) {
        this.is_active = is_active;
    }

    public String getUnit_price() {
        return unit_price;
    }

    public void setUnit_price(String unit_price) {
        this.unit_price = unit_price;
    }

    public String getRepair_parts_payment_status() {
        return repair_parts_payment_status;
    }

    public void setRepair_parts_payment_status(String repair_parts_payment_status) {
        this.repair_parts_payment_status = repair_parts_payment_status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTax_amount() {
        return tax_amount;
    }

    public void setTax_amount(String tax_amount) {
        this.tax_amount = tax_amount;
    }

    public String getTax_type() {
        return tax_type;
    }

    public void setTax_type(String tax_type) {
        this.tax_type = tax_type;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getBarcode_type() {
        return barcode_type;
    }

    public void setBarcode_type(String barcode_type) {
        this.barcode_type = barcode_type;
    }

    public String getExpiry_period() {
        return expiry_period;
    }

    public void setExpiry_period(String expiry_period) {
        this.expiry_period = expiry_period;
    }

    public String getExpiry_period_type() {
        return expiry_period_type;
    }

    public void setExpiry_period_type(String expiry_period_type) {
        this.expiry_period_type = expiry_period_type;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getProduct_custom_field1() {
        return product_custom_field1;
    }

    public void setProduct_custom_field1(String product_custom_field1) {
        this.product_custom_field1 = product_custom_field1;
    }

    public String getProduct_custom_field2() {
        return product_custom_field2;
    }

    public void setProduct_custom_field2(String product_custom_field2) {
        this.product_custom_field2 = product_custom_field2;
    }

    public String getProduct_custom_field3() {
        return product_custom_field3;
    }

    public void setProduct_custom_field3(String product_custom_field3) {
        this.product_custom_field3 = product_custom_field3;
    }

    public String getProduct_custom_field4() {
        return product_custom_field4;
    }

    public void setProduct_custom_field4(String product_custom_field4) {
        this.product_custom_field4 = product_custom_field4;
    }

    public String getYoutub_url() {
        return youtub_url;
    }

    public void setYoutub_url(String youtub_url) {
        this.youtub_url = youtub_url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getProduct_description() {
        return product_description;
    }

    public void setProduct_description(String product_description) {
        this.product_description = product_description;
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

    public String getProducts_model() {
        return products_model;
    }

    public void setProducts_model(String products_model) {
        this.products_model = products_model;
    }

    public String getProducts_price() {
        return products_price;
    }

    public void setProducts_price(String products_price) {
        this.products_price = products_price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getDiscount_type() {
        return discount_type;
    }

    public void setDiscount_type(String discount_type) {
        this.discount_type = discount_type;
    }

    public String getDiscount_start_date() {
        return discount_start_date;
    }

    public void setDiscount_start_date(String discount_start_date) {
        this.discount_start_date = discount_start_date;
    }

    public String getDiscount_expires_date() {
        return discount_expires_date;
    }

    public void setDiscount_expires_date(String discount_expires_date) {
        this.discount_expires_date = discount_expires_date;
    }

    public String getApplicable_to() {
        return applicable_to;
    }

    public void setApplicable_to(String applicable_to) {
        this.applicable_to = applicable_to;
    }

    public String getProducts_weight() {
        return products_weight;
    }

    public void setProducts_weight(String products_weight) {
        this.products_weight = products_weight;
    }

    public String getProducts_weight_unit() {
        return products_weight_unit;
    }

    public void setProducts_weight_unit(String products_weight_unit) {
        this.products_weight_unit = products_weight_unit;
    }

    public String getProducts_slug() {
        return products_slug;
    }

    public void setProducts_slug(String products_slug) {
        this.products_slug = products_slug;
    }

    public String getActual_name() {
        return actual_name;
    }

    public void setActual_name(String actual_name) {
        this.actual_name = actual_name;
    }

    public String getShort_name() {
        return short_name;
    }

    public void setShort_name(String short_name) {
        this.short_name = short_name;
    }

    public String getBase_unit_multiplier() {
        return base_unit_multiplier;
    }

    public void setBase_unit_multiplier(String base_unit_multiplier) {
        this.base_unit_multiplier = base_unit_multiplier;
    }

    public String getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(String deleted_at) {
        this.deleted_at = deleted_at;
    }
}

