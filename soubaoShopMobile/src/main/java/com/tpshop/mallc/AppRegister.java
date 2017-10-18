package com.tpshop.mallc;

import com.tpshop.mallc.common.SPMobileConstants;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AppRegister extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		final IWXAPI msgApi = WXAPIFactory.createWXAPI(context, null);
		msgApi.registerApp(SPMobileConstants.pluginLoginWeixinAppid);
		//http://bbs.csdn.net/topics/391080018
	}
}
