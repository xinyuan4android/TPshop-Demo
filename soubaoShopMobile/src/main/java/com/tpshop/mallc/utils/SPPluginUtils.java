package com.tpshop.mallc.utils;

import com.tpshop.mallc.global.SPMobileApplication;
import com.tpshop.mallc.model.SPPlugin;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2016/6/30.
 */
public class SPPluginUtils {

    final static String PLUGIN_ALIPAY_PARTNER = "alipay_partner";
    final static String PLUGIN_ALIPAY_ACCOUNT = "alipay_account";
    final static String PLUGIN_ALIPAY_PRIVATE_KEY = "alipay_private_key";

    final static String PLUGIN_ALIPAY_CODE = "alipay";          //支付宝code
    final static String PLUGIN_UNIONPAY_CODE = "unionpay";      //银联在线支付
    final static String PLUGIN_WIXINPAY_CODE = "appWeixinPay";  //支付宝code
    final static String PLUGIN_CODPAY_CODE  = "cod";    //支付宝code

    /** 微信支付APPID **/
    final static String PLUGIN_WXPAY_APPID  = "appid";    //微信code

    final static String PLUGIN_LOGIN_QQ  = "qq";    //支付宝code
    final static String PLUGIN_LOGIN_QQ_APPID  = "app_id";    //QQ: appid
    final static String PLUGIN_LOGIN_QQ_SECRET  = "app_secret";    //QQ: appid

    final static String PLUGIN_LOGIN_WEIXIN  = "appWeixinPay";    //微信code
    final static String PLUGIN_LOGIN_WEIXIN_APPID  = "appid";    //微信code
    final static String PLUGIN_LOGIN_WEIXIN_SECRET  = "secret";    //微信secret
    //final static String PLUGIN_LOGIN_WEIXIN_SECRET  = "secret";    //微信code

    /**
     *  支付宝支付 - 获取商户号ID
     *
     *  @return return value description
     */
    public static String getAlipayPartner(){
        return getAlipayConfigValueWithKey(PLUGIN_ALIPAY_PARTNER);
    }


    /**
     *  支付宝支付 - 获取支付账户
     *
     *  @return return value description
     */
    public static String getAlipayAccount(){
        return getAlipayConfigValueWithKey(PLUGIN_ALIPAY_ACCOUNT);

    }

    /**
     *  支付宝支付 - 密钥
     *
     *  @return return value description
     */
    public static String getAlipayPrivateKey(){
        return getAlipayConfigValueWithKey(PLUGIN_ALIPAY_PRIVATE_KEY);
    }


    /**
     *  微信支付Appid
     *
     *  @return return value description
     */
    public static String getWxPayAppid(){
        return getWxPayConfigValueWithKey(PLUGIN_WXPAY_APPID);
    }

    public static String getAlipayConfigValueWithKey(String key){

        Map<String , SPPlugin> pluginMap = SPMobileApplication.getInstance().getPayPluginMap();
        if (pluginMap == null)return null;
        SPPlugin plugin = pluginMap.get(PLUGIN_WIXINPAY_CODE);
        if (plugin==null)return null;
        JSONObject configValue = plugin.getConfigValue();
        try {
            if (configValue != null && configValue.has(key)){
                return configValue.getString(key);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取微信支付配置信息
     * @param key
     * @return
     */
    public static String getWxPayConfigValueWithKey(String key){

        Map<String , SPPlugin> pluginMap = SPMobileApplication.getInstance().getPayPluginMap();
        if (pluginMap == null)return null;
        SPPlugin plugin = pluginMap.get(PLUGIN_WIXINPAY_CODE);
        if (plugin==null)return null;
        JSONObject configValue = plugin.getConfigValue();
        try {
            if (configValue != null && configValue.has(key)){
                return configValue.getString(key);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** QQ相关 **/
    public static String getPluginLoginQQAppid(){
        return getQQConfigValueWithKey(PLUGIN_LOGIN_QQ_APPID);
    }
    public static String getPluginLoginQQSecret(){
        return getQQConfigValueWithKey(PLUGIN_LOGIN_QQ_SECRET);
    }
    /**
     * QQ登录相关
     * @param key
     * @return
     */
    public static String getQQConfigValueWithKey(String key){
        Map<String , SPPlugin> pluginMap = SPMobileApplication.getInstance().getLoginPluginMap();
        if (pluginMap == null)return null;
        SPPlugin plugin = pluginMap.get(PLUGIN_LOGIN_QQ);
        if (plugin==null)return null;
        JSONObject configValue = plugin.getConfigValue();
        try {
            if (configValue != null && configValue.has(key)){
                return configValue.getString(key);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** 微信相关 **/
    public static String getPluginLoginWeixinAppid(){
        return getWeiXinConfigValueWithKey(PLUGIN_LOGIN_WEIXIN_APPID);
    }
    public static String getPluginLoginWeixinSecret(){
        return getWeiXinConfigValueWithKey(PLUGIN_LOGIN_WEIXIN_SECRET);
    }
    /**
     * 微信登录相关
     * @param key
     * @return
     */
    public static String getWeiXinConfigValueWithKey(String key){
        Map<String , SPPlugin> pluginMap = SPMobileApplication.getInstance().getLoginPluginMap();
        if (pluginMap == null)return null;
        SPPlugin plugin = pluginMap.get(PLUGIN_LOGIN_WEIXIN);
        if (plugin == null)return null;
        JSONObject configValue = plugin.getConfigValue();
        try {
            if (configValue != null && configValue.has(key)){
                return configValue.getString(key);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
