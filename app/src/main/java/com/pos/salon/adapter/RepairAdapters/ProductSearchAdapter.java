package com.pos.salon.adapter.RepairAdapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.pos.salon.R;
import com.pos.salon.model.searchData.SearchItem;

import java.util.ArrayList;
import java.util.List;

public class ProductSearchAdapter extends ArrayAdapter<SearchItem> {
    private final List<SearchItem> searchProductList;

    public ProductSearchAdapter(Activity context, ArrayList<SearchItem> searchProList) {
        super(context, 0, searchProList);
        searchProductList = searchProList;
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
                    R.layout.proudct_search_items, parent, false
            );
        }

        TextView txt_repair_product_name = convertView.findViewById(R.id.txt_repair_product_name);

        SearchItem producTItemItem = getItem(position);

        if (producTItemItem != null) {
            txt_repair_product_name.setText(producTItemItem.getName());
        }


        return convertView;
    }

    private Filter countryFilter = new Filter() {


        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<SearchItem> suggestions = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                suggestions.addAll(searchProductList);
            } else {
                String filterPattern= "";
                filterPattern = constraint.toString().toLowerCase().trim();

                for (SearchItem item : searchProductList) {
                    if (item.getName().toLowerCase().contains(filterPattern)) {
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
            return ((SearchItem) resultValue).getName() ;
        }
    };
}
