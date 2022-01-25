package com.pos.salon.adapter.BookingAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.pos.salon.R;
import com.pos.salon.adapter.ContentAdapter;
import com.pos.salon.adapter.ListAdapters.DraftListAdapter;
import com.pos.salon.model.ContentModel;
import java.util.ArrayList;

public class BookingCategoryADapter extends RecyclerView.Adapter<BookingCategoryADapter.MyViewHolder> {

    public Context context;
    BookingCategoryADapter.OnClicked onClicked;
    ArrayList<ContentModel> list = new ArrayList<>();

    public BookingCategoryADapter(Context context, ArrayList<ContentModel> list) {
        this.context = context;
        this.list = list;

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView img_dash_img;
        TextView dash_text;
        RelativeLayout lay_dashboard;
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
    public BookingCategoryADapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_category_items, parent, false);
        return new BookingCategoryADapter.MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final BookingCategoryADapter.MyViewHolder viewHolder, final int i) {

//        ContentModel model=list.get(i);
//        viewHolder.dash_text.setText(model.dashText);
//        viewHolder.img_dash_img.setImageResource(model.dashImage);

        viewHolder.card_dash_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (onClicked != null)
                    onClicked.setOnClickedItem(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return 15;
    }

    public void setOnItmeClicked(BookingCategoryADapter.OnClicked onClicked) {
        this.onClicked = (BookingCategoryADapter.OnClicked) onClicked;
    }

    public interface OnClicked {
        void setOnClickedItem(int position);
    }
}
