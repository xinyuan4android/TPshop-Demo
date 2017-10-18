package com.tpshop.mallc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.tpshop.mallc.activity.common.SPBaseActivity;

import com.tpshop.mallc.global.SPMobileApplication;
import com.tpshop.mallc.global.SPSaveData;
import com.soubao.tpshop.utils.SPCommonUtils;


import java.util.ArrayList;

//闪屏图
public class SplashActivity extends SPBaseActivity {

	private Animation animation;
	private View view;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		view = View.inflate(this, R.layout.activity_splash, null);
	}

	@Override
	public void initSubViews() {

	}

	@Override
	public void initData() {

	}

	@Override
	public void initEvent() {

	}

	public void onResume() {
		// 每次显示界面执行into方法
		into();
		super.onResume();
	}

	// 进入主程序的方法
	private void into() {
		if (SPCommonUtils.isNetworkAvaiable(this)) {
			if (SPMobileApplication.MAINANIM) {
				// 设置动画效果是alpha，在anim目录下的anim_main.xml文件中定义动画效果
				animation = AnimationUtils.loadAnimation(this, R.anim.anim_main);
				view.startAnimation(animation);
				animation.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation arg0) {
					}

					@Override
					public void onAnimationRepeat(Animation arg0) {
					}

					@Override
					public void onAnimationEnd(Animation arg0) {
						intoActivity();
					}
				});
			} else {
				intoActivity();
			}
		} else {
			showToast("无可用网络");
			// 如果网络不可用，则弹出对话框，对网络进行设置
			/*new PromptDialog.Builder(this).setTitle("无可用网络")
					.setViewStyle(PromptDialog.VIEW_STYLE_NORMAL)
					.setMessage("是否前往网络设置?")
					.setButton2("设置", new PromptDialog.OnClickListener() {
						@Override
						public void onClick(Dialog dialog, int which) {
							dialog.dismiss();
							NetworkUtil.startNetSettingActivity(MainActivity.this);
						}

					}).setButton1("取消", new PromptDialog.OnClickListener() {

						@Override
						public void onClick(Dialog dialog, int which) {
							dialog.dismiss();
							System.exit(0);
						}
					}).show();*/
		}
	}

	private void intoActivity() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if (SPMobileApplication.WELCOME) {
					if (SPSaveData.getValue(SplashActivity.this , "First", true)) {
						startActivity(new Intent(SplashActivity.this,
								WelcomeActivity.class));
						overridePendingTransition(R.anim.in_from_right,
								R.anim.out_to_left);
						SplashActivity.this.finish();
					} else {
						startActivity(new Intent(SplashActivity.this,
								SPMainActivity.class));
						overridePendingTransition(R.anim.in_from_right,
								R.anim.out_to_left);
						SplashActivity.this.finish();
					}
				} else {
					startActivity(new Intent(SplashActivity.this,
							SPMainActivity.class));
					overridePendingTransition(R.anim.in_from_right,
							R.anim.out_to_left);
					SplashActivity.this.finish();
				}
			}
		}, SPMobileApplication.SPLASHTIME);
	}


}

