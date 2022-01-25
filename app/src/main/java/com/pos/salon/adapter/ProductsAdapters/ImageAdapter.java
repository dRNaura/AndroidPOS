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
import com.pos.salon.model.ProductModel.ImagesModel;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {

    public Context context;
    ImageAdapter.OnClicked onClicked;
    ArrayList<ImagesModel> list = new ArrayList<>();


    public ImageAdapter(Context context, ArrayList<ImagesModel> list) {
        this.context = context;
        this.list = list;


    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_imgNamegallry;
        ImageView img_remove;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_imgNamegallry=itemView.findViewById(R.id.txt_imgNamegallry);
            img_remove=itemView.findViewById(R.id.img_remove);

        }

    }

    @NonNull
    @Override
    public ImageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_gallery_images, parent, false);
        return new ImageAdapter.MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final ImageAdapter.MyViewHolder viewHolder, final int i) {

        ImagesModel model=list.get(i);
        viewHolder.txt_imgNamegallry.setText(i+1+". "+model.name);

        viewHolder.img_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClicked != null)
                    onClicked.setOnClickedItem(i);

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnItmeClicked(ImageAdapter.OnClicked onClicked) {
        this.onClicked = (ImageAdapter.OnClicked) onClicked;
    }

    public interface OnClicked {
        void setOnClickedItem(int position);
    }
}
