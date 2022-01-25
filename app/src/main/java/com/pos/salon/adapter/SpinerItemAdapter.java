package com.pos.salon.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.pos.salon.R;
import com.pos.salon.model.SpinnerModel;
import com.pos.salon.model.posLocation.BusinessLocationData;

import java.util.ArrayList;

public class SpinerItemAdapter extends ArrayAdapter<SpinnerModel> {

    LayoutInflater flater;
    public SpinerItemAdapter(Context context, ArrayList<SpinnerModel> list){
        super(context,0, list);
//        flater = context.getLayoutInflater();
    }

    public void setNewData(){
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        return rowview(convertView,position);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return rowview(convertView,position);
    }

    private View rowview(View convertView , int position){

        SpinnerModel rowItem = getItem(position);

        SpinerItemAdapter.viewHolder holder ;
        View rowview = convertView;
        if (rowview==null) {

            holder = new SpinerItemAdapter.viewHolder();
            flater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowview = flater.inflate(R.layout.spinner_items, null, false);
            holder.txtTitle = (TextView) rowview.findViewById(R.id.txtSpinnerText);
            rowview.setTag(holder);
        }else{
            holder = (SpinerItemAdapter.viewHolder) rowview.getTag();
        }
        holder.txtTitle.setText(rowItem.name);

        return rowview;
    }

    private class viewHolder{
        TextView txtTitle;
    }
}

