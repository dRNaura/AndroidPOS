package com.pos.salon.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pos.salon.R;
import com.pos.salon.model.SpinnerModel;

import java.util.ArrayList;

public class SpinnerAdapter implements ListAdapter {
    ArrayList<SpinnerModel> arrayList = new ArrayList<>();
    private Context context;
    SpinnerAdapter.OnClicked onClicked;

    public SpinnerAdapter(Context context, ArrayList<SpinnerModel> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int i) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        ViewHolder mHolder = new ViewHolder();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        SpinnerModel model = arrayList.get(i);
        if (view == null) {
            view = layoutInflater.inflate(R.layout.list_row_items, null);
            mHolder = new ViewHolder();
            view.setTag(mHolder);

        } else {
            view.setTag(view.getTag());
        }
        mHolder.lay_item = view.findViewById(R.id.lay_item);
        mHolder.lay_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClicked != null)
                    onClicked.setOnItem(i);
            }
        });
        mHolder.tittle = view.findViewById(R.id.txt_title);
        mHolder.tittle.setText(model.name);

        return view;
    }

    @Override
    public int getItemViewType(int i) {
        return i;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    public void setOnItemClicked(SpinnerAdapter.OnClicked onClicked) {
        this.onClicked = (SpinnerAdapter.OnClicked) onClicked;
    }

    public interface OnClicked {
        void setOnItem(int position);
    }

    private class ViewHolder {

        private TextView tittle;
        private RelativeLayout lay_item;
    }
}