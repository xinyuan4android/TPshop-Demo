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
 * Description:产品列表
 * @version V1.0
 */
package com.tpshop.mallc.activity.shop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Window;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


/*import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.State;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.extras.SoundPullEventListener;*/
import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.tpshop.mallc.R;
import com.tpshop.mallc.activity.common.SPBaseActivity;
import com.tpshop.mallc.activity.common.SPSearchCommonActivity_;
import com.tpshop.mallc.adapter.SPProductListAdapter;
import com.tpshop.mallc.adapter.SPProductListAdapter.ItemClickListener;
import com.tpshop.mallc.common.SPMobileConstants;
import com.tpshop.mallc.fragment.SPProductListFilterFragment;
import com.tpshop.mallc.global.SPMobileApplication;
import com.tpshop.mallc.http.base.SPFailuredListener;
import com.tpshop.mallc.http.base.SPSuccessListener;
import com.tpshop.mallc.http.condition.SPProductCondition;
import com.tpshop.mallc.http.shop.SPShopRequest;
import com.tpshop.mallc.model.SPCategory;
import com.tpshop.mallc.model.SPProduct;
import com.tpshop.mallc.model.shop.SPFilter;
import com.tpshop.mallc.model.shop.SPShopOrder;
import com.tpshop.mallc.utils.SPConfirmDialog;
import com.tpshop.mallc.utils.SPDialogUtils;
import com.tpshop.mallc.utils.SMobileLog;
import com.tpshop.mallc.view.SPProductFilterTabView;

/**
 * @author 飞龙
 * http://www.tpshop.com/index.php/Api/Goods/goodsInfo?id=1
 */
public class SPProductListActivity extends SPBaseActivity implements ItemClickListener , SPProductFilterTabView.OnSortClickListener {

	private String TAG = "SPProductListActivity";

	private static SPProductListActivity instance;

	PtrClassicFrameLayout ptrClassicFrameLayout;
	ListView mListView;
	TextView syntheisTxtv ;
	TextView salenumTxtv ;
	TextView priceTxtv ;
	EditText searchText ;//搜索文本框
	ImageView backImgv;	//返回键


	SPProductListAdapter mAdapter ;
	SPCategory mCategory ;	//分类

	SPProductFilterTabView mFilterTabView;

	DrawerLayout mDrawerLayout;

	String mSort = "";			//排序字段
	String mOrder = "asc" ;		//asc: 升序, desc:降序
	int mPageIndex = 1 ;		//当前第几页
	boolean mIsMaxPage;			//是否最大页数

	String mHref ;				//请求URL
	SPShopOrder mShopOrder;		//排序实体
	List<SPProduct> mProducts ;
	SPProductListFilterFragment mFilterFragment;
	Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
				case SPMobileConstants.MSG_CODE_FILTER_CHANGE_ACTION:
					if (msg.obj != null){
						mHref = msg.obj.toString();
						refreshData();
					}
					break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle bundle) {
		//super.setCustomerTitle(true, true , "商品列表");
		super.onCreate(bundle);
		/** 自定义标题栏 , 执行顺序必须是一下顺序, 否则无效果.  */
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.product_list);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.product_list_header);
		/** 自定义标题栏  */
		Intent intent = getIntent();
		if(intent != null){
			mCategory = (SPCategory)intent.getSerializableExtra("category");
		}
		//过滤标题
		super.init();
		refreshData();
		instance = this;
	}

	public static SPProductListActivity getInstance(){
		return instance;
	}
	
	@Override
	public void initSubViews() {

		WindowManager wm = (WindowManager)getBaseContext().getSystemService(Context.WINDOW_SERVICE);

		mFilterTabView = (SPProductFilterTabView)findViewById(R.id.filter_tabv);
		mFilterTabView.setOnSortClickListener(this);
		ptrClassicFrameLayout = (PtrClassicFrameLayout) this.findViewById(R.id.test_list_view_frame);
		mListView = (ListView)findViewById(R.id.pull_product_listv);
		View emptyView = findViewById(R.id.empty_lstv);
		mListView.setEmptyView(emptyView);

		//综合
		syntheisTxtv = (TextView)findViewById(R.id.sort_button_synthesis);
		salenumTxtv = (TextView)findViewById(R.id.sort_button_salenum);
		priceTxtv = (TextView)findViewById(R.id.sort_button_price);

		searchText = (EditText)findViewById(R.id.search_edtv);
		searchText.setFocusable(false);
		searchText.setFocusableInTouchMode(false);
		backImgv = (ImageView)findViewById(R.id.title_back_imgv);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);

		SPMobileApplication.getInstance().productListType = 1;
		mFilterFragment = (SPProductListFilterFragment)getSupportFragmentManager().findFragmentById(R.id.right_rlayout);

		mDrawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
			}

			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
			}

			@Override
			public void onDrawerStateChanged(int newState) {
				super.onDrawerStateChanged(newState);
			}
		});
	}


	@Override
	public void initData() {
		mPageIndex = 1;
		mIsMaxPage = false;
		mAdapter = new SPProductListAdapter(this,this);
		mListView.setAdapter(mAdapter);

		ptrClassicFrameLayout.postDelayed(new Runnable() {

			@Override
			public void run() {
				//ptrClassicFrameLayout.autoRefresh(true);
			}
		}, 150);

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
	
	
	/**
	 * 加载数据
	* @Description: 加载数据
	* @return void    返回类型 
	* @throws
	 */
	public void refreshData(){

		mPageIndex = 1;
		mIsMaxPage = false;
		SPProductCondition conditioon = new SPProductCondition();

		if(mCategory!=null){
			//获取某个分类的ID
			conditioon.categoryID = mCategory.getId();
		}

		conditioon.href = mHref;
		conditioon.page = mPageIndex;
		conditioon.orderby = mSort ;
		conditioon.orderdesc = mOrder;

		showLoadingSmallToast();
		SPShopRequest.getProductList(conditioon, new SPSuccessListener() {
			@Override
			public void onRespone(String msg, Object data) {
				hideLoadingSmallToast();
				refreshCompleted(mHandler , ptrClassicFrameLayout);
				try {
					mDataJson = (JSONObject) data;
					if (mDataJson != null) {

						if (mDataJson.has("product")){
							mProducts = (List<SPProduct>) mDataJson.get("product");
						}
						if (mDataJson.has("order")){
							mShopOrder = (SPShopOrder) mDataJson.get("order");
						}
						if (mProducts != null) {
							mAdapter.setData(mProducts);
							mAdapter.notifyDataSetChanged();
						}
						if (SPProductListFilterFragment.getInstance(mHandler)!=null){
							SPProductListFilterFragment.getInstance(mHandler).setDataSource(mDataJson);
						}
						mIsMaxPage = false;
					}else{
						mIsMaxPage = true;
					}
					refreshView();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, new SPFailuredListener() {
			@Override
			public void onRespone(String msg, int errorCode) {
				hideLoadingSmallToast();
				SPDialogUtils.showToast(SPProductListActivity.this, msg);
				refreshCompleted(mHandler , ptrClassicFrameLayout);
			}
		});
	}


	/**
	 * 加载数据
	 * @Description: 加载数据
	 * @return void    返回类型
	 * @throws
	 */
	public void loadMoreData(){

		if (mIsMaxPage){
			loadMoreComplete(mHandler , ptrClassicFrameLayout);
			showToast(getString(R.string.no_more));
			return;
		}
		mPageIndex++;

		SPProductCondition conditioon = new SPProductCondition();

		if(mCategory!=null){
			//获取某个分类的ID
			conditioon.categoryID = mCategory.getId();
		}

		conditioon.href = mHref;
		conditioon.page = mPageIndex;
		conditioon.orderby = mSort ;
		conditioon.orderdesc = mOrder;

		SPShopRequest.getProductList(conditioon, new SPSuccessListener() {
			@Override
			public void onRespone(String msg, Object data) {
				loadMoreComplete(mHandler , ptrClassicFrameLayout);
				try {
					mDataJson = (JSONObject) data;
					if (mDataJson != null) {
						mShopOrder = (SPShopOrder) mDataJson.get("order");
						List<SPProduct> results = (List<SPProduct>) mDataJson.get("product");
						if (results != null && results.size() > 0 && mProducts != null) {
							mProducts.addAll(results);
							mAdapter.setData(mProducts);
							mIsMaxPage = false;
						}else{
							mIsMaxPage = true;
						}
						if (SPProductListFilterFragment.getInstance(mHandler)!=null){
							SPProductListFilterFragment.getInstance(mHandler).setDataSource(mDataJson);
						}
					}
					refreshView();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, new SPFailuredListener() {
			@Override
			public void onRespone(String msg, int errorCode) {
				SPDialogUtils.showToast(SPProductListActivity.this, msg);
				loadMoreComplete(mHandler , ptrClassicFrameLayout);
				mPageIndex--;
			}
		});
	}

	@Override
	public void initEvent() {
		searchText.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SPProductListActivity.this , SPSearchCommonActivity_.class);
				SPProductListActivity.this.startActivity(intent);
			}
		});
		backImgv.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				SPProductListActivity.this.finish();
			}
		});
	}


	
	public void startupActivity(String goodsID){
		Intent intent = new Intent(this , SPProductDetailActivity_.class);
		intent.putExtra("goodsID", goodsID);
		startActivity(intent);
	}


	@Override
	public void onItemClickListener(SPProduct product) {
		startupActivity(product.getGoodsID());
	}

	@Override
	public void onFilterClick(SPProductFilterTabView.ProductSortType sortType) {

		switch (sortType){
			case composite:
				if (mShopOrder!=null){
					mHref = mShopOrder.getDefaultHref();
				}
				break;
			case salenum:
				if (mShopOrder!=null) {
					mHref = mShopOrder.getSaleSumHref();
				}
				break;
			case price:
				if (mShopOrder!=null) {
					mHref = mShopOrder.getPriceHref();
				}
				break;
			case filter:
				openRightFilterView();
				return;
		}
		refreshData();
	}

	public void openRightFilterView(){
		mDrawerLayout.openDrawer(Gravity.RIGHT);
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED,Gravity.RIGHT);
	}

	public void refreshView(){
		if (mShopOrder!=null && mShopOrder.getSortAsc()!=null) {
			if (mShopOrder.getSortAsc().equalsIgnoreCase("desc")) {
				mFilterTabView.setSort(true);
			}else{
				mFilterTabView.setSort(false);
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		searchText.setFocusable(false);
		searchText.setFocusableInTouchMode(false);
	}
}
