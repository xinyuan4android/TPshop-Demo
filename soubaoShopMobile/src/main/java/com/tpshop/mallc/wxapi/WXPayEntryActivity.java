package com.tpshop.mallc.wxapi;

import com.tpshop.mallc.R;
import com.tpshop.mallc.activity.common.SPBaseActivity;
import com.tpshop.mallc.activity.common.SPPayCompletedActivity_;
import com.tpshop.mallc.activity.common.SPPayListActivity;
import com.tpshop.mallc.common.SPMobileConstants;
import com.tpshop.mallc.global.SPMobileApplication;
import com.tpshop.mallc.model.order.SPOrder;
import com.tpshop.mallc.utils.SMobileLog;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class WXPayEntryActivity extends SPBaseActivity implements IWXAPIEventHandler , View.OnClickListener{
	
	private static final String TAG = "WXPayEntryActivity";
	
    private IWXAPI api;

	private ImageView mPayCompletedImgv ;
	private TextView mTradeNoTxtv  ;
	private TextView mPayMoneyTxtv  ;
	private TextView mPayTipTxtv  ;
	private Button mOrderBtn  ;
	private Button mHomeOrPayBtn  ;
	private int mPayStatus  ;	//支付状态, 1: 支付成功, 0:取消支付 , -1: 支付错误

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wx_pay_result);

		super.init();

    	api = WXAPIFactory.createWXAPI(this, SPMobileConstants.pluginLoginWeixinAppid);
        api.handleIntent(getIntent(), this);
    }

	@Override
	public void initSubViews() {

		mPayCompletedImgv = (ImageView)findViewById(R.id.pay_completed_imgv);
		mTradeNoTxtv = (TextView)findViewById(R.id.pay_trade_no_txtv);
		mPayMoneyTxtv = (TextView)findViewById(R.id.pay_money_txtv);
		mPayTipTxtv = (TextView)findViewById(R.id.pay_tip_txtv);
		mOrderBtn = (Button)findViewById(R.id.order_btn);
		mHomeOrPayBtn = (Button)findViewById(R.id.home_or_repay_btn);
	}

	@Override
	public void initData() {

	}

	@Override
	public void initEvent() {
		mOrderBtn.setOnClickListener(this);
		mHomeOrPayBtn.setOnClickListener(this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}




	@Override
	public void onResp(BaseResp resp) {

		SMobileLog.i(TAG , "resp.type : "+resp.getType() +" , errCode : "+resp.errCode);

		if(resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX){
			//微信结果返回
			if (resp.errCode == BaseResp.ErrCode.ERR_OK) {//-2 , 用户取消授权
				//Toast.makeText(this , "支付成功"+String.valueOf(resp.errCode) , Toast.LENGTH_LONG).show();
				mPayStatus = 1;
				onPayFinish();
			}else if (resp.errCode == BaseResp.ErrCode.ERR_USER_CANCEL) {
				//用户取消授权
				//Toast.makeText(this , "用户取消授权"+String.valueOf(resp.errCode) , Toast.LENGTH_LONG).show();
				showToast("用户取消支付");
				mPayStatus = 0;
				this.finish();
			}else{
				//支付错误
				//Toast.makeText(this , "支付错误"+String.valueOf(resp.errCode) , Toast.LENGTH_LONG).show();
				showToast("支付错误啦");
				mPayStatus = -1;
				this.finish();
			}
			//refreshView();
		}
	}

	/**
	 支付成功后调用
	 */
	public void onPayFinish(){

		SPOrder order = SPMobileApplication.getInstance().getPayOrder();
		if (order==null){
			this.finish();
			return;
		}
		if(SPPayListActivity.getInstantce()!=null)SPPayListActivity.getInstantce().finish();
		Intent completedIntent = new Intent(this, SPPayCompletedActivity_.class);
		completedIntent.putExtra("tradeFee" , order.getOrderAmount());
		completedIntent.putExtra("tradeNo" , order.getOrderSN());
		startActivity(completedIntent);
	}

	/**
	 * 刷新页面状态
	 */
	public void refreshView(){

		switch (mPayStatus){
			case 1:		//支付成功
				mPayTipTxtv.setText("感谢购买, 我们将尽快安排发货!");
				break;
			case 0:		//取消支付
				mPayTipTxtv.setText("亲, 给个表现的机会!");
				break;
			case -1:	//支付错误
				mPayTipTxtv.setText("抱歉, 支付出错啦");
				break;
		}
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.home_or_repay_btn){
			//
		}else if(v.getId() == R.id.order_btn){
			//
		}
	}
}