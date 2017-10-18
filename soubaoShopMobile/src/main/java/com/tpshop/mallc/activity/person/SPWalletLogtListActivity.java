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
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.loopj.android.http.RequestParams;
import com.tpshop.mallc.R;
import com.tpshop.mallc.activity.common.SPBaseActivity;
import com.tpshop.mallc.activity.shop.SPProductDetailActivity_;
import com.tpshop.mallc.adapter.SPCollectListAdapter;
import com.tpshop.mallc.adapter.SPWalletLogAdapter;
import com.tpshop.mallc.global.SPMobileApplication;
import com.tpshop.mallc.http.base.SPFailuredListener;
import com.tpshop.mallc.http.base.SPMobileHttptRequest;
import com.tpshop.mallc.http.base.SPSuccessListener;
import com.tpshop.mallc.http.person.SPPersonRequest;
import com.tpshop.mallc.model.person.SPWalletLog;
import com.tpshop.mallc.model.shop.SPCollect;
import com.tpshop.mallc.utils.SMobileLog;
import com.tpshop.mallc.utils.SPOrderUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.List;


/**
 * @author 飞龙
 *
 */
@EActivity(R.layout.person_walletlog_list)
public class SPWalletLogtListActivity extends SPBaseActivity {

	private String TAG = "SPWalletLogtListActivity";

	@ViewById(R.id.walletlog_listv)
	ListView walletlogListv;

	@ViewById(R.id.walletlog_list_view_frame)
	PtrClassicFrameLayout ptrClassicFrameLayout;


	SPWalletLogAdapter mAdapter ;
	List<SPWalletLog> mWalletLogs ;

	int pageIndex;   //当前第几页:从1开始
	/**
	 *  最大页数
	 */
	boolean maxIndex;

	Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle bundle) {
		setCustomerTitle(true , true , getString(R.string.title_balance_points));
		super.onCreate(bundle);}


	@AfterViews
	public void init(){
		super.init();
	}
	
	@Override
	public void initSubViews() {

		ptrClassicFrameLayout.setPtrHandler(new PtrDefaultHandler() {

			@Override
			public void onRefreshBegin(PtrFrameLayout frame) {
				//下拉刷新
				refreshData();
			}
		});

		ptrClassicFrameLayout.setOnLoadMoreListener(new OnLoadMoreListener() {

			@Override
			public void loadMore() {
				//上拉加载更多
				loadMoreData();
			}
		});

	}

	@Override
	public void initData() {

		mAdapter = new SPWalletLogAdapter(this);
		walletlogListv.setAdapter(mAdapter);
		refreshData();
	}

	public void refreshData(){
		pageIndex = 1;
		maxIndex = false;
		showLoadingToast();
		SPPersonRequest.getWalletLogsWithPage(pageIndex, new SPSuccessListener() {
			@Override
			public void onRespone(String msg, Object response) {
				refreshCompleted(mHandler , ptrClassicFrameLayout);
				if (response != null) {
					mWalletLogs = (List<SPWalletLog>) response;
					//更新收藏数据
					mAdapter.setData(mWalletLogs);
				} else {
					maxIndex = true;
				}
				hideLoadingToast();
			}
		}, new SPFailuredListener(SPWalletLogtListActivity.this) {
			@Override
			public void onRespone(String msg, int errorCode) {
				refreshCompleted(mHandler , ptrClassicFrameLayout);
				showToast(msg);
				hideLoadingToast();
			}
		});
	}

	public void loadMoreData() {

		if (maxIndex) {
			loadMoreComplete(mHandler , ptrClassicFrameLayout);
			return;
		}
		pageIndex++;
		showLoadingToast();
		SPPersonRequest.getWalletLogsWithPage(pageIndex, new SPSuccessListener() {
			@Override
			public void onRespone(String msg, Object response) {
				loadMoreComplete(mHandler , ptrClassicFrameLayout);
				if (response != null) {
					List<SPWalletLog> tempWalletLog = (List<SPWalletLog>) response;
					mWalletLogs.addAll(tempWalletLog);
					//更新收藏数据
					mAdapter.setData(mWalletLogs);
				} else {
					pageIndex--;
					maxIndex = true;
				}
				hideLoadingToast();
			}
		}, new SPFailuredListener() {
			@Override
			public void onRespone(String msg, int errorCode) {
				loadMoreComplete(mHandler , ptrClassicFrameLayout);
				hideLoadingToast();
				showToast(msg);
				pageIndex--;
			}
		});
	}

	@Override
	public void initEvent() {

	}


}
