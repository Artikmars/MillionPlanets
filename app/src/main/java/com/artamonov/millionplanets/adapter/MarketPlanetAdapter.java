package com.artamonov.millionplanets.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.artamonov.millionplanets.R;
import com.artamonov.millionplanets.model.ObjectModel;
import com.artamonov.millionplanets.model.User;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MarketPlanetAdapter extends RecyclerView.Adapter<MarketPlanetAdapter.ViewHolder> {

    //private static ItemClickListener listener;
    private static DialogListener dialogListener;
    private List<User> userList;
    private List<ObjectModel> objectModelList;
    private Context context;
    private NumberPicker numberPicker;
    private boolean isPlanetTab;

    public MarketPlanetAdapter(List<User> userList, List<ObjectModel> objectModelList, DialogListener listener) {
        this.userList = userList;
        dialogListener = listener;
        this.objectModelList = objectModelList;
        //   this.isPlanetTab = isPlanetTab;
    }

    public MarketPlanetAdapter(List<User> userList, Context context, MarketPlanetAdapter.DialogListener listener) {
        this.userList = userList;
        this.context = context;
        dialogListener = listener;
    }


    @NonNull
    @Override
    public MarketPlanetAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.market_you_items, parent, false);
        return new MarketPlanetAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MarketPlanetAdapter.ViewHolder holder, int position) {
        Log.i("myTags", "onBindViewHolder pos: " + position);
        User user = userList.get(position);
        ObjectModel objectModel = objectModelList.get(position);
        if (user.getResource_iron() == 0) {
            return;
        }
        holder.resourceName.setText(objectModel.getResourceName());
        holder.resourceAmount.setText(Integer.toString(objectModel.getIronAmount()));
        holder.resourcePrice.setText(Integer.toString(objectModel.getPrice_sell_iron()));


    }


    @Override
    public int getItemCount() {
        Log.i("myTags", "getItemCount size: " + userList.size());
        return userList.size();
    }


    /*public interface ItemClickListener {
        void onItemClick(int position);
    }*/

    public interface DialogListener {
        void onDialogCreate();
        //  void onDialogSubmit();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView resourceAmount;
        private final TextView resourceName;
        private final TextView resourcePrice;
        //  private final ConstraintLayout constraintLayout;

        ViewHolder(View itemView) {
            super(itemView);
            resourceName = itemView.findViewById(R.id.resource_name);
            resourceAmount = itemView.findViewById(R.id.resource_amount);
            resourcePrice = itemView.findViewById(R.id.resource_price);
            //  constraintLayout = itemView.findViewById(R.id.dialog_market_you_layout);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            //int position = getAdapterPosition();
            dialogListener.onDialogCreate();

        }
    }
}
