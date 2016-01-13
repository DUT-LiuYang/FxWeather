package com.chuyunxuanyu.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.chuyunxuanyu.app.db.FxWeatherDB;
import com.chuyunxuanyu.app.model.City;
import com.chuyunxuanyu.app.model.County;
import com.chuyunxuanyu.app.model.Province;

/*
 * 解析数据
 */
public class Utility {
	
	/*
	 * 解析和处理服务器返回的省级数据
	 */
	public synchronized static boolean handleProvinceResponse(FxWeatherDB fxWeatherDB, String response){
		if(!TextUtils.isEmpty(response)){
			String[] allProvince = response.split(",");
			if(allProvince != null && allProvince.length > 0){
				for(String str : allProvince){
					String[] array = str.split("\\|");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					//将解析出来的数据存储到Province表
					fxWeatherDB.saveProvince(province);
				} //for
				return true;
			} //if
		} //if
		return false;
	}
	
	/*
	 * 解析和处理服务器返回的市级数据
	 */
	public synchronized static boolean handleCityResponse(FxWeatherDB fxWeatherDB, 
			String response, int provinceId){
		if(!TextUtils.isEmpty(response)){
			String[] allCities = response.split(",");
			if(allCities != null && allCities.length > 0){
				for(String str : allCities){
					String[] array = str.split("\\|");
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					//将解析出来的数据存储到City表
					fxWeatherDB.saveCity(city);
				} //for
				return true;
			} //if
		} //if
		return false;
	}
	
	/*
	 * 解析和处理服务器返回的县级数据
	 */
	public synchronized static boolean handleCountyResponse(FxWeatherDB fxWeatherDB, 
			String response, int cityId){
		if(!TextUtils.isEmpty(response)){
			String[] allCounties = response.split(",");
			if(allCounties != null && allCounties.length > 0){
				for(String str : allCounties){
					String[] array = str.split("\\|");
					County county = new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					//将解析出来的数据存储到County表
					fxWeatherDB.saveCounty(county);
				} //for
				return true;
			} //if
		} //if
		return false;
	}

	/*
	 * 解析服务器返回的json信息，并将解析出的数据存储到本地。
	 
	public static void handleWeatherResponse(Context context, String response){
		//context用于获得SharedPreferences对象，之后调用其edit方法
		try{
			JSONObject jsonObject = new JSONObject(response);
			JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
			String cityName = weatherInfo.getString("city");
			String temp1 = weatherInfo.getString("temp1");
			String temp2 = weatherInfo.getString("temp2");
			String cityCode = weatherInfo.getString("cityid");
			String weatherDes = weatherInfo.getString("weather");
			String publishTime = weatherInfo.getString("ptime");
			saveWeatherInfo(context, cityName, temp1, temp2, cityCode, weatherDes, publishTime);
		} catch(JSONException e){
			e.printStackTrace();
		}
	}*/
	
	/*
	 * 解析服务器返回的json信息，并将解析出的数据存储到本地。
	 */
	public static void handleBaiduWeatherResponse(Context context, String response){
		//context用于获得SharedPreferences对象，之后调用其edit方法
		try{
			
			JSONObject jsonObject = new JSONObject(response);
			//JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
			//String results = jsonObject.getString("results");
			//Log.d("hehe", "results: " + results);
			Log.d("hehe", "111" );
			//JSONObject result = new JSONObject("{results:" + results + "}");
			Log.d("hehe", "222" ); 
			JSONArray weather = jsonObject.getJSONArray("results");
			Log.d("hehe", "333 " + weather.length()); 
			JSONObject j = weather.getJSONObject(0);
			Log.d("hehe", "334" ); 			
			JSONArray array = j.getJSONArray("weather_data");
			Log.d("hehe", "444" ); 
			JSONObject weatherInfo = array.getJSONObject(0);
			Log.d("hehe", "555" ); 
			String cityName = j.getString("currentCity");
			Log.d("hehe", "cityName: " + cityName);
			
			String temperature = weatherInfo.getString("temperature");
			Log.d("hehe", "temperature: " + temperature);
			
			String weatherDes = weatherInfo.getString("weather") + " " + weatherInfo.getString("wind");
			Log.d("hehe", "weatherDes: " + weatherDes);
			
			String publishTime = weatherInfo.getString("date");
			Log.d("hehe", "publishTime: " + publishTime);			
			saveWeatherInfo(context, cityName, temperature, weatherDes, publishTime);
		} catch(JSONException e){
			e.printStackTrace();
		}
	}

	/*
	 * 将服务器返回的所有天气信息存储到SharedPreferences中
	 */
	private static void saveWeatherInfo(Context context, String cityName,
			String temperature, String weatherDes, String publishTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("temp", temperature);
		editor.putString("current_date", sdf.format(new Date()));
		editor.putString("weather_des", weatherDes);
		editor.putString("publish_time", publishTime);
		editor.commit();
	}
}
