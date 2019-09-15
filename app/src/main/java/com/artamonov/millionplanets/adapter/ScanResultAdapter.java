package com.artamonov.millionplanets.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.artamonov.millionplanets.R;
import com.artamonov.millionplanets.model.ObjectModel;
import java.util.List;

public class ScanResultAdapter extends RecyclerView.Adapter<ScanResultAdapter.ViewHolder> {

    private static ItemClickListener listener;
    private List<ObjectModel> objectList;

    public ScanResultAdapter(List<ObjectModel> objectList, ItemClickListener itemClickListener) {
        this.objectList = objectList;
        listener = itemClickListener;
    }

    public ScanResultAdapter(List<ObjectModel> objectList) {
        this.objectList = objectList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.scan_result_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScanResultAdapter.ViewHolder holder, int position) {
        ObjectModel objectModel = objectList.get(position);
        holder.name.setText(objectModel.getName());
        holder.distance.setText(Integer.toString(objectModel.getDistance()));

        switch (objectModel.getType()) {
            case "planet":
                holder.name.setTextColor(Color.parseColor("#FFFF00"));
                holder.distance.setTextColor(Color.parseColor("#FFFF00"));
                break;
            case "fuel":
                holder.name.setTextColor(Color.parseColor("#008000"));
                holder.distance.setTextColor(Color.parseColor("#008000"));
                break;
            case "other":
                holder.name.setTextColor(Color.parseColor("#ff0000"));
                holder.distance.setTextColor(Color.parseColor("#ff0000"));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return objectList.size();
    }

    public interface ItemClickListener {
        void onItemClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView distance;
        private final TextView name;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.object_name);
            distance = itemView.findViewById(R.id.distance_to_object);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            listener.onItemClick(position);
        }
    }
}
