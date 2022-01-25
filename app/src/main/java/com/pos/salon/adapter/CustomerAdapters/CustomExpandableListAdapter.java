package com.pos.salon.adapter.CustomerAdapters;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import com.pos.salon.R;
import com.pos.salon.model.searchData.SearchItem;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
   // private List<String> expandableListTitle;
    final Set<Pair<Long, Long>> mCheckedItems = new HashSet<Pair<Long, Long>>();
    ArrayList<SearchItem.Modifierrlist> arrModifierSets = new ArrayList<SearchItem.Modifierrlist>();
    SearchItem objSearchItem = null;
    public CustomExpandableListAdapter(Context context, SearchItem objSearchItem) {
        this.context = context;


        this.objSearchItem = objSearchItem;

        if(objSearchItem.getModifier_sets() != null)
        {
            arrModifierSets = objSearchItem.getModifier_sets();
        }

        //Log.d("msg", "CustomExpandableListAdapter: "+expandableListDetail);
      //  Toast.makeText(context, ""+expandableListDetail, Toast.LENGTH_LONG).show();
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition)
    {
        SearchItem.Modifierrlist objModifier = arrModifierSets.get(listPosition);
        ArrayList<SearchItem.Variations> arrVariations = objModifier.getVariations();
        SearchItem.Variations objVariation = arrVariations.get(expandedListPosition);

        return objVariation;
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(final int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        SearchItem.Variations objVariation = (SearchItem.Variations) getChild(listPosition, expandedListPosition);


        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.custom_variations, null);
        }
        TextView expandedListTextView = (TextView) convertView
                .findViewById(R.id.tv_variationname);

        expandedListTextView.setText(objVariation.getName());

        CheckBox cb_variation = convertView.findViewById(R.id.cb_variation);


        final Pair<Long, Long> tag = new Pair<Long, Long>(
                getGroupId(listPosition),
                getChildId(listPosition, expandedListPosition));
        cb_variation.setTag(tag);
        // set checked if groupId/childId in checked items
        cb_variation.setChecked(mCheckedItems.contains(tag));
        // set OnClickListener to handle checked switches
        cb_variation.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                final CheckBox cb = (CheckBox) v;
                final Pair<Long, Long> tag = (Pair<Long, Long>) v.getTag();

                SearchItem.Modifierrlist objSelectedModifier = null;
                SearchItem.Variations objSelectedVariation = null;

                if(arrModifierSets.size() > 0)
                {
                    objSelectedModifier = arrModifierSets.get(listPosition);

                }



                if(objSelectedModifier != null)
                {
                    ArrayList<SearchItem.Variations> arrModifierVariations = objSelectedModifier.getVariations();

                    objSelectedVariation = arrModifierVariations.get(expandedListPosition);

                }

                //get variation price
                String strVariationPrice = "0.0";
                if(objSelectedVariation != null)
                {
                    strVariationPrice = objSelectedVariation.getSell_price_inc_tax();
                }
                //end variation price .

                if (cb.isChecked())
                {

                    mCheckedItems.add(tag);



              /*      csi.cartselect(modnames.get(listPosition),modid.get(listPosition),expandedListText,variationid.get(expandedListPosition)
                            ,price.get(expandedListPosition),totalvariationprice);*/
                   // Toast.makeText(context, "" + expandedListText, Toast.LENGTH_LONG).show();
                    //Toast.makeText(context, "" + mCheckedItems.size(), Toast.LENGTH_LONG).show();
                     Log.d("msg", "onClickkk1: "+mCheckedItems.size());

                } else {
                    mCheckedItems.remove(tag);

                   // csi.cartselect("","", "","","",totalvariationprice);
                  // Log.d("msg", "onClickkk2: " + mCheckedItems.size());
                }

            }
        });

        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition)
    {
        SearchItem.Modifierrlist objModifier = arrModifierSets.get(listPosition);
        ArrayList<SearchItem.Variations> arrVariations = objModifier.getVariations();
        return arrVariations.size();
    }

    @Override
    public Object getGroup(int listPosition)
    {
        return arrModifierSets.get(listPosition);
    }

    @Override
    public int getGroupCount()
    {
        return arrModifierSets.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(final int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent)
    {
        SearchItem.Modifierrlist objModifier = arrModifierSets.get(listPosition);

        final String listTitle = objModifier.getName();
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.custom_modifierset, null);
        }
        TextView listTitleTextView = (TextView) convertView
                .findViewById(R.id.tv_modifiername);


        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);
        return convertView;
    }

    @Override
    public boolean hasStableIds()
    {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}
