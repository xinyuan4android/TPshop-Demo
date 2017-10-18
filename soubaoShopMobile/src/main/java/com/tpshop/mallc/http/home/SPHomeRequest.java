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
 * Date: @date 2015年10月28日 下午9:31:20 
 * Description: 首页相关数据接口
 * @version V1.0
 */
package com.tpshop.mallc.http.home;

import android.util.Log;

import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import com.loopj.android.http.RequestParams;
import com.tpshop.mallc.common.SPMobileConstants;
import com.tpshop.mallc.common.SPMobileConstants.Response;
import com.tpshop.mallc.http.base.SPDownListener;
import com.tpshop.mallc.http.base.SPFailuredListener;
import com.tpshop.mallc.http.base.SPMobileHttptRequest;
import com.tpshop.mallc.http.base.SPSuccessListener;
import com.tpshop.mallc.model.AppVersionInfo;
import com.tpshop.mallc.model.SPHomeBanners;
import com.tpshop.mallc.model.SPHomeCategory;
import com.tpshop.mallc.model.SPPlugin;
import com.tpshop.mallc.model.SPProduct;
import com.tpshop.mallc.model.SPServiceConfig;
import com.soubao.tpshop.utils.SPJsonUtil;
import com.soubao.tpshop.utils.SPMyFileTool;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

/**
 * @author 飞龙
 *
 */
public class SPHomeRequest {

	private static String TAG = "SPHomeRequest";

	/**
	 *  查询系统配置信息
	 *  使用万能SQL: index.php?m=Api&c=Index&a=getConfig
	 *  @param successListener success description
	 *  @param failuredListener failure description
	 */
	public static void getServiceConfig(final SPSuccessListener successListener,  final SPFailuredListener failuredListener){
		assert(successListener!=null);
		assert(failuredListener!=null);

		String url =  SPMobileHttptRequest.getRequestUrl("Index", "getConfig");

		try{
			SPMobileHttptRequest.post(url, null, new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
					try {

						String msg = (String) response.getString(Response.MSG);
						int status = response.getInt(Response.STATUS);
						if (status > 0){
							JSONArray resultArray = (JSONArray) response.getJSONArray(Response.RESULT);
							List<SPServiceConfig> serviceConfigs = SPJsonUtil.fromJsonArrayToList(resultArray, SPServiceConfig.class);
							successListener.onRespone("success", serviceConfigs);
						}else {
							failuredListener.onRespone(msg , -1);
						}
					} catch (Exception e) {
						failuredListener.onRespone(e.getMessage(), -1);
						e.printStackTrace();
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
					failuredListener.onRespone(throwable.getMessage(), statusCode);
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
					failuredListener.onRespone(throwable.getMessage(), statusCode);
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
					failuredListener.onRespone(throwable.getMessage(), statusCode);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 *  查询插件配置信息
	 *  使用万能SQL: index.php?m=Api&c=Index&a=getPluginConfig
	 *  @param successListener success description
	 *  @param failuredListener failure description
	 */
	public static void getServicePlugin(final SPSuccessListener successListener,  final SPFailuredListener failuredListener){
		assert(successListener!=null);
		assert(failuredListener!=null);

		String url =  SPMobileHttptRequest.getRequestUrl("Index", "getPluginConfig");

		try{
			SPMobileHttptRequest.get(url, null, new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
					try {

						String msg = (String) response.getString(Response.MSG);

						int status = response.getInt(Response.STATUS);
						if (status > 0){
							JSONObject resultJson = (JSONObject)response.getJSONObject(Response.RESULT);

							Map<String , SPPlugin> pluginMap = new HashMap<String, SPPlugin>();
							//支付插件
							List<SPPlugin> payPlugins = SPJsonUtil.fromJsonArrayToList(resultJson.getJSONArray("payment"), SPPlugin.class);
							//登录插件
							List<SPPlugin> loginPlugins = SPJsonUtil.fromJsonArrayToList(resultJson.getJSONArray("login"), SPPlugin.class);

							packPluginData(pluginMap , payPlugins);
							packPluginData(pluginMap , loginPlugins);

							successListener.onRespone("success", pluginMap);
						}else {
							failuredListener.onRespone(msg , -1);
						}
					} catch (Exception e) {
						failuredListener.onRespone(e.getMessage(), -1);
						e.printStackTrace();
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
					failuredListener.onRespone(throwable.getMessage(), statusCode);
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
					failuredListener.onRespone(throwable.getMessage(), statusCode);
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
					failuredListener.onRespone(throwable.getMessage(), statusCode);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void getHomeData(final SPSuccessListener successListener,  final SPFailuredListener failuredListener) {
		assert (successListener != null);
		assert (failuredListener != null);
		String url =  SPMobileHttptRequest.getRequestUrl("Index", "home");
		try{
			SPMobileHttptRequest.post(url, null, new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
					try {

						String msg = (String) response.getString(Response.MSG);
						JSONObject resultJson = (JSONObject) response.getJSONObject(Response.RESULT);
						JSONObject dataJson = new JSONObject();

						if (resultJson != null){
							//商品列表
							if (!resultJson.isNull("goods")) {
								JSONArray goods = resultJson.getJSONArray("goods");

								if (goods != null) {
									List<SPHomeCategory> homeCategories = SPJsonUtil.fromJsonArrayToList(goods, SPHomeCategory.class);
									for(int i = 0;i<goods.length();i++){
										JSONObject entityObj = goods.getJSONObject(i);
										if (entityObj.has("goods_list")){
											JSONArray products = entityObj.getJSONArray("goods_list");
											List<SPProduct> pros = SPJsonUtil.fromJsonArrayToList(products, SPProduct.class);
											homeCategories.get(i).setGoodsList(pros);
										}

									}
									dataJson.put("homeCategories", homeCategories);
								}
							}
							//ad
							if (!resultJson.isNull("ad")) {
								JSONArray ads = resultJson.getJSONArray("ad");
								if (ads != null) {
									List<SPHomeBanners> banners = SPJsonUtil.fromJsonArrayToList(ads, SPHomeBanners.class);
									dataJson.put("banners", banners);
								}
							}
							successListener.onRespone("success", dataJson);
						}else {
							failuredListener.onRespone(msg , -1);
						}
					} catch (Exception e) {
						failuredListener.onRespone(e.getMessage(), -1);
						e.printStackTrace();
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
					failuredListener.onRespone(throwable.getMessage(), statusCode);
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
					failuredListener.onRespone(throwable.getMessage(), statusCode);
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
					failuredListener.onRespone(throwable.getMessage(), statusCode);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取首页数据
	 * @param successListener
	 * @param failuredListener
     */
	public static void getHomePageData(final SPSuccessListener successListener,  final SPFailuredListener failuredListener) {
		assert (successListener != null);
		assert (failuredListener != null);
		String url =  SPMobileHttptRequest.getRequestUrl("Index", "homePage");
		try{
			SPMobileHttptRequest.post(url, null, new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
					try {

						String msg = (String) response.getString(Response.MSG);
						int status = response.getInt(Response.STATUS);
						if (status > 0){
							JSONObject resultJson = (JSONObject) response.getJSONObject(Response.RESULT);
							JSONObject dataJson = new JSONObject();

							if (resultJson != null){
								//促销商品
								if (!resultJson.isNull("promotion_goods")) {
									JSONArray promotionArray = resultJson.getJSONArray("promotion_goods");
									List<SPProduct> promotions = SPJsonUtil.fromJsonArrayToList(promotionArray, SPProduct.class);
									dataJson.put("promotion_goods", promotions);
								}

								//精品推荐
								if (!resultJson.isNull("high_quality_goods")) {
									JSONArray qualityArray = resultJson.getJSONArray("high_quality_goods");
									List<SPProduct> qualitys = SPJsonUtil.fromJsonArrayToList(qualityArray, SPProduct.class);
									dataJson.put("high_quality_goods", qualitys);
								}


								//新品上市
								if (!resultJson.isNull("new_goods")) {
									JSONArray newArray = resultJson.getJSONArray("new_goods");
									List<SPProduct> news = SPJsonUtil.fromJsonArrayToList(newArray, SPProduct.class);
									dataJson.put("new_goods", news);
								}

								//热门商品
								if (!resultJson.isNull("hot_goods")) {
									JSONArray hotArray = resultJson.getJSONArray("hot_goods");
									List<SPProduct> hots = SPJsonUtil.fromJsonArrayToList(hotArray, SPProduct.class);
									dataJson.put("hot_goods", hots);
								}

								//首页轮播广告
								if (!resultJson.isNull("ad")) {
									JSONArray ads = resultJson.getJSONArray("ad");
									if (ads != null) {
										List<SPHomeBanners> banners = SPJsonUtil.fromJsonArrayToList(ads, SPHomeBanners.class);
										dataJson.put("banners", banners);
									}
								}
								successListener.onRespone("success", dataJson);
							}else {
								failuredListener.onRespone(msg , -1);
							}
						}else {
							failuredListener.onRespone(msg , status);
						}

					} catch (Exception e) {
						failuredListener.onRespone(e.getMessage(), -1);
						e.printStackTrace();
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
					failuredListener.onRespone(throwable.getMessage(), statusCode);
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
					failuredListener.onRespone(throwable.getMessage(), statusCode);
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
					failuredListener.onRespone(throwable.getMessage(), statusCode);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * 首页猜你喜欢
	 * @param successListener
	 * @param failuredListener
	 */
	public static void getFavouritePageData(int pageIndex , final SPSuccessListener successListener,  final SPFailuredListener failuredListener) {
		assert (successListener != null);
		assert (failuredListener != null);

		RequestParams params = new RequestParams();
		params.put("p" , pageIndex);
		params.put("page_size" , SPMobileConstants.SizeOfPage);

		String url =  SPMobileHttptRequest.getRequestUrl("Index", "favourite");
		try{
			SPMobileHttptRequest.post(url, params, new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
					try {
						String msg = (String) response.getString(Response.MSG);
						int status = response.getInt(Response.STATUS);
						if (status > 0) {

							JSONObject resultJson = (JSONObject) response.getJSONObject(Response.RESULT);

							//猜你喜欢
							if (resultJson != null && !resultJson.isNull("favourite_goods")){

								JSONArray favouriteArray = resultJson.getJSONArray("favourite_goods");
								List<SPProduct> favourites = SPJsonUtil.fromJsonArrayToList(favouriteArray, SPProduct.class);
								successListener.onRespone("success", favourites);

							}else {
								failuredListener.onRespone(msg , -1);
							}

						}else {
							failuredListener.onRespone(msg , -1);
						}
					} catch (Exception e) {
						failuredListener.onRespone(e.getMessage(), -1);
						e.printStackTrace();
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
					failuredListener.onRespone(throwable.getMessage(), statusCode);
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
					failuredListener.onRespone(throwable.getMessage(), statusCode);
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
					failuredListener.onRespone(throwable.getMessage(), statusCode);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 检查APP是否需要更新
	 * @param successListener
	 * @param failuredListener
	 */
	public static void checkAppUpdate(String currVersion , final SPSuccessListener successListener,  final SPFailuredListener failuredListener) {
		assert (successListener != null);
		assert (failuredListener != null);

		RequestParams params = new RequestParams();
		params.put("version" , currVersion);

		String url =  SPMobileHttptRequest.getRequestUrl("Index", "checkAppUpdate");
		try{
			SPMobileHttptRequest.post(url, params, new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
					try {
						String msg = (String) response.getString(Response.MSG);
						int status = response.getInt(Response.STATUS);
						if (status > 0) {
							JSONObject resultJson = (JSONObject) response.getJSONObject(Response.RESULT);
							AppVersionInfo versionInfo = SPJsonUtil.fromJsonToModel(resultJson , AppVersionInfo.class);
							successListener.onRespone(msg , versionInfo);
						}else {
							failuredListener.onRespone(msg , -1);
						}
					} catch (Exception e) {
						failuredListener.onRespone(e.getMessage(), -1);
						e.printStackTrace();
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
					failuredListener.onRespone(throwable.getMessage(), statusCode);
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
					failuredListener.onRespone(throwable.getMessage(), statusCode);
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
					failuredListener.onRespone(throwable.getMessage(), statusCode);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 检查APP是否需要更新
	 * @param successListener
	 * @param failuredListener
	 */
	public static void downApk(String url , final SPDownListener successListener, final SPFailuredListener failuredListener) {
		assert (successListener != null);
		assert (failuredListener != null);

		try{

			// 指定文件类型
			String[] fileTypes = new String[] { "application/vnd.android.package-archive"};
			String apkUrl = SPMobileConstants.BASE_HOST + "/"+ url ;
			SPMobileHttptRequest.downloadFile(apkUrl , null , new BinaryHttpResponseHandler(fileTypes) {
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] binaryData) {
					successListener.onRespone(statusCode ,binaryData);
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] binaryData, Throwable error) {
					failuredListener.onRespone(error.getMessage(), -1);
				}

				@Override
				public void onProgress(long bytesWritten, long totalSize) {
					successListener.onProgress(bytesWritten , totalSize);
					super.onProgress(bytesWritten, totalSize);
				}

				@Override
				public void onRetry(int retryNo) {
					super.onRetry(retryNo);
					// 返回重试次数
				}

			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * 填装插件数据
	 * @param pluginMap
	 * @param plugins
     */
	public static void packPluginData(Map<String , SPPlugin> pluginMap , List<SPPlugin> plugins){

		if (pluginMap==null || plugins==null || plugins.size() < 1)return;

		if (plugins!=null) {
			for (SPPlugin plugin : plugins) {
				//插件安装后才可使用
				if (plugin.getStatus().equals("1")){
					String key = plugin.getCode();
					pluginMap.put(key , plugin);
				}
			}
		}

	}

}
