/**
 * shopmobile for tpshop
 * ============================================================================
 * 版权所有 2015-2099 深圳搜豹网络科技有限公司，并保留所有权利。
 * 网站地址: http://www.tp-shop.cn
 * ——————————————————————————————————————
 * 这不是一个自由软件！您只能在不用于商业目的的前提下对程序代码进行修改和使用 .
 * 不允许对程序代码以任何形式任何目的的再发布。
 * ============================================================================
 * Author: 飞龙  wangqh01292@163.com
 * Date: @date 2015年10月28日 下午8:10:34 
 * Description:Application
 * @version V1.0
 */
package com.tpshop.mallc.global;

import com.tpshop.mallc.R;
import com.tpshop.mallc.common.SPMobileConstants;
import com.tpshop.mallc.http.base.SPMobileHttptRequest;
import com.tpshop.mallc.model.SPCategory;
import com.tpshop.mallc.model.SPPlugin;
import com.tpshop.mallc.model.SPServiceConfig;
import com.tpshop.mallc.model.order.SPOrder;
import com.tpshop.mallc.model.person.SPUser;
import com.tpshop.mallc.model.shop.SPCollect;
import com.soubao.tpshop.utils.SPMyFileTool;
import com.tpshop.mallc.utils.SPPluginUtils;
import com.soubao.tpshop.utils.SPStringUtils;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.PlatformConfig;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * @author 飞龙
 *
 */
public class SPMobileApplication extends Application {

	private static SPMobileApplication instance ;

	public List<SPCollect> goodsCollects;	//商品收藏


	public boolean isLogined ;
	private SPUser loginUser ;
	private String deviceId ;
	private String id ;
	DisplayMetrics mDisplayMetrics;
	public JSONObject json;
	public JSONObject json1;
	public Map<String , String> map;
	public List list;
	public JSONArray jsonArray;
	public int productListType = 1; //1: 商品列表, 2: 产品搜索结果列表
	private long cutServiceTime;
	private List<SPCategory> topCategorys;//分类左边菜单一级分类
	private List<String> imageUrls;
	private List<SPServiceConfig> serviceConfigs;
	private Map<String , SPPlugin> payPluginMap;	//支付插件
	private Map<String , SPPlugin> loginPluginMap;	//登录插件
	private TelephonyManager telephonyManager;
	private SPOrder payOrder;			//当前支付订单信息
	public static boolean MAINANIM = false;	// 闪屏动画
	public static int SPLASHTIME = 2000;	// 闪屏延迟时间

	// 是否开启引导页
	public static boolean WELCOME = true;
	// 引导页图片
	public static int[] IMAGES = new int[] { R.drawable.w1, R.drawable.w2, R.drawable.w3 , R.drawable.w4 };


	public int actionType = 1;	//操作类型, 1: 注册, 2:找回密码

	@Override
	public void onCreate() {
		super.onCreate();

		/** 初始化 Vollery 网络请求 */
		SPMobileHttptRequest.init(getApplicationContext());
		/** 初始化 Facebook SimpleDraweeView 网络请求 */

		loginUser = SPSaveData.loadUser(getApplicationContext());
		if (SPStringUtils.isEmpty(loginUser.getUserID()) || loginUser.getUserID().equals("-1")){
			isLogined = false;
		}else{
			isLogined = true;
		}
		instance = this;
		//初始化购物车管理类
		SPShopCartManager.getInstance(getApplicationContext());

		PackageManager manager = this.getPackageManager();
		mDisplayMetrics = getResources().getDisplayMetrics();

		telephonyManager = (TelephonyManager)getApplicationContext().getSystemService(TELEPHONY_SERVICE);
		if(telephonyManager!=null){
			String deviceId = telephonyManager.getDeviceId();
			SPMyFileTool.cacheValue(this, SPMyFileTool.key1, deviceId);
		}

		//直接写死配置appid和secret
		String loginQQAppid = SPMobileConstants.pluginLoginQQAppid;
		String loginQQSecret = SPMobileConstants.pluginLoginQQSecret;

		String loginWXAppid = SPMobileConstants.pluginLoginWeixinAppid;
		String loginWXSecret = SPMobileConstants.pluginLoginWeixinSecret;

		final IWXAPI msgApi = WXAPIFactory.createWXAPI(getApplicationContext(), null);

		PlatformConfig.setQQZone(loginQQAppid, loginQQSecret);
		PlatformConfig.setWeixin(loginWXAppid, loginWXSecret);

		/*** 将该app注册到微信, 微信支付时需要 **/
		msgApi.registerApp(loginWXAppid);
 	}

	public DisplayMetrics getDisplayMetrics(){
		return mDisplayMetrics;
	}

	public static SPMobileApplication getInstance(){
		return instance;
	}

	public List<SPServiceConfig> getServiceConfigs() {
		return serviceConfigs;
	}

	public void setServiceConfigs(List<SPServiceConfig> serviceConfigs) {
		this.serviceConfigs = serviceConfigs;
	}


	public Map<String, SPPlugin> getLoginPluginMap() {
		return loginPluginMap;
	}

	public void setLoginPluginMap(Map<String, SPPlugin> loginPluginMap) {
		this.loginPluginMap = loginPluginMap;

		//初始化QQ,微信快捷登录相关配置
		/*String loginQQAppid = SPPluginUtils.getPluginLoginQQAppid();
		String loginQQSecret = SPPluginUtils.getPluginLoginQQSecret();

		String loginWXAppid = SPPluginUtils.getPluginLoginWeixinAppid();
		String loginWXSecret = SPPluginUtils.getPluginLoginWeixinSecret();*/

		/*if (SPStringUtils.isEmpty(loginWXAppid) || SPStringUtils.isEmpty(loginQQSecret) ||
				SPStringUtils.isEmpty(loginWXAppid) || SPStringUtils.isEmpty(loginWXSecret)){

			loginQQAppid = SPMobileConstants.pluginLoginQQAppid;
			loginQQSecret = SPMobileConstants.pluginLoginQQSecret;

			loginWXAppid = SPMobileConstants.pluginLoginWeixinAppid;
			loginWXSecret = SPMobileConstants.pluginLoginWeixinSecret;

		}

		loginQQAppid = SPMobileConstants.pluginLoginQQAppid;
		loginQQSecret = SPMobileConstants.pluginLoginQQSecret;

		loginWXAppid = SPMobileConstants.pluginLoginWeixinAppid;
		loginWXSecret = SPMobileConstants.pluginLoginWeixinSecret;

		final IWXAPI msgApi = WXAPIFactory.createWXAPI(getApplicationContext(), null);

		PlatformConfig.setQQZone(loginQQAppid, loginQQSecret);
		PlatformConfig.setWeixin(loginWXAppid, loginWXSecret);

		/*** 将该app注册到微信, 微信支付时需要 **/
		/*msgApi.registerApp(loginWXAppid);*/

	}

	public Map<String, SPPlugin> getPayPluginMap() {
		return payPluginMap;
	}

	public void setPayPluginMap(Map<String, SPPlugin> payPluginMap) {
		this.payPluginMap = payPluginMap;
	}



	public SPUser getLoginUser() {
		return loginUser;
	}

	public void setLoginUser(SPUser loginUser) {
		this.loginUser = loginUser;
		if (this.loginUser !=null){
			SPSaveData.saveUser(getApplicationContext() , "user" ,this.loginUser);
			isLogined = true;
		}else{
			isLogined = false;
		}
	}

	public String getApplicationName() {
		PackageManager packageManager = null;
		ApplicationInfo applicationInfo = null;
		try {
			packageManager = getApplicationContext().getPackageManager();
			applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
		} catch (PackageManager.NameNotFoundException e) {
			applicationInfo = null;
		}
		String applicationName = (String) packageManager.getApplicationLabel(applicationInfo);
		return applicationName;
	}

	public String getSystemPackage(){
		return this.getPackageName();
	}

	//退出登录
	public void exitLogin(){
		loginUser = null;
		isLogined = false;
		SPSaveData.clearUser(getApplicationContext());

	}

	public List<SPCategory> getTopCategorys() {
		return topCategorys;
	}

	public void setTopCategorys(List<SPCategory> topCategorys) {
		this.topCategorys = topCategorys;
	}

	/**
	 * 获取设备IMEI
	 * @return
	 */
	public String getDeviceId() {
		if (telephonyManager!=null){
			deviceId = telephonyManager.getDeviceId();//String
		}else{
			deviceId = "unionid001";
		}
		return deviceId;
	}

	public long getCutServiceTime() {
		return cutServiceTime;
	}

	public void setCutServiceTime(long cutServiceTime) {
		this.cutServiceTime = cutServiceTime;
	}
	public SPOrder getPayOrder() {
		return payOrder;
	}

	public void setPayOrder(SPOrder payOrder) {
		this.payOrder = payOrder;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getImageUrls() {
		return imageUrls;
	}

	public void setImageUrl(List<String> imageUrls) {
		this.imageUrls = imageUrls;
	}
}
