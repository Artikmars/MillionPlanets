package com.artamonov.millionplanets.modules;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.artamonov.millionplanets.R;
import com.artamonov.millionplanets.model.Module;
import com.artamonov.millionplanets.model.Weapon;
import java.util.List;

public class ModulesAdapter extends RecyclerView.Adapter<ModulesAdapter.ViewHolder> {
    private static ItemClickListener listener;
    private List<Module> moduleList;
    private Context context;
    private List<Weapon> existedItem;

    ModulesAdapter(
            List<Module> moduleList,
            List<Weapon> existedItem,
            Context context,
            ItemClickListener itemClickListener) {
        listener = itemClickListener;
        this.context = context;
        this.moduleList = moduleList;
        this.existedItem = existedItem;
    }

    @NonNull
    @Override
    public ModulesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.modules_items, parent, false);
        return new ModulesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ModulesAdapter.ViewHolder holder, int position) {
        //        if (position == 3 || position == 4) {
        //            holder.modulesPrice.setEnabled(false);
        //            holder.modulesName.setEnabled(false);
        //
        // holder.modulesPrice.setBackgroundColor(context.getResources().getColor(R.color.grey));
        //
        // holder.modulesName.setBackgroundColor(context.getResources().getColor(R.color.grey));
        //        }
        holder.modulesName.setText(moduleList.get(position).getName());
        holder.modulesPrice.setText(Integer.toString(moduleList.get(position).getPrice()));

        if (existedItem.contains((long) position)) {
            holder.modulesName.setTextColor(context.getResources().getColor(R.color.yellow));
            holder.modulesPrice.setTextColor(context.getResources().getColor(R.color.yellow));
        }
    }

    @Override
    public int getItemCount() {
        return moduleList.size();
    }

    public interface ItemClickListener {
        void onItemClick(int position);
    }

    public interface DialogListener {
        void onDialogCreate();
        //  void onDialogSubmit();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView modulesName;
        private final TextView modulesPrice;

        ViewHolder(View itemView) {
            super(itemView);
            modulesName = itemView.findViewById(R.id.modules_name);
            modulesPrice = itemView.findViewById(R.id.modules_price);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            listener.onItemClick(position);
        }
    }
}
