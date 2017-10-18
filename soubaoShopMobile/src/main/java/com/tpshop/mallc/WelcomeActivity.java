package com.tpshop.mallc;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.tpshop.mallc.adapter.SPWelcomePagerAdapter;
import com.tpshop.mallc.global.SPMobileApplication;
import com.tpshop.mallc.global.SPSaveData;

//第一次运行的引导页代码
public class WelcomeActivity extends Activity implements
		OnPageChangeListener, OnClickListener {
	private Animation shakeAnim;
	private ViewPager viewPager;
	private PagerAdapter pagerAdapter;
	private ImageButton startButton;
	private LinearLayout indicatorLayout;
	private ArrayList<View> views;
	private ImageView[] indicators = null;
	private int[] images;

	private int page=0;
	private int pageState;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_welcome);
		images = SPMobileApplication.IMAGES;
		initView();
	}

	// 初始化视图
	private void initView() {
		// 实例化视图控件
		viewPager = (ViewPager) findViewById(R.id.viewpage);
		startButton = (ImageButton) findViewById(R.id.start_Button);
		shakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake_x);
		shakeAnim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation arg0) {
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
			}

			// 动画结束自动进入
			@Override
			public void onAnimationEnd(Animation animation) {
				if (page == indicators.length - 1) {
					into();
				}
			}
		});
		startButton.setOnClickListener(this);
		indicatorLayout = (LinearLayout) findViewById(R.id.indicator);
		views = new ArrayList<View>();
		indicators = new ImageView[images.length]; // 定义指示器数组大小
		for (int i = 0; i < images.length; i++) {
			// 循环加入图片
			ImageView imageView = new ImageView(this);
			imageView.setBackgroundResource(images[i]);
			views.add(imageView);
			// 循环加入指示器
			indicators[i] = new ImageView(this);

			if (i == 0) {
				indicators[i].setBackgroundResource(R.drawable.ic_indicators_now);
			} else {
				indicators[i]
						.setBackgroundResource(R.drawable.ic_indicators_default);
				LayoutParams layoutParams = new LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				layoutParams.setMargins(15, 0, 0, 0);
				indicators[i].setLayoutParams(layoutParams);
			}
			indicatorLayout.addView(indicators[i]);
		}
		pagerAdapter = new SPWelcomePagerAdapter(views);
		viewPager.setAdapter(pagerAdapter); // 设置适配器
		viewPager.setOnPageChangeListener(this);
	}

	// 按钮的点击事件
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.start_Button) {
			into();
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		pageState = arg0;
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		if (arg0 == images.length - 1) {
			// 已经在最后一页还想往右划
			if (arg2 == 0 && pageState == 1) {
				pageState = 0;
				into();
			}
		}
	}

	// 监听viewpage
	@Override
	public void onPageSelected(int arg0) {
		page = arg0;
		// 显示最后一个图片时显示按钮
		if (arg0 == indicators.length - 1) {
			startButton.setVisibility(View.VISIBLE);
			startButton.startAnimation(shakeAnim);
		} else {
			startButton.setVisibility(View.INVISIBLE);
			startButton.clearAnimation();
		}
		// 更改指示器图片
		for (int i = 0; i < indicators.length; i++) {
			indicators[arg0].setBackgroundResource(R.drawable.ic_indicators_now);
			if (arg0 != i) {
				indicators[i].setBackgroundResource(R.drawable.ic_indicators_default);
			}
		}
	}

	public void into() {
		SPSaveData.putValue(this , "First" , false);
		startActivity(new Intent(WelcomeActivity.this, SPMainActivity.class));
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		this.finish();
	}
}

