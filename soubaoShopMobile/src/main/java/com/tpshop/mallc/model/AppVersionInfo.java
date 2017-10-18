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
 * Date: @date 2015年10月27日 下午9:14:42 
 * Description: 分类  model
 * @version V1.0
 */
package com.tpshop.mallc.model;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.List;

/**
 * @author 飞龙
 *
 */
public class AppVersionInfo implements SPModel, Serializable{
	
	private int isNeedUpdate ;		//是否需要更新
	private String apkUrl ;			//Apk 下载地址
	private String updateLog;		//Apk更新日志
	private String lastVersion;		//Apk最新版本号

	public int getIsNeedUpdate() {
		return isNeedUpdate;
	}

	public void setIsNeedUpdate(int isNeedUpdate) {
		this.isNeedUpdate = isNeedUpdate;
	}

	public String getApkUrl() {
		return apkUrl;
	}

	public void setApkUrl(String apkUrl) {
		this.apkUrl = apkUrl;
	}

	public String getUpdateLog() {
		return updateLog;
	}

	public void setUpdateLog(String updateLog) {
		this.updateLog = updateLog;
	}

	public String getLastVersion() {
		return lastVersion;
	}

	public void setLastVersion(String lastVersion) {
		this.lastVersion = lastVersion;
	}

	@Override
	public String[] replaceKeyFromPropertyName() {
		
		return new String[]{
				"isNeedUpdate","is_need_update",
				"apkUrl","apk_url",
				"updateLog" , "update_log",
				"lastVersion","last_version"
		};
	}


	
}
