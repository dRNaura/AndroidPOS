package com.pos.salon.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.pos.salon.R;
import com.pos.salon.model.repairModel.SpinnerSelectionModel;

import java.util.ArrayList;

public class SpinnerSelectionAdapter extends ArrayAdapter<SpinnerSelectionModel> {

    LayoutInflater flater;

    public SpinnerSelectionAdapter(Context context, ArrayList<SpinnerSelectionModel> list) {

        super(context, 0, list);
//        flater = context.getLayoutInflater();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        return rowview(convertView, position);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return rowview(convertView, position);
    }

    private View rowview(View convertView, int position) {

        SpinnerSelectionModel rowItem = getItem(position);

        SpinnerSelectionAdapter.viewHolder holder;
        View rowview = convertView;
        if (rowview == null) {

            holder = new SpinnerSelectionAdapter.viewHolder();
            flater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowview = flater.inflate(R.layout.brand_item_types_row, null, false);

            holder.txtTitle = (TextView) rowview.findViewById(R.id.txtSpinnerText);
            holder.imageView = (ImageView) rowview.findViewById(R.id.imgSpinner);
            rowview.setTag(holder);
        } else {
            holder = (SpinnerSelectionAdapter.viewHolder) rowview.getTag();
        }
//        holder.imageView.setImageResource(rowItem.getImageId());
        holder.txtTitle.setText(rowItem.getName());

        return rowview;
    }

    private class viewHolder {
        TextView txtTitle;
        ImageView imageView;
    }
}
