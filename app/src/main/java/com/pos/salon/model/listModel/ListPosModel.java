package com.pos.salon.model.listModel;


import java.io.Serializable;

public class ListPosModel implements Serializable {

    public String transactionId="0",order_Date="", invoice_no="",customer_name="",payemnt_method="",location="",final_total="",tax_amount="",discount_amount="",discount_type="",unit_price_inc_tax="";
    public String total_before_tax="",total_paid="",return_paid="",amount_return="",product_name="",unit_price="",returnSubtotal="",returnQuantity="";
    public int  return_exists=0,is_direct_sale=0,quantity,sell_line_id=0,isDefective=0;

    public String contactInvoice="",contactTtansactionDate="",contactName="",contactTotalAmount="",contactTotalPaid="",contactTotalRemain="",contactPaymentstatus="";

}
