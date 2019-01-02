package com.artamonov.millionplanets.adapter;

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

public class MarketYouAdapter extends RecyclerView.Adapter<MarketYouAdapter.ViewHolder> {

    //private static ItemClickListener listener;
    private static DialogListener dialogListener;
    private static List<User> userList;
    private List<ObjectModel> objectModelList;
    private static Context context;
    private NumberPicker numberPicker;
    private boolean isPlanetTab;

    public MarketYouAdapter(List<User> userList, List<ObjectModel> objectModelList, Context context, DialogListener listener) {
        this.userList = userList;
        dialogListener = listener;
        this.objectModelList = objectModelList;
        this.context = context;
        // this.isPlanetTab = isPlanetTab;

    }

    public MarketYouAdapter(List<User> userList, Context context, MarketYouAdapter.DialogListener listener) {
        this.userList = userList;
        this.context = context;
        dialogListener = listener;
    }


    @NonNull
    @Override
    public MarketYouAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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


        return new MarketYouAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MarketYouAdapter.ViewHolder holder, int position) {
        Log.i("myTags", "onBindViewHolder pos: " + position);
        User user = userList.get(position);
        ObjectModel objectModel = objectModelList.get(position);
        Log.i("myTags", "onBindViewHolder res name: " + objectModel.getResourceName());
        holder.resourceName.setText(objectModel.getResourceName());
        Log.i("myTags", "onBindViewHolder price buy : " + objectModel.getPrice_buy_iron());

        holder.resourcePrice.setText(Integer.toString(objectModel.getPrice_buy_iron()));
        if (user.getResource_iron() == 0) {
            holder.resourceAmount.setText("0");
        } else {
            holder.resourceAmount.setText(Integer.toString(user.getResource_iron()));
        }

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
            if (userList.get(getAdapterPosition()).getResource_iron() == 0) {
                Toast.makeText(context, "No resources to sell", Toast.LENGTH_SHORT).show();
                return;
            }
            dialogListener.onDialogCreate();

        }
    }
}
