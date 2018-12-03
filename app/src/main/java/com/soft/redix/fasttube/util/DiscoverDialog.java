package com.soft.redix.fasttube.util;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.soft.redix.fasttube.R;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;


/**
 * Created by Gonzalo on 4/4/2016.
 */
public class DiscoverDialog extends DialogFragment {

    private DiscreteSeekBar seekBar;
    private OnSimpleDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_discover, null);

        String radius = getArguments().getString("radius");

        seekBar = v.findViewById(R.id.seekBar);
        final TextView seekBarValue = v.findViewById(R.id.radiusValue);

        seekBarValue.setText(radius);
        seekBar.setMax(20000);
        seekBar.setProgress(Integer.parseInt(radius));
        seekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                seekBarValue.setText(String.valueOf(seekBar.getProgress()));
            }
        });

        TextView cancelButton = (TextView) v.findViewById(R.id.dialogDiscoverCancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onNegativeButtonClick();
                dismiss();
            }
        });
        TextView okButton = (TextView) v.findViewById(R.id.dialogDiscoverOk);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onPossitiveButtonClick(seekBar.getProgress());
                dismiss();
            }
        });

        builder.setView(v);
        Dialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        return dialog;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            listener = (OnSimpleDialogListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(
                    activity.toString() +
                            " not implemented OnSimpleDialogListener");

        }
    }

    public interface OnSimpleDialogListener {
        void onPossitiveButtonClick(int radius);
        void onNegativeButtonClick();
    }

}
