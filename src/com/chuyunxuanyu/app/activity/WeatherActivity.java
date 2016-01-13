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
	private TextView cityNameText;			//��ʾ������
	private TextView publishTimeText;		//��ʾ����ʱ��
	private TextView DateText;				//��ʾ����
	private TextView WeatherText;			//��������������Ϣ
	private TextView tempText;				//��ʾ����

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		initView();
		//��ȡ֮ǰ����������ؼ��������
		String countyName = getIntent().getStringExtra("county_name");
		if(!TextUtils.isEmpty(countyName)){
			publishTimeText.setText("ͬ���С���");
			cityNameText.setVisibility(View.INVISIBLE);
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			try {
				queryWeatherInfo(countyName);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			//���û���ؼ����ž�ֱ����ʾ��������
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
	 * ��ѯ������������Ӧ�ĵ�����
	 */
	private void queryWeatherInfo(String countyName) throws UnsupportedEncodingException{
		//String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
		
		Log.d("hehe", "��Ҫ��ѯ�ĳ���Ϊ " + countyName);
		String address = "http://api.map.baidu.com/telematics/v3/weather?location=" + 
				URLEncoder.encode(countyName, "UTF-8") + "&output=json&ak=gA9BDfwsd6cgMqz3WdftuiNS" +
				"&mcode=98:33:C6:4E:4A:F0:53:E0:93:1A:29:19:E8:2B:14:83:A1:5D:E1:42;com.chuyunxuanyu.app";	
			
		//Log.d("hehe", "...");
		queryFromServer(address, "weatherCode");
		//Log.d("hehe", "2 " + countyName);
	}

	private void queryFromServer(final String address, final String type) {
		Log.d("hehe", "��ʼ��ѯ");
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener(){

			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				if("countyCode".equals(type)){
					if(!TextUtils.isEmpty(response)){
						String[] array = response.split("\\|");
						if(array != null && 2 == array.length){
							//�����������Ų�ѯ������Ϣ
							Log.d("hehe", "��ѯ�������ųɹ�");
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
						publishTimeText.setText("ͬ��ʧ��");
					}
					
				});
			}
			
		});
	}
	
	private void showWeather() {
		Log.d("hehe", "������ʾ");
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
