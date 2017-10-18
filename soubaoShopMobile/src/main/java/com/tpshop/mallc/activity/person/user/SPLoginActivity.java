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
 * $description: 登录
 */
package com.tpshop.mallc.activity.person.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tpshop.mallc.R;
import com.tpshop.mallc.SPMainActivity;
import com.tpshop.mallc.activity.common.SPBaseActivity;
import com.tpshop.mallc.common.SPMobileConstants;
import com.tpshop.mallc.global.SPMobileApplication;
import com.tpshop.mallc.http.base.SPFailuredListener;
import com.tpshop.mallc.http.base.SPMobileHttptRequest;
import com.tpshop.mallc.http.base.SPSuccessListener;
import com.tpshop.mallc.http.person.SPUserRequest;
import com.tpshop.mallc.model.person.SPUser;
import com.tpshop.mallc.utils.SMobileLog;
import com.tpshop.mallc.utils.SPDialogUtils;
import com.soubao.tpshop.utils.SPStringUtils;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@EActivity(R.layout.activity_splogin)
public class SPLoginActivity extends SPBaseActivity {

    private String TAG = "SPLoginActivity";

    public static String KEY_FROM = "from";

    private UMShareAPI mShareAPI = null;

    @ViewById(R.id.edit_phone_num)
    EditText txtPhoneNum;
    @ViewById(R.id.edit_password)
    EditText txtPassword;
    @ViewById(R.id.btn_login)
    Button btnLogin;
    @ViewById(R.id.txt_register)
    TextView txtRegister;
    @ViewById(R.id.txt_forget_pwd)
    TextView txtForgetPwd;


    @ViewById(R.id.test_account_txtv)
    TextView txtTestAccount;

    @ViewById(R.id.test_pwd_txtv)
    TextView txtTestPwd;


    @ViewById(R.id.qq_icon_txt)
    TextView qqIconTxt;

    @ViewById(R.id.wx_icon_txt)
    TextView wxIconTxt;

    String fromActivity = "";   //跳转路径, 从其他activity跳转到登录页面的路径, 如果该值存在则进入原来页面, 否则跳转到主页面

    SHARE_MEDIA platform ;   //哪个平台

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setCustomerTitle(true,true,getString(R.string.login_title));
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

        /** init auth api**/
        mShareAPI = UMShareAPI.get( this );

        if (!SPMobileConstants.IsRelease){
            txtTestAccount.setText("测试账号:13800138006");
            txtTestPwd.setText("测试密码:123456");
            txtPhoneNum.setText("13800138006");
            txtPassword.setText("123456");
        }

        fromActivity = getIntent().getStringExtra(KEY_FROM);
    }

    @Override
    public void initEvent() {

    }

    public void onLoginClick(View view){

        if (SPStringUtils.isEditEmpty(txtPhoneNum)) {
            txtPhoneNum.setError(Html.fromHtml("<font color='red'>"+getString(R.string.login_phone_number_null)+"</font>"));
            return;
        }
        if (SPStringUtils.isEditEmpty(txtPassword)) {
            txtPassword.setError(Html.fromHtml("<font color='red'>"+getString(R.string.login_password_null)+"</font>"));
            return;
        }
        showLoadingSmallToast();
        SPUserRequest.doLogin(txtPhoneNum.getText().toString(),txtPassword.getText().toString(),
                new SPSuccessListener(){
                    @Override
                    public void onRespone(String msg, Object response) {
                        hideLoadingSmallToast();
                        if (response != null) {
                            SPUser user = (SPUser)response;
                            SPMobileApplication.getInstance().setLoginUser(user);
							SPLoginActivity.this.sendBroadcast(new Intent(SPMobileConstants.ACTION_LOGIN_CHNAGE));
                            showToast("登录成功");
                            loginSuccess();
                        }
                    }
                },new SPFailuredListener(){
                    @Override
                    public void onRespone(String msg, int errorCode) {
                        hideLoadingSmallToast();
                        showToast("账号密码错误");
                    }
                });

    }

    private void loginSuccess(){
        if (SPStringUtils.isEmpty(fromActivity)){
            Intent intent = new Intent();
            intent.setClass(this,SPMainActivity.class);
            startActivity(intent);
        }
        finish();
    }

    public void onRegisterClick(View view){
        SPMobileApplication.getInstance().actionType = 1;
        Intent registerIntent = new Intent(this, SPRegisterOrForgetActivity_.class);
        registerIntent.putExtra(SPMobileConstants.KEY_ACTION_TYPE , 1);
        startActivity(registerIntent);
    }

    public void onForgetPwdClick(View view){
        SPMobileApplication.getInstance().actionType = 2;
        Intent forgetIntent = new Intent(this, SPRegisterOrForgetActivity_.class);
        forgetIntent.putExtra(SPMobileConstants.KEY_ACTION_TYPE , 2);
        startActivity(forgetIntent);
    }

    @Click({R.id.qq_icon_txt ,R.id.wx_icon_txt})
    public void onClickListener(View v) {
        switch (v.getId()){
            case R.id.qq_icon_txt:
                loginWithQQ();
                break;
            case R.id.wx_icon_txt:
                loginWithWeiXin();
                break;
        }
    }

    /**
     * QQ第三方登录
     */
    public void loginWithQQ(){

        platform = SHARE_MEDIA.QQ;
        boolean isInstall = mShareAPI.isInstall(this , platform);
        if (!isInstall){
            showToast("请先安装QQ!");
            return;
        }
        mShareAPI.getPlatformInfo(this , platform ,umUserinfoListener);
    }

    /**
     * 微信第三方登录
     */
    public void loginWithWeiXin(){
        platform = SHARE_MEDIA.WEIXIN;
       if (!mShareAPI.isInstall(this , platform)){
            showToast("请先安装微信!");
            return;
        }
        //mShareAPI.getPlatformInfo(this , platform ,umUserinfoListener);
        mShareAPI.doOauthVerify(SPLoginActivity.this , platform ,umAuthListener);
    }

     /**
     * 授权成功
     */
    private UMAuthListener umAuthListener = new UMAuthListener() {

        @Override
        public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
            //授权成功, 获取用户信息
            mShareAPI.getPlatformInfo(SPLoginActivity.this , platform , umUserinfoListener);
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            Toast.makeText( getApplicationContext(), "授权失败:"+t.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Toast.makeText( getApplicationContext(), "取消授权", Toast.LENGTH_SHORT).show();
        }
    };


    /**
     * 获取用户信息
     */
    private UMAuthListener umUserinfoListener = new UMAuthListener() {

        @Override
        public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
            //调用后台接口进行登录
            thirdLoginWithMap(map);

        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            //信息获取失败, 先去授权
            mShareAPI.doOauthVerify(SPLoginActivity.this , platform ,umAuthListener);
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Toast.makeText( getApplicationContext(), "Authorize cancel", Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mShareAPI.HandleQQError(this,requestCode,umAuthListener);
        UMShareAPI mShareAPI = UMShareAPI.get(this);
        mShareAPI.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 第三方登录
     * @param userMap
     */
    public void thirdLoginWithMap(Map<String , String> userMap){

        String nickName = userMap.get("screen_name");                   //昵称
        String gender = getGenderByCn(userMap.get("gender"));           //性别
        String headPic = userMap.get("profile_image_url");              //头像
        String openid = userMap.get("openid");                          //头像
        String from = (platform == SHARE_MEDIA.WEIXIN ? "weixin" : "qq");   //平台
        String unionid = userMap.get("unionid");
        showLoadingSmallToast();
        SPUserRequest.loginWithThirdPart(openid,unionid , from, nickName, headPic, gender, new SPSuccessListener() {
            @Override
            public void onRespone(String msg, Object response) {
                hideLoadingSmallToast();
                if (response != null) {
                    SPUser user = (SPUser)response;
                    SPMobileApplication.getInstance().setLoginUser(user);
                    SPLoginActivity.this.sendBroadcast(new Intent(SPMobileConstants.ACTION_LOGIN_CHNAGE));
                    showToast("登录成功");
                    loginSuccess();
                }
            }
        }, new SPFailuredListener() {
            @Override
            public void onRespone(String msg, int errorCode) {
                hideLoadingSmallToast();
                showToast(msg);
            }
        });
    }

    public String getGenderByCn(String gender){

        if (gender.equals("男")){
            return "1";
        }else if (gender.equals("女")){
            return "2";
        }else{
            return gender;
        }
    }
}
