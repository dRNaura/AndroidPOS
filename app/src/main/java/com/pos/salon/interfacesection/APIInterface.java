package com.pos.salon.interfacesection;

import com.pos.salon.model.PurchaseModel.PurchaseProductDataSend;
import com.pos.salon.model.repairModel.RepairDetailModel;
import com.pos.salon.model.customerData.FetchPartialCustomer;
import com.pos.salon.model.login.LoginParse;
import com.pos.salon.model.login.LoginSendData;
import com.pos.salon.model.logout.LogoutResponse;
import com.pos.salon.model.payment.PaymentDataSend;
import com.pos.salon.model.posLocation.PosLocationResponse;
import com.pos.salon.model.searchData.PurchaseItemResponse;
import com.pos.salon.model.searchData.SearchItemResponse;
import com.pos.salon.newkotlin.ActivateUserModel;
import com.pos.salon.newkotlin.AddSupplierModel;
import com.pos.salon.newkotlin.AddUserModel;
import com.pos.salon.newkotlin.CheckEmailModel;
import com.pos.salon.newkotlin.CheckMobileMode;
import com.pos.salon.newkotlin.ResendOtpModel;
import com.pos.salon.newkotlin.Response.AddResponseSupplier;
import com.pos.salon.newkotlin.Response.AddUserResposne;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface APIInterface {

    @POST
    Call<LoginParse> userLogin(@Url String url, @Body LoginSendData loginData);

    @GET
    Call<LoginParse> commonApi(@Url String url);

    @GET
    Call<LogoutResponse> userLogout(@Url String url);

    @GET
    Call<PosLocationResponse> getPosData(@Url String url);

    @GET
    Call<SearchItemResponse> getSearchItem(@Url String url);

    @GET
    Call<PurchaseItemResponse> getPurchaseSearchItem(@Url String url);

    @GET
    Call<ResponseBody> getRepairSearchItem(@Url String url,@Query("product_for") String product_for);

    @GET
    Call<ResponseBody> getCustomerList(@Url String url);

    @GET
    Call<FetchPartialCustomer> getpartialCustomer(@Url String url);

    @POST()
    Call<ResponseBody> sendPayment(@Url String url, @Body PaymentDataSend paymentData);

    @POST()
    Call<ResponseBody> sendPuchasePayment(@Url String url, @Body PurchaseProductDataSend paymentData);

    @PUT()
    Call<ResponseBody> updatePuchasePayment(@Url String url, @Body PurchaseProductDataSend paymentData);

    @POST()
    Call<ResponseBody> sendPartialPayment(@Url String url, @Body PaymentDataSend paymentData);

    @POST()
    Call<AddUserResposne> saveNewUser(@Url String url, @Body AddUserModel userModel);

    @POST()
    Call<AddResponseSupplier> saveNewSupplierUser(@Url String url, @Body AddSupplierModel userModel);

    @PUT()
    Call<AddUserResposne> updateUserData(@Url String url, @Body AddUserModel userModel);

    @PUT()
    Call<AddResponseSupplier> updateSupplierData(@Url String url, @Body AddSupplierModel userModel);

    @POST()
    Call<AddUserResposne> callResendOtp(@Url String url, @Body ResendOtpModel otpModel);

    @POST()
    Call<AddUserResposne> callActivateUser(@Url String url, @Body ActivateUserModel otpModel);

    @POST()
    Call<Boolean> callCheckEmail(@Url String url, @Body CheckEmailModel otpModel);

    @POST()
    Call<Boolean> callCheckMobile(@Url String url, @Body CheckMobileMode otpModel);

    @POST()
    Call<ResponseBody> storeCredit(@Url String url, @Body RequestBody json);

    @POST()
    Call<ResponseBody> rewardAmount(@Url String url, @Body RequestBody json);

    @POST()
    Call<ResponseBody> openRegister(@Url String url, @Body RequestBody json);

    @GET()
    Call<ResponseBody> registerDetail(@Url String url);

    @GET()
    Call<ResponseBody> checkRegisterOpen(@Url String url);

    @POST()
    Call<ResponseBody> sendEmailCopy(@Url String url, @Body RequestBody json);

    @GET
    Call<ResponseBody> getCustomersList(@Url String url);

    @GET
    Call<ResponseBody> getRepairDetail(@Url String url);

    @GET
    Call<ResponseBody> getsalesDetail(@Url String url);

    @GET
    Call<ResponseBody> getCustomerDetail(@Url String url);

    @GET
    Call<ResponseBody> getReturnSaleDetail(@Url String url, @Query("transaction_id") int transaction_id);

    @GET
    Call<ResponseBody> returnSale(@Url String url);

    @POST
    Call<ResponseBody> deletesalesDetail(@Url String url, @Query("transaction_id") String transaction_id);

    @POST
    Call<ResponseBody> deleteRetrunSaleDetail(@Url String url, @Query("transaction_id") int transaction_id);

    @GET
    Call<ResponseBody> editsalesDetail(@Url String url);

    @GET
    Call<ResponseBody> drafttsalesDetail(@Url String url,@Query("start_date") String start_date, @Query("end_date") String end_date,@Query("limit") int limit);

    @POST
    Call<ResponseBody> returnSaleUpdate(@Url String url, @Body RequestBody json);

    @POST
    Call<ResponseBody> addRepairs(@Url String url, @Body RequestBody json);

    @PUT
    Call<ResponseBody> updateRepairsSale(@Url String url, @Body RequestBody json);

    @PUT
    Call<ResponseBody> updateProductDetail(@Url String url, @Body RequestBody json);

    @PUT
    Call<ResponseBody> updateSalesDetail(@Url String url, @Body RequestBody json);

    @POST
    Call<ResponseBody> completePartialSaleRequest(@Url String url, @Body RequestBody json);

    @GET
    Call<ResponseBody> getSalesFilters(@Url String url);

    @GET
    Call<ResponseBody> returnSaleList(@Url String url,@Query("start_date") String start_date, @Query("end_date") String end_date,@Query("limit") int limit);

    @GET
    Call<ResponseBody> quatationSaleList(@Url String url,@Query("start_date") String start_date, @Query("end_date") String end_date,@Query("limit") int limit);


    @POST
    Call<SearchItemResponse> getProductDetailFromId(@Url String url, @Body RequestBody json);

    @POST
    Call<PurchaseItemResponse> getPurchaseProductDetailFromId(@Url String url, @Body RequestBody json);

    @GET
    Call<ResponseBody> repairList(@Url String url,@Query("limit") int limit);

    @GET
    Call<ResponseBody> repairSalesList(@Url String url, @Query("repair_id") int repair_id/*,@Query("limit") int limit*/);

    @GET
    Call<PosLocationResponse> repairDetail(@Url String url);

    @GET
    Call<RepairDetailModel> editrepairDetail(@Url String url);

    @GET
    Call<ResponseBody> deliveryOrder(@Url String url);

    @GET
    Call<ResponseBody> getList(@Url String url);

    @POST
    Call<ResponseBody> getNotificationDetail(@Url String url, @Query("id") int id);

    @POST
    Call<ResponseBody> deleteDetail(@Url String url);

    @DELETE
    Call<ResponseBody> deleteCategory(@Url String url);

    @PUT
    Call<ResponseBody> add(@Url String url, @Body RequestBody json);

    @PUT
    Call<ResponseBody> updateBooking(@Url String url, @Body RequestBody json);

    @POST
    Call<ResponseBody> detail(@Url String url, @Body RequestBody json);

    @GET
    Call<ResponseBody> subscribetoWonderpush(@Url String url,@Query("device_id") String device_id, @Query("installation_id") String installation_id);
}
