/**
 * tpshop
 * ============================================================================
 * * 版权所有 2015-2027 深圳搜豹网络科技有限公司，并保留所有权利。
 * 网站地址: http://www.tp-shop.cn
 * ----------------------------------------------------------------------------
 * 这不是一个自由软件！您只能在不用于商业目的的前提下对程序代码进行修改和使用 .
 * 不允许对程序代码以任何形式任何目的的再发布。
 * ============================================================================
 * $Author: Ben  16/07/08
 * $description: 输入手机号码
 */
package com.tpshop.mallc.activity.person.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.tpshop.mallc.R;
import com.tpshop.mallc.activity.common.SPBaseActivity;
import com.tpshop.mallc.common.SPMobileConstants;
import com.tpshop.mallc.utils.SPUtils;
import com.soubao.tpshop.utils.SPCommonUtils;
import com.soubao.tpshop.utils.SPStringUtils;
import com.tpshop.mallc.utils.SPValidate;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.user_input)
public class SPInputActivity extends SPBaseActivity {

    @ViewById(R.id.phone_num_edtv)
    EditText phoneNumEdtv;          //手机号码

    @ViewById(R.id.next_btn)
    Button nextBtn;                 //下一步

    int mActionType;                 //操作类型, 1:注册;2:找回密码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.setCustomerTitle(true, true , getString(R.string.login_phone_number));
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

        if (getIntent()!=null){
            mActionType = getIntent().getIntExtra(SPMobileConstants.KEY_ACTION_TYPE , 1);
        }

        if (!SPMobileConstants.IsRelease){
            phoneNumEdtv.setText("15889560679");
        }
    }

    @Override
    public void initEvent() {

    }

    @Click({R.id.next_btn})
    public void onViewClick(View v){
        if (v.getId() == R.id.next_btn){
           String phoneNumber = phoneNumEdtv.getText().toString();

            if (SPStringUtils.isEmpty(phoneNumber)) {
                phoneNumEdtv.setError(Html.fromHtml("<font color='red'>"+getString(R.string.register_phone_number_null)+"</font>"));
                return;
            } else if (!SPValidate.validatePhoneNumber(phoneNumber)) {
                phoneNumEdtv.setError(Html.fromHtml("<font color='red'>"+getString(R.string.register_error_phone_format_error)+"</font>"));
                return;
            }


        }
    }
}
