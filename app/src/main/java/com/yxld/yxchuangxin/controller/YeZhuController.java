package com.yxld.yxchuangxin.controller;

import com.android.volley.RequestQueue;
import com.yxld.yxchuangxin.base.BaseEntity;
import com.yxld.yxchuangxin.contain.Contains;
import com.yxld.yxchuangxin.entity.Base1Entity;
import com.yxld.yxchuangxin.entity.CxwyMallUseDaijinquan;
import com.yxld.yxchuangxin.entity.CxwyMallUser;
import com.yxld.yxchuangxin.entity.CxwyMallUserBalance;
import com.yxld.yxchuangxin.entity.CxwyYezhu;
import com.yxld.yxchuangxin.entity.WuyeRecordAndroid;
import com.yxld.yxchuangxin.listener.ResultListener;

import java.util.Map;

/**
 * @ClassName: YeZhuController 
 * @Description: 业主接口类
 * @author wwx
 * @date 2016年4月7日 下午4:43:27 
 */
public interface YeZhuController extends API{
	
	/**
	 * @Title: getYeZhuWuYeList 
	 * @Description: 获取业主物业费列表
	 * @param mRequestQueue
	 * @param parm
	 * @param listener    
	 * @return void
	 * @throws
	 */
	void getYeZhuWuYeList(RequestQueue mRequestQueue, String url, Map<String, String> parm,
						  final ResultListener<WuyeRecordAndroid> listener);
	
	/**
	 * @Title: getAllPaymentRecords 
	 * @Description: 获取业主所有缴费记录列表
	 * @param mRequestQueue
	 * @param parm
	 * @param listener    
	 * @return void
	 * @throws
	 */
	void getAllPaymentRecords(RequestQueue mRequestQueue, String url, Map<String, String> parm,
							  final ResultListener<WuyeRecordAndroid> listener);

	/**
	 * @Title: getAllChengyuanList
	 * @Description: 获取成员集合
	 * @param mRequestQueue
	 * @param listener
	 * @return void
	 * @throws
	 */
	void getAllChengyuanList(RequestQueue mRequestQueue, Object[] parm, ResultListener<CxwyYezhu> listener);



	/**
	 * @Title: getDeleteChengyuanList
	 * @Description: 删除成员集合
	 * @param mRequestQueue
	 * @param listener
	 * @return void
	 * @throws
	 */
	void getDeleteChengyuanList(RequestQueue mRequestQueue, Object[] parm, ResultListener<BaseEntity> listener);


	/**
	 * @Title: getAllPaymentRecords
	 * @Description: 获取业主所有缴费记录列表
	 * @param mRequestQueue
	 * @param parm
	 * @param listener
	 * @return void
	 * @throws
	 */
	void addChengyuan(RequestQueue mRequestQueue, String url, Map<String, String> parm,
							  final ResultListener<BaseEntity> listener);

	/**
	 * @param mRequestQueue
	 * @param parm
	 * @param listener
	 * @return void
	 * @throws
	 * @Title: getAllPaymentRecords
	 * @Description: 充值成功
	 */
	void getAllChongzhi(RequestQueue mRequestQueue, String url, Map<String, String> parm, final ResultListener<Base1Entity> listener);

	void getAllYue(RequestQueue mRequestQueue, String url, Map<String, String> parm, final ResultListener<CxwyMallUser> listener);
	/**
	 * @param mRequestQueue
	 * @param parm
	 * @param listener
	 * @return void
	 * @throws
	 * @Title: getAllYHQ
	 * @Description: 查询所有可用优惠券
	 */
	void getAllYHQ(RequestQueue mRequestQueue, String url, Map<String, String> parm, final ResultListener<CxwyMallUseDaijinquan> listener);


	/**
	 * @param mRequestQueue
	 * @param parm
	 * @param listener
	 * @return void
	 * @throws
	 * @Title: getAllNOYHQ
	 * @Description: 查询不所有可用优惠券
	 */
	void getAllNOYHQ(RequestQueue mRequestQueue, String url, Map<String, String> parm, final ResultListener<CxwyMallUseDaijinquan> listener);


	/**
	 * @param mRequestQueue
	 * @param parm
	 * @param listener
	 * @return void
	 * @throws
	 * @Title: getAllRecharge
	 * @Description: 查询所有充值记录
	 */
	void getAllRecharge(RequestQueue mRequestQueue, String url, Map<String, String> parm, final ResultListener<CxwyMallUserBalance> listener);

	/**
	 * @param mRequestQueue
	 * @param parm
	 * @param listener
	 * @return void
	 * @throws
	 * @Title: getAllRecharge
	 * @Description: 查询所有支出记录
	 */
	 void getAllExpenditure(RequestQueue mRequestQueue, String url, Map<String, String> parm, final ResultListener<CxwyMallUserBalance> listener);
}