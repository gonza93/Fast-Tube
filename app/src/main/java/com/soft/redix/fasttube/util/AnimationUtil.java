package com.soft.redix.fasttube.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Build;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;

/**
 * Created by Gonzalo Cham on 20/7/17.
 */

public class AnimationUtil {

    public static void expand(final View v) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration(300);
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration(300);
        v.startAnimation(a);
    }

    public static void rotateView(View v, float degree) {
        final RotateAnimation rotateAnim = new RotateAnimation(0.0f, degree,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        rotateAnim.setDuration(500);
        rotateAnim.setFillAfter(true);
        v.startAnimation(rotateAnim);
    }

    /** Efecto para revelar los items de hoja de ruta/buscador de forma circular **/
    public static void enterReveal(View myView) {
        // get the center for the clipping circle
        int cx = myView.getMeasuredWidth() / 2;
        int cy = myView.getMeasuredHeight() / 2;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(myView.getWidth(), myView.getHeight()) / 2;

        // create the animator for this view (the start radius is zero)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Animator anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);
            myView.setVisibility(View.VISIBLE);
            anim.start();
        }
        else{
            myView.setVisibility(View.VISIBLE);
        }
    }

    /** Efecto para ocultar el buscador de forma circular **/
    public static void hideReveal(final View view) {
        int cx = view.getMeasuredWidth() / 2;
        int cy = view.getMeasuredHeight() / 2;

        int radius = Math.max(view.getWidth(), view.getHeight()) / 2;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, radius, 0);
            anim.setDuration(500);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    view.setVisibility(View.INVISIBLE);
                }
            });
            anim.start();
        } else
            view.setVisibility(View.INVISIBLE);
    }

    public static void translateView(final View viewToTranslate, final View referenceView,
                                     final View flagText, final View flagView  ,int duration){
        TranslateAnimation animation = new TranslateAnimation(0, referenceView.getX() - viewToTranslate.getX(),
                                                                0, referenceView.getY()- viewToTranslate.getY());
        animation.setRepeatMode(0);
        animation.setDuration(duration);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                viewToTranslate.setVisibility(View.VISIBLE);
                flagText.setVisibility(View.INVISIBLE);
                flagView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                viewToTranslate.setVisibility(View.INVISIBLE);
                enterReveal(flagText);
                enterReveal(flagView);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        viewToTranslate.startAnimation(animation);
    }

    public static void fadeIn(final View view, int duration){
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(duration);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(fadeIn);
    }

    public static void fadeOut(final View view, int duration){
        Animation fadeIn = new AlphaAnimation(1, 0);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(duration);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(fadeIn);
    }

}
