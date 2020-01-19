package com.artamonov.millionplanets.market;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.artamonov.millionplanets.R;
import com.artamonov.millionplanets.model.PlanetType;
import com.artamonov.millionplanets.model.User;
import com.artamonov.millionplanets.utils.Price;
import com.artamonov.millionplanets.utils.Utils;

public class MarketPlanetAdapter extends RecyclerView.Adapter<MarketPlanetAdapter.ViewHolder> {

    private static DialogListener dialogListener;
    public User user;
    private @PlanetType.AnnotationPlanetType String planetType;

    public MarketPlanetAdapter(User user, String planetType, DialogListener listener) {
        this.user = user;
        this.planetType = planetType;
        dialogListener = listener;
    }

    @NonNull
    @Override
    public MarketPlanetAdapter.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.market_you_items, parent, false);
        return new MarketPlanetAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MarketPlanetAdapter.ViewHolder holder, int position) {
        holder.resourceName.setText(Utils.INSTANCE.getResourceItemNameById(position));
        holder.resourcePrice.setText(String.valueOf(Price.INSTANCE.getPlayerBuyPrice(position)));
    }

    @Override
    public int getItemCount() {
        return Utils.INSTANCE.getResourceTypeAmountByPlanetType(planetType);
    }

    public interface DialogListener {
        void onDialogCreate(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView resourceAmount;
        private final TextView resourceName;
        private final TextView resourcePrice;

        ViewHolder(View itemView) {
            super(itemView);
            resourceName = itemView.findViewById(R.id.resource_name);
            resourceAmount = itemView.findViewById(R.id.resource_amount);
            resourcePrice = itemView.findViewById(R.id.resource_price);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            dialogListener.onDialogCreate(getAdapterPosition());
        }
    }
}
