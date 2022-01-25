package com.pos.salon.adapter.ListAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.pos.salon.R;
import com.pos.salon.model.listModel.QuatationListModel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class QuatationListAdapter extends RecyclerView.Adapter<QuatationListAdapter.MyViewHolder> {

    public Context context;
    ArrayList<QuatationListModel> quatationSalesList = new ArrayList<>();
    OnClicked onClicked;

    public QuatationListAdapter(Context context,ArrayList<QuatationListModel> quatationSalesList) {
        this.context = context;
        this.quatationSalesList = quatationSalesList;

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txt_quatation_date,txt_quatation_reference,txt_quatation_cus_name,txt_quatation_location;
        ImageView img_forward;
        LinearLayout linear_layout_id;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_quatation_date=itemView.findViewById(R.id.txt_quatation_date);
            txt_quatation_reference=itemView.findViewById(R.id.txt_quatation_reference);
            txt_quatation_cus_name=itemView.findViewById(R.id.txt_quatation_cus_name);
            txt_quatation_location=itemView.findViewById(R.id.txt_quatation_location);
            img_forward=itemView.findViewById(R.id.img_forward);
            linear_layout_id=itemView.findViewById(R.id.linear_layout_id);
        }
    }
    @NonNull
    @Override
    public QuatationListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.quatation_list_items, parent, false);
        return new QuatationListAdapter.MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final QuatationListAdapter.MyViewHolder viewHolder, final int i) {

        QuatationListModel model=quatationSalesList.get(i);

        String dateArray = model.quatationDate;

        SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date newDate = null;
        try {
            newDate = spf.parse(dateArray);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        spf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String strDate1 = spf.format(newDate);


        String[] separated = strDate1.split("\\ ");
        String part1 = separated[0];
        String part2 = separated[1];

        viewHolder.txt_quatation_date.setText(part1 + " | " + part2);
        viewHolder.txt_quatation_reference.setText((model.quatationInvoiceNo));
        viewHolder.txt_quatation_cus_name.setText((model.quatationCustomerName));
        viewHolder.txt_quatation_location.setText((model.quatationBusinessLocation));

        viewHolder.linear_layout_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClicked != null) {
                    onClicked.setOnClickedItem(i);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return quatationSalesList.size();
    }
    public void setOnItmeClicked(QuatationListAdapter.OnClicked onClicked) {
        this.onClicked = (QuatationListAdapter.OnClicked) onClicked;
    }

    public interface OnClicked {
        void setOnClickedItem(int position);
    }

}
