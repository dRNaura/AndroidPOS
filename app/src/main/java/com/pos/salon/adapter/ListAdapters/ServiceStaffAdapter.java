package com.pos.salon.adapter.ListAdapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.pos.salon.R;
import com.pos.salon.interfacesection.Selectedtable;
import com.pos.salon.model.posLocation.ServiceStaff;

import java.util.ArrayList;

public class ServiceStaffAdapter extends ArrayAdapter<ServiceStaff> {

    LayoutInflater flater;
Selectedtable selectedtable;
    public ServiceStaffAdapter(Activity context, ArrayList<ServiceStaff> list,Selectedtable selectedtable){
        super(context,0, list);
        this.selectedtable=selectedtable;
//        flater = context.getLayoutInflater();
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

        ServiceStaff rowItem = getItem(position);

        viewHolder holder ;
        View rowview = convertView;
        if (rowview==null) {

            holder = new viewHolder();
            flater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowview = flater.inflate(R.layout.location_spinner_row, null, false);

            holder.txtTitle = (TextView) rowview.findViewById(R.id.txtSpinnerText);
            holder.imageView = (ImageView) rowview.findViewById(R.id.imgSpinner);
            rowview.setTag(holder);
        }else{
            holder = (viewHolder) rowview.getTag();
        }
        holder.imageView.setImageResource(rowItem.getImageId());
        holder.txtTitle.setText(rowItem.getFirst_name());

        selectedtable.staffselect(position,rowItem.getId(),rowItem.getFirst_name());

        return rowview;
    }

    private class viewHolder{
        TextView txtTitle;
        ImageView imageView;
    }
}
