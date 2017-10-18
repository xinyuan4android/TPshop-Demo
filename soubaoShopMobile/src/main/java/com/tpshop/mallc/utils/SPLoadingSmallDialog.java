package com.tpshop.mallc.utils;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tpshop.mallc.R;

/**
 * Created by admin on 2016/6/20.
 */
public class SPLoadingSmallDialog extends Dialog {

    ImageView iamgeView;

    public SPLoadingSmallDialog(Context context) {
        super(context, R.style.loadingDialogStyle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_small_dialog);
        iamgeView = (ImageView)findViewById(R.id.loading_imgv);
        Animation operatingAnim = AnimationUtils.loadAnimation(getContext(), R.anim.loading_small_rotate);
        iamgeView.setAnimation(operatingAnim);
    }

    @Override
    protected void onStop() {
        super.onStop();
        iamgeView.clearAnimation();
    }


}
