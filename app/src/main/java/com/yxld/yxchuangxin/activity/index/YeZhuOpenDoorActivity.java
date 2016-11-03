package com.yxld.yxchuangxin.activity.index;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.security.mobile.module.commonutils.LOG;
import com.google.zxing.WriterException;
import com.yxld.yxchuangxin.R;
import com.yxld.yxchuangxin.base.BaseActivity;
import com.yxld.yxchuangxin.contain.Contains;
import com.yxld.yxchuangxin.controller.API;
import com.yxld.yxchuangxin.controller.DoorController;
import com.yxld.yxchuangxin.controller.impl.DoorControllerImpl;
import com.yxld.yxchuangxin.entity.CxwyYezhu;
import com.yxld.yxchuangxin.entity.OpenDoorCode;
import com.yxld.yxchuangxin.entity.ShareInfo;
import com.yxld.yxchuangxin.listener.ResultListener;
import com.yxld.yxchuangxin.util.ToastUtil;
import com.yxld.yxchuangxin.util.YouMengShareUtil;
import com.zxing.encoding.EncodingHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wwx
 * @ClassName: phoneOpenDoorActivity
 * @Description: 手机开门
 * @date 2016年5月27日 下午5:14:24
 */
public class YeZhuOpenDoorActivity extends BaseActivity {

	/**门禁实现类*/
	private DoorController doorController;

	private YouMengShareUtil mengShareUtil = null;

	private TextView update,youxiaoqi,shareSms;

	/** 二维码图片 */
	private ImageView codeImg;

	private Intent intent;

	private Bundle bundle1;

	private  String address;

	private ShareInfo shareInfo = new ShareInfo();

	private CxwyYezhu yezhu = new CxwyYezhu();
	private String shareUrl = "";

	private int i = 0;
	/** 更新二维码时间*/
	private int UPDATETIME = 1000*30;

	@Override
	protected void initContentView(Bundle savedInstanceState) {
		setContentView(R.layout.phone_open_door_activity_layout);
		getSupportActionBar().setTitle("业主开门");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		shareInfo.setTitle("业主开门二维码");
		shareInfo.setShareCon(address);

		List<CxwyYezhu> list = Contains.cxwyYezhu;
		yezhu = list.get(0);
		address = yezhu.getYezhuLoupan()+""+yezhu.getYezhuLoudong()+"栋"+yezhu.getYezhuDanyuan()+"单元" +yezhu.getYezhuFanghao();
		Log.d("geek","业主"+yezhu.toString());
		handler.postDelayed(runnable, UPDATETIME); //每隔1s执行
		initDataFromNet();
	}

	Handler handler = new Handler();
	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			//handler自带方法实现定时器
			try {
				handler.postDelayed(this, UPDATETIME);
				initDataFromNet();
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("exception...");
			}
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(handler != null){
			handler.removeCallbacks(runnable);
			handler = null;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void initView() {
		update = (TextView) findViewById(R.id.update);
		update.setOnClickListener(this);
		codeImg = (ImageView) findViewById(R.id.codeImg);
		youxiaoqi = (TextView) findViewById(R.id.youxiaoqi);
		youxiaoqi.setVisibility(View.INVISIBLE);
		shareSms = (TextView)findViewById(R.id.shareSms);
		shareSms.setVisibility(View.INVISIBLE);
		shareSms.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
//			case R.id.update:
//				initDataFromNet();
//				break;
		}
	}

	@Override
	protected void initDataFromLocal() {
		Log.d("geek", "进入initDataFromLocal（）");
		mengShareUtil = new YouMengShareUtil(this);
	}

	private void getOpenDoor(String contentString,String time){
		try {
			if (!contentString.equals("")) {
				//根据字符串生成二维码图片并显示在界面上，第二个参数为图片的大小（350*350）
				Bitmap qrCodeBitmap = EncodingHandler.createQRCode(contentString, 450);
				codeImg.setImageBitmap(qrCodeBitmap);
				youxiaoqi.setText("有效期至："+time);
				shareInfo.setBitmap(qrCodeBitmap);
				shareUrl = API.yuming+"/qr_code.html?timr="+time+"&code="+contentString;
				shareInfo.setImgUrl(shareUrl);
			}else {
				Toast.makeText(YeZhuOpenDoorActivity.this, "生成二维码失败", Toast.LENGTH_SHORT).show();
			}
		} catch (WriterException e) {
			e.printStackTrace();
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
//			if (name.equals("1")) {
//				Intent intent = new Intent(phoneOpenDoorActivity.this, NewMainActivity2.class);
//				setResult(RESULT_OK, intent);
//				ActivityOptions opts = null;
//				opts = ActivityOptions.makeCustomAnimation(
//						phoneOpenDoorActivity.this, R.anim.slide_up_in,
//						R.anim.slide_down_out);
//				startActivity(intent, opts.toBundle());
//				finish();
//			}   else if (name.equals("2")){
//				finish();
//			}
		}
		return false;
	}


	@Override
	protected void initDataFromNet() {
		Log.d("geek","initDataFromNet");
		if(doorController == null ){
			doorController = new DoorControllerImpl();
		}
		Map<String, String> parm = new HashMap<String, String>();
		parm.put("houses", yezhu.getYezhuLoupan());
		parm.put("dong", yezhu.getYezhuLoudong());
		parm.put("danyuan", yezhu.getYezhuDanyuan());
		parm.put("name", yezhu.getYezhuName());
		parm.put("tel", yezhu.getYezhuShouji());
		parm.put("yezhuid", yezhu.getYezhuId()+"");
		doorController.GetYEZHUDoorCODE(mRequestQueue,parm,yezhuDoorCode);

	}

	/**
	 * 获取业主开门二维码
	 */
	private ResultListener<OpenDoorCode>  yezhuDoorCode = new ResultListener<OpenDoorCode>() {
		@Override
		public void onResponse(OpenDoorCode info) {
			Log.d("geek","业主开门 OpenDoorCode info"+info.toString());

			Log.d("geek","OpenDoorCode 数据"+info.getCode().toString());
			if(info != null && info.getCode().getStr() != null && !"".equals(info.getCode().getStr()) && info.getCode().getShijian() != null
					&& !"".equals(info.getCode().getShijian() )){
				getOpenDoor(info.getCode().getStr(),info.getCode().getShijian());
			}else{
				ToastUtil.show(YeZhuOpenDoorActivity.this,"获取业主二维码失败");
			}
			progressDialog.hide();
		}

		@Override
		public void onErrorResponse(String errMsg) {
			onError(errMsg);
		}
	};

}