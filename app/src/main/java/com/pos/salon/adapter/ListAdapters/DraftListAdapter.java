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
import com.pos.salon.model.listModel.ListPosModel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DraftListAdapter extends RecyclerView.Adapter<DraftListAdapter.MyViewHolder>{

public Context context;
        OnClicked onClicked;
        ArrayList<ListPosModel> draftList=new ArrayList<>();

public DraftListAdapter(Context context,ArrayList<ListPosModel> draftList){
        this.context=context;
        this.draftList=draftList;

        }

public class MyViewHolder extends RecyclerView.ViewHolder {

    ImageView img_forward;
    LinearLayout linear_layout_id;
    TextView txt_draft_date, txt_draft_location, txt_draft_reference, txt_draft_cus_name;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        img_forward = itemView.findViewById(R.id.img_forward);
        txt_draft_date = itemView.findViewById(R.id.txt_draft_date);
        txt_draft_location = itemView.findViewById(R.id.txt_draft_location);
        txt_draft_reference = itemView.findViewById(R.id.txt_draft_reference);
        txt_draft_cus_name = itemView.findViewById(R.id.txt_draft_cus_name);
        linear_layout_id = itemView.findViewById(R.id.linear_layout_id);


    }

}

    @NonNull
    @Override
    public DraftListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.draft_list_items, parent, false);
        return new DraftListAdapter.MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final DraftListAdapter.MyViewHolder viewHolder, final int i) {


        ListPosModel model = draftList.get(i);

        String dateArray = model.order_Date;

//        Log.e("datetime",model.order_Date);

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

        viewHolder.txt_draft_date.setText(part1 + " | " + part2);
        viewHolder.txt_draft_reference.setText(model.invoice_no);
        viewHolder.txt_draft_cus_name.setText(model.customer_name);
        viewHolder.txt_draft_location.setText(model.location);

        viewHolder.linear_layout_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClicked != null)
                    onClicked.setOnClickedItem(i);
            }

        });
    }

    @Override
    public int getItemCount() {
        return draftList.size();
    }

    public void setOnItmeClicked(DraftListAdapter.OnClicked onClicked) {
        this.onClicked = (DraftListAdapter.OnClicked) onClicked;
    }

    public interface OnClicked {
        void setOnClickedItem(int position);
    }
}