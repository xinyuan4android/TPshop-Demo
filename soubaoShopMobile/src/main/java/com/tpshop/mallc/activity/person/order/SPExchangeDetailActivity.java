/**
 * shopmobile for tpshop
 * ============================================================================
 * 版权所有 2015-2127 深圳搜豹网络科技有限公司，并保留所有权利。
 * 网站地址: http://www.tp-shop.cn
 * ——————————————————————————————————————
 * 这不是一个自由软件！您只能在不用于商业目的的前提下对程序代码进行修改和使用 .
 * 不允许对程序代码以任何形式任何目的的再发布。
 * ============================================================================
 * Author: 飞龙  wangqh01292@163.com
 * Date: @date 2015-10-15 20:32:41
 * Description: 退换货进度详情
 * @version V1.0
 */
package com.tpshop.mallc.activity.person.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tpshop.mallc.R;
import com.tpshop.mallc.activity.common.SPBaseActivity;
import com.tpshop.mallc.common.SPMobileConstants;
import com.tpshop.mallc.http.base.SPFailuredListener;
import com.tpshop.mallc.http.base.SPMobileHttptRequest;
import com.tpshop.mallc.http.base.SPSuccessListener;
import com.tpshop.mallc.http.person.SPPersonRequest;
import com.tpshop.mallc.model.order.SPExchange;
import com.soubao.tpshop.utils.SPCommonUtils;
import com.tpshop.mallc.utils.SPOrderUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.List;

/**
 * Created by admin on 2016/7/4.
 */
@EActivity(R.layout.order_exchange_detail)
public class SPExchangeDetailActivity extends SPBaseActivity {

    private String TAG = "SPExchangeDetailActivity";

    @ViewById(R.id.exchange_product_pic_imgv)
    ImageView picImgv;

    @ViewById(R.id.exchange_product_name_txtv)
    TextView nameTxtv;

    @ViewById(R.id.exchange_status_content_txtv)
    TextView statusTxtv;

    @ViewById(R.id.exchange_reason_content_txtv)
    TextView reasonTxtv;

    @ViewById(R.id.exchange_remark_content_txtv)
    TextView remarkTxtv;

    @ViewById(R.id.exchange_gallery_lyaout)
    LinearLayout exchangeGallery;


    int pageIndex;   //当前第几页:从1开始
    /**
     *  最大页数
     */
    boolean maxIndex;
    String exchangeId;

    SPExchange exchange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.setCustomerTitle(true,true,getString(R.string.title_exchange_detail));
        super.onCreate(savedInstanceState);
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

        if ( getIntent()== null || getIntent().getStringExtra("exchangeId") == null){
            showToast(getString(R.string.data_error));
            return;
        }

        exchangeId = getIntent().getStringExtra("exchangeId");
        SPPersonRequest.getExchangeDetailWithId(exchangeId, new SPSuccessListener() {
            @Override
            public void onRespone(String msg, Object response) {

                if (response != null){
                    exchange = (SPExchange)response;
                    refreshView();
                }

            }
        }, new SPFailuredListener() {
            @Override
            public void onRespone(String msg, int errorCode) {
                showToast(msg);
            }
        });
    }

    @Override
    public void initEvent() {

    }

    public void refreshView(){

        String imgUrl = SPCommonUtils.getThumbnail(SPMobileConstants.FLEXIBLE_THUMBNAIL, exchange.getGoodsId());
        Glide.with(this).load(imgUrl).placeholder(R.drawable.icon_product_null).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(picImgv);

        nameTxtv.setText(exchange.getGoodsName());

        String status = SPOrderUtils.getExchangeTypeNameWithType(exchange.getStatus());
        statusTxtv.setText(status);
        reasonTxtv.setText(exchange.getReason());
        remarkTxtv.setText(exchange.getRemark());
        buildProductGallery();
    }

    private void buildProductGallery(){
        List<String> images = exchange.getImages();
        if (SPCommonUtils.verifyArray(images)){
            for (int i = 0; i < images.size(); i++){
                String url = SPMobileConstants.BASE_HOST + images.get(i);
                View view = LayoutInflater.from(this).inflate(R.layout.activity_index_gallery_item, exchangeGallery, false);
                ImageView img = (ImageView) view.findViewById(R.id.id_index_gallery_item_image);
                Glide.with(this).load(url).placeholder(R.drawable.icon_product_null).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(img);
                exchangeGallery.addView(view);
            }
        }
    }


}
