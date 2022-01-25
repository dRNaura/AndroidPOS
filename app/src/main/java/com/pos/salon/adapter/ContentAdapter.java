package com.pos.salon.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.pos.salon.R;
import com.pos.salon.model.ContentModel;
import java.util.ArrayList;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.MyViewHolder> {

    public Context context;
    ContentAdapter.OnClicked onClicked;
    ArrayList<ContentModel> list = new ArrayList<>();


    public ContentAdapter(Context context, ArrayList<ContentModel> list) {

        this.context = context;
        this.list = list;


    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView img_dash_img;
        TextView dash_text;
        LinearLayout lay_dashboard;
        CardView card_dash_click;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            img_dash_img=itemView.findViewById(R.id.img_dash_img);
            dash_text=itemView.findViewById(R.id.dash_text);
            lay_dashboard=itemView.findViewById(R.id.lay_dashboard);
            card_dash_click=itemView.findViewById(R.id.card_dash_click);

        }

    }

    @NonNull
    @Override
    public ContentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_items, parent, false);
        return new ContentAdapter.MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final ContentAdapter.MyViewHolder viewHolder, final int i) {

        ContentModel model=list.get(i);

        if(model.dashText.equalsIgnoreCase("POS SALE")){
            viewHolder.card_dash_click.setCardBackgroundColor(Color.parseColor("#293F4C"));
            viewHolder.dash_text.setTextColor(Color.parseColor("#F0CA4D"));
        }else{
            viewHolder.card_dash_click.setCardBackgroundColor(Color.parseColor("#ffffff"));
        }
        viewHolder.dash_text.setText(model.dashText);
        viewHolder.img_dash_img.setImageResource(model.dashImage);

        viewHolder.card_dash_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (onClicked != null)
                    onClicked.setOnClickedItem(i,model.dashText);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnItmeClicked(ContentAdapter.OnClicked onClicked) {
        this.onClicked = (ContentAdapter.OnClicked) onClicked;
    }

    public interface OnClicked {
        void setOnClickedItem(int position,String fromLayout);
    }
}
