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
 * Description: 退换货列表
 * @version V1.0
 */
package com.tpshop.mallc.activity.person.order;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.tpshop.mallc.R;
import com.tpshop.mallc.activity.common.SPBaseActivity;
import com.tpshop.mallc.activity.shop.SPProductListActivity;
import com.tpshop.mallc.adapter.SPExchangeListAdapter;
import com.tpshop.mallc.dao.SPPersonDao;
import com.tpshop.mallc.http.base.SPFailuredListener;
import com.tpshop.mallc.http.base.SPMobileHttptRequest;
import com.tpshop.mallc.http.base.SPSuccessListener;
import com.tpshop.mallc.http.condition.SPProductCondition;
import com.tpshop.mallc.http.person.SPPersonRequest;
import com.tpshop.mallc.http.shop.SPShopRequest;
import com.tpshop.mallc.model.SPProduct;
import com.tpshop.mallc.model.order.SPExchange;
import com.tpshop.mallc.model.shop.SPShopOrder;
import com.tpshop.mallc.utils.SMobileLog;
import com.tpshop.mallc.utils.SPDialogUtils;


import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by admin on 2016/7/4.
 */
@EActivity(R.layout.order_exchange_list)
public class SPExchangeListActivity extends SPBaseActivity {

    private String TAG = "SPExchangeListActivity";

    @ViewById(R.id.order_exchange_list_view_frame)
    PtrClassicFrameLayout ptrClassicFrameLayout;

    @ViewById(R.id.order_exchange_lstv)
    ListView exchangeLstv;


    int pageIndex;   //当前第几页:从1开始
    /**
     *  最大页数
     */
    boolean maxIndex;

    List<SPExchange> exchanges;
    SPExchangeListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.setCustomerTitle(true,true,getString(R.string.title_exchange_list));
        super.onCreate(savedInstanceState);
    }

    @AfterViews
    public void init(){
        super.init();
    }

    @Override
    public void initSubViews() {
        ptrClassicFrameLayout.setPtrHandler(new PtrDefaultHandler() {

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                //下拉刷新
                refreshData();
            }
        });

        ptrClassicFrameLayout.setOnLoadMoreListener(new OnLoadMoreListener() {

            @Override
            public void loadMore() {
                //上拉加载更多
                loadMoreData();
            }
        });
        exchangeLstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SPExchange exchange =  (SPExchange)mAdapter.getItem(position);
                Intent intent = new Intent(SPExchangeListActivity.this , SPExchangeDetailActivity_.class);
                intent.putExtra("exchangeId" ,exchange.getExchangeId());
                startActivity(intent);

            }
        });
    }

    @Override
    public void initData() {

        mAdapter = new SPExchangeListAdapter(this);
        exchangeLstv.setAdapter(mAdapter);
        refreshData();

    }

    @Override
    public void initEvent() {

    }

    /**
     *  刷新数据
     */
    public void refreshData() {
        pageIndex = 1;
        maxIndex = false;

        showLoadingToast();
        SPPersonRequest.getExchangeListWithPage(pageIndex, new SPSuccessListener() {
            @Override
            public void onRespone(String msg, Object response) {

                if (response!=null){
                    exchanges = (List<SPExchange>)response;
                    maxIndex = false;
                    mAdapter.setData(exchanges);
                    ptrClassicFrameLayout.setLoadMoreEnable(true);
                }else {
                    maxIndex = true;
                    ptrClassicFrameLayout.setLoadMoreEnable(false);
                }
                ptrClassicFrameLayout.refreshComplete();
                hideLoadingToast();
            }
        }, new SPFailuredListener() {
            @Override
            public void onRespone(String msg, int errorCode) {
                hideLoadingToast();
            }
        });
    }


    /**
     * 加载数据
     * @Description: 加载数据
     * @return void    返回类型
     * @throws
     */
    public void loadMoreData(){

        if (maxIndex){
            return;
        }
        pageIndex++;
        SPPersonRequest.getExchangeListWithPage(pageIndex, new SPSuccessListener() {
            @Override
            public void onRespone(String msg, Object response) {

                if (response != null) {
                    List<SPExchange> partExchanges = (List<SPExchange>) response;
                    exchanges.addAll(partExchanges);
                    mAdapter.setData(exchanges);
                    maxIndex = false;
                    ptrClassicFrameLayout.setLoadMoreEnable(true);
                } else {
                    maxIndex = true;
                    ptrClassicFrameLayout.setLoadMoreEnable(false);
                    pageIndex--;
                }
                ptrClassicFrameLayout.refreshComplete();
                hideLoadingToast();
            }
        }, new SPFailuredListener() {
            @Override
            public void onRespone(String msg, int errorCode) {
                hideLoadingToast();
                pageIndex--;
            }
        });
    }



}
