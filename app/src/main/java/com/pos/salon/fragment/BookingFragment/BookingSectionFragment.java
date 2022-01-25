package com.pos.salon.fragment.BookingFragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.pos.salon.R;
import com.pos.salon.adapter.BookingAdapter.BookingListAdapter;

import static com.pos.salon.activity.HomeActivity.tool_title;


public class BookingSectionFragment extends Fragment {

    LinearLayout lay_list_booking,lay_calenderview;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_booking_section, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tool_title.setText("BOOKING");

        findViewIds(view);
    }


    public void findViewIds(View view) {

        lay_list_booking=view.findViewById(R.id.lay_list_booking);
        lay_calenderview=view.findViewById(R.id.lay_calenderview);

        listeners();
    }
    public void listeners(){


        lay_list_booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookingListFragment ldf = new BookingListFragment();
                FragmentManager fragmentManager =getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter,R.anim.exit,R.anim.left_to_right,R.anim.right_to_left);
                transaction.replace(R.id.lay_booking_section, ldf,"bookingListFragment");
                transaction.addToBackStack("bookingListFragment");
                transaction.commit();
            }
        });
        lay_calenderview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalenderViewFragment ldf = new CalenderViewFragment();
                FragmentManager fragmentManager =getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter,R.anim.exit,R.anim.left_to_right,R.anim.right_to_left);
                transaction.replace(R.id.lay_booking_section, ldf);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

}