package com.example.sharewheel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StateAdapter extends RecyclerView.Adapter<StateAdapter.StateViewHolder> {
    private List<String> stateList;

    public StateAdapter(List<String> stateList) {
        this.stateList = stateList;
    }

    @NonNull
    @Override
    public StateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new StateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StateViewHolder holder, int position) {
        holder.bind(stateList.get(position));
    }

    @Override
    public int getItemCount() {
        return stateList.size();
    }

    public void updateList(List<String> newList) {
        this.stateList = newList;
        notifyDataSetChanged();
    }

    static class StateViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public StateViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }

        public void bind(String stateName) {
            textView.setText(stateName);
        }
    }
}

