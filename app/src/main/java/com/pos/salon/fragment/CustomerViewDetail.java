package com.pos.salon.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pos.salon.R;
import com.pos.salon.adapter.CustomerAdapters.ContactSalesAdapter;
import com.pos.salon.client.APIClient;
import com.pos.salon.interfacesection.APIInterface;
import com.pos.salon.model.listModel.ListPosModel;
import com.pos.salon.utilConstant.AppConstant;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.pos.salon.activity.HomeActivity.tool_title;


public class CustomerViewDetail extends Fragment {
    RecyclerView rv_contact_sales;
    LinearLayoutManager mLayoutManager;
    ArrayList<ListPosModel> contactSalesList=new ArrayList<>();
    ContactSalesAdapter contactSalesAdapter;
    int contact_id=0;
    TextView txt_Nodata;
    TextView txt_contactName,txt_contactMobile,txt_contactEmail,txt_contactAddress,txt_contactTotalSale,txt_contactTotaleSalePayment,txt_contactTotalSaleDue,txt_contactTotalReward,txt_contactTotalStoreCredit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customer_view_detail, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tool_title.setText("VIEW CONTACT");
        rv_contact_sales = view.findViewById(R.id.rv_contact_sales);
        txt_contactName = view.findViewById(R.id.txt_contactName);
        txt_contactMobile = view.findViewById(R.id.txt_contactMobile);
        txt_contactEmail = view.findViewById(R.id.txt_contactEmail);
        txt_contactAddress = view.findViewById(R.id.txt_contactAddress);
        txt_contactTotalSale = view.findViewById(R.id.txt_contactTotalSale);
        txt_contactTotaleSalePayment = view.findViewById(R.id.txt_contactTotaleSalePayment);
        txt_contactTotalSaleDue = view.findViewById(R.id.txt_contactTotalSaleDue);
        txt_contactTotalReward = view.findViewById(R.id.txt_contactTotalReward);
        txt_contactTotalStoreCredit = view.findViewById(R.id.txt_contactTotalStoreCredit);
        txt_Nodata = view.findViewById(R.id.txt_Nodata);

        mLayoutManager = new LinearLayoutManager(getContext());
        rv_contact_sales.setLayoutManager(mLayoutManager);
        contactSalesAdapter = new ContactSalesAdapter(getContext(), contactSalesList);
        rv_contact_sales.setAdapter(contactSalesAdapter);

        Bundle bundle = getArguments();
        contact_id = bundle.getInt("contact_id"); // your selected customer_id id.

        customerDetail();
        salesRelatedToContact();
    }

    public void customerDetail() {

        AppConstant.showProgress(getContext(), false);
        Retrofit retrofit = APIClient.getClientToken(getContext());

        Log.e("ContactId", String.valueOf(contact_id));

        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.getCustomerDetail("contacts/" + contact_id);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    try {
                        if (response.body() != null) {
                            String respo = response.body().string();
                            JSONObject responseObject = new JSONObject(respo);
                            Log.e("Customer Detail", responseObject.toString());
                            String successstatus = responseObject.optString("success");

                            if (successstatus.equalsIgnoreCase("true")) {

                                if (responseObject.has("contact") && !responseObject.isNull("contact")) {

                                    JSONObject contacts = responseObject.getJSONObject("contact");

                                    txt_contactName.setText(contacts.getString("name"));

                                    if (contacts.has("mobile") && !contacts.isNull("mobile")) {
                                        txt_contactMobile.setText(contacts.getString("mobile"));
                                    }
                                    if (contacts.has("email") && !contacts.isNull("email")) {
                                        txt_contactEmail.setText(contacts.getString("email"));
                                    }

                                    String strAddress = "";

                                    if (contacts.has("landmark") && !contacts.isNull("landmark")) {
                                        strAddress = strAddress + contacts.getString("landmark") + ", ";
                                    }
                                    if (contacts.has("city") && !contacts.isNull("city")) {
                                        strAddress = strAddress + contacts.getString("city") + ", ";
                                    }
                                    if (contacts.has("state") && !contacts.isNull("state")) {
                                        strAddress = strAddress + contacts.getString("state") + ", ";
                                    }
                                    if (contacts.has("country") && !contacts.isNull("country")) {
                                        strAddress = strAddress + contacts.getString("country");
                                    }

                                    txt_contactAddress.setText(strAddress);

                                    Double totalInvoice =0.0;
                                    Double invoiceRecived = 0.0;

                                    if (contacts.has("total_invoice") && !contacts.isNull("total_invoice")) {
                                        txt_contactTotalSale.setText(String.format("%.2f",Double.valueOf(contacts.getString("total_invoice"))));
                                        totalInvoice=Double.valueOf(contacts.getString("total_invoice"));
                                    }
                                    if (contacts.has("invoice_received") && !contacts.isNull("invoice_received")) {
                                        txt_contactTotaleSalePayment.setText(String.format("%.2f",Double.valueOf(contacts.getString("invoice_received"))));
                                        invoiceRecived=Double.valueOf(contacts.getString("invoice_received"));
                                    }

                                    double totalDue = totalInvoice - invoiceRecived;
                                    txt_contactTotalSaleDue.setText(String.format("%.2f",totalDue));
                                }
                                if(responseObject.has("total_reward") && !responseObject.isNull("total_reward")){
                                    String total_reward = responseObject.optString("total_reward");
                                    if(!total_reward.equalsIgnoreCase("0")){
                                        txt_contactTotalReward.setText(String.format("%.2f",Double.valueOf(total_reward)));
                                    }

                                }
                                if(responseObject.has("total_sc") && !responseObject.isNull("total_sc")) {
                                    String store_credit = responseObject.optString("total_sc");
                                    if(store_credit.equalsIgnoreCase("0")) {
                                        txt_contactTotalStoreCredit.setText("0.0");
                                    }else{
                                        txt_contactTotalStoreCredit.setText(String.format("%.2f",Double.valueOf(store_credit)));

                                    }
                                }

                            }

                        } else
                            {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(getContext(),"contacts/ API","(CustomerViewDetail Screen)","Web API Error : API Response Is Null");
                            Toast.makeText(getContext(), "Could Not Load Customer Detail. Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(getContext(), "Could Not Load Customer Detail. Please Try Again", Toast.LENGTH_LONG).show();
                }

            });

        }
    }


    public  void salesRelatedToContact() {
        Retrofit retrofit = APIClient.getClientToken(getContext());

        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.getCustomerDetail("sales?contact_id="+contact_id);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    try {
                        if (response.body() != null) {

                            AppConstant.hideProgress();
                            contactSalesList.clear();
                            String respo = response.body().string();

                            Log.e("Customer Sales",respo.toString());
                            JSONObject responseObject = new JSONObject(respo);
                            String successstatus = responseObject.optString("success");

                            if (successstatus.equalsIgnoreCase("true")) {


                                JSONArray dataObj = responseObject.getJSONArray("sells");
                                for (int i = 0; i < dataObj.length(); i++) {

                                    JSONObject data = dataObj.getJSONObject(i);
                                    ListPosModel listPosModel = new ListPosModel();

                                    listPosModel.contactInvoice=data.getString("invoice_no");
                                    listPosModel.contactName=data.getString("name");
                                    listPosModel.contactTtansactionDate=data.getString("transaction_date");
                                    listPosModel.contactTotalAmount=data.getString("final_total");

                                    if(data.has("total_paid") && !data.isNull("total_paid")){
                                        listPosModel.contactTotalPaid=data.getString("total_paid");
                                    }else{
                                        listPosModel.contactTotalPaid="0.0";
                                    }
                                    listPosModel.contactPaymentstatus=data.getString("payment_status");
                                    contactSalesList.add(listPosModel);

                                }

                                if(contactSalesList.isEmpty()){
                                    txt_Nodata.setVisibility(View.VISIBLE);
                                }else{
                                    txt_Nodata.setVisibility(View.GONE );
                                }
                                contactSalesAdapter.notifyDataSetChanged();
                            }

                        } else
                        {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(getContext(),"sales?contact_id API","(CustmerViewDetail Screen)","Web API Error : API Response Is Null");
                            Toast.makeText(getContext(), "Could Not Load Customer Detail. Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(getContext(), "Could Not Load Customer Detail. Please Try Again", Toast.LENGTH_LONG).show();
                }

            });

        }
    }

}
