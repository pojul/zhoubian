package com.yjyc.zhoubian.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.yjyc.zhoubian.app.BaseApplication;

public class AnimatorUtil {

    public static void startPopAnimator(View view, AnimatorListener animatoviewrListener){
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        float rawX = view.getX();
        float rawY = view.getY();

        float rawWidth = layoutParams.width;
        float rawHeight = layoutParams.height;

        float scalex = BaseApplication.SCREEN_WIDTH *1.0f / rawWidth;
        float scaley = BaseApplication.SCREEN_HEIGHT *1.0f / rawHeight;

        float dscale;
        float dy;
        float dx;
        if(scalex <= scaley){
            dscale = scalex - 1;
            dx =  - view.getX();
            dy = - view.getY() + (scaley - scalex) * rawHeight *0.5f;
        }else{
            dscale = scaley - 1;
            dx =  - view.getX() + (scalex - scaley) * rawWidth *0.5f;
            dy = - view.getY();
        }
        view.setPivotX(0);
        view.setPivotY(0);

        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(380);

        animator.addUpdateListener(animation -> {
            float animatedValue = (float) animation.getAnimatedValue();
            view.setScaleX((1 +  dscale * animatedValue));
            view.setScaleY((1 +  dscale * animatedValue));
            view.setTranslationX((rawX + dx * animatedValue));
            view.setTranslationY((rawY + dy * animatedValue));
            if(animatedValue >= 1){
                if(animatoviewrListener != null){
                    animatoviewrListener.onFinished();
                }
            }
        });
        animator.start();
    }

    public static void startPopObjAnimator(ImageView view, AnimatorListener animatoviewrListener){
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        float rawX = view.getX();
        float rawY = view.getY();

        float rawWidth = layoutParams.width;
        float rawHeight = layoutParams.height;

        float scalex = BaseApplication.SCREEN_WIDTH *1.0f / rawWidth;
        float scaley = BaseApplication.SCREEN_HEIGHT *1.0f / rawHeight;

        float scale;
        float dy;
        float dx;

        /*if(scalex < scaley){
            dx =  - view.getX() + (scalex - scaley) * rawWidth *0.5f;
            dy = - view.getY();
            scale = scaley;
        }else{
            dx =  - view.getX();
            dy = - view.getY() + (scaley - scalex) * rawHeight *0.5f;
            scale = scalex;
        }*/
        dx =  - view.getX();
        dy = - view.getY() + (scaley - scalex) * rawHeight *0.5f;
        scale = scalex;
        view.setPivotX(0);
        view.setPivotY(0);

        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(view, "translationX", (rawX + dx));
        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(view, "translationY", (rawY + dy));
        ObjectAnimator objectAnimator3 = ObjectAnimator.ofFloat(view, "scaleX", scale);
        ObjectAnimator objectAnimator4 = ObjectAnimator.ofFloat(view, "scaleY", scale);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(400);
        animatorSet.play(objectAnimator1).with(objectAnimator2).with(objectAnimator3).with(objectAnimator4);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}
            @Override
            public void onAnimationEnd(Animator animation) {
                if(animatoviewrListener != null){
                    animatoviewrListener.onFinished();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        animatorSet.start();
    }

    public static void startThumbUpAnimator(View view){
        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(view, "translationX", 35);
        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(view, "translationY", -35);
        ObjectAnimator objectAnimator3 = ObjectAnimator.ofFloat(view, "scaleX", 1.5f);
        ObjectAnimator objectAnimator4 = ObjectAnimator.ofFloat(view, "scaleY", 1.5f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(700);
        animatorSet.play(objectAnimator1).with(objectAnimator2).with(objectAnimator3).with(objectAnimator4);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
                view.setTranslationX(0);
                view.setTranslationY(0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        view.setVisibility(View.VISIBLE);
        animatorSet.start();
    }

    public interface AnimatorListener{
        void onFinished();
    }

}
