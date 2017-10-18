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
 * $description: 用户注册/找回密码
 */
package com.tpshop.mallc.activity.person.user;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tpshop.mallc.R;
import com.tpshop.mallc.SPMainActivity;
import com.tpshop.mallc.activity.common.SPBaseActivity;
import com.tpshop.mallc.common.SPMobileConstants;
import com.tpshop.mallc.global.SPMobileApplication;
import com.tpshop.mallc.http.base.SPFailuredListener;
import com.tpshop.mallc.http.base.SPSuccessListener;
import com.tpshop.mallc.http.person.SPUserRequest;
import com.tpshop.mallc.model.person.SPUser;
import com.tpshop.mallc.utils.SMobileLog;
import com.tpshop.mallc.utils.SPServerUtils;
import com.soubao.tpshop.utils.SPStringUtils;
import com.tpshop.mallc.utils.SPUtils;
import com.tpshop.mallc.utils.SPValidate;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.register_forget)
public class SPRegisterOrForgetActivity extends SPBaseActivity {

    private String TAG = "SPRegisterOrForgetActivity";

    @ViewById(R.id.phone_num_edtv)
    EditText phoneNumEdtv;          //手机号码

    @ViewById(R.id.check_code_edtv)
    EditText checkCodeEdtv;          //验证码

    @ViewById(R.id.submit_btn)
    Button submitBtn;                  //提交

    @ViewById(R.id.send_code_btn)
    Button sendCodeBtn;              //发送短信验证码

    @ViewById(R.id.password_edtv)
    EditText passwordEdtv;

    @ViewById(R.id.repassword_edtv)
    EditText repasswordEdtv;

    String mPhoneNumber;    //手机号码
    String mCheckCode;      //验证码
    int mActionType;        //操作类型
    int mSmsTimeOut;        //短信验证码超时时间
    String scene = "1";     //发送短信场景

    CheckCodeCountTimer mCountDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (SPMobileApplication.getInstance().actionType == 1){
            super.setCustomerTitle(true,true,getString(R.string.register_title));//注册
        }else{
            super.setCustomerTitle(true,true,getString(R.string.forget_title));//找回密码
        }
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
            mPhoneNumber = getIntent().getStringExtra(SPMobileConstants.KEY_PHONE_NUMBER);
            mActionType = getIntent().getIntExtra(SPMobileConstants.KEY_ACTION_TYPE , 1);
        }

        if (SPMobileApplication.getInstance().actionType == 1){
            //注册
            submitBtn.setText(getString(R.string.register_title));
            scene = "1";
        }else{
            //找回密码
            submitBtn.setText(getString(R.string.forget_title));
            scene = "2";
        }

        if (SPMobileConstants.DevTest){
            phoneNumEdtv.setText("15889560679");
            passwordEdtv.setText("123456");
            repasswordEdtv.setText("123456");
        }

        sendCodeBtn.setText(getString(R.string.register_btn_re_code_done));
        sendCodeBtn.setBackgroundResource(R.drawable.btn_bg);
        mSmsTimeOut = SPServerUtils.getSmsTimeOut();
        mCountDownTimer = new CheckCodeCountTimer(mSmsTimeOut*1000 , 1000);
    }

    @Override
    public void initEvent() {

    }

    @Click({R.id.submit_btn , R.id.send_code_btn})
    public void onViewClick(View v) {

        mPhoneNumber = phoneNumEdtv.getText().toString();

        if (SPStringUtils.isEmpty(mPhoneNumber)) {
            phoneNumEdtv.setError(Html.fromHtml("<font color='red'>"+getString(R.string.register_phone_number_null)+"</font>"));
            return;
        } else if (!SPValidate.validatePhoneNumber(mPhoneNumber)) {
            phoneNumEdtv.setError(Html.fromHtml("<font color='red'>"+getString(R.string.register_error_phone_format_error)+"</font>"));
            return;
        }

        if (v.getId() == R.id.submit_btn) {
            //提交
            mCheckCode = checkCodeEdtv.getText().toString();
            if (SPStringUtils.isEmpty(mCheckCode)){
                checkCodeEdtv.setError(Html.fromHtml("<font color='red'>"+getString(R.string.edit_code_null)+"</font>"));
                return;
            }

            String pwd = passwordEdtv.getText().toString();
            String repwd = repasswordEdtv.getText().toString();

            if (SPStringUtils.isEmpty(pwd)) {
                passwordEdtv.setError(Html.fromHtml("<font color='red'>"+getString(R.string.register_password_null)+"</font>"));
                return;
            }

            if (SPStringUtils.isEmpty(repwd)) {
                repasswordEdtv.setError(Html.fromHtml("<font color='red'>"+getString(R.string.register_confirm_password_null)+"</font>"));
                return;
            }
            if (!pwd.equals(repwd)){
                repasswordEdtv.setError(Html.fromHtml("<font color='red'>"+getString(R.string.register_error_info_re)+"</font>"));
                return;
            }
            if (pwd.length()< 6 || pwd.length() > 16) {
                repasswordEdtv.setError(Html.fromHtml("<font color='red'>"+getString(R.string.register_error_info)+"</font>"));
                return;
            }

            if(SPMobileApplication.getInstance().actionType == 1){
                //注册
                showLoadingSmallToast();
                SPUserRequest.doRegister(mPhoneNumber, pwd , mCheckCode ,
                        new SPSuccessListener(){
                            @Override
                            public void onRespone(String msg, Object response) {
                                hideLoadingSmallToast();
                                if (response != null) {
                                    SPUser user = (SPUser)response;
                                    showToast("注册成功!");
                                    SPMobileApplication.getInstance().setLoginUser(user);
                                    SPMobileApplication.getInstance().isLogined = true;
                                    startActivity(new Intent(SPRegisterOrForgetActivity.this, SPMainActivity.class));
                                }
                            }
                        },new SPFailuredListener(){
                            @Override
                            public void onRespone(String msg, int errorCode) {
                                hideLoadingSmallToast();
                                showToast(msg);
                            }
                        });
            }else{
                //找回密码
                SPUserRequest.forgetPassword(mPhoneNumber, pwd, mCheckCode, new SPSuccessListener() {
                    @Override
                    public void onRespone(String msg, Object response) {
                        showToast(msg);
                        //充值密码, 跳到登录页面
                        Intent intent = new Intent(SPRegisterOrForgetActivity.this , SPLoginActivity_.class);
                        startActivity(intent);
                    }
                }, new SPFailuredListener() {
                    @Override
                    public void onRespone(String msg, int errorCode) {
                        showToast(msg);
                    }
                });
            }

        } else if (v.getId() == R.id.send_code_btn) {

            //发送短信验证码
            mCountDownTimer.start();
            setSendSmsButtonStatus(false);

            if(SPMobileConstants.ENABLE_SMS_CODE){
                //启用短信验证码
                    SPUserRequest.sendSmsValidateCode(mPhoneNumber, scene,  new SPSuccessListener(){
                        @Override
                        public void onRespone(String msg, Object response) {
                            showToast(msg);
                            mCountDownTimer.start();
                            setSendSmsButtonStatus(false);
                        }
                    },new SPFailuredListener(){
                        @Override
                        public void onRespone(String msg, int errorCode) {
                            showToast(msg);
                        }
                    });
            }else{
                //未启用短信验证码
                setSendSmsButtonStatus(false);
                checkCodeEdtv.setText("1234");
            }
        }
    }

    /**
     * 修改发送短信验证码状态
     * @param enable
     */
    public void setSendSmsButtonStatus(boolean enable){
        if (enable){
            //启用
            sendCodeBtn.setEnabled(true);
            sendCodeBtn.setBackgroundResource(R.drawable.btn_bg);
            sendCodeBtn.setTextColor(getResources().getColor(R.color.white));
        }else{
            //禁用
            sendCodeBtn.setEnabled(false);
            sendCodeBtn.setBackgroundResource(R.drawable.btn_unpressed);
            sendCodeBtn.setTextColor(getResources().getColor(R.color.color_font_gray));
        }
    }

    public class CheckCodeCountTimer extends CountDownTimer{

        public CheckCodeCountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            SMobileLog.i(TAG , "onTick millisUntilFinished : "+millisUntilFinished/1000);
            sendCodeBtn.setText(getString(R.string.register_btn_re_code,millisUntilFinished / 1000));
        }

        @Override
        public void onFinish() {
            SMobileLog.i(TAG , "onFinish...");
            sendCodeBtn.setText(getString(R.string.register_btn_re_code_done));
            setSendSmsButtonStatus(true);
        }
    };
}
