package com.pos.salon.adapter.UserManageAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.pos.salon.R;
import com.pos.salon.model.UserManageModel;

import java.util.ArrayList;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.MyViewHolder> {

    public Context context;
    UserListAdapter.OnClicked onClicked;
    ArrayList<UserManageModel> list = new ArrayList<>();

    public UserListAdapter(Context context, ArrayList<UserManageModel> list) {

        this.context = context;
        this.list = list;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txt_username,txt_user_name,txt_user_role,txt_user_email;
        LinearLayout lay_user;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_username=itemView.findViewById(R.id.txt_username);
            txt_user_name=itemView.findViewById(R.id.txt_user_name);
            txt_user_role=itemView.findViewById(R.id.txt_user_role);
            txt_user_email=itemView.findViewById(R.id.txt_user_email);
            lay_user=itemView.findViewById(R.id.lay_user);
        }
    }
    @NonNull
    @Override
    public UserListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_items, parent, false);
        return new UserListAdapter.MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final UserListAdapter.MyViewHolder viewHolder, final int i) {

        UserManageModel model=list.get(i);
        viewHolder.txt_username.setText(model.username);
        viewHolder.txt_user_name.setText(model.full_name);
        viewHolder.txt_user_email.setText(model.email);

        viewHolder.lay_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClicked != null)
                    onClicked.setOnClickedItem(i);

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnItmeClicked(UserListAdapter.OnClicked onClicked) {
        this.onClicked = (UserListAdapter.OnClicked) onClicked;
    }

    public interface OnClicked {
        void setOnClickedItem(int position);
    }

    public void updateList(ArrayList<UserManageModel> list){
        this.list = list;

        notifyDataSetChanged();
    }
}
