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
 * Date: @date 2015年10月20日 下午7:13:14 
 * Description:
 * @version V1.0
 */
package com.tpshop.mallc.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.View;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.tpshop.mallc.R;
import com.tpshop.mallc.activity.common.SPIViewController;
import com.tpshop.mallc.activity.person.user.SPLoginActivity;
import com.tpshop.mallc.activity.person.user.SPLoginActivity_;
import com.tpshop.mallc.model.SPProduct;
import com.tpshop.mallc.utils.SPConfirmDialog;
import com.tpshop.mallc.utils.SPDialogUtils;
import com.tpshop.mallc.utils.SPLoadingDialog;
import com.tpshop.mallc.utils.SPLoadingSmallDialog;
import com.soubao.tpshop.utils.SPStringUtils;

import org.json.JSONObject;

import java.util.List;

/**
 * @author admin
 *
 */
public abstract class SPBaseFragment extends Fragment implements SPIViewController {

	SPLoadingDialog mLoadingDialog;
	SPLoadingSmallDialog mLoadingSmallDialog;
	JSONObject mDataJson;
	/**
	 * 跳转登录界面
	 */
	public void gotoLogin(){
		
	}

	public void init(View view){
		initSubView(view);
		initEvent();
		initData();
	}
	
	/**
	 * 取消网络请求
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param obj    设定文件 
	* @return void    返回类型 
	* @throws
	 */
	public void cancelRequest(Object obj){
		
	}

	public void showToast(String msg){
		SPDialogUtils.showToast(getActivity(), msg);
	}

	public void showToastUnLogin(){
		showToast(getString(R.string.toast_person_unlogin));
	}

	public void  toLoginPage(){
		toLoginPage(null);
	}

	public void  toLoginPage(String from){
		Intent loginIntent = new Intent(getActivity() , SPLoginActivity_.class);
		if(!SPStringUtils.isEmpty(from))loginIntent.putExtra(SPLoginActivity.KEY_FROM , from);
		startActivity(loginIntent);
	}

	public void showLoadingToast(){
		showLoadingToast(null);
	}

	public void showLoadingToast(String title){
		mLoadingDialog = new SPLoadingDialog(getActivity() , title);
		mLoadingDialog.setCanceledOnTouchOutside(false);
		mLoadingDialog.show();
	}

	public void hideLoadingToast(){
		if(mLoadingDialog !=null){
			mLoadingDialog.dismiss();
		}
	}

	public void showLoadingSmallToast(){
		mLoadingSmallDialog = new SPLoadingSmallDialog(getActivity());
		mLoadingSmallDialog.setCanceledOnTouchOutside(false);
		mLoadingSmallDialog.show();
	}

	public void hideLoadingSmallToast(){
		if(mLoadingSmallDialog!=null)mLoadingSmallDialog.dismiss();
	}


	public void showConfirmDialog(String message , String title , final SPConfirmDialog.ConfirmDialogListener confirmDialogListener , final int actionType){
		SPConfirmDialog.Builder builder = new SPConfirmDialog.Builder(getActivity());
		builder.setMessage(message);
		builder.setTitle(title);
		builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				//设置你的操作事项
				if(confirmDialogListener!=null)confirmDialogListener.clickOk(actionType);
			}
		});

		builder.setNegativeButton(getResources().getString(R.string.cancel),
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}
	
	/**
	 * 
	* @Description: 初始化子类视图 
	* @param view    设定文件 
	* @return void    返回类型 
	* @throws
	 */
	public abstract void initSubView(View view);
	
	public abstract void initEvent();

	public abstract void initData();

	@Override
	public void gotoLoginPage() {
		/*if (!SPStringUtils.isEmpty(msg)){
			showToast(msg);
		}*/
		toLoginPage();

	}

	@Override
	public void gotoLoginPage(String from) {
		toLoginPage(from);
	}

	/**
	 * 刷新完成
	 */
	public void refreshCompleted(Handler handler , final PtrClassicFrameLayout ptrLayout){

		handler.post(new Runnable() {
			@Override
			public void run() {
				ptrLayout.setLoadMoreEnable(true);
				ptrLayout.refreshComplete();
				ptrLayout.loadMoreComplete(true);
			}
		});
	}

	/**
	 * 加载更多完成
	 */
	public void loadMoreComplete(Handler handler , final PtrClassicFrameLayout ptrLayout){
		handler.post(new Runnable() {
			@Override
			public void run() {
				ptrLayout.loadMoreComplete(true);
			}
		});
	}
}
