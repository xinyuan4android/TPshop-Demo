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
 * Description: 图片选择框
 * @version V1.0
 */
package com.tpshop.mallc.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tpshop.mallc.R;

/**
 * @author 飞龙
 *
 */
public class SPPictureSelectView extends FrameLayout {

	private ImageView imageImgv;
	private ImageView closeImgv;
	private boolean hasPic ;//	是否设置了图片
	/**
	 * @param context
	 * @param attrs
	 */
	public SPPictureSelectView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		/** 获取自定义属性 titleText */
	    View view = LayoutInflater.from(context).inflate(R.layout.picture_select_view, this);
		imageImgv = (ImageView)view.findViewById(R.id.image_imgv);
		closeImgv = (ImageView)view.findViewById(R.id.close_imgv);
		hasPic = false;
	}

	/***
	 * 设置图片
	 */
	public void setPicture(Bitmap bitmpap){
		if(imageImgv!=null){
			imageImgv.setImageBitmap(bitmpap);
			closeImgv.setVisibility(View.VISIBLE);
			hasPic = true;
		}
	}

	/***
	 * 删除图片
	 */
	public void clearPicture(){
		if(imageImgv!=null){
			imageImgv.setImageResource(R.drawable.icon_camera);
			closeImgv.setVisibility(View.INVISIBLE);
			hasPic = false;
		}
	}

	public boolean hasSetPicture(){
		return hasPic ;
	}

}





















































































