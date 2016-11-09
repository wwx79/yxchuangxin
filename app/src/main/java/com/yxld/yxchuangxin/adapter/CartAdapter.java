package com.yxld.yxchuangxin.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.yxld.yxchuangxin.R;
import com.yxld.yxchuangxin.activity.cart.CartMainFragment;
import com.yxld.yxchuangxin.base.BaseEntity;
import com.yxld.yxchuangxin.contain.Contains;
import com.yxld.yxchuangxin.controller.API;
import com.yxld.yxchuangxin.controller.CartController;
import com.yxld.yxchuangxin.controller.impl.CartControllerImpl;
import com.yxld.yxchuangxin.entity.CxwyMallCart;
import com.yxld.yxchuangxin.listener.ResultListener;
import com.yxld.yxchuangxin.util.ToastUtil;
import com.yxld.yxchuangxin.view.LoadingImg;
import com.yxld.yxchuangxin.view.ProgressDialog;

import java.util.ArrayList;
import java.util.List;

import cn.refactor.library.SmoothCheckBox;


/**
 * @author 1152008367@qq.com
 * @ClassName: IntegralMallAdapter
 * @Description: 购物车适配器
 * @date 2015年8月19日 下午2:21:02
 */
@SuppressLint("InflateParams")
public class CartAdapter extends BaseAdapter {
    /**
     * 打印信息Tag
     */
    private String LOG_TAG = "geek";

    private CartController cartController;
    /**
     * 网络请求列队
     */
    private RequestQueue mRequestQueues;
    /**
     * 运行上下文
     */
    private Context mContext = null;
    private ProgressDialog progressDialog;

    /**
     * 数据集合
     */
    private List<CxwyMallCart> listData = new ArrayList<CxwyMallCart>();
    /**
     * 视图容器
     */
    private LayoutInflater listContainer;
    /**
     * 自定义视图
     */
    private ListItemView listItemView = null;
    /**
     * 更新视图
     */
    private Handler handler = null;

    private int curPosition = 0;

    /** 当前商品数量*/
    private int curNum = 0;

    private myWatcher mWatcher;
    private int index=-1;//记录选中的位置


    class ListItemView { // 自定义控件集合
        /**
         * 选中状态
         */
        public SmoothCheckBox cartGoodsChecked;
        /**
         * 商品图片
         */
        public ImageView cartGoodsImg;
        /**
         * 商品名称
         */
        public TextView cartGoodsName;
        /**
         * 商品价格
         */
        public TextView cartGoodsPrice;
        /**
         * 减号按钮
         */
        public ImageView SubtractNum;
        /**
         * 当前数量
         */
        public EditText cartGoodsNum;
        /**
         * 加号按钮
         */
        public ImageView AddNum;
    }

    /**
     * 实例化Adapter
     * @param mRequestQueue
     * @param progressDialog
     * @param context
     * @param list
     * @param handler
     */
    public CartAdapter(RequestQueue mRequestQueue, ProgressDialog progressDialog, Context context, List<CxwyMallCart> list, Handler handler) {
        this.mRequestQueues = mRequestQueue;
        this.progressDialog = progressDialog;
        this.mContext = context;
        this.listData = list;
        this.handler = handler;
        this.listContainer = LayoutInflater.from(mContext); // 创建视图容器并设置上下文
        if (cartController == null) {
            cartController = new CartControllerImpl();
        }
    }

    public int getCount() {
        return listData.size();
    }

    public Object getItem(int arg0) {
        return listData.get(arg0);
    }

    public long getItemId(int arg0) {
        return arg0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            // 获取list_item布局文件的视图
            convertView = listContainer.inflate(R.layout.cart_item, null);
            listItemView = new ListItemView();
            listItemView.cartGoodsChecked = (SmoothCheckBox) convertView.findViewById(R.id.cartIsSelect);
            listItemView.cartGoodsImg = (ImageView) convertView.findViewById(R.id.cartListImg);
            listItemView.cartGoodsName = (TextView) convertView.findViewById(R.id.cartListGoodsName);
            listItemView.cartGoodsPrice = (TextView) convertView.findViewById(R.id.cartListPrice);
            listItemView.SubtractNum = (ImageView) convertView.findViewById(R.id.cartOut);
            listItemView.AddNum = (ImageView) convertView.findViewById(R.id.cart_Add);
            listItemView.cartGoodsNum = (EditText) convertView.findViewById(R.id.cart_goods_Num);
            // 设置控件集到convertView
            convertView.setTag(listItemView);
        } else {
            listItemView = (ListItemView) convertView.getTag();
        }

        //加载数据
        final CxwyMallCart goodsVo = listData.get(position);
        Log.d(LOG_TAG, "goods =" + goodsVo);
        if (goodsVo.getCartShangpName() != null) {
            listItemView.cartGoodsName.setText(goodsVo.getCartShangpName());
        }
        if (goodsVo.getCartOneRmb() != null) {
            listItemView.cartGoodsPrice.setText(goodsVo.getCartOneRmb());
        }
        if (goodsVo.getCartNum() != null) {
            listItemView.cartGoodsNum.setText(goodsVo.getCartNum() + "");
        }

        if (goodsVo.getCartImgSrc() != null && !"".equals(goodsVo.getCartImgSrc())) {
            if (goodsVo.getCartImgSrc().indexOf(";") > 0) {
                String[] urlArray = goodsVo.getCartImgSrc().split(";");
                Contains.loadingImg.displayImage(API.PIC + urlArray[0], listItemView.cartGoodsImg, LoadingImg.option1);
            } else {
                Contains.loadingImg.displayImage(API.PIC + goodsVo.getCartImgSrc(), listItemView.cartGoodsImg, LoadingImg.option1);
            }
        }

        listItemView.cartGoodsChecked.setChecked(goodsVo.isChecked());

        //设置点击事件
        listItemView.cartGoodsChecked.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                Contains.CartList.get(position).setChecked(isChecked);
                if (!isChecked) {
                    handler.sendEmptyMessage(CartMainFragment.updateNoCheck);
                }
                int count = 0;
                for (int i = 0; i < Contains.CartList.size(); i++) {
                    CxwyMallCart goodsVo = Contains.CartList.get(i);
                    if (goodsVo.isChecked()) {
                        count++;
                    }
                }
                if (count == Contains.CartList.size()) {
                    handler.sendEmptyMessage(CartMainFragment.updateAllCheck);
                }
                handler.sendEmptyMessage(CartMainFragment.updateCurPrise);
            }
        });


        //减号
        listItemView.SubtractNum.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("geek","减号Po="+position+"index="+index);
                setCount(position, false);
            }
        });

        //加号
        listItemView.AddNum.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("geek","加号Po="+position+"index="+index);
                setCount(position, true);
            }
        });


//        listItemView.cartGoodsNum.setOnTouchListener(new View.OnTouchListener() {
//
//            @SuppressLint("ClickableViewAccessibility")
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                // TODO Auto-generated method stub
//                if(event.getAction()==MotionEvent.ACTION_UP){
//                    index=position;
//                }
//                return false;
//            }
//        });
//
//        listItemView.cartGoodsNum.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            //设置焦点监听，当获取到焦点的时候才给它设置内容变化监听解决卡的问题
//
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                EditText et=(EditText) v;
//                if(mWatcher==null){
//                    mWatcher=new myWatcher();
//                }
//                if(hasFocus){
//                    et.addTextChangedListener(mWatcher);//设置edittext内容监听
//                }else {
//                    et.removeTextChangedListener(mWatcher);
//                }
//
//            }
//        });
//
//        listItemView.cartGoodsNum.clearFocus();//防止点击以后弹出键盘，重新getview导致的焦点丢失
//        if (index != -1 && index == position) {
//            // 如果当前的行下标和点击事件中保存的index一致，手动为EditText设置焦点。
//            listItemView.cartGoodsNum.requestFocus();
//        }
//        listItemView.cartGoodsNum.setText(goodsVo.getCartNum());//这一定要放在clearFocus()之后，否则最后输入的内容在拉回来时会消失
//        listItemView.cartGoodsNum.setSelection(listItemView.cartGoodsNum.getText().length());
////		viewHolder.editText.addTextChangedListener(new myWatcher());//放弃直接的为每一个edittext设置监听内容变化
        return convertView;
    }

    class myWatcher implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
//           // text[index]=s.toString();//为输入的位置内容设置数组管理器，防止item重用机制导致的上下内容一样的问题
//            if(index == -1){
//                return;
//            }
//            if(listData.get(index).getCartNum().equals(s.toString())){
//                Log.d("geek","afterTextChanged 修改时数量一致 ="+index);
//                return;
//            }
//            if(s == null || s.toString().equals("")){
//                Log.d("geek","afterTextChanged 没有修改值 ="+index);
//                return;
//            }
//            listData.get(index).setCartNum(s.toString());
//            Log.d("geek","afterTextChanged index ="+index);
//            cartController.updateCartInfoFromID(mRequestQueues, new Object[]{Contains.CartList.get(index).getCartId(), s.toString(),Contains.CartList.get(index).getCartShangpNum()}, updateListener);
//            curNum = Integer.parseInt(s.toString());
//            curPosition = index;
        }
    }


    private void setCount(int position, boolean operate) {
        int count = Integer.parseInt(Contains.CartList.get(position).getCartNum());
        if (operate) {
            count++;
        } else {
            if (count == 1) {

            } else {
                count--;
            }
        }
        curPosition = position;
        curNum = count;
        //请求修改购物车数量接口
        cartController.updateCartInfoFromID(mRequestQueues, new Object[]{Contains.CartList.get(position).getCartId(), count,Contains.CartList.get(position).getCartShangpNum()}, updateListener);
    }

    public List<CxwyMallCart> getListData() {
        return listData;
    }

    public void setListData(List<CxwyMallCart> cartList) {
        this.listData = cartList;
    }

    /**
     * 更新数量购物车请求
     */
    private ResultListener<BaseEntity> updateListener = new ResultListener<BaseEntity>() {

        @Override
        public void onResponse(BaseEntity info) {
            if (info.status != 0) {
                ToastUtil.show(mContext, ((BaseEntity) info).MSG);
                return;
            }
            Log.d("geek","请求完成="+curNum);
            //请求成功
            Contains.CartList.get(curPosition).setCartNum(curNum + "");
            listItemView.cartGoodsNum.setText(curNum + "");
            notifyDataSetChanged();
            handler.sendEmptyMessage(CartMainFragment.updateCurPrise);
        }

        @Override
        public void onErrorResponse(String errMsg) {
            Toast.makeText(mContext, "修改失败", Toast.LENGTH_SHORT).show();
        }
    };
}