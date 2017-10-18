package com.tpshop.mallc.activity.common;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tpshop.mallc.R;
import com.tpshop.mallc.SPMainActivity;
import com.tpshop.mallc.activity.person.order.SPOrderListActivity_;
import com.tpshop.mallc.utils.SMobileLog;
import com.tpshop.mallc.utils.SPOrderUtils;
import com.soubao.tpshop.utils.SPStringUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * Created by admin on 2016/6/29.
 */
@EActivity(R.layout.pay_completed)
public class SPPayCompletedActivity extends  SPBaseActivity{

    private String TAG = "SPPayCompletedActivity";

    @ViewById(R.id.pay_money_txtv)
    TextView payMoneyText;

    @ViewById(R.id.pay_trade_no_txtv)
    TextView payTradeNoText;

    @ViewById(R.id.home_btn)
    Button homeBtn;

    @ViewById(R.id.order_btn)
    Button orderBtn;

    private String tradeFee ;
    private String tradeNo ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.setCustomerTitle(true, true, getString(R.string.title_pay_completed));
        super.onCreate(savedInstanceState);
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
        if(getIntent()!=null){
            tradeFee = getIntent().getStringExtra("tradeFee");
            tradeNo = getIntent().getStringExtra("tradeNo");
        }

        if (SPStringUtils.isEmpty(tradeFee) || SPStringUtils.isEmpty(tradeNo)){
            showToast("数据不完整!");
            return;
        }

        String totalFeeFmt = "支付金额:¥"+tradeFee;
        String totalNoFmt = "订单编号:" + tradeNo;

        int startIndex = 5;
        int endIndex = totalFeeFmt.length() ;
        SpannableString totalFeeSpanStr = new SpannableString(totalFeeFmt);
        totalFeeSpanStr.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.light_red)), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //设置前景色为洋红色
        payMoneyText.setText(totalFeeSpanStr);

        endIndex = totalNoFmt.length();
        SpannableString tradeNoSpanStr = new SpannableString(totalNoFmt);
        tradeNoSpanStr.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.light_red)), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //设置前景色为洋红色
        payTradeNoText.setText(tradeNoSpanStr);

    }

    @Override
    public void initEvent() {

    }

    @Click({R.id.home_btn , R.id.order_btn})
    public void onButtonClick(View v){
        switch (v.getId()){
            case R.id.home_btn:
                Intent shopcartIntetn = new Intent(this , SPMainActivity.class);
                shopcartIntetn.putExtra(SPMainActivity.SELECT_INDEX , SPMainActivity.INDEX_HOME);
                startActivity(shopcartIntetn);
                this.finish();
                break;
            case R.id.order_btn:
                Intent allOrderList = new Intent(this , SPOrderListActivity_.class);
                allOrderList.putExtra("orderStatus" , SPOrderUtils.OrderStatus.all.value());
                this.startActivity(allOrderList);
                this.finish();
                break;
        }
    }

}
