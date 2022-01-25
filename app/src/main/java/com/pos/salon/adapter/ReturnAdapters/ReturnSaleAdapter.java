package com.pos.salon.adapter.ReturnAdapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.pos.salon.R;
import com.pos.salon.fragment.ReturnFragments.ReturnSaleFragment;
import com.pos.salon.model.listModel.ListPosModel;

import java.util.ArrayList;

public class ReturnSaleAdapter extends RecyclerView.Adapter<ReturnSaleAdapter.MyViewHolder> {

public Context context;

        OnClicked onClicked;
    OnDefectiveClicked onDefectiveClicked;
//    ArrayList<ListPosModel> productList = new ArrayList<>();
    public int isDefective=0;
    ArrayList arrDefectiveList=new ArrayList<>();

public ReturnSaleAdapter(Context context) {
        this.context = context;
//        this.productList=productList;

        }

public class MyViewHolder extends RecyclerView.ViewHolder {

    TextView product_name,txt_unit_price,txt_sale_quantity,txt_subtotal,txt_returnQuantity;
     EditText et_return_quantity;
     CheckBox checkbox_is_defective;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        product_name=itemView.findViewById(R.id.txt_product_name);
        txt_unit_price=itemView.findViewById(R.id.txt_return_unit_price);
        txt_sale_quantity=itemView.findViewById(R.id.txt_return_sale_quantity);
        txt_subtotal=itemView.findViewById(R.id.txt_return_subtotal);
        et_return_quantity=itemView.findViewById(R.id.et_return_quantity);
        txt_returnQuantity=itemView.findViewById(R.id.txt_returnQuantity);
        checkbox_is_defective=itemView.findViewById(R.id.checkbox_is_defective);

    }
}
    @NonNull
    @Override
    public ReturnSaleAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.return_sale_items, parent, false);
        return new ReturnSaleAdapter.MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final ReturnSaleAdapter.MyViewHolder viewHolder, final int i) {

         final ListPosModel model = ReturnSaleFragment.productList.get(i);

        viewHolder.product_name.setText(model.product_name);
        viewHolder.txt_unit_price.setText(model.unit_price_inc_tax);
        viewHolder.txt_sale_quantity.setText(String.valueOf(model.quantity));

        viewHolder.checkbox_is_defective.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(viewHolder.checkbox_is_defective.isChecked()){
                    model.isDefective=1;
                    ReturnSaleFragment.productList.set(i,model);
                }else{
                    model.isDefective=0;
                    ReturnSaleFragment.productList.set(i,model);
                }
            }
        });

        viewHolder.et_return_quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {
               String returnQuantity = s.toString();
                if(returnQuantity.isEmpty())
                {
                    returnQuantity="0";

                    viewHolder.txt_subtotal.setText("0");
//                   viewHolder.txt_returnQuantity.setText("");
                    model.returnQuantity = returnQuantity;
                    model.returnSubtotal = "0";

                    ReturnSaleFragment.productList.set(i,model);
                    if (onClicked != null){
                        onClicked.setOnClickedItem(i);

                    }
                    return;
                }


                int quantity=Integer.parseInt(String.valueOf(model.quantity));
                int returnQunt=Integer.parseInt(returnQuantity);
                double unitPrice = Double.parseDouble(model.unit_price_inc_tax);

                if(returnQunt<=quantity) {

                    double returnTotal = unitPrice * returnQunt;

                    viewHolder.txt_subtotal.setText(String.valueOf(returnTotal));
//                    viewHolder.txt_returnQuantity.setText(String.valueOf(returnTotal));

                    model.returnQuantity = String.valueOf(returnQunt);
                    model.returnSubtotal = String.valueOf(returnTotal);

                }
                else{
                    viewHolder.et_return_quantity.setText("");
                    viewHolder.txt_subtotal.setText("0");
//                    viewHolder.txt_returnQuantity.setText("");
                    viewHolder.et_return_quantity.setError("Invalid return Quantity");
                    model.returnQuantity = "0";
                    model.returnSubtotal = "0";

                }

                ReturnSaleFragment.productList.set(i,model);
                if (onClicked != null){
                    onClicked.setOnClickedItem(i);
                   }
            }
        });

        }


    @Override
    public int getItemCount() {
        return ReturnSaleFragment.productList.size();
    }

    public void setOnItmeClicked(ReturnSaleAdapter.OnClicked onClicked) {
        this.onClicked = (ReturnSaleAdapter.OnClicked) onClicked;
    }

    public interface OnClicked {
        void setOnClickedItem(int position);
    }

    public void setOnIsDefectiveClicked(ReturnSaleAdapter.OnDefectiveClicked onDefectiveClicked) {
        this.onDefectiveClicked = (ReturnSaleAdapter.OnDefectiveClicked) onDefectiveClicked;
    }

    public interface OnDefectiveClicked {
        void setOnClickedItem(int position);
    }

}
