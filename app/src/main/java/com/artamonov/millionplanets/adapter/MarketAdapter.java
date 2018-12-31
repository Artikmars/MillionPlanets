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

public class MarketAdapter extends RecyclerView.Adapter<MarketAdapter.ViewHolder> {

    //private static ItemClickListener listener;
    private static DialogListener dialogListener;
    private List<User> userList;
    private List<ObjectModel> objectModelList;
    private Context context;
    private NumberPicker numberPicker;
    private boolean isPlanetTab;

    public MarketAdapter(List<User> userList, List<ObjectModel> objectModelList, boolean isPlanetTab, DialogListener listener) {
        this.userList = userList;
        dialogListener = listener;
        this.objectModelList = objectModelList;
        this.isPlanetTab = isPlanetTab;

    }

    public MarketAdapter(List<User> userList, Context context, MarketAdapter.DialogListener listener) {
        this.userList = userList;
        this.context = context;
        dialogListener = listener;
    }


    @NonNull
    @Override
    public MarketAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.market_you_items, parent, false);
        Log.i("myTags", "onCreateViewHolder " + userList.get(0).getResource_iron());
        //   final MarketYouAdapter.ViewHolder viewHolder = new MarketYouAdapter.ViewHolder(view);

      /*  viewHolder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogListener.onDialogCreate();
            }
        });*/

         /*   @Override
            public void onClick(View view) {
                User user = userList.get(viewHolder.getAdapterPosition());
                if (user.getResource_iron() != 0) {
                    numberPicker = view.findViewById(R.id.numberPicker);
                    numberPicker.setMinValue(1);
                    numberPicker.setMaxValue(user.getResource_iron());
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.dialog_market_you);
                    dialog.show();
                }*/

        //   Button btnNumberPickerConfirm = view.findViewById(R.id.btnNumberPickerConfirm);


        return new MarketAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MarketAdapter.ViewHolder holder, int position) {
        Log.i("myTags", "onBindViewHolder pos: " + position);
        User user = userList.get(position);
        ObjectModel objectModel = objectModelList.get(position);
        if (user.getResource_iron() == 0) {
            return;
        }
        if (!isPlanetTab) {
            Log.i("myTags", "onBindViewHolder objectModel.getResourceName(): " + objectModel.getResourceName());
            holder.resourceName.setText(objectModel.getResourceName());
            holder.resourceAmount.setText(Integer.toString(user.getResource_iron()));
            holder.resourcePrice.setText(Integer.toString(objectModel.getPrice_buy_iron()));
        } else {
            holder.resourceName.setText(objectModel.getResourceName());
            holder.resourceAmount.setText(Integer.toString(user.getResource_iron()));
            holder.resourcePrice.setText(Integer.toString(objectModel.getPrice_sell_iron()));
        }

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
