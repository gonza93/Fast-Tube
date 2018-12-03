package com.soft.redix.fasttube.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.soft.redix.fasttube.R;
import com.squareup.picasso.Picasso;

/**
 * Created by Gonzalo on 27/3/2016.
 */
public class PlaylistDialog extends DialogFragment {

    private View v;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        String title = getArguments().getString("playlist_title");
        String channelTitle = getArguments().getString("channel_title");
        String videoCount = getArguments().getString("video_count");
        String thumb = getArguments().getString("playlist_thumb");

        LayoutInflater inflater = getActivity().getLayoutInflater();
        v = inflater.inflate(R.layout.dialog_playlist, null);

        TextView playlistChannelTitle = (TextView) v.findViewById(R.id.playlistDialogChannelTitle);
        playlistChannelTitle.setText(channelTitle);
        TextView playlistTitle = (TextView) v.findViewById(R.id.playlistDialogTitle);
        playlistTitle.setText(title);
        TextView playlistVideoCount = (TextView) v.findViewById(R.id.playlistDialogVideos);
        playlistVideoCount.setText(videoCount + " videos");
        ImageView thumbnail = (ImageView) v.findViewById(R.id.playlistDialogThumb);
        Picasso.get()
                .load(thumb)
                .fit().centerCrop()
                .into(thumbnail);


        builder.setView(v);

        return builder.create();
    }
}
