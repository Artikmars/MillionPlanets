package com.artamonov.millionplanets.shipyard;

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
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ShipyardAdapter extends RecyclerView.Adapter<ShipyardAdapter.ViewHolder> {

    //private static ItemClickListener listener;
    private static DialogListener dialogListener;
    private static ItemClickListener listener;
    List<User> shipsList;
    List<User> shipTrader;
    List<User> shipRS;
    private List<User> userList;
    private Map<Integer, Object> shipsMap;
    private List<ObjectModel> objectModelList;
    private Context context;
    private NumberPicker numberPicker;
    private boolean isPlanetTab;

    ShipyardAdapter(List<User> shipsList, ItemClickListener itemClickListener) {
        listener = itemClickListener;
        this.shipsList = shipsList;
    }

    @NonNull
    @Override
    public ShipyardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shipyard_items, parent, false);
        return new ShipyardAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShipyardAdapter.ViewHolder holder, int position) {
        Log.i("myTags", "onBindViewHolder pos: " + position);
        holder.shipyardName.setText(shipsList.get(position).getShip());
        holder.shipyardPrice.setText(Integer.toString(shipsList.get(position).getShipPrice()));
    }

    @Override
    public int getItemCount() {
        return shipsList.size();
    }

    public interface ItemClickListener {
        void onItemClick(int position);
    }

    public interface DialogListener {
        void onDialogCreate();
        //  void onDialogSubmit();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView shipyardName;
        private final TextView shipyardPrice;

        ViewHolder(View itemView) {
            super(itemView);
            shipyardName = itemView.findViewById(R.id.shipyard_name);
            shipyardPrice = itemView.findViewById(R.id.shipyard_price);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            listener.onItemClick(position);

        }
    }
}
