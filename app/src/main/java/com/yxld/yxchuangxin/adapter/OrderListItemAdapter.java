package com.yxld.yxchuangxin.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yxld.yxchuangxin.R;
import com.yxld.yxchuangxin.activity.goods.GoodsPraiseActivity;
import com.yxld.yxchuangxin.activity.order.OrderDetailActivity;
import com.yxld.yxchuangxin.contain.Contains;
import com.yxld.yxchuangxin.controller.API;
import com.yxld.yxchuangxin.entity.CxwyMallComment;
import com.yxld.yxchuangxin.entity.CxwyMallOrder;
import com.yxld.yxchuangxin.entity.CxwyMallSale;
import com.yxld.yxchuangxin.view.NoScrollListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author wwx
 * @ClassName: OrderListItemAdapter
 * @Description: 订单适配器
 * @date 2015年12月15日 上午10:58:31
 */
@SuppressLint("InflateParams")
public class OrderListItemAdapter extends BaseAdapter {

    private final int UPDATE_ORDER_STATE = 0;

    private List<CxwyMallOrder> listOrderDatas = new ArrayList<CxwyMallOrder>();
    private List<CxwyMallSale> listSaleData = new ArrayList<CxwyMallSale>();

    private List<CxwyMallSale> curSaleData = new ArrayList<CxwyMallSale>();

    /**
     * 运行上下文
     */
    private Context mContext = null;
    /**
     * 视图容器
     */
    private LayoutInflater listContainer;
    /**
     * 自定义视图
     */
    private ListItemView listItemView = null;

    /**
     * 总价
     */
    private String totalPrice;

    private Handler mHandler;

    class ListItemView { // 自定义控件集合
        /**
         * 订单下单时间
         */
        private TextView orderTime;

        /**
         * 订单编号
         */
        private TextView orderBianhao;
        /**
         * 订单状态
         */
        public TextView orderState;
        public TextView orderSumDestail;
        public TextView orderDestailPrice;
        /**
         * 订单信息
         */
        private NoScrollListView gvOrderInfo = null;

        /**
         * 操作类
         */
        private LinearLayout llOperate = null;
        /**
         * 操作按钮1
         */
        private TextView orderType1 = null;
        /**
         * 操作按钮2
         */
        private TextView orderType2 = null;
    }

    /**
     * 实例化Adapter
     *
     * @param context
     * @param listOrderData
     * @param listSaleData
     */
    public OrderListItemAdapter(Handler handler, Context context, List<CxwyMallOrder> listOrderData, List<CxwyMallSale> listSaleData) {
        this.listOrderDatas = listOrderData;
        this.mContext = context;
        this.listContainer = LayoutInflater.from(mContext); // 创建视图容器并设置上下文
        this.mHandler = handler;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            // 获取list_item布局文件的视图
            convertView = listContainer.inflate(
                    R.layout.order_list_item_layout, null);
            listItemView = new ListItemView();
            listItemView.orderTime = (TextView) convertView
                    .findViewById(R.id.orderTime);
            listItemView.orderState = (TextView) convertView
                    .findViewById(R.id.orderState);
            listItemView.orderDestailPrice = (TextView) convertView
                    .findViewById(R.id.orderDestailPrice);
            listItemView.orderSumDestail = (TextView) convertView
                    .findViewById(R.id.orderSumDestail);
            listItemView.gvOrderInfo = (NoScrollListView) convertView
                    .findViewById(R.id.gvOrderInfo);
            listItemView.llOperate = (LinearLayout) convertView
                    .findViewById(R.id.llOperate);
            listItemView.orderType1 = (TextView) convertView
                    .findViewById(R.id.orderType1);
            listItemView.orderType1.setOnClickListener(clickListener);
            listItemView.orderType2 = (TextView) convertView
                    .findViewById(R.id.orderType2);
            listItemView.orderType2.setOnClickListener(clickListener);

            // 设置控件集到convertView
            convertView.setTag(listItemView);
        } else {
            listItemView = (ListItemView) convertView.getTag();
        }

        CxwyMallOrder order = listOrderDatas.get(position);
        listItemView.orderTime.setText("下单时间:" + order.getDingdanXiadanTime());
        //listItemView.orderBianhao.setText("订单编号:"+ order.getDingdanBianhao());
        listItemView.orderState.setText(order.getDingdanZhuangtai());
        totalPrice = order.getDingdanTotalRmb() + "";
        if (order.getDingdanYouhuijia() != null && !"".equals(order.getDingdanYouhuijia())) {
            listItemView.orderDestailPrice.setText("¥" + totalPrice + "(优惠券-¥" + order.getDingdanYouhuijia() + ")");
        } else {
            listItemView.orderDestailPrice.setText("¥" + totalPrice);
        }

        int orderId = order.getDingdanId();
        curSaleData.clear();
        for (int i = 0; i < listSaleData.size(); i++) {
            CxwyMallSale mallSale = listSaleData.get(i);
            if (mallSale.getSaleDingdanId() == orderId) {
                curSaleData.add(mallSale);
            }
        }

        listItemView.orderSumDestail.setText("共" + order.getDingdanGoodsnum() + "件商品");
        updateData(listItemView.gvOrderInfo, curSaleData);

        listItemView.gvOrderInfo.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                //进入商品详情界面，传入订单ID
                Intent intent = new Intent(mContext, OrderDetailActivity.class);
                intent.putExtra("orderId", listOrderDatas.get(position).getDingdanId() + "");
                mContext.startActivity(intent);
            }
        });

        updateView(orderId, order.getDingdanZhuangtai(), listItemView.llOperate, listItemView.orderType1, listItemView.orderType2);
        return convertView;
    }

    @Override
    public int getCount() {
        return listOrderDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return listOrderDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public List<CxwyMallOrder> getListOrderDatas() {
        return listOrderDatas;
    }

    public void setListOrderDatas(List<CxwyMallOrder> listOrderDatas) {
        this.listOrderDatas = listOrderDatas;
        notifyDataSetChanged();
    }

    /**
     * 添加数据
     *
     * @param list
     */
    public void addData(List<CxwyMallOrder> list) {
        if (list != null) {
            this.listOrderDatas.addAll(list);
            notifyDataSetChanged();
        }
    }

    public List<CxwyMallSale> getListSaleData() {
        return listSaleData;
    }

    public void setListSaleData(List<CxwyMallSale> listSaleData) {
        this.listSaleData = listSaleData;
    }

    /**
     * 添加商品数据
     *
     * @param list
     */
    public void addSaleData(List<CxwyMallSale> list) {
        if (list != null) {
            this.listSaleData.addAll(list);
        }
    }

    /**
     * 更新二级分类详情数据的方法
     */
    public void updateData(NoScrollListView listView, List<CxwyMallSale> curSaleData) {
        // 创建一个集合来装图片和文字
        ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();
        // 装在图片和文字
        for (int x = 0; x < curSaleData.size(); x++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            if (curSaleData.get(x).getSaleImgSrc() != null && !"".equals(curSaleData.get(x).getSaleImgSrc())) {
                if (curSaleData.get(x).getSaleImgSrc().indexOf(";") > 0) {
                    String[] urlArray = curSaleData.get(x).getSaleImgSrc().split(";");
                    map.put("goodsImgs", API.PIC + urlArray[0]);
                } else {
                    map.put("goodsImgs", API.PIC + curSaleData.get(x).getSaleImgSrc());
                }
            } else {
                map.put("goodsImgs", API.PIC + "/wygl/files/img/201605/empty_photo.png");
            }

            map.put("orderDestail", curSaleData.get(x).getSaleShangpName());
            map.put("orderClass", "规格:" + curSaleData.get(x).getSaleGuige());
            map.put("orderPrice", "¥" + curSaleData.get(x).getSaleOneRmb());
            map.put("orderNum", "x" + curSaleData.get(x).getSaleNum());

            lstImageItem.add(map);
        }
        BaseMapListApater sm = new BaseMapListApater(mContext, lstImageItem,
                R.layout.member_order_main_info_item, new String[] {
                "goodsImgs", "orderDestail", "orderClass",
                "orderPrice" ,"orderNum"},
                new int[] { R.id.goodsImg, R.id.orderDestail,
                        R.id.orderClass, R.id.orderPrice,R.id.orderSum});
        listView.setAdapter(sm);
    }

    /**
     * @param state
     * @param llOperate
     * @param orderType1
     * @param orderType2
     * @return void
     * @throws
     * @Title: updateView
     * @Description: 更新操作栏
     */
    private void updateView(int orderID, String state, LinearLayout llOperate, TextView orderType1, TextView orderType2) {
        orderType1.setTag(orderID);
        orderType2.setTag(orderID);

        if (state.equals("待付款")) {
            llOperate.setVisibility(View.VISIBLE);
            orderType1.setText("立即付款");
            orderType1.setVisibility(View.VISIBLE);
            orderType2.setText("取消订单");
            orderType2.setVisibility(View.VISIBLE);
        } else if (state.equals("待发货")) {
            llOperate.setVisibility(View.VISIBLE);
            orderType1.setText("退款");
            orderType1.setVisibility(View.VISIBLE);
            orderType2.setVisibility(View.GONE);
        } else if (state.equals("待收货")) {
            llOperate.setVisibility(View.VISIBLE);
            orderType1.setText("确认收货");
            orderType1.setVisibility(View.VISIBLE);
            orderType2.setVisibility(View.GONE);
        } else if (state.equals("待评价")) {
            llOperate.setVisibility(View.VISIBLE);
            orderType1.setText("立即评价");
            orderType1.setVisibility(View.VISIBLE);
            orderType2.setText("申请退货");
            orderType2.setVisibility(View.VISIBLE);
        } else if (state.equals("退货") || state.equals("退货中") || state.equals("退款中")) {
            llOperate.setVisibility(View.VISIBLE);
            orderType1.setText("删除订单");
            orderType1.setVisibility(View.VISIBLE);
            orderType2.setVisibility(View.GONE);
        } else {
            llOperate.setVisibility(View.VISIBLE);
            orderType1.setText("删除订单");
            orderType1.setVisibility(View.VISIBLE);
            orderType2.setVisibility(View.GONE);
        }

    }

    private OnClickListener clickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v.getVisibility() != View.VISIBLE) {
                return;
            }
            TextView tv = (TextView) v;
            if (tv.getText().toString().equals("立即评价")) {
                Contains.curCommData.clear();
                //遍历商品集合
                for (int i = 0; i < listSaleData.size(); i++) {
                    CxwyMallSale mallSale = listSaleData.get(i);
                    if (mallSale.getSaleDingdanId() == Integer.parseInt(v.getTag() + "")) {
                        CxwyMallComment comment = new CxwyMallComment();
                        comment.setPingjiaShangpNum(mallSale.getSaleShangpNum());
                        comment.setPingjiaBeiyong1(mallSale.getSaleShangpName());
                        comment.setPingjiaImgSrc1(mallSale.getSaleImgSrc());
                        comment.setPingjiaBeiyong2(mallSale.getSaleDingdanId() + "");
                        Contains.curCommData.add(comment);
                    }
                }
                Log.d("geek", "订单  Contains.curCommData" + Contains.curCommData.toString());
                //跳转至评价界面
                Intent intent = new Intent(mContext, GoodsPraiseActivity.class);
                mContext.startActivity(intent);
            } else {
                Message msg = new Message();
                msg.arg1 = Integer.parseInt(v.getTag() + "");
                msg.obj = tv.getText().toString();
                msg.what = UPDATE_ORDER_STATE;
                if (tv.getText().toString().equals("立即付款")) {
                    msg.obj = "待发货";
                    Log.d("geek", "订单id" + v.getTag() + "");
                } else if (tv.getText().toString().equals("取消订单")) {
                    msg.obj = "取消订单";
                } else if (tv.getText().toString().equals("确认收货")) {
                    msg.obj = "待评价";
                } else if (tv.getText().toString().equals("申请退货")) {
                    msg.obj = "退货中";
                } else if (tv.getText().toString().equals("退款")) {
                    msg.obj = "退款中";
                }else if(tv.getText().toString().equals("删除订单")){
                    msg.obj = "用户删除订单";  //用户删除
                }
                mHandler.sendMessage(msg);
            }
        }
    };

}