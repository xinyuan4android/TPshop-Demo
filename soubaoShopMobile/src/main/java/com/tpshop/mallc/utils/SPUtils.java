package com.tpshop.mallc.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;


import com.facebook.common.file.FileUtils;
import com.soubao.tpshop.utils.SPEncryptUtil;
import com.soubao.tpshop.utils.SPStringUtils;
import com.tpshop.mallc.common.SPMobileConstants;


import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by admin on 2016/7/28.
 */
public class SPUtils {

    public static String getHost(String url){
        if (SPStringUtils.isEmpty(url)){
            return null;
        }
        if(url.startsWith("http://") || url.startsWith("https://")){
            return url = url.replaceAll("http://" , "").replaceAll("https://" , "");
        }
        return url;
    }


    /**
     *
     * @Title: isNetworkAvaiable
     * @Description:(是否打开网络)
     * @param: @param pContext
     * @param: @return
     * @return: boolean
     * @throws
     */
    public static boolean isNetworkAvaiable(Context pContext){
        boolean isAvaiable = false ;
        ConnectivityManager cm = (ConnectivityManager)pContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if(networkInfo!=null && networkInfo.isAvailable()){
            isAvaiable = true;
        }
        return isAvaiable;
    }


    public static String convertFullTimeFromPhpTime(long phpTime){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date(phpTime * 1000));
    }



    /**
     * 加密之前带上AUTH_CODE
     * @param src
     * @return
     * @throws Exception
     */
    public static String md5WithAuthCode(String src) throws Exception {
        String source = SPMobileConstants.SP_AUTH_CODE + src;
        return SPEncryptUtil.md5Digest(source);
    }

    /**
     * 获取外部存储路径
     * @return
     */
    public static String getExtSDCardPaths() {
        String extFileStatus = Environment.getExternalStorageState();
        File extFile = Environment.getExternalStorageDirectory();
        if (extFileStatus.equals(Environment.MEDIA_MOUNTED)
                && extFile.exists() && extFile.isDirectory()
                && extFile.canWrite()) {
           return extFile.getAbsolutePath();
        }
        return null;
    }

}
