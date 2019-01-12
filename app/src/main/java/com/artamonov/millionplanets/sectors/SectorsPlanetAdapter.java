package com.artamonov.millionplanets.sectors;

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

public class SectorsPlanetAdapter extends RecyclerView.Adapter<SectorsPlanetAdapter.ViewHolder> {

    //private static ItemClickListener listener;
    private static DialogListener dialogListener;
    private List<User> userList;
    private List<ObjectModel> objectModelList;
    private Context context;
    private NumberPicker numberPicker;
    private boolean isPlanetTab;

    SectorsPlanetAdapter(List<User> userList, List<ObjectModel> objectModelList, DialogListener listener) {
        this.userList = userList;
        dialogListener = listener;
        this.objectModelList = objectModelList;
        //   this.isPlanetTab = isPlanetTab;
    }

    public SectorsPlanetAdapter(List<User> userList, Context context, SectorsPlanetAdapter.DialogListener listener) {
        this.userList = userList;
        this.context = context;
        dialogListener = listener;
    }


    @NonNull
    @Override
    public SectorsPlanetAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sectors_you_items, parent, false);
        return new SectorsPlanetAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SectorsPlanetAdapter.ViewHolder holder, int position) {
        Log.i("myTags", "onBindViewHolder pos: " + position);
        User user = userList.get(position);
        ObjectModel objectModel = objectModelList.get(position);
        holder.sectorsAmount.setText(Integer.toString(objectModel.getPlanetSectors()));
        holder.sectorsPrice.setText(Integer.toString(objectModel.getPlanetSectorsPrice()));

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
        private final TextView sectorsAmount;
        private final TextView sectorsPrice;
        //  private final ConstraintLayout constraintLayout;

        ViewHolder(View itemView) {
            super(itemView);
            sectorsAmount = itemView.findViewById(R.id.sectors_resource_amount);
            sectorsPrice = itemView.findViewById(R.id.sectors_resource_price);
            //  constraintLayout = itemView.findViewById(R.id.dialog_market_you_layout);
            // itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            //int position = getAdapterPosition();
            dialogListener.onDialogCreate();

        }
    }
}
