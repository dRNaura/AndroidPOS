package com.pos.salon.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pos.salon.R;
import com.pos.salon.model.customerData.CustomerListData;

import java.util.ArrayList;
import java.util.List;

public class ServiceSearchAdapter extends ArrayAdapter<CustomerListData> {
    private List<CustomerListData> customerListFull;

    public ServiceSearchAdapter(Activity context, ArrayList<CustomerListData> customerList) {
        super(context, 0, customerList);
        customerListFull = customerList;
        //  this.customerName = customerName;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return countryFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.service_search_items, parent, false
            );
        }
        TextView textViewName = convertView.findViewById(R.id.txtSpinnerText);
        ImageView imageViewFlag = convertView.findViewById(R.id.imgSpinner);

        CustomerListData customerItem = getItem(position);

        if (customerItem != null) {
            textViewName.setText(customerItem.getText());
            //  customerName.customername(customerItem.getText(), String.valueOf(position));
//            imageViewFlag.setImageResource(customerItem.getImageId());
        }


        return convertView;
    }
    public void updateList(ArrayList<CustomerListData> list2){
        customerListFull = list2;
        notifyDataSetChanged();
    }


    private Filter countryFilter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<CustomerListData> suggestions = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                suggestions.addAll(customerListFull);
            } else {
                String filterPattern= "";
                filterPattern = constraint.toString().toLowerCase().trim();

                for (CustomerListData item : customerListFull) {
                    if (item.getText().toLowerCase().contains(filterPattern) || item.getMobile().toLowerCase().contains(filterPattern) || item.getEmail().toLowerCase().contains(filterPattern)) {
                        suggestions.add(item);
                    }
                }
            }

            results.values = suggestions;
            results.count = suggestions.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            if (results.values != null)
                addAll((List) results.values);
            notifyDataSetChanged();
        }




        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((CustomerListData) resultValue).getText();
        }
    };

}

