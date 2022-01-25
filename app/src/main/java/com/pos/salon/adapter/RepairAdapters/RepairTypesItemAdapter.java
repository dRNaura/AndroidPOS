package com.pos.salon.adapter.RepairAdapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.pos.salon.R;
import com.pos.salon.model.repairModel.RepairItemTypesModel;

import java.util.ArrayList;

public class RepairTypesItemAdapter extends ArrayAdapter<RepairItemTypesModel> {

    LayoutInflater flater;

    public RepairTypesItemAdapter(Activity context, ArrayList<RepairItemTypesModel> list) {

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

        RepairItemTypesModel rowItem = getItem(position);

        RepairTypesItemAdapter.viewHolder holder;
        View rowview = convertView;
        if (rowview == null) {

            holder = new RepairTypesItemAdapter.viewHolder();
            flater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowview = flater.inflate(R.layout.brand_item_types_row, null, false);

            holder.txtTitle = (TextView) rowview.findViewById(R.id.txtSpinnerText);
            holder.imageView = (ImageView) rowview.findViewById(R.id.imgSpinner);
            rowview.setTag(holder);
        } else {
            holder = (RepairTypesItemAdapter.viewHolder) rowview.getTag();
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
