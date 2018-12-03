package com.soft.redix.fasttube.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.soft.redix.fasttube.R;

/**
 * Created by gonzalo on 13/7/17.
 */

public class ProgressAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public class SimpleViewHolder extends RecyclerView.ViewHolder{

        public SimpleViewHolder(View itemView) {
            super(itemView);
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_videoinfo_progress, parent, false);
        return new SimpleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SimpleViewHolder viewHolder = (SimpleViewHolder) holder;
        viewHolder.itemView.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return 1;
    }


}
