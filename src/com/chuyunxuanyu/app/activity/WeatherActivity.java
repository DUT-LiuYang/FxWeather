package com.chuyunxuanyu.app.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.chuyunxuanyu.app.R;
import com.chuyunxuanyu.app.util.HttpCallbackListener;
import com.chuyunxuanyu.app.util.HttpUtil;
import com.chuyunxuanyu.app.util.Utility;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity{

	private LinearLayout weatherInfoLayout;
	private TextView cityNameText;			//显示城市名
	private TextView publishTimeText;		//显示发布时间
	private TextView DateText;				//显示日期
	private TextView WeatherText;			//像是天气描述信息
	private TextView tempText;				//显示气温

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		initView();
		//获取之前活动传过来的县级代码参数
		String countyName = getIntent().getStringExtra("county_name");
		if(!TextUtils.isEmpty(countyName)){
			publishTimeText.setText("同步中……");
			cityNameText.setVisibility(View.INVISIBLE);
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			try {
				queryWeatherInfo(countyName);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			//如果没有县级代号就直接显示本地天气
			showWeather();
		}
	}

	private void initView() {
		// TODO Auto-generated method stub
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		cityNameText = (TextView) findViewById(R.id.title_text);
		publishTimeText = (TextView) findViewById(R.id.publish_text);
		DateText = (TextView) findViewById(R.id.current_date);
		WeatherText = (TextView) findViewById(R.id.weather_desp);
		tempText = (TextView) findViewById(R.id.temp);
	}
	
	/*
	 * 查询天气代号所对应的的天气
	 */
	private void queryWeatherInfo(String countyName) throws UnsupportedEncodingException{
		//String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
		
		Log.d("hehe", "将要查询的城市为 " + countyName);
		String address = "http://api.map.baidu.com/telematics/v3/weather?location=" + 
				URLEncoder.encode(countyName, "UTF-8") + "&output=json&ak=gA9BDfwsd6cgMqz3WdftuiNS" +
				"&mcode=98:33:C6:4E:4A:F0:53:E0:93:1A:29:19:E8:2B:14:83:A1:5D:E1:42;com.chuyunxuanyu.app";	
			
		//Log.d("hehe", "...");
		queryFromServer(address, "weatherCode");
		//Log.d("hehe", "2 " + countyName);
	}

	private void queryFromServer(final String address, final String type) {
		Log.d("hehe", "开始查询");
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener(){

			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				if("countyCode".equals(type)){
					if(!TextUtils.isEmpty(response)){
						String[] array = response.split("\\|");
						if(array != null && 2 == array.length){
							//根据天气代号查询天气信息
							Log.d("hehe", "查询天气代号成功");
							try {
								queryWeatherInfo(array[1]);
							} catch (UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						}
					}
				} else if("weatherCode".equals(type)){
				
					Utility.handleBaiduWeatherResponse(WeatherActivity.this, response);
					
					runOnUiThread(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							showWeather();
							
						}
						
					});
					
					Log.d("hehe", response);
				}
			}

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						publishTimeText.setText("同步失败");
					}
					
				});
			}
			
		});
	}
	
	private void showWeather() {
		Log.d("hehe", "调用显示");
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
		cityNameText.setText(prefs.getString("city_name", ""));
		publishTimeText.setText(prefs.getString("publish_time", ""));
		DateText.setText(prefs.getString("current_date", ""));
		WeatherText.setText(prefs.getString("weather_des", ""));
		tempText.setText(prefs.getString("temp", ""));
		cityNameText.setVisibility(View.VISIBLE);
		weatherInfoLayout.setVisibility(View.VISIBLE);
	}
	
}
