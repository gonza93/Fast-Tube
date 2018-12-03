package com.soft.redix.fasttube.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.soft.redix.fasttube.R;
import com.wang.avi.AVLoadingIndicatorView;

/**
 * Created by gonzalo on 2/7/17.
 */

public class ProgressViewHolder extends RecyclerView.ViewHolder {

    public AVLoadingIndicatorView progress;

    public ProgressViewHolder(View v) {

        super(v);
        progress = (AVLoadingIndicatorView) v.findViewById(R.id.progress);
    }

}
