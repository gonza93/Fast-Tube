package com.soft.redix.fasttube.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.soft.redix.fasttube.R;



/**
 * Created by Gonzalo on 2/4/2016.
 */
public class LocationVideoHeaderAdapter extends RecyclerView.Adapter<LocationVideoHeaderAdapter.SimpleViewHolder>
        implements ItemClickListener {

    private final Context context;


    public static class SimpleViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        // Campos respectivos de un item
        public ItemClickListener listener;

        public SimpleViewHolder(View v, ItemClickListener listener) {
            super(v);
            this.listener = listener;
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(v, getAdapterPosition());
        }
    }

    public LocationVideoHeaderAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.header_location_videos, viewGroup, false);
        return new SimpleViewHolder(v, this);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder viewHolder, int i) {

    }

    /**
     * Sobrescritura del método de la interfaz {@link ItemClickListener}
     *
     * @param view     item actual
     * @param position posición del item actual
     */
    @Override
    public void onItemClick(View view, final int position) {

    }

}
