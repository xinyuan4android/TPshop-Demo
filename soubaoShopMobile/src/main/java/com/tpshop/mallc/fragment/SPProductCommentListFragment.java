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
 * Date: @date 2015年10月20日 下午7:19:26 
 * Description: 商品评论
 * @version V1.0
 */
package com.tpshop.mallc.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.tpshop.mallc.R;
import com.tpshop.mallc.activity.common.SPImagePreviewActivity_;
import com.tpshop.mallc.adapter.SPProductDetailCommentAdapter;
import com.tpshop.mallc.global.SPMobileApplication;
import com.tpshop.mallc.http.base.SPFailuredListener;
import com.tpshop.mallc.http.base.SPSuccessListener;
import com.tpshop.mallc.http.shop.SPShopRequest;
import com.tpshop.mallc.model.shop.SPGoodsComment;

import java.util.List;


/**
 *  商品详情 -> 商品评论列表
 *
 */
public class SPProductCommentListFragment extends SPBaseFragment implements SPProductDetailCommentAdapter.OnImageClickListener {

	private String TAG = "SPProductCommentListFragment";
	private Context mContext;
	private String goodsId ;

	PtrClassicFrameLayout ptrClassicFrameLayout;
	ListView commentListv;
	SPProductDetailCommentAdapter mAdapter;
	List<SPGoodsComment> mComments ;

	boolean isFirstLoad;

	int pageIndex;   //当前第几页:从1开始
	/**
	 *  最大页数
	 */
	boolean maxIndex;

	Handler mHandler = new Handler() {

	};

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mContext = activity;
	}

	public void setGoodsId(String goodsId){
		this.goodsId = goodsId;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		isFirstLoad = true;
	    View view = inflater.inflate(R.layout.product_details_comment_list, null,false);
		initSubView(view);
		return view;
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	@Override
	public void initSubView(View view) {
		ptrClassicFrameLayout = (PtrClassicFrameLayout)view.findViewById(R.id.product_comment_list_view_frame);
		commentListv  = (ListView)view.findViewById(R.id.product_comment_listv);

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

		mAdapter = new SPProductDetailCommentAdapter(getActivity());
		commentListv.setAdapter(mAdapter);
		mAdapter.setOnImageListener(this);
		loadData();
	}

	@Override
	public void initEvent() {

	}

	@Override
	public void initData() {

	}

	public void refreshData(){
		pageIndex = 1;
		maxIndex = false;
		//showLoadingToast();
		SPShopRequest.getGoodsCommentWithGoodsID(goodsId , pageIndex , new SPSuccessListener() {
			@Override
			public void onRespone(String msg, Object response) {
				refreshCompleted(mHandler , ptrClassicFrameLayout);
				if (response != null) {
					mComments = (List<SPGoodsComment>) response;
					//更新收藏数据
					mAdapter.setData(mComments);
				} else {
					maxIndex = true;
				}
				hideLoadingToast();
			}
		}, new SPFailuredListener() {
			@Override
			public void onRespone(String msg, int errorCode) {
				showToast(msg);
				refreshCompleted(mHandler , ptrClassicFrameLayout);
				hideLoadingToast();
			}
		});
	}


	public void loadMoreData() {
		if (maxIndex) {
			refreshCompleted(mHandler , ptrClassicFrameLayout);
			return;
		}
		pageIndex++;
		SPShopRequest.getGoodsCommentWithGoodsID(goodsId, pageIndex, new SPSuccessListener() {
			@Override
			public void onRespone(String msg, Object response) {
				loadMoreComplete(mHandler , ptrClassicFrameLayout);
				if (response != null) {
					List<SPGoodsComment> tempComment = (List<SPGoodsComment>) response;
					mComments.addAll(tempComment);
					//更新收藏数据
					mAdapter.setData(mComments);
					
				} else {
					pageIndex--;
					maxIndex = true;
					
				}
				hideLoadingToast();
			}
		}, new SPFailuredListener() {
			@Override
			public void onRespone(String msg, int errorCode) {
				hideLoadingToast();
				loadMoreComplete(mHandler , ptrClassicFrameLayout);
				showToast(msg);
				pageIndex--;
			}
		});
	}

	public void loadData(){
		if (isFirstLoad && commentListv!=null) {
			refreshData();
			isFirstLoad = false;
		}
	}

	/**
	 * 图片预览
	 */
	public void startupImagePreview(List<String> imgUrls){
		SPMobileApplication.getInstance().setImageUrl(imgUrls);
		Intent previewIntent = new Intent(getActivity(), SPImagePreviewActivity_.class);
		startActivity(previewIntent);
	}

	@Override
	public void onClickListener(List<String> imgUrls) {
		startupImagePreview(imgUrls);
	}
}
