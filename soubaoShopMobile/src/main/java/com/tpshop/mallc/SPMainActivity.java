/**
 * shopmobile for tpshop
 * ============================================================================
 * 版权所有 2015-2127 深圳搜豹网络科技有限公司，并保留所有权利。
 * 网站地址: http://www.tp-shop.cn
 * ——————————————————————————————————————
 * 这不是一个自由软件！您只能在不用于商业目的的前提下对程序代码进行修改和使用 .
 * 不允许对程序代码以任何形式任何目的的再发布。
 * ============================================================================
 * Author: 飞龙  wangqh01292@163.com
 * Date: @date 2015-10-15 20:32:41
 * Description: 商城主界面Activity (底部包含四个tab item)
 * @version V1.0
 */
package com.tpshop.mallc;

import com.loopj.android.http.RequestParams;
import com.tpshop.mallc.activity.common.SPBaseActivity;

import com.tpshop.mallc.common.SPDataAsyncManager;
import com.tpshop.mallc.common.SPMobileConstants;
import com.tpshop.mallc.dao.SPPersonDao;
import com.tpshop.mallc.fragment.SPBaseFragment;
import com.tpshop.mallc.fragment.SPCategoryFragment;
import com.tpshop.mallc.fragment.SPHomeFragment;
import com.tpshop.mallc.fragment.SPPersonFragment;
import com.tpshop.mallc.fragment.SPShopCartFragment;
import com.tpshop.mallc.global.SPSaveData;
import com.tpshop.mallc.http.base.SPDownListener;
import com.tpshop.mallc.http.base.SPFailuredListener;
import com.tpshop.mallc.http.base.SPMobileHttptRequest;
import com.tpshop.mallc.http.base.SPSuccessListener;
import com.tpshop.mallc.http.home.SPHomeRequest;
import com.tpshop.mallc.http.shop.SPShopRequest;
import com.tpshop.mallc.model.AppVersionInfo;
import com.tpshop.mallc.model.person.SPRegionModel;

import com.tpshop.mallc.utils.SMobileLog;
import com.tpshop.mallc.utils.SPDialogUtils;
import com.tpshop.mallc.utils.SPUtils;
import com.tpshop.mallc.view.NumberProgressBar;
import com.tpshop.mallc.view.OnProgressBarListener;
import com.umeng.analytics.MobclickAgent;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PortUnreachableException;
import java.net.URL;
import java.util.List;

public class SPMainActivity extends SPBaseActivity implements OnProgressBarListener {
	
	private String TAG = "SPMainActivity";

	List<SPRegionModel> mRegionModels;

	public static final String SELECT_INDEX = "selectIndex";
	public static final String CACHE_SELECT_INDEX = "cacheSelectIndex";
	public static final int INDEX_HOME = 0;
	public static final int INDEX_CATEGORY = 1;
	public static final int INDEX_SHOPCART = 2;
	public static final int INDEX_PERSON = 3;

	public int mCurrentSelectIndex  ;

	FragmentManager mFragmentManager ;
	SPHomeFragment mHomeFragment ;
	SPCategoryFragment mCategoryFragment ;
	SPShopCartFragment mShopCartFragment ;
	SPPersonFragment mPersonFragment ;
	
	RadioGroup mRadioGroup;
	RadioButton rbtnHome;
	RadioButton rbtnCategory;
	RadioButton rbtnShopcart;
	RadioButton rbtnPerson;
	
	RadioButton mCurrRb;
	RadioButton mLastRb;
	AppVersionInfo mVersionInfo;
	private long exitTime = 0;

	private static SPMainActivity mInstance;

	public static boolean isForeground = false;

	public String mApkFilePath ; //Apk文件存储路径
	private Notification.Builder mBuilder;
	public static final int NOTIFICATION_ID = 200; // 通知唯一id

	public Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
				case SPMobileConstants.MSG_CODE_LOAD_DATAE_CHANGE:
					if (msg.obj !=null){
						mRegionModels = (List<SPRegionModel>)msg.obj ;
						SaveAddressTask task = new SaveAddressTask();
						task.execute();
					}
					break;
				case SPMobileConstants.MSG_CODE_SHOW:
					if (msg.obj!=null){
						SPDialogUtils.showToast(SPMainActivity.this , msg.obj.toString());
					}
					break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.setCustomerTitle(false, false, getString(R.string.title_home));
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mFragmentManager = this.getSupportFragmentManager();
		super.init();
		addFragment();
		hiddenFragment();

		if (savedInstanceState!=null){
			mCurrentSelectIndex = savedInstanceState.getInt(CACHE_SELECT_INDEX , INDEX_HOME);
		}else{
			mCurrentSelectIndex = INDEX_HOME;
		}
		setSelectIndex(mCurrentSelectIndex);
		mInstance = this;

		/** 友盟第三方登录 android6.0 适配 **/
		if(Build.VERSION.SDK_INT>=23){
			String[] mPermissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.CALL_PHONE,Manifest.permission.READ_LOGS,Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.SET_DEBUG_APP,Manifest.permission.SYSTEM_ALERT_WINDOW,Manifest.permission.GET_ACCOUNTS,Manifest.permission.WRITE_APN_SETTINGS};
			ActivityCompat.requestPermissions(this,mPermissionList,123);
		}
	}

	@Override
	public void initSubViews() {
		mHomeFragment = new SPHomeFragment();
		mCategoryFragment = new SPCategoryFragment();
		mShopCartFragment = new SPShopCartFragment();
		mPersonFragment = new SPPersonFragment();
		mHomeFragment.setMainActivity(this);

		mRadioGroup = (RadioGroup) this.findViewById(R.id.radioGroup);
		rbtnHome = (RadioButton) this.findViewById(R.id.rbtn_home);
		rbtnCategory = (RadioButton) this.findViewById(R.id.rbtn_category);
		rbtnShopcart = (RadioButton) this.findViewById(R.id.rbtn_shopcart);
		rbtnPerson = (RadioButton) this.findViewById(R.id.rbtn_mine);

		LayoutInflater inflater = LayoutInflater.from(getApplication());
		final View mFloatLayout = inflater.inflate(R.layout.activity_notification, null);
		rl_notification = (RelativeLayout) mFloatLayout.findViewById(R.id.rl_notification);
		pb_download2 = (NumberProgressBar) mFloatLayout.findViewById(R.id.pb_download);
	}

	@Override
	public void initData() {
		//同步数据
		SPDataAsyncManager.getInstance(this , mHandler).startSyncData(new SPDataAsyncManager.SyncListener() {
			@Override
			public void onPreLoad() {

			}

			@Override
			public void onLoading() {

			}

			@Override
			public void onFinish() {

			}

			@Override
			public void onFailure(String error) {

			}
		});

		try{
			String extPath = SPUtils.getExtSDCardPaths();
			if(extPath == null){
				showToast("无非访问SD卡, APK无法下载!");
				return;
			}
			String pkgName = this.getPackageName();
			String apkName = "new_apk.apk";
			File apkDir = new File(extPath , pkgName);
			if(!apkDir.exists()){
				apkDir.mkdirs();//创建目录
			}
			File apkFile = new File(apkDir , apkName);
			mApkFilePath = apkFile.getPath();

		}catch(Exception e){

		}
		checkVersion();
	}

	@Override
	public void initEvent() {
		///Log.i(TAG, "initEvent...");
		mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup arg0, int key) {
				switch (key) {
					case R.id.rbtn_home:
						setSelectIndex(INDEX_HOME);
						break;
					case R.id.rbtn_category:
						setSelectIndex(INDEX_CATEGORY);
						break;
					case R.id.rbtn_shopcart:
						setSelectIndex(INDEX_SHOPCART);
						break;
					case R.id.rbtn_mine:
						setSelectIndex(INDEX_PERSON);
						break;
					default:
						break;
				}
			}
		});


	}

	public void setSelectIndex(int index){
		switch (index){
			case INDEX_HOME:
				//setTitleType(TITLE_HOME);
				showFragment(mHomeFragment);
				changeTabtextSelector(rbtnHome);
				setTitle(getString(R.string.title_home));
				mCurrentSelectIndex = INDEX_HOME;
				break;
			case INDEX_CATEGORY:
				//setTitleType(TITLE_CATEGORY);
				showFragment(mCategoryFragment);
				changeTabtextSelector(rbtnCategory);
				setTitle(getString(R.string.tab_item_category));
				mCurrentSelectIndex = INDEX_CATEGORY;
				break;
			case INDEX_SHOPCART:
				//setTitleType(TITLE_DEFAULT);
				showFragment(mShopCartFragment);
				changeTabtextSelector(rbtnShopcart);
				setTitle(getString(R.string.tab_item_shopcart));
				mCurrentSelectIndex = INDEX_SHOPCART;
				break;
			case INDEX_PERSON:
				showFragment(mPersonFragment);
				changeTabtextSelector(rbtnPerson);
				setTitle(getString(R.string.tab_item_mine));
				mCurrentSelectIndex = INDEX_PERSON;
				break;
		}
	}

	/**
	 * 
	 * @Title: showFragment
	 * @Description: 
	 * @param: @param fragment
	 * @return: void
	 * @throws
	 */
	private void showFragment(SPBaseFragment fragment) {
		hiddenFragment();
		FragmentTransaction mTransaction = mFragmentManager.beginTransaction();
		mTransaction.show(fragment);
		mTransaction.commitAllowingStateLoss();

	}
	//add by zzx
	public void setShowFragment(int flag){
		if(flag == SPHomeFragment.CATEGORY_FRAGMENT){
			//setTitleType(TITLE_CATEGORY);
			showFragment(mCategoryFragment);
			changeTabtextSelector(rbtnCategory);
			setTitle(getString(R.string.tab_item_category));
			rbtnCategory.setChecked(true);
		}else if(flag == SPHomeFragment.SHOPCART_FRAGMENT){
			//setTitleType(TITLE_DEFAULT);
			showFragment(mShopCartFragment);
			changeTabtextSelector(rbtnShopcart);
			setTitle(getString(R.string.tab_item_shopcart));
			rbtnShopcart.setChecked(true);
		}
	}

	/**
	 * 
	 * @Title: hiddenFragment
	 * @Description:
	 * @param:
	 * @return: void
	 * @throws
	 */
	private void hiddenFragment() {
		FragmentTransaction mTransaction = mFragmentManager.beginTransaction();
		mTransaction.hide(mHomeFragment);
		mTransaction.hide(mCategoryFragment);
		mTransaction.hide(mShopCartFragment);
		mTransaction.hide(mPersonFragment);
		mTransaction.commitAllowingStateLoss();
	}
	
	/**
	 * 
	 * @Title: addFragment
	 * @Description:
	 * @param:
	 * @return: void
	 * @throws
	 */

	private void addFragment() {

		FragmentTransaction mTransaction = mFragmentManager.beginTransaction();
		mTransaction.add(R.id.fragmentView, mHomeFragment);
		mTransaction.add(R.id.fragmentView, mCategoryFragment);
		mTransaction.add(R.id.fragmentView, mShopCartFragment);
		mTransaction.add(R.id.fragmentView, mPersonFragment);
		mTransaction.commitAllowingStateLoss();
	}
	
	public void changeTabtextSelector(RadioButton rb){
		
		mLastRb = mCurrRb;
		mCurrRb = rb;
		
		if(mLastRb != null){
			mLastRb.setTextColor(getResources().getColor(R.color.color_tab_item_normal));
			mLastRb.setSelected(false);
		}
		
		if(mCurrRb != null){
			mCurrRb.setTextColor(getResources().getColor(R.color.color_tab_item_fous));
			mCurrRb.setChecked(true);
		}
	}


	private class SaveAddressTask extends AsyncTask<URL, Integer, Long>{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Long aLong) {
			super.onPostExecute(aLong);

		}

		@Override
		protected Long doInBackground(URL... params) {


			SPPersonDao personDao = SPPersonDao.getInstance(SPMainActivity.this);
			personDao.insertRegionList(mRegionModels);
			SPSaveData.putValue(SPMainActivity.this, SPMobileConstants.KEY_IS_FIRST_STARTUP, false);

			return null;

		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		int selectIndex = -1;
		if (getIntent()!=null && getIntent().hasExtra(SELECT_INDEX)){
			selectIndex = getIntent().getIntExtra(SELECT_INDEX, -1);
			getIntent().putExtra(SELECT_INDEX , -1);//清除缓存
			if (selectIndex!= -1)setSelectIndex(selectIndex);
		}

	}

	@Override
	protected void onStart() {
		super.onStart();
		SPShopRequest.refreshServiceTime(new SPSuccessListener() {
			@Override
			public void onRespone(String msg, Object response) {

			}
		}, new SPFailuredListener() {
			@Override
			public void onRespone(String msg, int errorCode) {

			}
		});
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(CACHE_SELECT_INDEX, mCurrentSelectIndex);
	}

	@Override
	public void onBackPressed() {

		if(pb_download!=null)pb_download.setVisibility(View.GONE);// 隐藏进度条
		if(rl_notification!=null)rl_notification.setVisibility(View.GONE);

		if(mCurrRb == rbtnHome ){

			if((System.currentTimeMillis()-exitTime) > 2000){
				Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
			}
		}else{
			setSelectIndex(INDEX_HOME);
		}
	}

	public static SPMainActivity getmInstance(){
		return mInstance;
	}

	@Override
	protected void onResume() {
		isForeground = true;
		super.onResume();

		MobclickAgent.onResume(this);          //统计时长
	}


	@Override
	protected void onPause() {
		isForeground = false;
		super.onPause();
		MobclickAgent.onPause(this);
	}

	public void checkVersion(){

		String version = getVersion();
		SPHomeRequest.checkAppUpdate(version, new SPSuccessListener() {
			@Override
			public void onRespone(String msg, Object response) {
				if(response != null){
					mVersionInfo = (AppVersionInfo)response;
					if (mVersionInfo.getIsNeedUpdate() == 1){
						//发现新版本
						showUpdateDialog();
					}
				}
			}
		}, new SPFailuredListener() {
			@Override
			public void onRespone(String msg, int errorCode) {

			}
		});
	}

	public String getVersion(){
		//获取软件版本
		// 获取自己的版本信息
		PackageManager pm = getPackageManager();
		try {
			PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
			// 版本名
			String versionName = packageInfo.versionName;
			return versionName;

		} catch (PackageManager.NameNotFoundException e) {
			// can not reach 异常不会发生
		}
		return "0";
	}
	AlertDialog dialog;
	NumberProgressBar pb_download;
	private NumberProgressBar pb_download2;
	private RelativeLayout rl_notification;

	/**
	* 显示是否更新新版本的对话框
	*/
	public void showUpdateDialog() {
		dialog = new AlertDialog.Builder(this).create();
		dialog.setCancelable(false);
		dialog.show();
		Window window = dialog.getWindow();
		window.setContentView(R.layout.prompt_alertdialog);
		LinearLayout ll_title = (LinearLayout) window
				.findViewById(R.id.ll_title);
		ll_title.setVisibility(View.VISIBLE);
		TextView tv_title = (TextView) window.findViewById(R.id.tv_title);
		pb_download = (NumberProgressBar) window
				.findViewById(R.id.pb_splash_download);
		pb_download.setVisibility(View.GONE);// 隐藏进度条
		pb_download.setOnProgressBarListener(this);
		tv_title.setText("版本更新");
		TextView tv_content = (TextView) window.findViewById(R.id.tv_content);
		tv_content.setMovementMethod(new ScrollingMovementMethod());
		tv_content.setText(mVersionInfo.getUpdateLog());
		final TextView tv_sure = (TextView) window.findViewById(R.id.tv_sure);
		final TextView tv_cancle = (TextView) window.findViewById(R.id.tv_cancle);
		tv_cancle.setText("取消");
		tv_cancle.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.cancel();
			}
		});
		tv_sure.setText("更新");
		tv_sure.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				downLoadApk();// 下载新版本
				createFloatView();
				tv_cancle.setEnabled(false);
				tv_sure.setEnabled(false);
				dialog.cancel();
				pb_download.setVisibility(View.VISIBLE);
			}
		});
	}

	private void createFloatView() {
		final WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
		getApplication();
		final WindowManager mWindowManager = (WindowManager) getApplication()
				.getSystemService(Context.WINDOW_SERVICE);
		wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
		wmParams.format = PixelFormat.RGBA_8888;
		wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		wmParams.gravity = Gravity.CENTER | Gravity.CENTER;
		wmParams.x = 0;
		wmParams.y = 0;
		wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		LayoutInflater inflater = LayoutInflater.from(getApplication());
		final View mFloatLayout = inflater.inflate(
				R.layout.activity_notification, null);
		mWindowManager.addView(mFloatLayout, wmParams);
		rl_notification = (RelativeLayout) mFloatLayout
				.findViewById(R.id.rl_notification);
		pb_download2 = (NumberProgressBar) mFloatLayout
				.findViewById(R.id.pb_download);

		mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
				.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

		/*rl_notification.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				wmParams.x = (int) event.getRawX()
						- rl_notification.getMeasuredWidth() / 2;
				wmParams.y = (int) event.getRawY()
						- rl_notification.getMeasuredHeight() / 2 - 25;
				mWindowManager.updateViewLayout(mFloatLayout, wmParams);
				return false;
			}
		});*/
	}

	/**
	 * 下载APK文件
	 */
	public void downLoadApk(){

		SPHomeRequest.downApk(mVersionInfo.getApkUrl(), new SPDownListener() {
			@Override
			public void onRespone(int statusCode, byte[] binaryData) {

				try{

					SMobileLog.e(TAG, "downApk onRespone : " + "共下载了：" + binaryData.length);
					File apkFile = new File(mApkFilePath);
					if(apkFile.exists()){
						apkFile.delete();
					}else{
						apkFile.createNewFile();
					}

					//SMobileLog.i(TAG, "download file: " + apkFile.getName());
					FileOutputStream fos = new FileOutputStream(apkFile);
					BufferedOutputStream bfw = new BufferedOutputStream(fos);
					bfw.write(binaryData);
					bfw.flush();
					fos.flush();

					if(bfw != null){
						bfw.close();
					}
					if(fos != null) {
						fos.close();
					}

					installApk();// 安装apk
					pb_download.setVisibility(View.GONE);// 隐藏进度条
					rl_notification.setVisibility(View.GONE);

				}catch (Exception e){
					String msg = e.getMessage();
					SMobileLog.e(TAG , msg);
				}
			}

			@Override
			public void onProgress(long currSize, long totalSize) {
				pb_download.setVisibility(View.VISIBLE);// 设置进度的显示
				int max = (int) totalSize;
				int progress = (int) currSize;
				pb_download.setMax(max);// 设置进度条的最大值
				pb_download.setProgress(progress);// 设置当前进度
				pb_download2.setMax(max);
				pb_download2.setProgress(progress);

				showNotifi(max, progress);
			}
		}, new SPFailuredListener() {
			@Override
			public void onRespone(String msg, int errorCode) {
				showToast(msg);
			}
		});
	}

	private void showNotifi(final int total, final int current) {

		NotificationManager notiManage;
		Notification note;
		mBuilder = new Notification.Builder(this);
		notiManage = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		note = new Notification();
		note.flags = Notification.FLAG_AUTO_CANCEL;
		RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.activity_notifi);

		contentView.setTextViewText(R.id.notificationTitle, getString(R.string.app_name));
		String str_progress =current*100/total+"%";
		contentView.setTextViewText(R.id.notificationPercent, str_progress);
		contentView.setProgressBar(R.id.notificationProgress, total, current,
				false);
		note.contentView = contentView;
		note.tickerText = "正在下载";
		note.icon = R.drawable.appicon;

		PendingIntent p = PendingIntent.getActivity(this, 0, new Intent(Intent.ACTION_VIEW), 0);// 这个非要不可。
		note.contentIntent = p;
		notiManage.notify(NOTIFICATION_ID, note);
		if (current / total == 1) {
			mBuilder.setContentText("下载完毕");
			notiManage.cancelAll();
		}
	}

	/**
	 * 安装下载的新版本
	 */
	protected void installApk() {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		String type = "application/vnd.android.package-archive";
		Uri data = Uri.fromFile(new File(mApkFilePath));
		intent.setDataAndType(data, type);
		startActivityForResult(intent, 0);
	}

	@Override
	public void onProgressChange(int current, int max) {
		pb_download.setProgress(current);
		pb_download2.setProgress(current);
	}

	@Override
	protected void onDestroy() {

		if(pb_download!=null)pb_download.setVisibility(View.GONE);// 隐藏进度条
		if(rl_notification!=null)rl_notification.setVisibility(View.GONE);

		super.onDestroy();
	}
}
