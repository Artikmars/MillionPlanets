package com.artamonov.millionplanets.sectors;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.artamonov.millionplanets.R;
import com.artamonov.millionplanets.model.ObjectModel;
import com.artamonov.millionplanets.model.User;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SectorsYouAdapter extends RecyclerView.Adapter<SectorsYouAdapter.ViewHolder> {

    //private static ItemClickListener listener;
    private static DialogListener dialogListener;
    private static List<User> userList;
    private static Context context;
    private List<ObjectModel> objectModelList;
    private NumberPicker numberPicker;
    private boolean isPlanetTab;

    public SectorsYouAdapter(List<User> userList, List<ObjectModel> objectModelList, Context context, DialogListener listener) {
        this.userList = userList;
        dialogListener = listener;
        this.objectModelList = objectModelList;
        this.context = context;
        // this.isPlanetTab = isPlanetTab;

    }

    public SectorsYouAdapter(List<User> userList, Context context, SectorsYouAdapter.DialogListener listener) {
        this.userList = userList;
        this.context = context;
        dialogListener = listener;
    }


    @NonNull
    @Override
    public SectorsYouAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sectors_you_items, parent, false);
        Log.i("myTags", "onCreateViewHolder " + userList.get(0).getResource_iron());
        return new SectorsYouAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SectorsYouAdapter.ViewHolder holder, int position) {
        Log.i("myTags", "onBindViewHolder pos: " + position);
        User user = userList.get(position);
        ObjectModel objectModel = objectModelList.get(position);
        Log.i("myTags", "onBindViewHolder res name: " + objectModel.getResourceName());
        Log.i("myTags", "onBindViewHolder price buy : " + objectModel.getPrice_buy_iron());

        holder.resourcePrice.setText(Integer.toString(objectModel.getPlanetSectorsPrice() / 2));
        holder.resourceAmount.setText(Integer.toString(user.getSectors()));
    }


    @Override
    public int getItemCount() {
        Log.i("myTags", "getItemCount size: " + userList.size());
        return userList.size();
    }


    public interface DialogListener {
        void onDialogCreate();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView resourceAmount;
        private final TextView resourcePrice;

        ViewHolder(View itemView) {
            super(itemView);
            resourceAmount = itemView.findViewById(R.id.sectors_resource_amount);
            resourcePrice = itemView.findViewById(R.id.sectors_resource_price);
            //  itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (userList.get(getAdapterPosition()).getResource_iron() == 0) {
                Toast.makeText(context, "No resources to sell", Toast.LENGTH_SHORT).show();
                return;
            }
            dialogListener.onDialogCreate();

        }
    }
}
