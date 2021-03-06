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
 * $description: 账号安全/找回密码
 */
package com.tpshop.mallc.activity.person.user;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.soubao.tpshop.utils.SPStringUtils;
import com.tpshop.mallc.R;
import com.tpshop.mallc.activity.common.SPBaseActivity;
import com.tpshop.mallc.http.base.SPFailuredListener;
import com.tpshop.mallc.http.base.SPSuccessListener;
import com.tpshop.mallc.http.person.SPUserRequest;
import com.tpshop.mallc.utils.SPValidate;


import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.modify_password)
public class SPModifyPasswordActivity extends SPBaseActivity {

    private String TAG = "SPModifyPasswordActivity";

    @ViewById(R.id.old_password_edtv)
    EditText oldPwdEdtv;            //旧密码

    @ViewById(R.id.password_edtv)
    EditText newPwdEdtv;            //新密码

    @ViewById(R.id.repassword_edtv)
    EditText rePwdEdtv;            //确认密码

    @ViewById(R.id.ok_btn)
    Button submitBtn;                  //提交


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.setCustomerTitle(true,true,getString(R.string.title_modify_password));//修改密码
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

    }

    @Override
    public void initEvent() {

    }

    @Click({R.id.ok_btn})
    public void onViewClick(View v) {

        if (v.getId() == R.id.ok_btn){
            String oldPwd = oldPwdEdtv.getText().toString();
            String newPwd = newPwdEdtv.getText().toString();
            String confirmPwd = rePwdEdtv.getText().toString();


            if (SPStringUtils.isEmpty(oldPwd)) {
                oldPwdEdtv.setError(Html.fromHtml("<font color='red'>"+getString(R.string.modify_pwd_old_pwd_null)+"</font>"));
                return;
            }

            if (SPStringUtils.isEmpty(newPwd)){
                newPwdEdtv.setError(Html.fromHtml("<font color='red'>"+getString(R.string.modify_pwd_new_pwd_null)+"</font>"));
                return;
            }

            if (SPStringUtils.isEmpty(confirmPwd)){
                rePwdEdtv.setError(Html.fromHtml("<font color='red'>"+getString(R.string.modify_pwd_confirm_pwd_null)+"</font>"));
                return;
            }

            if (!newPwd.equals(confirmPwd)){
                rePwdEdtv.setError(Html.fromHtml("<font color='red'>"+getString(R.string.modify_pwd_new_confirm_pwd_not_equal)+"</font>"));
                return;
            }
            //验证
            if (!SPValidate.checkPassword(newPwd)) {
                newPwdEdtv.setError(Html.fromHtml("<font color='red'>"+getString(R.string.register_error_info)+"</font>"));
                return;
            }

            //修改密码
            showLoadingSmallToast();
            SPUserRequest.modifyPassword(oldPwd, newPwd, new SPSuccessListener() {
                @Override
                public void onRespone(String msg, Object response) {
                    hideLoadingSmallToast();
                    showToast(msg);
                }
            }, new SPFailuredListener() {
                @Override
                public void onRespone(String msg, int errorCode) {
                    hideLoadingSmallToast();
                    showToast(msg);
                }
            });
        }
    }

}
