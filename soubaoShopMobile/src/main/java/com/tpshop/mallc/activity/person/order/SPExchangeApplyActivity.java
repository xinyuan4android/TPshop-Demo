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
 * Description: 商品列表 -> 退换货商品
 * @version V1.0
 */
package com.tpshop.mallc.activity.person.order;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.loopj.android.http.RequestParams;
import com.tpshop.mallc.R;
import com.tpshop.mallc.activity.common.SPBaseActivity;
import com.tpshop.mallc.activity.shop.SPProductShowListActivity_;
import com.tpshop.mallc.common.SPMobileConstants;
import com.tpshop.mallc.http.base.SPFailuredListener;
import com.tpshop.mallc.http.base.SPMobileHttptRequest;
import com.tpshop.mallc.http.base.SPSuccessListener;
import com.tpshop.mallc.http.person.SPPersonRequest;
import com.tpshop.mallc.model.SPProduct;
import com.soubao.tpshop.utils.SPCommonUtils;
import com.tpshop.mallc.utils.SPServerUtils;
import com.soubao.tpshop.utils.SPStringUtils;
import com.tpshop.mallc.view.SPPictureSelectView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by admin on 2016/7/4.
 */
@EActivity(R.layout.person_exchange_apply)
public class SPExchangeApplyActivity extends SPBaseActivity {

    private String TAG = "SPExchangeApplyActivity";

    private static final int REQUEST_CODE_PHOTO = 0x1;
    private static final int REQUEST_CODE_CAMERA = 0x2;

    @ViewById(R.id.product_pic_imgv)
    ImageView picImgv;

    @ViewById(R.id.product_name_txtv)
    TextView nameTxtv;

    @ViewById(R.id.product_spec_txtv)
    TextView specTxtv;

    @ViewById(R.id.product_change_btn)
    Button exchangeBtn;

    @ViewById(R.id.product_return_btn)
    Button returnBtn;

    @ViewById(R.id.comment_picture1_psv)
    SPPictureSelectView picture1Psv;

    @ViewById(R.id.comment_picture2_psv)
    SPPictureSelectView picture2Psv;

    @ViewById(R.id.comment_picture3_psv)
    SPPictureSelectView picture3Psv;

    @ViewById(R.id.comment_picture4_psv)
    SPPictureSelectView picture4Psv;

    @ViewById(R.id.comment_picture5_psv)
    SPPictureSelectView picture5Psv;

    @ViewById(R.id.comment_content_edtv)
    EditText contentEdtv;

    @ViewById(R.id.limit_txtv)
    TextView limitTxtv;


    @ViewById(R.id.product_address_txtv)
    TextView addressTxtv;

    @ViewById(R.id.product_worktime_txtv)
    TextView worktimeTxtv;

    @ViewById(R.id.product_customer_txtv)
    TextView customerTxtv;

    /**
     *  0:为退货 , 1为换货
     */
    Integer applyType ;

    SPProduct mProduct;
    int selectPictureIndex; //选中图片索引
    final static String imageSavePath = "comment.png";
    private String imageDataPath =  "/sdcard/headPhoto";// 存放本地的头像照片

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.setCustomerTitle(true , true , getString(R.string.title_apply_exchnage));
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

        applyType = 1;

        if (getIntent() == null || getIntent().getSerializableExtra("product") == null){
            showToast(getString(R.string.data_error));
            return;
        }

        mProduct = (SPProduct)getIntent().getSerializableExtra("product");
        nameTxtv.setText(mProduct.getGoodsName());
        specTxtv.setText(mProduct.getSpecKeyName());

        String imgUrl1 = SPCommonUtils.getThumbnail(SPMobileConstants.FLEXIBLE_THUMBNAIL, mProduct.getGoodsID());
        Glide.with(this).load(imgUrl1).placeholder(R.drawable.icon_product_null).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(picImgv);

        imageDataPath = getFilesDir().getPath();

        String workTime = SPServerUtils.getWorkTime();
        String servicePhone = SPServerUtils.getServicePhone();
        String address = SPServerUtils.getAddress();

        addressTxtv.setText("收货地址:" + address);
        worktimeTxtv.setText("工作时间:"+ workTime);
        customerTxtv.setText("服务时间:"+ servicePhone);

        //情况历史残余评论图片
        for (int i=0; i<5; i++){
            deleteImage(i);
        }
    }

    @Override
    public void initEvent() {

    }

    @Click({R.id.submit_btn , R.id.comment_picture1_psv , R.id.comment_picture2_psv , R.id.comment_picture3_psv ,
            R.id.comment_picture4_psv , R.id.comment_picture5_psv , R.id.product_return_btn , R.id.product_change_btn})
    public void onButtonClick(View v){
        switch (v.getId()){
            case R.id.submit_btn:
                commitComment();
                break;
            case R.id.product_return_btn:
                applyType = 0;
                returnBtn.setBackgroundResource(R.drawable.tag_button_bg_checked);
                exchangeBtn.setBackgroundResource(R.drawable.tag_button_bg_unchecked);
                break;
            case R.id.product_change_btn:
                applyType = 1 ;
                returnBtn.setBackgroundResource(R.drawable.tag_button_bg_unchecked);
                exchangeBtn.setBackgroundResource(R.drawable.tag_button_bg_checked);
                break;
            case R.id.comment_picture1_psv:
                if (picture1Psv.hasSetPicture()){
                    picture1Psv.clearPicture();
                    deleteImage(0);
                }else{
                    selectImage(0);
                }

                break;
            case R.id.comment_picture2_psv:
                if (picture2Psv.hasSetPicture()){
                    picture2Psv.clearPicture();
                    deleteImage(1);
                }else{
                    selectImage(1);
                }
                break;
            case R.id.comment_picture3_psv:
                if (picture3Psv.hasSetPicture()){
                    picture3Psv.clearPicture();
                    deleteImage(2);
                }else{
                    selectImage(2);
                }
                break;
            case R.id.comment_picture4_psv:
                if (picture4Psv.hasSetPicture()){
                    picture4Psv.clearPicture();
                    deleteImage(3);
                }else{
                    selectImage(3);
                }
                break;
            case R.id.comment_picture5_psv:
                if (picture5Psv.hasSetPicture()){
                    picture5Psv.clearPicture();
                    deleteImage(4);
                }else{
                    selectImage(4);
                }
                break;
        }


    }

    private void selectImage(int index) {
        this.selectPictureIndex = index;
        final String[] items = getResources().getStringArray(R.array.user_head_name);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.user_head_title));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                //boolean result = Utility.checkPermission(MainActivity.this);
                if (item == 0) {

                    Intent intent_pat = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent_pat.putExtra(MediaStore.EXTRA_OUTPUT, Uri
                            .fromFile(new File(Environment
                                    .getExternalStorageDirectory(), "head.jpg")));
                    startActivityForResult(intent_pat, REQUEST_CODE_CAMERA);
                } else if (item == 1) {

                    Intent intent_photo = new Intent(Intent.ACTION_PICK, null);
                    intent_photo.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(intent_photo, REQUEST_CODE_PHOTO);
                }
            }
        });
        builder.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case REQUEST_CODE_PHOTO:
                if (resultCode == RESULT_OK) {
                    shearPhoto(data.getData());// 剪切图片
                }
                break;
            case REQUEST_CODE_CAMERA:
                if (resultCode == RESULT_OK) {
                    File fileTemp = new File(Environment.getExternalStorageDirectory() + "/head.jpg");
                    shearPhoto(Uri.fromFile(fileTemp));// 剪切图片
                }
                break;
            case SPMobileConstants.Result_Code_GetPicture:
                if (data != null) {
                    Bundle mBundle = data.getExtras();
                    Bitmap  mBitmap = mBundle.getParcelable("data");
                    if (mBitmap != null) {
                        /** 上传到服务器 待--- */
                        saveImageToSD(mBitmap);// 保存本地
                        setPicture(mBitmap);
                    }
                }
                break;
        }
    }

    private void setPicture(Bitmap mBitmap){
        switch (selectPictureIndex){
            case 0:
                picture1Psv.setPicture(mBitmap);
                break;
            case 1:
                picture2Psv.setPicture(mBitmap);
                break;
            case 2:
                picture3Psv.setPicture(mBitmap);
                break;
            case 3:
                picture4Psv.setPicture(mBitmap);
                break;
            case 4:
                picture5Psv.setPicture(mBitmap);
                break;
        }
    }

    /**
     * 删除图片
     * @param index
     */
    private void deleteImage(int index){
        File delFile = new File(imageDataPath + "/"+index +"_"+imageSavePath);
        if (delFile.exists()){
            delFile.delete();
        }
    }

    private String getSavePathName(int index){
        return imageDataPath + "/"+index +"_"+imageSavePath;// 图片名字
    }

    private void saveImageToSD(Bitmap bitmap) {

        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {// 检测SD卡的可用性
            showToast("SD存储卡不可用");
            return;
        }
        FileOutputStream fos = null;
        File file = new File(imageDataPath);
        file.mkdirs();// 创建文件夹
        String fileName = getSavePathName(selectPictureIndex);// 图片名字
        try {
            fos = new FileOutputStream(fileName);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);// 压缩后写入文件
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                // 关闭输出流
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @Title: shearPhoto
     * @Description:直接调用系统的剪切功能
     * @param: @param uri
     * @return: void
     * @throws
     */
    private void shearPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, SPMobileConstants.Result_Code_GetPicture);
    }

    /**
     * 提交评论
     */
    private void commitComment(){

        String error = "";
        String content = contentEdtv.getText().toString();
        if (SPStringUtils.isEmpty(content)){
            error =  "请输入退换货原因";
        }else if (content.length() > 120 ){
            error =  "原因内容过长";
        }

        if (!SPStringUtils.isEmpty(error)){
            showToast(error);
            return;
        }

        List<File> images = new ArrayList<File>();
        for(int i=0; i<5; i++){
            String imagePath = getSavePathName(i);
            File file = new File(imagePath);
            if (file.exists()){
                images.add(file);
            }
        }

        RequestParams params = new RequestParams();
        params.put("goods_id" , mProduct.getGoodsID());
        params.put("order_id" , mProduct.getOrderID());
        params.put("order_sn" , mProduct.getOrderSN());
        params.put("spec_key" , mProduct.getSpecKey());
        params.put("type" , applyType);
        params.put("reason" , content);

        if (images.size() > 0){
            showLoadingToast("正在上传图片,请稍后");
        }else{
            showLoadingToast("正在提交申请,请稍后");
        }

        SPPersonRequest.exchangeApplyWithParameter(params, images , new SPSuccessListener() {
            @Override
            public void onRespone(String msg, Object response) {
                hideLoadingToast();
                showToast(msg);
                Intent data = new Intent(SPExchangeApplyActivity.this, SPProductShowListActivity_.class);
                data.putExtra("goodsId", mProduct.getGoodsID());
                //SPExchangeApplyActivity.this.setResult(SPMobileConstants.Result_Code_Refresh, data);
                SPExchangeApplyActivity.this.finish();
            }
        }, new SPFailuredListener() {
            @Override
            public void onRespone(String msg, int errorCode) {
                hideLoadingToast();
                showToast(msg);
            }
        });


    }
}
