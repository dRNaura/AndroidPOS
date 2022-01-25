package com.pos.salon.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pos.salon.R;
import com.pos.salon.activity.AddCustomerActivity;
import com.pos.salon.adapter.CustomerAdapters.CustomerListAdapter;
import com.pos.salon.client.APIClient;
import com.pos.salon.customview.DelayAutoCompleteTextView;
import com.pos.salon.interfacesection.APIInterface;
import com.pos.salon.model.LoginPermissionsData;
import com.pos.salon.model.listModel.CustmerListModel;
import com.pos.salon.newkotlin.ActivateUserModel;
import com.pos.salon.newkotlin.ResendOtpModel;
import com.pos.salon.newkotlin.Response.AddUserResposne;
import com.pos.salon.utilConstant.AppConstant;
import org.json.JSONArray;
import org.json.JSONObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import static com.pos.salon.activity.HomeActivity.tool_title;

public class CustomerListFragment extends Fragment {
    private LinearLayoutManager mLayoutManager;
    private CustomerListAdapter customerListAdapter;
    private final ArrayList<CustmerListModel> customerList = new ArrayList<>();
    private String quaryText = "";
    private SharedPreferences sharedPreferences;
    private int positio;
    private int currentItems, totalItems, scrollItems;
    private ProgressBar progressBar;
    private boolean isScrolling = true;
    private TextView txt_no_resut;
    public boolean isCustomerUpdate=false;
    public boolean isCustomerView=false;
    public boolean isCustomerDelete=false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customer_list_fragmnet, container, false);


    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tool_title.setText("CUSTOMER LIST");

        RecyclerView rv_listCustomer = view.findViewById(R.id.listCustomerRecycler);
        EditText edt_searchCustomer = view.findViewById(R.id.edt_searchCustomer);
        DelayAutoCompleteTextView ac_searchcustomer = view.findViewById(R.id.ac_searchcustomer);
        progressBar = view.findViewById(R.id.progressBar);
        txt_no_resut = view.findViewById(R.id.txt_no_resut);

        sharedPreferences = getContext().getSharedPreferences("login", Context.MODE_PRIVATE);


        mLayoutManager = new LinearLayoutManager(getContext());
        rv_listCustomer.setLayoutManager(mLayoutManager);
        customerListAdapter = new CustomerListAdapter(getContext(), customerList);
        rv_listCustomer.setAdapter(customerListAdapter);

        ArrayList<LoginPermissionsData> permissionsDataList = new ArrayList<LoginPermissionsData>();
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("login", Context.MODE_PRIVATE);
        String strPermission = sharedPreferences.getString("permissionDataList", "");
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<LoginPermissionsData>>() {
        }.getType();
        if (strPermission != null) {
            permissionsDataList = (ArrayList<LoginPermissionsData>) gson.fromJson(strPermission, type);
        }

        if (permissionsDataList.isEmpty()) {
            isCustomerUpdate = true;
            isCustomerView = true;
            isCustomerDelete = true;

        } else {

            for (int i = 0; i < permissionsDataList.size(); i++) {
                if (permissionsDataList.get(i).getPermission_name().equalsIgnoreCase("customer.update")) {
                    isCustomerUpdate = true;
                }
                if (permissionsDataList.get(i).getPermission_name().equalsIgnoreCase("customer.view")) {
                    isCustomerView = true;
                }
                if (permissionsDataList.get(i).getPermission_name().equalsIgnoreCase("customer.delete")) {
                    isCustomerDelete = true;
                }

            }
        }


        rv_listCustomer.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) //check for scroll down
                {
                    currentItems = mLayoutManager.getChildCount();
                    totalItems = mLayoutManager.getItemCount();
                    scrollItems = mLayoutManager.findFirstVisibleItemPosition();

                    if (isScrolling) {
                        if ((currentItems + scrollItems) >= totalItems) {
                            isScrolling = false;
                            customerList(quaryText, false, true);
                        }
                    }
                }
            }
        });


        edt_searchCustomer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {


                if (s.length() > 0) {
                    quaryText = s.toString();

                    customerList(quaryText, false, false);

                } else {

                    customerList("", true, false);
                }

            }
        });

        customerListAdapter.setOnItmeClicked(new CustomerListAdapter.OnClicked() {
            @Override
            public void setOnClickedItem(int position, View v) {
                positio = position;
                customerViewPopUp(v,position);


            }
        });

        customerList("", true, false);
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//
//
//    }o

    private void customerViewPopUp(View v, int position) {
        PopupMenu popup = new PopupMenu(getContext(), v);
        try {
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        popup.getMenuInflater().inflate(R.menu.customer_view, popup.getMenu());
        final CustmerListModel model=customerList.get(position);
        if (model.isActive == 0) {
            popup.getMenu().findItem(R.id.send_otp).setVisible(true);
        } else {
            popup.getMenu().findItem(R.id.send_otp).setVisible(false);
        }
        if (isCustomerDelete) {
            popup.getMenu().findItem(R.id.delete_customer).setVisible(true);
        } else {
            popup.getMenu().findItem(R.id.delete_customer).setVisible(false);
        }
        if (isCustomerView) {
            popup.getMenu().findItem(R.id.customer_view).setVisible(true);
        } else {
            popup.getMenu().findItem(R.id.customer_view).setVisible(false);
        }
        if (isCustomerUpdate) {
            popup.getMenu().findItem(R.id.customer_edit).setVisible(true);
        } else {
            popup.getMenu().findItem(R.id.customer_edit).setVisible(false);
        }
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.customer_edit:

                            CustmerListModel listModel = customerList.get(positio);

                            int contactId = listModel.id;
                            Intent i = new Intent(getContext(), AddCustomerActivity.class);
                            i.putExtra("country_list", sharedPreferences.getString("country_list", ""));
                            i.putExtra("isComing", "toUpdate");
                            i.putExtra("contactId", contactId);
                            startActivity(i);
                        getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);


                        break;

                    case R.id.customer_view:

                            CustmerListModel cusListModel = customerList.get(positio);

                            CustomerViewDetail ldf = new CustomerViewDetail();
                            Bundle args = new Bundle();
                            args.putInt("contact_id", cusListModel.id);
                            ldf.setArguments(args);

                            FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter,R.anim.exit,R.anim.left_to_right,R.anim.right_to_left);
                            transaction.add(R.id.lay_customer_layout, ldf);
//                            activeCenterFragments.add(ldf);
                            transaction.addToBackStack(null);
                            transaction.commit();

                        break;

                    case R.id.send_otp:

                        callResendOtp(model.contact_id);
                        break;
                    case R.id.delete_customer:
                        delete_customer();
                        break;




                }
                return true;
            }
        });
        popup.show();

    }

    public void delete_customer() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
        builder1.setMessage("Are you sure you want to Delete this?");
        builder1.setTitle("Delete Sale Detail");
        builder1.setCancelable(true);

        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                CustmerListModel cusListModel = customerList.get(positio);

                deletedetail(cusListModel.id);

            }
        });

        builder1.setNegativeButton(
                "No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();


    }

    public void deletedetail(int contact_id) {
        AppConstant.showProgress(getContext(), false);
        Retrofit retrofit = APIClient.getClientToken(getContext());
        Log.e("contact_id", String.valueOf(contact_id));

        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.deleteDetail("contacts/deleteContact/"+contact_id);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    AppConstant.hideProgress();
                    try {
                        if (response.body() != null) {
                            AppConstant.hideProgress();
                            String respo = response.body().string();
                            Log.e("delete contact ", response.toString());
                            JSONObject responseObject = new JSONObject(respo);


                            String successstatus = responseObject.optString("success");
                            String msg = responseObject.getString("msg");
                            //Toast.makeText( this, ""+successstatus, Toast.LENGTH_LONG).show();
                            if (successstatus.equalsIgnoreCase("true")) {

                                customerList("", true, false);
                                AppConstant.showToast(getContext(), "" + msg);

                            }
                            else {
                                AppConstant.showToast(getContext(), "" + msg);
                            }

                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(getContext(), "contacts/deleteContact", "(CustomerListFragment Screen)", "Web API Error : API Response Is Null");
                            Toast.makeText(getContext(), "Could Not Delete Customer. Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {

                        AppConstant.hideProgress();
                        Log.e("Exception", e.toString());
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(getContext(), "Could Not Delete Customer. Please Try Again", Toast.LENGTH_LONG).show();
                }

            });
        }

    }



    public void customerList(final String quaryText, boolean isFirstTime, final boolean isFromScroll) {
        if (isFirstTime) {
            customerList.clear();
            AppConstant.showProgress(getContext(), false);
        } else {
            progressBar.setVisibility(View.VISIBLE);

        }

        Retrofit retrofit = APIClient.getClientToken(getContext());

        // when you type fast then calling api 2 or 3 times for this we need to clear list.
        if (isFromScroll) {

        } else {
            customerList.clear();
        }

        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.getCustomersList("contacts?term=" + quaryText + "&limit=" + customerList.size()+"&type="+"customer ");
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    // when you type fast then calling api 2 or 3 times for this we need to clear list.
                    if (isFromScroll) {

                    } else {
                        customerList.clear();
                    }

                    AppConstant.hideProgress();
                    progressBar.setVisibility(View.GONE);
                    try {
                        if (response.body() != null) {
                            String respo = response.body().string();
                            JSONObject responseObject = new JSONObject(respo);
                            Log.e("Customer List", responseObject.toString());
                            String successstatus = responseObject.optString("success");
                            if (successstatus.equalsIgnoreCase("true")) {


                                JSONArray dataObj = responseObject.getJSONArray("contacts");
                                for (int i = 0; i < dataObj.length(); i++) {

                                    JSONObject data = dataObj.getJSONObject(i);
                                    CustmerListModel listPosModel = new CustmerListModel();

                                    listPosModel.setContact_id(data.getString("contact_id"));
                                    listPosModel.setName(data.getString("name"));
                                    listPosModel.setCustomer_group(data.getString("customer_group"));
                                    listPosModel.setCreated_at(data.getString("created_at"));
                                    listPosModel.setCity(data.getString("city"));
                                    listPosModel.setState(data.getString("state"));
                                    listPosModel.setCountry(data.getString("country"));
                                    listPosModel.setLandmark(data.getString("landmark"));
                                    if(data.has("mobile") && !data.isNull("mobile")){
                                        listPosModel.setMobile(data.getString("mobile"));
                                    }

                                    listPosModel.setId(data.getInt("id"));
                                    listPosModel.setIs_default(data.getInt("is_default"));
                                    listPosModel.setIsActive(data.getInt("isActive"));

                                    if (data.has("total_invoice") && !data.isNull("total_invoice")) {
                                        listPosModel.setTotal_invoice(data.getString("total_invoice"));
                                    } else {
                                        listPosModel.setTotal_invoice("0.00");
                                    }
                                    if (data.has("invoice_received") && !data.isNull("invoice_received")) {
                                        listPosModel.setInvoice_received(data.getString("invoice_received"));
                                    } else {
                                        listPosModel.setInvoice_received("0.00");
                                    }

                                    listPosModel.setTotal_sell_return(data.getString("total_sell_return"));
                                    listPosModel.setSell_return_paid(data.getString("sell_return_paid"));
                                    listPosModel.setOpening_balance(data.getString("opening_balance"));
                                    listPosModel.setOpening_balance_paid(data.getString("opening_balance_paid"));
                                    customerList.add(listPosModel);

                                }

                                if(customerList.size() ==0){
                                    txt_no_resut.setVisibility(View.VISIBLE);
                                }else {
                                    txt_no_resut.setVisibility(View.GONE);
                                }

                                customerListAdapter.notifyDataSetChanged();

                            }

                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(getContext(), "contacts?term API", "(CustomerListFragment Screen)", "Web API Error : API Response Is Null");
                            Toast.makeText(getContext(), "Could Not Load Customer List. Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        AppConstant.hideProgress();
                        Log.e("Excetion", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(getContext(), "Could Not Load Customer List. Please Try Again", Toast.LENGTH_LONG).show();
                }


            });

        }

    }

    // Call Resend Code if user not Received any code
    void callResendOtp(final String contact_id) {
        ResendOtpModel otpModel = new ResendOtpModel();
        otpModel.setContact_id(contact_id);
        Log.e("Contact Id", contact_id);

        AppConstant.showProgress(getContext(), false);
        Retrofit retrofit = APIClient.getClientToken(getContext());

        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Gson gson = new Gson();
            String json = gson.toJson(otpModel);
            Log.e("json",json.toString());
            Call<AddUserResposne> call = apiService.callResendOtp("resendCode", otpModel);
            call.enqueue(new Callback<AddUserResposne>() {
                @Override
                public void onResponse(@NonNull Call<AddUserResposne> call, @NonNull Response<AddUserResposne> response) {
                    //Toast.makeText(ActivityPosTerminalDropdown.this, ""+response.body(), Toast.LENGTH_LONG).show();
                    AppConstant.hideProgress();
                    if (response.body() != null) {

                        AddUserResposne userResponse = response.body();
                        Gson gson = new Gson();
                        String json = gson.toJson(userResponse);
                        Log.e("json",json.toString());
                        if(userResponse.getSuccess()){
                            openOtpDialog(contact_id);
                        }
                        Log.e("OTP Resend Response", userResponse.toString());
                        Log.e("Resend OTP Model", "" + userResponse.getData().getContact_id());
                        AppConstant.showToast(getContext(), userResponse.getMsg());
                        Log.e("Resend response", "" + userResponse.getMsg());

                        // Log.e("msg", "onResponse: "+posResponse.getService_staff() );
                    } else {
                        AppConstant.sendEmailNotification(getContext(), "resendCode API", "(AddCustomerActivity Screen)", "Web API Error : API Response Is Null");
                        Toast.makeText(getContext(), "Unable To Resend Code Again. Please Try Again", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<AddUserResposne> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(getContext(), "Unable To Resend Code Again. Please Try Again", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    void openOtpDialog(final String contact_id) {



        final Dialog dialog = new Dialog(getContext());

        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_otp_verification);

        Button activateWithoutOtpBtn = dialog.findViewById(R.id.btnActivateWithoutOtp);
        Button activateBtn = dialog.findViewById(R.id.btnActivate);
        TextView resendBtn = dialog.findViewById(R.id.btnResendOtp);
        TextView msgText = dialog.findViewById(R.id.msg);
        ImageView img_cancel_dialog = dialog.findViewById(R.id.img_cancel_dialog);

        msgText.setText("Please Enter Verification Code");

        final EditText editText = dialog.findViewById(R.id.etPin);

        dialog.show();

        activateWithoutOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userActiveWithoutOtp("", 0, dialog,contact_id);
                dialog.dismiss();
//                finish();
            }
        });
        resendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                dialog.dismiss();
                callResendOtp(contact_id);
                editText.setText("");
            }
        });


        activateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String getOtp = editText.getText().toString().trim();
                if (getOtp.isEmpty()) {
                    Toast.makeText(getContext(), "Please Enter OTP ", Toast.LENGTH_LONG).show();
                } else {
                    userActiveWithoutOtp(getOtp, 1, dialog,contact_id);
                }

            }
        });

        img_cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }


    // Call Resend Code if user not Received any code
    void userActiveWithoutOtp(final String otp, final int isActive, final Dialog objDialog,String contact_id) {

        final ActivateUserModel otpModel = new ActivateUserModel();
        otpModel.setContact_id(contact_id);
        otpModel.set_active(isActive);
        otpModel.setOtp(otp);

        AppConstant.showProgress(getContext(), false);

        AppConstant.hideKeyboardFrom(getContext());

        Retrofit retrofit = APIClient.getClientToken(getContext());

        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);

            Call<AddUserResposne> call = apiService.callActivateUser("activateContact", otpModel);
            call.enqueue(new Callback<AddUserResposne>() {
                @Override
                public void onResponse(@NonNull Call<AddUserResposne> call, @NonNull Response<AddUserResposne> response) {
                    //Toast.makeText(ActivityPosTerminalDropdown.this, ""+response.body(), Toast.LENGTH_LONG).show();
                    AppConstant.hideProgress();

                    if (response.body() != null) {

                        Log.e("otp response", response.toString());

                        AddUserResposne userResponse = response.body();

                        if (isActive == 1) {
                            if (otpModel.getOtp().equalsIgnoreCase(otp)) {
                                Toast.makeText(getContext(), "User Added  Successfully", Toast.LENGTH_LONG).show();

                                objDialog.dismiss();

                                customerList(quaryText, false, false);
//                                finish();
                            } else {
                                Toast.makeText(getContext(), "Enter a valid OTP", Toast.LENGTH_LONG).show();
                            }


                        } else if (isActive == 0) {
                            customerList(quaryText, false, false);
                            Toast.makeText(getContext(), "User Added  Successfully", Toast.LENGTH_LONG).show();
                            objDialog.dismiss();

//                            finish();
                        }
                        Log.e("Resend Otp Model ", "" + userResponse.getData().getContact_id());
                        // Log.e("msg", "onResponse: "+posResponse.getService_staff() );

                    } else {

                        AppConstant.sendEmailNotification(getContext(),"activateContact API","(AddCustomerActivity Screen)","Web API Error : API Response Is Null");
                        Toast.makeText(getContext(), "Some Error To Add User. Please Try Again", Toast.LENGTH_LONG).show();

                    }
                }

                @Override
                public void onFailure(Call<AddUserResposne> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(getContext(), "Some Error To Add User. Please Try Again", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

}