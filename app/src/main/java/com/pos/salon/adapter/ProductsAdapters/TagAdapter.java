package com.pos.salon.adapter.ProductsAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.pos.salon.R;
import java.util.ArrayList;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.MyViewHolder> {

    public Context context;
    TagAdapter.RemoveTag removeTag;
    ArrayList<String> list ;
    public String from="";

    public TagAdapter(Context context, ArrayList<String> list,String from) {
        this.context = context;
        this.list = list;
        this.from = from;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_tag_name;
        ImageView img_delete_tag;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_tag_name=itemView.findViewById(R.id.txt_tag_name);
            img_delete_tag=itemView.findViewById(R.id.img_delete_tag);
        }
    }

    @NonNull
    @Override
    public TagAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.get_tags, parent, false);
        return new TagAdapter.MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final TagAdapter.MyViewHolder viewHolder, final int i) {

        if(from.equalsIgnoreCase("")){
            viewHolder.img_delete_tag.setVisibility(View.VISIBLE);
        }else{
            viewHolder.img_delete_tag.setVisibility(View.GONE);
        }
        viewHolder.txt_tag_name.setText(list.get(i));
        viewHolder.img_delete_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (removeTag != null){
                    removeTag.setOnClickedItem(i);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public void setOnRemoveClicked(TagAdapter.RemoveTag removeTag) {
        this.removeTag = (TagAdapter.RemoveTag) removeTag;
    }

    public interface RemoveTag {
        void setOnClickedItem(int position);
    }


}
