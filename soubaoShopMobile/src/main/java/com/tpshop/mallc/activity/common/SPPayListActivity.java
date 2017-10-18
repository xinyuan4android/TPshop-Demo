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
 * Date: @date 2015年10月20日 下午7:52:58
 * Description:Activity 支付列表
 * @version V1.0
 */
package com.tpshop.mallc.activity.common;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.tpshop.mallc.R;
import com.tpshop.mallc.common.SPMobileConstants;
import com.tpshop.mallc.global.SPMobileApplication;
import com.tpshop.mallc.http.base.SPFailuredListener;
import com.tpshop.mallc.http.base.SPSuccessListener;
import com.tpshop.mallc.http.shop.SPShopRequest;
import com.tpshop.mallc.model.order.SPOrder;
import com.tpshop.mallc.model.shop.WxPayInfo;
import com.tpshop.mallc.utils.PayResult;
import com.tpshop.mallc.utils.SMobileLog;
import com.tpshop.mallc.utils.SPPluginUtils;
import com.tpshop.mallc.utils.SPServerUtils;
import com.soubao.tpshop.utils.SPStringUtils;
import com.tpshop.mallc.utils.SignUtils;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by admin on 2016/6/29.
 */
@EActivity(R.layout.pay_list)
public class SPPayListActivity extends  SPBaseActivity{

    private String TAG = "SPPayListActivity";

    @ViewById(R.id.pay_money_txtv)
    TextView payMoneyText;

    private final int SDK_PAY_FLAG = 1;
    private SPOrder order;

    private IWXAPI mWXApi ;

    WxPayInfo mWxPayInfo;

    public static  SPPayListActivity mPayListActivity;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    /**
                     * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                     * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                     * docType=1) 建议商户依赖异步通知
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        showToast("支付成功");
                        onPayFinish();
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(SPPayListActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(SPPayListActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.setCustomerTitle(true, true, getString(R.string.title_pay_list));
        super.onCreate(savedInstanceState);
        mPayListActivity = this;
    }

    public static SPPayListActivity getInstantce(){
        return mPayListActivity;
    }

    @AfterViews
    public  void init(){
        super.init();
    }

    @Override
    public void initSubViews() {

    }

    @Override
    public void initData() {

        if (getIntent()==null || getIntent().getSerializableExtra("order") == null){
            showToast("数据错误, 无法完成支付, 请检查! ");
            this.finish();
            return;
        }
        String wxpayid = SPPluginUtils.getWxPayAppid();
        mWXApi = WXAPIFactory.createWXAPI(this, wxpayid);

        order = (SPOrder)getIntent().getSerializableExtra("order");
        payMoneyText.setText("¥"+order.getOrderAmount());
    }

    @Override
    public void initEvent() {

    }

    @Click({R.id.pay_alipay_aview , R.id.pay_cod_aview , R.id.pay_wechat_aview})
    public void onButtonClick(View v){
        String tip = getString(R.string.toast_next_version);


        switch (v.getId()){
            case R.id.pay_alipay_aview:
                aliPay();
                break;
            case R.id.pay_wechat_aview:
                wxPay();
                break;
            case R.id.pay_cod_aview:
                showToast(tip);
                break;
        }
    }


    public static  String PARTNER = "";
    // 商户收款账号
    public static  String SELLER = "";
    // 商户私钥，pkcs8格式
    public static  String RSA_PRIVATE = "";
    String notifyUrl;
    /**
     *  支付宝支付
     */
    public void aliPay(){

        if (order == null) {
            showToast("订单信息不完整, 无法支付");
            return;
        }

    /*
     *商户的唯一的parnter和seller。
     *签约后，支付宝会为每个商户分配一个唯一的 parnter 和 seller。
     */

    /*============================================================================*/
    /*=======================需要填写商户app申请的===================================*/
    /*============================================================================*/
        PARTNER = SPPluginUtils.getAlipayPartner();
        SELLER = SPPluginUtils.getAlipayAccount();
        RSA_PRIVATE = SPPluginUtils.getAlipayPrivateKey();
    /*============================================================================*/
    /*============================================================================*/
    /*============================================================================*/

       String storeName = SPServerUtils.getStoreName();

        //partner和seller获取失败,提示
        if (SPStringUtils.isEmpty(PARTNER) || SPStringUtils.isEmpty(SELLER) || SPStringUtils.isEmpty(RSA_PRIVATE))
        {
            showToast("缺少partner或者seller或者私钥");
            return;
        }

        /*
         *生成订单信息及签名
         */
        notifyUrl = SPMobileConstants.BASE_HOST + SPMobileConstants.PAY_NOTIFYURL;
        String orderInfo = getOrderInfo(storeName, storeName, order.getOrderAmount() , order.getOrderSN());

        /**
         * 特别注意，这里的签名逻辑需要放在服务端，切勿将私钥泄露在代码中！
         */
        String sign = SignUtils.sign(orderInfo , RSA_PRIVATE);
        if (SPStringUtils.isEmpty(sign)){
            showToast(getString(R.string.error_private));
            return;
        }
        try {
            /**
             * 仅需对sign 做URL编码
             */
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        /**
         * 完整的符合支付宝参数规范的订单信息
         */
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + SignUtils.getSignType();

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(SPPayListActivity.this);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo, true);
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }


    /**
     * create the order info. 创建订单信息
     *
     */
    private String getOrderInfo(String subject, String body, String price , String orderSn) {

        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + PARTNER + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + orderSn + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + notifyUrl + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";
        return orderInfo;
    }

    /**
     *  微信支付
     */
    public void wxPay(){
        try{
            String orderSN =  order.getOrderSN();
            SPMobileApplication.getInstance().setPayOrder(order);

            if (mWxPayInfo!=null){
                startupWxPay();
                return;
            }
            showLoadingSmallToast();
            SPShopRequest.getWxPayInfo(orderSN, new SPSuccessListener() {
                @Override
                public void onRespone(String msg, Object response) {
                    hideLoadingSmallToast();
                    if (response!=null){
                        mWxPayInfo = (WxPayInfo)response;
                        startupWxPay();
                    }
                }
            }, new SPFailuredListener() {
                @Override
                public void onRespone(String msg, int errorCode) {
                    hideLoadingSmallToast();
                    showToast(msg);
                }
            });

        }catch (Exception e){
            Log.e(TAG , "异常："+e.getMessage());
            showToast("异常："+e.getMessage());
        }
    }

    /**
     * 调用微信支付
     */
    public void startupWxPay(){
        PayReq req = new PayReq();

        String extData = (mWxPayInfo.getExtData() != null) ? mWxPayInfo.getExtData() : getString(R.string.app_name);
        req.appId = mWxPayInfo.getAppid();
        req.partnerId = mWxPayInfo.getPartnerid();
        req.prepayId = mWxPayInfo.getPrepayid();
        req.nonceStr = mWxPayInfo.getNoncestr();
        req.timeStamp = mWxPayInfo.getTimestamp();
        req.packageValue = mWxPayInfo.getPackageValue();
        req.sign = mWxPayInfo.getSign();
        req.extData = extData;
        /** 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信 **/
        mWXApi.sendReq(req);

    }



    /**
     支付成功后调用
     */
    public void onPayFinish(){
        this.finish();
        Intent completedIntent = new Intent(this, SPPayCompletedActivity_.class);
        completedIntent.putExtra("tradeFee" , order.getOrderAmount());
        completedIntent.putExtra("tradeNo" , order.getOrderSN());
        startActivity(completedIntent);
    }

}
