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
 * Description:商城Fragment
 * @version V1.0
 */
package com.tpshop.mallc.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tpshop.mallc.R;
import com.tpshop.mallc.SPMainActivity;
import com.tpshop.mallc.activity.common.SPSearchCommonActivity;
import com.tpshop.mallc.activity.person.SPCollectListActivity;
import com.tpshop.mallc.activity.person.order.SPOrderListActivity;
import com.tpshop.mallc.activity.shop.SPProductDetailActivity;
import com.tpshop.mallc.activity.shop.SPProductListActivity;
import com.tpshop.mallc.adapter.SPProductListAdapter;
import com.tpshop.mallc.global.SPMobileApplication;
import com.tpshop.mallc.http.base.SPFailuredListener;
import com.tpshop.mallc.http.base.SPSuccessListener;
import com.tpshop.mallc.http.home.SPHomeRequest;
import com.tpshop.mallc.model.SPCategory;
import com.tpshop.mallc.model.SPHomeBanners;
import com.tpshop.mallc.model.SPProduct;
import com.tpshop.mallc.utils.SPDialogUtils;
import com.tpshop.mallc.utils.SPOrderUtils;
import com.tpshop.mallc.view.SPPageView;
import com.tpshop.mallc.view.SPProductScrollView;
import com.tpshop.mallc.zxing.MipcaActivityCapture;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * 首页 -> 商城首页
 * http://blog.csdn.net/jdsjlzx/article/details/49966101
 *
 */
public class SPHomeFragment extends SPBaseFragment implements View.OnClickListener ,
		SPProductListAdapter.ItemClickListener ,
		SPProductScrollView.ProductScrollViewListener ,
		SPPageView.PageListener , AbsListView.OnScrollListener{

	private String TAG = "SPHomeFragment";
	public final static int CATEGORY_FRAGMENT = 1;
	public final static int SHOPCART_FRAGMENT = 2;
	private Context mContext;


	int mIsShowTitleHeight = 100;
	View mHeaderView;
	ImageButton topBtn;
	PullToRefreshListView mFavouritLstv ;
	SparseArray recordSp = new SparseArray(0);//设置容器大小，默认是10

	private int mCurrentfirstVisibleItem = 0;

	View categoryLayout;
	View shopcartLayout;
	View orderLayout;
	View couponLayout;
	HomeHandler mHnadler;
	private List<SPProduct> mPromotions;//促销商品
	private List<SPProduct> mQualitys;	//精品推荐
	private List<SPProduct> mNews;		//新品上市
	private List<SPProduct> mHots;		//热门商品
	RelativeLayout homeTitleView;
	EditText searchText;
	SPPageView mScrolllayout;	//轮播广告scrollLayout
	LinearLayout mGallery;

	/** 精品推荐 **/
	SPProductScrollView mQualityPsv;	//精品推荐
	SPProductScrollView mNewPsv;		//新品上市
	SPProductScrollView mHotPsv;		//热门商品

	ViewGroup mPointerLayout; //指示点Layout
	SPProductListAdapter mAdapter;

	SPMainActivity mainActivity;



	int mPageIndex = 1 ;		//当前第几页
	boolean mIsMaxPage;			//是否最大页数

	List<SPProduct> mFavourites;	//猜你喜欢

	private final static int SCANNIN_GREQUEST_CODE = 1;
	TextView scanView;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mContext = activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

	    View view = inflater.inflate(R.layout.home_fragment, null,false);
		mHeaderView = inflater.inflate(R.layout.home_header_view, null);
		categoryLayout = mHeaderView.findViewById(R.id.home_menu_categroy_layout);
		shopcartLayout = mHeaderView.findViewById(R.id.home_menu_shopcart_layout);
		orderLayout = mHeaderView.findViewById(R.id.home_menu_order_layout);
		couponLayout = mHeaderView.findViewById(R.id.home_menu_coupon_layout);

		
		mQualityPsv = (SPProductScrollView) mHeaderView.findViewById(R.id.quality_scrollv);
		mNewPsv = (SPProductScrollView) mHeaderView.findViewById(R.id.new_scrollv);
		mHotPsv = (SPProductScrollView) mHeaderView.findViewById(R.id.hot_scrollv);


		topBtn = (ImageButton) view.findViewById(R.id.top_ibtn);
		homeTitleView = (RelativeLayout) view.findViewById(R.id.toprela);
		mFavouritLstv = (PullToRefreshListView) view.findViewById(R.id.home_listv);
		mFavouritLstv.setMode(PullToRefreshBase.Mode.BOTH);
		ListView listView = mFavouritLstv.getRefreshableView();
		listView.addHeaderView(mHeaderView);

		searchText = (EditText) homeTitleView.findViewById(R.id.searchkey_edtv);

		
		scanView= (TextView) view.findViewById(R.id.image_left);
		scanView.setOnClickListener(this);


		mAdapter  = new SPProductListAdapter(mContext , this);
		mFavouritLstv.setAdapter(mAdapter);
		mFavouritLstv.setOnScrollListener(this);


		/** 设置listView header view : 广告轮播 */
		mScrolllayout = (SPPageView)mHeaderView.findViewById(R.id.home_banner_slayout);
		mScrolllayout.setPageListener(this);
		mGallery = (LinearLayout)mHeaderView.findViewById(R.id.home_banner_lyaout);
		mPointerLayout = (ViewGroup)mHeaderView.findViewById(R.id.pointer_layout);
		super.init(view);

		return view;
	}



	@Override
	public void onDetach() {
		super.onDetach();
	}


	@Override
	public void initSubView(View view) {

	}

	@Override
	public void initEvent() {
		searchText.setOnClickListener(this);
		categoryLayout.setOnClickListener(this);
		shopcartLayout.setOnClickListener(this);
		orderLayout.setOnClickListener(this);
		orderLayout.setOnClickListener(this);
		couponLayout.setOnClickListener(this);
		topBtn.setOnClickListener(this);

		mFavouritLstv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>(){
			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				refreshData();
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				loadMoreData();
			}
		});

		mQualityPsv.setProductScrollViewListener(this);
		mHotPsv.setProductScrollViewListener(this);
		mNewPsv.setProductScrollViewListener(this);

	}

	@Override
	public void initData() {
		refreshData();
		mHnadler = new HomeHandler();
	}

	public void refreshData() {

		this.mPageIndex = 1;
		mIsMaxPage = false;

		//首页: 促销商品 , 新品上市, 精品推荐
		SPHomeRequest.getHomePageData(new SPSuccessListener() {
			@Override
			public void onRespone(String msg, Object response) {
				hideLoadingToast();
				mDataJson = (JSONObject) response;
				try {
					//促销商品
					if (!mDataJson.isNull("promotion_goods")) {
						mPromotions = (List<SPProduct>)mDataJson.get("promotion_goods");
					}

					//精品推荐
					if (!mDataJson.isNull("high_quality_goods")) {
						mQualitys = (List<SPProduct>)mDataJson.get("high_quality_goods");
						mQualityPsv.setDataSource(mQualitys);
					}

					//新品上市
					if (!mDataJson.isNull("new_goods")) {
						mNews = (List<SPProduct>)mDataJson.get("new_goods");
						mNewPsv.setDataSource(mNews);
					}

					//热门商品
					if (!mDataJson.isNull("hot_goods")) {
						mHots = (List<SPProduct>)mDataJson.get("new_goods");
						mHotPsv.setDataSource(mHots);
					}

					if (mDataJson.has("banners")) {
						List<SPHomeBanners> banners = (List<SPHomeBanners>) mDataJson.get("banners");
						List<String> gallerys = new ArrayList<String>();
						for (SPHomeBanners banner : banners) {
							gallerys.add(banner.getAdCode());
						}
						buildProductGallery(gallerys);
						buildPointer(mPointerLayout , gallerys.size());
					}

				} catch (Exception e) {
					showToast(e.getMessage());
					e.printStackTrace();
				}
			}
		}, new SPFailuredListener() {
			@Override
			public void onRespone(String msg, int errorCode) {
				hideLoadingToast();
			}
		});

		//猜你喜欢
		SPHomeRequest.getFavouritePageData(this.mPageIndex, new SPSuccessListener() {
			@Override
			public void onRespone(String msg, Object response) {
				if (response != null && (response instanceof ArrayList) && ((ArrayList)response).size() > 0){
					mFavourites = (List<SPProduct>)response;
					mIsMaxPage = false;
					mAdapter.setData(mFavourites);
					mAdapter.notifyDataSetChanged();
				}else{
					mIsMaxPage = true;
				}
				refreshCompleted();
			}
		}, new SPFailuredListener() {
			@Override
			public void onRespone(String msg, int errorCode) {
				SPDialogUtils.showToast(SPMainActivity.getmInstance(), msg);
				refreshCompleted();
			}
		});
	}

	public void loadMoreData(){
		if (mIsMaxPage){
			showToast(getString(R.string.no_more));
			refreshCompleted();

			return;
		}
		mPageIndex++;

		//猜你喜欢
		SPHomeRequest.getFavouritePageData(this.mPageIndex, new SPSuccessListener() {
			@Override
			public void onRespone(String msg, Object response) {
			
				if (response != null && (response instanceof ArrayList) && ((ArrayList)response).size() > 0){
					List<SPProduct> moreFavourts = (List<SPProduct>)response;
					mFavourites.addAll(moreFavourts);
					mIsMaxPage = false;
					mAdapter.setData(mFavourites);
				}else{
					mIsMaxPage = true;
				}
				refreshCompleted();
			}
		}, new SPFailuredListener() {
			@Override
			public void onRespone(String msg, int errorCode) {
				SPDialogUtils.showToast(SPMainActivity.getmInstance(), msg);
				refreshCompleted();
				mPageIndex--;
			}
		});
	}

	public void refreshCompleted(){
		mHnadler.post(new Runnable() {
			@Override
			public void run() {
				mFavouritLstv.onRefreshComplete();
			}
		});
	}


	/**
	 * // 获取子view个数, 用来计算圆点指示器个数
	 = mGallery.getChildCount();

	 */

	/**
	 * 构建轮播广告"圆点指示器"
	 */
	public void buildPointer(ViewGroup pLayout , int count){

		int pageSize = count;
		ImageView[] pointerImgv = new ImageView[pageSize];
		pLayout.removeAllViews();
		for (int i = 0; i < pageSize; i++) {
			ImageView imageView = new ImageView(this.mContext);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(20 , 20);//圆点指示器宽高
			lp.setMargins(8, 0, 8, 0);
			imageView.setLayoutParams(lp);
			imageView.setPadding(20, 0, 20, 0);
			pointerImgv[i] = imageView;
			if (i == 0) {
				//默认选中第一张图片
				pointerImgv[i].setBackgroundResource(R.drawable.ic_home_arrows_focus);
			} else {
				pointerImgv[i].setBackgroundResource(R.drawable.ic_home_arrows_normal);
			}
			pLayout.addView(pointerImgv[i]);
		}
	}


	public void setMainActivity(SPMainActivity mainActivity){
		this.mainActivity = mainActivity;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()){
			case R.id.searchkey_edtv:
				Intent intent = new Intent(getActivity() , SPSearchCommonActivity.class);
				getActivity().startActivity(intent);
				break;
			case R.id.home_menu_categroy_layout :
				mainActivity.setShowFragment(CATEGORY_FRAGMENT);
				break;
			case R.id.home_menu_shopcart_layout :
				mainActivity.setShowFragment(SHOPCART_FRAGMENT);
				break;
			case R.id.home_menu_order_layout :
				startupOrderList(SPOrderUtils.OrderStatus.all.value());
				break;
			case R.id.home_menu_coupon_layout :
				startupCollection();
				break;
			case R.id.top_ibtn :
				scrollTop();
				break;
			case R.id.image_left:
				Intent intentC = new Intent();
				intentC.setClass(getActivity(), MipcaActivityCapture.class);
				intentC.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intentC, SCANNIN_GREQUEST_CODE);
				break;
		}
	}

	


	
	
	
	/**
	 * 滚回到顶部
	 */
	public void scrollTop(){
		mFavouritLstv.getRefreshableView().setSelection(0);
	}
	
	boolean checkLogin(){
		if (!SPMobileApplication.getInstance().isLogined){
			showToastUnLogin();
			toLoginPage();
			return false;
		}
		return true;
	}

	public void startupCollection(){
		if (!checkLogin())return;
		Intent collectIntent = new Intent(getActivity() , SPCollectListActivity.class);
		getActivity().startActivity(collectIntent);
	}

	public void startupOrderList(int orderStatus){
		if (!SPMobileApplication.getInstance().isLogined){
			showToastUnLogin();
			toLoginPage();
			return;
		}
		Intent allOrderList = new Intent(getActivity() , SPOrderListActivity.class);
		allOrderList.putExtra("orderStatus" , orderStatus);
		getActivity().startActivity(allOrderList);
	}

	/**
	 * 我的收藏
	 * @param
	 */
	public void startupCollect(){
		if (!SPMobileApplication.getInstance().isLogined){
			showToastUnLogin();
			toLoginPage();
			return;
		}
		Intent collectIntent = new Intent(getActivity() , SPCollectListActivity.class);
		getActivity().startActivity(collectIntent);
	}

	public void startupProductListActivity(SPCategory category){
		Intent intent = new Intent(getActivity() , SPProductListActivity.class);
		if (category!=null){
			intent.putExtra("category", category);
		}
		getActivity().startActivity(intent);
	}

	public void startupActivity(String goodsID){
		Intent intent = new Intent(SPMainActivity.getmInstance() , SPProductDetailActivity.class);
		intent.putExtra("goodsID", goodsID);
		startActivity(intent);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode){
				case SCANNIN_GREQUEST_CODE:
					Bundle bundle = data.getExtras();
					//显示扫描到的内容
					//mTextView.setText(bundle.getString("result"));
					//显示
					//mImageView.setImageBitmap((Bitmap) data.getParcelableExtra("bitmap"));
					showToast(bundle.getString("result"));
					break;
			}
		}
	}

	private void buildProductGallery(List<String> gallerys){
		if(gallerys == null || gallerys.size() < 1)return;
		mGallery.removeAllViews();
		int height = Float.valueOf(getResources().getDimension(R.dimen.dp_180)).intValue();
		mScrolllayout.setScreenHeight(height);
		for (int i = 0; i < gallerys.size(); i++){
			String url = gallerys.get(i);

			ImageView imageView = new ImageView(mContext);
			ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT ,ViewGroup.LayoutParams.MATCH_PARENT);
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			imageView.setLayoutParams(lp);
			Glide.with(this).load(url).placeholder(R.drawable.icon_banner_null).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imageView);
			mScrolllayout.addPage(imageView);
		}
	}

	@Override
	public void onItemClickListener(SPProduct product) {
		startupActivity(product.getGoodsID());
	}

	@Override
	public void onScrollViewItemClick(SPProduct product) {
		startupActivity(product.getGoodsID());
	}

	@Override
	public void page(int page) {

		int count = mPointerLayout.getChildCount();
		for(int i=0; i<count; i++){
			View v = mPointerLayout.getChildAt(i);
			if(v instanceof ImageView){
				if(i == page){
					v.setBackgroundResource(R.drawable.ic_home_arrows_focus);
				}else{
					v.setBackgroundResource(R.drawable.ic_home_arrows_normal);
				}
			}
		}
	}


	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

		int alpha = 0;
		int scrollH = 0;
		mCurrentfirstVisibleItem = firstVisibleItem;
		View firstView = view.getChildAt(0);
		if (null != firstView) {
			ItemRecod itemRecord = (ItemRecod) recordSp.get(firstVisibleItem);
			if (null == itemRecord) {
				itemRecord = new ItemRecod();
			}
			itemRecord.height = firstView.getHeight();
			itemRecord.top = firstView.getTop();
			recordSp.append(firstVisibleItem, itemRecord);
			scrollH = getScrollY();//滚动距离
		}

		if(scrollH < 300){
			alpha = Float.valueOf(scrollH / 300.0f * (255-55)).intValue();// (int) (new Float(scrollDistance) / new Float(titleHeight) * 200);//255
		}else{
			alpha = 255 - 55;
		}
		homeTitleView.getBackground().setAlpha(alpha);

		if(scrollH > 1200){
			topBtn.setVisibility(View.VISIBLE);
		}else{
			topBtn.setVisibility(View.INVISIBLE);
		}
	}

	private int getScrollY() {
		int height = 0;
		for (int i = 0; i < mCurrentfirstVisibleItem; i++) {
			ItemRecod itemRecod = (ItemRecod) recordSp.get(i);
			if(itemRecod!=null){
				height += itemRecod.height;
			}
		}
		ItemRecod itemRecod = (ItemRecod) recordSp.get(mCurrentfirstVisibleItem);
		if (null == itemRecod) {
			itemRecod = new ItemRecod();
		}
		return height - itemRecod.top;
	}


	class ItemRecod {
		int height = 0;
		int top = 0;
	}

	class HomeHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

		}
	}

}
