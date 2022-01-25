package com.pos.salon.adapter.PurchaseSectionAdapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pos.salon.R;
import com.pos.salon.model.PurchaseModel.PurchaseProductSearchModel;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import static android.content.Context.MODE_PRIVATE;


public class AddPurchaseProductAdapter extends RecyclerView.Adapter<AddPurchaseProductAdapter.MyViewHolder> {

    public Context context;
    //    RepairListAdapter.OnClicked onClicked;
    private ArrayList<PurchaseProductSearchModel> list;
    SharedPreferences sharedPreferences;
    ArrayList<String> arrdepartmentsList = new ArrayList<String>();
    private ArrayList<PurchaseProductSearchModel> tempItem = new ArrayList<>();
    private ArrayList<PurchaseProductSearchModel> arrOrignalProductDetails = new ArrayList<>();
    private PurchaseProductSearchModel objOrignalItem = null;
    private DecimalFormat df = new DecimalFormat("##.00");


    public interface ClickDeleteItem {

        public void onDeleteClick(int position, PurchaseProductSearchModel item);
        public void onQuantityClick(int value, PurchaseProductSearchModel item, int position);
        public void onDiscountCalculation(String discountAmt, double unitFinalPrice, int position);
        public void onUnitCostBeforeDisocunt(String ammnt, double unitFinalPrice, int position);

    }


    AddPurchaseProductAdapter.ClickDeleteItem clickItem;

    public AddPurchaseProductAdapter(Context context, ArrayList<PurchaseProductSearchModel> list, final ArrayList<PurchaseProductSearchModel> arrOrignalProductDetails, AddPurchaseProductAdapter.ClickDeleteItem clickItem) {

        this.context = context;
        this.list = list;
        this.clickItem = clickItem;
        this.arrOrignalProductDetails = arrOrignalProductDetails;


    }

    public void setNewItem(ArrayList<PurchaseProductSearchModel> tempItem) {
        this.tempItem = tempItem;

        notifyDataSetChanged();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtProductName, tv_mvariations, editQuantity, txt_lineTotal;
        EditText edtUnitDiscountAmt, et_discount_percent, et_UnitCostTax, et_NetCost, et_profit_margin, et_UnitSellingPrice;
        ImageView imgDelete;
        Button addButton, minusButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            tv_mvariations = itemView.findViewById(R.id.tv_mvariations);
            editQuantity = itemView.findViewById(R.id.editQuantity);
            txt_lineTotal = itemView.findViewById(R.id.txt_lineTotal);
//            txt_LineTotal=itemView.findViewById(R.id.txt_LineTotal);

            edtUnitDiscountAmt = itemView.findViewById(R.id.edtUnitDiscountAmt);
            et_discount_percent = itemView.findViewById(R.id.et_discount_percent);
            et_UnitCostTax = itemView.findViewById(R.id.et_UnitCostTax);
//            et_NetCost=itemView.findViewById(R.id.et_NetCost);
            et_profit_margin = itemView.findViewById(R.id.et_profit_margin);
            et_UnitSellingPrice = itemView.findViewById(R.id.et_UnitSellingPrice);
            imgDelete = itemView.findViewById(R.id.imgDelete);
            addButton = itemView.findViewById(R.id.addButton);
            minusButton = itemView.findViewById(R.id.minusButton);
        }

    }

    @NonNull
    @Override
    public AddPurchaseProductAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.add_purchase_product_items, parent, false);
        return new AddPurchaseProductAdapter.MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final AddPurchaseProductAdapter.MyViewHolder viewHolder, final int i) {


        viewHolder.et_UnitSellingPrice.setEnabled(false);
        viewHolder.et_UnitCostTax.setEnabled(false);
        sharedPreferences = context.getSharedPreferences("login", MODE_PRIVATE);
        //check for departments
        Gson gsonenables = new Gson();
        Type typeDept = new TypeToken<ArrayList<String>>() {
        }.getType();
        String strdepartmentrs = sharedPreferences.getString("departments", "");

        if (strdepartmentrs != null) {
            arrdepartmentsList = (ArrayList<String>) gsonenables.fromJson(strdepartmentrs, typeDept);
        }

        Log.e("list", list.toString());
        PurchaseProductSearchModel model = list.get(i);

        viewHolder.txtProductName.setText(model.style_no + " " + model.getName());
        viewHolder.editQuantity.setText("" + model.getQuantity());
        viewHolder.et_UnitSellingPrice.setText(""+df.format(Double.parseDouble(model.getUnitFinalPrice())));
        if(model.getProfit_percent().equalsIgnoreCase("")){
            model.setProfit_percent("0.0");
        }
        viewHolder.et_profit_margin.setText(""+df.format(Double.parseDouble(model.getProfit_percent())));
        if(model.getDefault_purchase_price().equalsIgnoreCase("")){
            model.setDefault_purchase_price("0.0");
        }
        viewHolder.edtUnitDiscountAmt.setText(""+df.format(Double.parseDouble(model.getDefault_purchase_price())));
//        model.setUnitCostBeforTax(model.getDefault_purchase_price());
        if(model.getUnitCostBeforTax().equalsIgnoreCase("")){
            model.setUnitCostBeforTax("0.0");
        }
        viewHolder.et_UnitCostTax.setText(""+df.format(Double.parseDouble(model.getUnitCostBeforTax())));
        double linrTotal =Double.parseDouble(model.getUnitCostBeforTax())  * model.getQuantity();
        model.setLineTotal(df.format(linrTotal));
        viewHolder.txt_lineTotal.setText(model.getLineTotal());

        viewHolder.et_discount_percent.setText(model.getDiscount_percent());
//        viewHolder.tv_mvariations.setText(list.get(i).qty_available);

        if (!model.getVariation_name().isEmpty()) {
            if (model.getVariation_name().equalsIgnoreCase("DUMMY")) {

                viewHolder.tv_mvariations.setText("( " + model.getSub_sku() + " )");
            } else {
                viewHolder.tv_mvariations.setText("( "+"Size : " + model.getVariation() + ", Color : " + model.getColor()+" )");
            }
        } else {

            viewHolder.tv_mvariations.setText("( " + model.getSub_sku() + " )");
        }

        viewHolder.imgDelete.setTag(model);
        viewHolder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if(ActivityPosItemList.comingFrom.equalsIgnoreCase("fromSaleDetail")){
//
//                }else{

                PurchaseProductSearchModel data = (PurchaseProductSearchModel) v.getTag();
                clickItem.onDeleteClick(i, data);

            }
        });

//        viewHolder.edtUnitDiscountAmt.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                String unitCostTax= s.toString();
//                model.setDefault_purchase_price(unitCostTax);
//                String discount_percentage= viewHolder.et_discount_percent.getText().toString();
//                double total= ((Double.parseDouble(discount_percentage) / 100 ) * Double.parseDouble(model.getDefault_purchase_price()));
//                model.setDiscount_percent(""+df.format(total));
//
//                double afterProfit =(Double.parseDouble(model.getDefault_purchase_price()) - total);
//                model.setUnitCostBeforTax(""+df.format(afterProfit));
//                viewHolder.et_UnitCostTax.setText(""+df.format(afterProfit));
//
//                double linetotal=Double.parseDouble(model.getUnitCostBeforTax()) * model.getQuantity();
//                model.setLineTotal(""+df.format(linetotal));
//                viewHolder.txt_lineTotal.setText(""+df.format(linetotal));
//                clickItem.onUnitCostBeforeDisocunt(unitCostTax,linetotal,i);
//
//            }
//        });

        viewHolder.edtUnitDiscountAmt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (event == null || !event.isShiftPressed()) {
                        // the user is done typing.

                        String unitCostTax= viewHolder.edtUnitDiscountAmt.getText().toString();
                        model.setDefault_purchase_price(unitCostTax);
                        String discount_percentage= viewHolder.et_discount_percent.getText().toString();
                        double total= ((Double.parseDouble(discount_percentage) / 100 ) * Double.parseDouble(model.getDefault_purchase_price()));
                        model.setDiscount_percent(""+df.format(total));

                        double afterProfit =(Double.parseDouble(model.getDefault_purchase_price()) - total);
                        model.setUnitCostBeforTax(""+df.format(afterProfit));
                        viewHolder.et_UnitCostTax.setText(""+df.format(afterProfit));

                        double linetotal=Double.parseDouble(model.getUnitCostBeforTax()) * model.getQuantity();
                        model.setLineTotal(""+df.format(linetotal));
                        viewHolder.txt_lineTotal.setText(""+df.format(linetotal));
                        clickItem.onUnitCostBeforeDisocunt(unitCostTax,linetotal,i);

                        return true; // consume.
                    }

                }
                return false; // pass on to other listeners.

            }
        });

//        viewHolder.et_UnitCostTax.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//                String unitCostTax=s.toString();
//                model.setUnitCostBeforTax(unitCostTax);
//
//                String discount_percentage= viewHolder.et_discount_percent.getText().toString();
//                double total= ((Double.parseDouble(discount_percentage) / 100 ) * Double.parseDouble(model.getDefault_purchase_price()));
//                model.setDiscount_percent(""+df.format(total));
//
//                double totalDi= ((Double.parseDouble(model.getDiscount_percent())) + Double.parseDouble(model.getDefault_purchase_price()));
//                double afterProfit =(Double.parseDouble(model.getDefault_purchase_price()) - totalDi);
//
//                model.setDefault_purchase_price(""+df.format(afterProfit));
//                viewHolder.edtUnitDiscountAmt.setText(""+df.format(afterProfit));
//
//                double linetotal=Double.parseDouble(model.getUnitCostBeforTax()) * model.getQuantity();
//                model.setLineTotal(""+df.format(linetotal));
//                viewHolder.txt_lineTotal.setText(""+df.format(linetotal));
//
//            }
//        });

        viewHolder.et_UnitCostTax.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (event == null || !event.isShiftPressed()) {
                        // the user is done typing.

//                        hideSoftKeyboard(context);
                        String unitCostTax= viewHolder.et_UnitCostTax.getText().toString();
                        model.setUnitCostBeforTax(unitCostTax);

                        String discount_percentage= viewHolder.et_discount_percent.getText().toString();
                        double total= ((Double.parseDouble(discount_percentage) / 100 ) * Double.parseDouble(model.getDefault_purchase_price()));
                        model.setDiscount_percent(""+df.format(total));

                        double totalDi= ((Double.parseDouble(model.getDiscount_percent())) + Double.parseDouble(model.getDefault_purchase_price()));
                        double afterProfit =(Double.parseDouble(model.getDefault_purchase_price()) - totalDi);

                        model.setDefault_purchase_price(""+df.format(afterProfit));
                        viewHolder.edtUnitDiscountAmt.setText(""+df.format(afterProfit));

                        double linetotal=Double.parseDouble(model.getUnitCostBeforTax()) * model.getQuantity();
                        model.setLineTotal(""+df.format(linetotal));
                        viewHolder.txt_lineTotal.setText(""+df.format(linetotal));
                        return true;

                    }

                }
                return false;

            }
        });

//        viewHolder.et_discount_percent.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                // the user is done typing.
//                String discount_percentage= s.toString();
//                double total= ((Double.parseDouble(discount_percentage) / 100 ) * Double.parseDouble(model.getDefault_purchase_price()));
//                model.setDiscount_percent(""+df.format(total));
//                double afterProfit =(Double.parseDouble(model.getDefault_purchase_price()) - total);
//                double totalLine=afterProfit * model.getQuantity();
//                model.setUnitCostBeforTax(""+df.format(afterProfit));
//                model.setLineTotal(""+df.format(totalLine));
//                viewHolder.txt_lineTotal.setText(""+df.format(totalLine));
//                viewHolder.et_UnitCostTax.setText(""+df.format(afterProfit));
//
//                clickItem.onDiscountCalculation(String.valueOf(total),totalLine,i);
//
//            }
//        });
        viewHolder.et_discount_percent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (event == null || !event.isShiftPressed()) {
                        // the user is done typing.

                        String discount_percentage= viewHolder.et_discount_percent.getText().toString();
                        double total= ((Double.parseDouble(discount_percentage) / 100 ) * Double.parseDouble(model.getDefault_purchase_price()));
                        model.setDiscount_percent(""+df.format(total));
                        double afterProfit =(Double.parseDouble(model.getDefault_purchase_price()) - total);
                        double totalLine=afterProfit * model.getQuantity();
                        model.setUnitCostBeforTax(""+df.format(afterProfit));
                        model.setLineTotal(""+df.format(totalLine));
                        viewHolder.txt_lineTotal.setText(""+df.format(totalLine));
                        viewHolder.et_UnitCostTax.setText(""+df.format(afterProfit));

                        clickItem.onDiscountCalculation(String.valueOf(total),totalLine,i);


                        return true;

                    }

                }
                return false;

            }
        });

//        viewHolder.et_profit_margin.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//                    String profitMargin= s.toString();
//                    model.setProfit_percent(""+df.format(Double.parseDouble(profitMargin)));
//                    double marginTotalSellingPrice =(Double.parseDouble(model.getUnitCostBeforTax()) + Double.parseDouble(model.getUnitCostBeforTax()) * (Double.parseDouble(profitMargin) /100 ));
//                    model.setUnitFinalPrice(""+df.format(marginTotalSellingPrice));
//                    viewHolder.et_UnitSellingPrice.setText(String.valueOf(df.format(model.getUnitFinalPrice())));
//
//
//            }
//        });
        viewHolder.et_profit_margin.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (event == null || !event.isShiftPressed()) {
                        // the user is done typing.

                        String profitMargin= viewHolder.et_profit_margin.getText().toString();
                        model.setProfit_percent(""+df.format(Double.parseDouble(profitMargin)));
                        double marginTotalSellingPrice =(Double.parseDouble(model.getUnitCostBeforTax()) + Double.parseDouble(model.getUnitCostBeforTax()) * (Double.parseDouble(profitMargin) /100 ));
                        model.setUnitFinalPrice(""+df.format(marginTotalSellingPrice));
                        viewHolder.et_UnitSellingPrice.setText(model.getUnitFinalPrice());

                        return true;

                    }

                }
                return false;

            }
        });
//        viewHolder.edtUnitDiscountAmt.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//
//                String unitCostTax=editable.toString();
//                model.setDefault_purchase_price(unitCostTax);
//
//                viewHolder.et_UnitCostTax.setText(unitCostTax);
//                double total=Double.parseDouble(model.getDefault_purchase_price()) * model.getQuantity();
//                viewHolder.txt_lineTotal.setText(String.valueOf(total));
//
//            }
//        });

//        viewHolder.et_UnitCostTax.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//                String unitCostTax=editable.toString();
//                model.setDefault_purchase_price(unitCostTax);
//
//                viewHolder.edtUnitDiscountAmt.setText(unitCostTax);
//                double total=Double.parseDouble(model.getDefault_purchase_price()) * model.getQuantity();
//                viewHolder.txt_lineTotal.setText(String.valueOf(total));
//
//            }
//        });

        viewHolder.addButton.setTag(model);
        viewHolder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final PurchaseProductSearchModel data = (PurchaseProductSearchModel) view.getTag();

                for (int i = 0; i < arrOrignalProductDetails.size(); i++) {
                    if (arrOrignalProductDetails.get(i).getName().equalsIgnoreCase(data.getName())) {
                        objOrignalItem = arrOrignalProductDetails.get(i);
                        break;
                    }
                }

                try {
                    int adapterPosition = viewHolder.getAdapterPosition();

                    list.get(adapterPosition).setQuantity(list.get(adapterPosition).getQuantity() + 1);
                    PurchaseProductSearchModel getItem = list.get(adapterPosition);

                    viewHolder.editQuantity.setText(getItem.getQuantity() + "");
                    double lineTotal=(Double.parseDouble(model.getUnitCostBeforTax()) * getItem.getQuantity());
                    model.setLineTotal(""+df.format(lineTotal));
                    viewHolder.txt_lineTotal.setText(""+df.format(lineTotal));
                    clickItem.onQuantityClick(getItem.getQuantity(),model,i);

                } catch (Exception e) {
                    Log.e("On add button cart : ", e.toString());
                }
            }

        });
        viewHolder.minusButton.setTag(model);
        viewHolder.minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PurchaseProductSearchModel data = (PurchaseProductSearchModel) v.getTag();
                int getCurrentCount = data.getQuantity();
                if (getCurrentCount > 1) {
                    int getPosition = viewHolder.getAdapterPosition();
                    list.get(getPosition).setQuantity(getCurrentCount - 1);
                    PurchaseProductSearchModel getItem = list.get(getPosition);

                    viewHolder.editQuantity.setText(getItem.getQuantity() + "");
                    double lineTotal=(Double.parseDouble(model.getUnitCostBeforTax()) * getItem.getQuantity());
                    model.setLineTotal(""+df.format(lineTotal));
                    viewHolder.txt_lineTotal.setText(df.format(lineTotal));
                    clickItem.onQuantityClick(getItem.getQuantity(),model,i);

                } else {
//                    Toast.makeText(context, "Enter valid item", Toast.LENGTH_LONG).show();
                }

            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}