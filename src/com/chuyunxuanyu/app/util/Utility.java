package com.chuyunxuanyu.app.util;

import android.text.TextUtils;

import com.chuyunxuanyu.app.db.FxWeatherDB;
import com.chuyunxuanyu.app.model.City;
import com.chuyunxuanyu.app.model.County;
import com.chuyunxuanyu.app.model.Province;

/*
 * ��������
 */
public class Utility {
	
	/*
	 * �����ʹ�����������ص�ʡ������
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
					//���������������ݴ洢��Province��
					fxWeatherDB.saveProvince(province);
				} //for
				return true;
			} //if
		} //if
		return false;
	}
	
	/*
	 * �����ʹ�����������ص��м�����
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
					//���������������ݴ洢��City��
					fxWeatherDB.saveCity(city);
				} //for
				return true;
			} //if
		} //if
		return false;
	}
	
	/*
	 * �����ʹ�����������ص��ؼ�����
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
					//���������������ݴ洢��County��
					fxWeatherDB.saveCounty(county);
				} //for
				return true;
			} //if
		} //if
		return false;
	}
	
}
