package com.tpshop.mallc.wxapi;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.utils.Log;
import com.umeng.socialize.weixin.view.WXCallbackActivity;

import java.util.Map;


public class WXEntryActivity extends WXCallbackActivity {

    @Override
    protected void handleIntent(Intent intent){

        mWxHandler.setAuthListener(new UMAuthListener() {
            @Override
            public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
                Log.e("UMWXHandler onComplete");
            }

            @Override
            public void onError(SHARE_MEDIA platform, int action, Throwable t) {
                Log.e("UMWXHandler onError "+t.getMessage());
            }

            @Override
            public void onCancel(SHARE_MEDIA platform, int action) {
                Log.e("UMWXHandler onCancel "+action);
            }
        });
        super.handleIntent(intent);
    }

}
