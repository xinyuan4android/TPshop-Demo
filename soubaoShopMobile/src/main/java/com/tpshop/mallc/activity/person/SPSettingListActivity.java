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
 * Date: @date 2015年11月3日 下午10:04:49 
 * Description: 设置列表
 * @version V1.0
 */
package com.tpshop.mallc.activity.person;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.tpshop.mallc.R;
import com.tpshop.mallc.activity.common.SPBaseActivity;
import com.tpshop.mallc.activity.shop.SPProductDetailActivity_;
import com.tpshop.mallc.adapter.SPCollectListAdapter;
import com.tpshop.mallc.adapter.SPSettingListAdapter;
import com.tpshop.mallc.common.SPMobileConstants;
import com.tpshop.mallc.global.SPMobileApplication;
import com.tpshop.mallc.http.base.SPMobileHttptRequest;
import com.tpshop.mallc.http.person.SPPersonRequest;
import com.tpshop.mallc.model.shop.SPCollect;
import com.tpshop.mallc.utils.SMobileLog;
import com.tpshop.mallc.utils.SPServerUtils;
import com.soubao.tpshop.utils.SPStringUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;


/**
 * @author 飞龙
 *
 */
@EActivity(R.layout.person_setting_list)
public class SPSettingListActivity extends SPBaseActivity {

	private String TAG = "SPSettingListActivity";

	@ViewById(R.id.setting_listv)
	ListView settingListv;

	@ViewById(R.id.exit_btn)
	Button exitBtn;

	//售后客服电话
	String mAfterSalePhone ;

	SPSettingListAdapter mAdapter ;
	List<String> mTexts ;

	@Override
	protected void onCreate(Bundle bundle) {
		super.setCustomerTitle(true, true , getString(R.string.settings));
		super.onCreate(bundle);}


	@AfterViews
	public void init(){
		super.init();
	}
	
	@Override
	public void initSubViews() {

	}

	@Override
	public void initData() {

		mAdapter = new SPSettingListAdapter(this);
		settingListv.setAdapter(mAdapter);

		mAfterSalePhone = SPServerUtils.getServicePhone();
		mTexts = new ArrayList<String>();
		mTexts.add("客服电话:"+mAfterSalePhone);
		mTexts.add("触屏版");

		mAdapter.setData(mTexts);

		if (SPMobileApplication.getInstance().isLogined){
			this.exitBtn.setEnabled(true);
			this.exitBtn.setBackgroundResource(R.drawable.button_selector);
		}else{
			this.exitBtn.setEnabled(true);
			this.exitBtn.setBackgroundResource(R.drawable.button_gray_selector);
		}

	}

	@Override
	public void initEvent() {
		settingListv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0) {
					//联系客服
					Intent intent = new Intent(Intent.ACTION_DIAL);
					if (SPStringUtils.isEmpty(mAfterSalePhone)){
						showToast("请在后台配置客服电话");
						return;
					}
					intent.setData(Uri.parse("tel:" + mAfterSalePhone));
					if (intent.resolveActivity(getPackageManager()) != null) {
						startActivity(intent);
					}
				} else if (position == 1) {
					//
					startWebViewActivity(SPMobileConstants.BASE_HOST+"/index.php/mobile", "触屏版");
				}
			}
		});
	}


	@Click({R.id.exit_btn})
	public void onViewClick(View v){

		if (v.getId() == R.id.exit_btn){
			SPMobileApplication.getInstance().exitLogin();
			this.sendBroadcast(new Intent(SPMobileConstants.ACTION_LOGIN_CHNAGE));
			this.exitBtn.setEnabled(false);
			this.exitBtn.setBackgroundResource(R.drawable.button_gray_selector);
			showToast("安全退出");
		}
	}



}
