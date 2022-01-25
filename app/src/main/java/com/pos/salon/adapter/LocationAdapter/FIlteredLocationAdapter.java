package com.pos.salon.adapter.LocationAdapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.pos.salon.R;
import com.pos.salon.model.posLocation.BusinessLocationData;

import java.util.ArrayList;

public class FIlteredLocationAdapter extends ArrayAdapter<BusinessLocationData> {

    LayoutInflater flater;
    public FIlteredLocationAdapter(Activity context, ArrayList<BusinessLocationData> list) {
        super(context, 0, list);
//        flater = context.getLayoutInflater();
    }

    public void setNewData() {
        notifyDataSetChanged();
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

        BusinessLocationData rowItem = getItem(position);

        viewHolder holder;
        View rowview = convertView;
        if (rowview == null) {

            holder = new viewHolder();
            flater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowview = flater.inflate(R.layout.location_filter_row, null, false);

            holder.txtTitle = (TextView) rowview.findViewById(R.id.locationFilterText);
//        holder.imageView = (ImageView) rowview.findViewById(R.id.imgSpinner);
            rowview.setTag(holder);
        } else {
            holder = (viewHolder) rowview.getTag();
        }
//        holder.imageView.setImageResource(rowItem.getImageId());
        holder.txtTitle.setText(rowItem.getName());

        return rowview;
    }

    private class viewHolder {
        TextView txtTitle;
//    ImageView imageView;

    }
}

