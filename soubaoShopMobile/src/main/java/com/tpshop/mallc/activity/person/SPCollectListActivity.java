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
 * Description: 商品收藏列表
 * @version V1.0
 */
package com.tpshop.mallc.activity.person;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.tpshop.mallc.R;
import com.tpshop.mallc.activity.common.SPBaseActivity;
import com.tpshop.mallc.activity.shop.SPProductDetailActivity_;
import com.tpshop.mallc.adapter.SPCollectListAdapter;
import com.tpshop.mallc.adapter.SPProductShowListAdapter;
import com.tpshop.mallc.global.SPMobileApplication;
import com.tpshop.mallc.http.base.SPFailuredListener;
import com.tpshop.mallc.http.base.SPMobileHttptRequest;
import com.tpshop.mallc.http.base.SPSuccessListener;
import com.tpshop.mallc.http.person.SPPersonRequest;
import com.tpshop.mallc.model.SPProduct;
import com.tpshop.mallc.model.shop.SPCollect;
import com.tpshop.mallc.utils.SMobileLog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.List;


/**
 * @author 飞龙
 *
 */
@EActivity(R.layout.person_collect_list)
public class SPCollectListActivity extends SPBaseActivity {

	private String TAG = "SPCollectListActivity";

	@ViewById(R.id.product_listv)
	ListView productListv;

	SPCollectListAdapter mAdapter ;
	List<SPCollect> mCollects ;

	@Override
	protected void onCreate(Bundle bundle) {
		super.setCustomerTitle(true,true,getString(R.string.title_collect));
		super.onCreate(bundle);
	}


	@AfterViews
	public void init(){
		super.init();
	}
	
	@Override
	public void initSubViews() {

	}

	@Override
	public void initData() {
		mAdapter = new SPCollectListAdapter(this);
		productListv.setAdapter(mAdapter);

		showLoadingToast();
		SPPersonRequest.getGoodsCollectWithSuccess(new SPSuccessListener() {
			@Override
			public void onRespone(String msg, Object response) {

				if (response != null) {
					mCollects = (List<SPCollect>) response;
					SPMobileApplication.getInstance().goodsCollects = mCollects;
					//更新收藏数据
					mAdapter.setData(mCollects);
				}
				hideLoadingToast();
			}
		}, new SPFailuredListener(SPCollectListActivity.this) {
			@Override
			public void onRespone(String msg, int errorCode) {
				showToast(msg);
				hideLoadingToast();
			}
		});
	}

	@Override
	public void initEvent() {
		productListv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id){
				SPCollect collect = (SPCollect)mAdapter.getItem(position);
				Intent intent = new Intent(SPCollectListActivity.this , SPProductDetailActivity_.class);
				intent.putExtra("goodsID", collect.getGoodsID());
				SPCollectListActivity.this.startActivity(intent);
			}
		});
	}





}
