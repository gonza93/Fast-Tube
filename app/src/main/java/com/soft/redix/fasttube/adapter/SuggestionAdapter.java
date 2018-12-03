package com.soft.redix.fasttube.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.soft.redix.fasttube.R;
import com.soft.redix.fasttube.activity.MainActivity;

import java.util.List;

/**
 * Created by gonzalo on 15/10/17.
 */

public class SuggestionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements ItemClickListener {

    private Activity activity;
    private List<String> items;

    public static class SimpleViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        // Campos respectivos de un item
        public TextView textSuggestion;
        public ItemClickListener listener;

        public SimpleViewHolder(View v, ItemClickListener listener) {
            super(v);
            textSuggestion = (TextView) v.findViewById(R.id.list_text_suggestion);
            v.setOnClickListener(this);
            this.listener = listener;
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(v, getAdapterPosition());
        }
    }

    public SuggestionAdapter(Activity activity, List<String> items) {
        this.activity = activity;
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_suggestion, viewGroup, false);
        return new SimpleViewHolder(v, this);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        String suggestion = items.get(position);

        SimpleViewHolder holder = (SimpleViewHolder) viewHolder;
        holder.textSuggestion.setText(suggestion);
    }

    @Override
    public void onItemClick(View view, final int position) {
        activity.findViewById(R.id.search_suggestions).setVisibility(View.GONE);

        String selectedItem = items.get(position);
        EditText searchBar = (EditText) activity.findViewById(R.id.main_search);
        searchBar.setText(selectedItem);
        searchBar.setSelection(selectedItem.length());

        MainActivity mainActivity = (MainActivity) activity;
        InputMethodManager imm = (InputMethodManager) mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchBar.getWindowToken(), 0);
        mainActivity.query = searchBar.getText().toString();
        mainActivity.search();

    }

}
