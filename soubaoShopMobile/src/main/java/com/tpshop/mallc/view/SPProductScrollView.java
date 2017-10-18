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
 * Date: @date 2015年11月14日 下午8:17:18 
 * Description: 自定义产品滑动view , 每一屏显示3个产品
 * @version V1.0
 */
package com.tpshop.mallc.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tpshop.mallc.R;
import com.tpshop.mallc.SPMainActivity;
import com.tpshop.mallc.common.SPMobileConstants;
import com.tpshop.mallc.global.SPMobileApplication;
import com.tpshop.mallc.model.SPProduct;
import com.tpshop.mallc.utils.SMobileLog;
import com.soubao.tpshop.utils.SPCommonUtils;

import java.util.List;

/**
 * @author 飞龙
 *
 */
public class SPProductScrollView extends LinearLayout implements SPPageView.PageListener , View.OnClickListener {

	SPPageView mPagev;			//滑屏PageView
	LinearLayout mPointerLayout;//指示器
	List<SPProduct> mProducts;	//产品列表
	private Context mContext;
	private ProductScrollViewListener mListener;

	/**
	 * @param context
	 * @param attrs
	 */
	public SPProductScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.mContext = context;
		/** 获取自定义属性 titleText */
		View view = LayoutInflater.from(context).inflate(R.layout.product_scrollview, this);
		mPagev = (SPPageView)view.findViewById(R.id.pagev);
		mPointerLayout = (LinearLayout) view.findViewById(R.id.pointer_layout);
		mPagev.setPageListener(this);
	}

	public void setProductScrollViewListener(ProductScrollViewListener listener){
		this.mListener = listener;
	}

	/**
	 * 设置数据源
	 * @param products
     */
	public void setDataSource(List<SPProduct> products){
		if (products == null)return;

		this.mProducts = products;
		buildQualityGallery();
		buildPointer();
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

	private void buildQualityGallery(){
		if(mProducts == null || mProducts.size() < 1)return;
		//先删除原来旧的子view
		((LinearLayout)mPagev.getChildAt(0)).removeAllViews();
		DisplayMetrics displayMetrics = SPMobileApplication.getInstance().getDisplayMetrics();
		//每个item直接的间距
		float marginSpaceHalf = getResources().getDimension(R.dimen.margin_space_half);

		//计算每一个item宽度
		float itemWidth = (displayMetrics.widthPixels - 4 * marginSpaceHalf) / 3 ;

		for (int i = 0; i < mProducts.size(); i++){
			SPProduct product = mProducts.get(i);

			View productView = LayoutInflater.from(SPMainActivity.getmInstance()).inflate(R.layout.home_middle_product_item, null, false);
			View itemView = productView.findViewById(R.id.item_contain_layout);
			itemView.setTag(product);
			itemView.setOnClickListener(this);
			ImageView productImgv = (ImageView)productView.findViewById(R.id.product_imgv);
			TextView nameTxtv = (TextView)productView.findViewById(R.id.product_name_txtv);
			TextView priceTxtv = (TextView)productView.findViewById(R.id.product_price_txtv);

			String imgUrl = SPCommonUtils.getThumbnail(SPMobileConstants.FLEXIBLE_THUMBNAIL, product.getGoodsID());
			nameTxtv.setText(product.getGoodsName());
			priceTxtv.setText("¥"+product.getShopPrice());

			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(Float.valueOf(itemWidth).intValue() , ViewGroup.LayoutParams.MATCH_PARENT);
			if(i!=0 && i%3==2){
				//每一屏的最后一个设置右边间距
				lp.setMargins(Float.valueOf(marginSpaceHalf).intValue() , 0 , Float.valueOf(marginSpaceHalf).intValue() , 0);
			}else{
				lp.setMargins(Float.valueOf(marginSpaceHalf).intValue() , 0 , 0 , 0);
			}
			productView.setLayoutParams(lp);
			Glide.with(mContext).load(imgUrl).placeholder(R.drawable.icon_product_null).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(productImgv);
			mPagev.addPage(productView , lp);
		}
	}

	/**
	 * 构建轮播广告"圆点指示器"
	 */
	public void buildPointer(){
		int count = mProducts.size();
		int pageSize = Double.valueOf(Math.ceil(count/3.0)).intValue();
		ImageView[] pointerImgv = new ImageView[pageSize];
		mPointerLayout.removeAllViews();
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
			mPointerLayout.addView(pointerImgv[i]);
		}
	}

	@Override
	public void onClick(View v) {

		 if(v.getTag() instanceof  SPProduct){
			 SPProduct product = (SPProduct)v.getTag();
			 if(this.mListener!=null)this.mListener.onScrollViewItemClick(product);
		 }
	}

	public interface ProductScrollViewListener{
		public void onScrollViewItemClick(SPProduct product);
	}
}





















































































