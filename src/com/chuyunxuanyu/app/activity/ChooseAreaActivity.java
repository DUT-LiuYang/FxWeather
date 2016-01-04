package com.chuyunxuanyu.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.chuyunxuanyu.app.db.FxWeatherDB;
import com.chuyunxuanyu.app.model.*;
import com.chuyunxuanyu.app.util.HttpCallbackListener;
import com.chuyunxuanyu.app.util.HttpUtil;
import com.chuyunxuanyu.app.util.Utility;

import com.chuyunxuanyu.app.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity{
	
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	
	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private List<String> dataList = new ArrayList<String>();
	private FxWeatherDB fxWeatherDB;
	/*
	 * 省列表
	 */
	private List<Province> provinceList;
	/*
	 * 市列表
	 */
	private List<City> cityList;
	/*
	 * 县列表
	 */
	private List<County> countyList;
	/*
	 * 选中的省份
	 */
	private Province selectedProvince;
	/*
	 * 选中的城市
	 */
	private City selectedCity;
	/*
	 * 当前选中的级别
	 */
	private int currentLevel = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		
		listView = (ListView) findViewById(R.id.list_view);
		titleText = (TextView) findViewById(R.id.title_text);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, dataList);
		fxWeatherDB = FxWeatherDB.getInstance(this);  
		
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if(currentLevel == LEVEL_PROVINCE){
					selectedProvince = provinceList.get(position);
					Log.d("hehe", "选中的省是 " + selectedProvince.getProvinceName() + " " + selectedProvince.getProvinceCode());
					queryCities();
				} else if(currentLevel == LEVEL_CITY){
					selectedCity = cityList.get(position);
					queryCounties();
				}
			}
			
		});
		queryProvinces();
	}

	/*
	 * 查询全国所有的省，优先从数据库查询；如果没有从服务器获取
	 */
	private void queryProvinces() {
		// TODO Auto-generated method stub
		provinceList = fxWeatherDB.loadProvinces();
		if(provinceList.size() > 0){
			dataList.clear();
			for(Province province : provinceList){
				dataList.add(province.getProvinceName());
				Log.d("hehe", "011 " + province.getProvinceCode());;
			}
			adapter.notifyDataSetChanged();
			
			Log.d("hehe", "0 ");
			
			listView.setSelection(0);
			titleText.setText("中国");           
			currentLevel = LEVEL_PROVINCE;            //将当前选中级别变为省级
		} else {
			queryFromServer(null, "Province");
		}
	}
	
	/*
	 * 查询选定的省的所有城市，优先从数据库查询；如果没有从服务器获取
	 * 这里选定的省由selectedProvince提供，并不传参
	 * 这里不传参更多的考虑是基于这样选中、后退操作比较方便
	 * 否则传参的话，后退的时候，其实是并不知道上一级的id
	 */
	private void queryCities() {
		// TODO Auto-generated method stub
		cityList = fxWeatherDB.loadCities(selectedProvince.getId());
		//Log.d("hehe", "11 " + cityList.get(1).getCityName());
		if(cityList.size() > 0){
			dataList.clear();
			for(City city : cityList){
				dataList.add(city.getCityName());
				Log.d("hehe", "111 " + city.getCityName());
			}
			adapter.notifyDataSetChanged();
			
			Log.d("hehe", "1 " + dataList.get(0) + " " + cityList.get(0).getCityName());
			
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());           
			currentLevel = LEVEL_CITY;            //将当前选中级别变为城市级
		} else {
			Log.d("hehe", "查询省" + selectedProvince.getProvinceCode() + "的城市");
			queryFromServer(selectedProvince.getProvinceCode(), "City");
		}
	}
	
	private void queryCounties() {
		// TODO Auto-generated method stub
		countyList = fxWeatherDB.loadCountries(selectedCity.getId());
		if(countyList.size() > 0){
			dataList.clear();
			for(County county : countyList){
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			
			Log.d("hehe", "2 "+ " " + countyList.get(0).getCountyName());
			
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());           
			currentLevel = LEVEL_COUNTY;            //将当前选中级别变为城市级
		} else {
			queryFromServer(selectedCity.getCityCode(), "County");
		}
	}
	
	/*
	 *根据传入的代号和类型从服务器上查询省县市数据
	 */
	private void queryFromServer(final String code, final String type) {
		Log.d("hehe", "4 ");
		// TODO Auto-generated method stub
		String address;
		if(!TextUtils.isEmpty(code)){
			address = "http://www.weather.com.cn/data/list3/city" + code +".xml";
			Log.d("hehe", "a");
		} else {
			address = "http://www.weather.com.cn/data/list3/city.xml";
			Log.d("hehe", "b");
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener(){

			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				boolean result = false;
				//判断读取数据结果
				if("Province".equals(type)){
					result = Utility.handleProvinceResponse(fxWeatherDB, response);
				} else if("City".equals(type)){
					result = Utility.handleCityResponse(fxWeatherDB, response, selectedProvince.getId());
					Log.d("hehe", "41 " + result);
				} else if("County".equals(type)){
					result = Utility.handleCountyResponse(fxWeatherDB, response, selectedCity.getId());
				}
				if(result){
					//通过runOnUiThread()方法回到主线程处理逻辑
					
					runOnUiThread(new Runnable(){
						@Override
						public void run() {
							// TODO Auto-generated method stub
							closeProgressDialog();     //关闭进度对话框
							if("Province".equals(type)){
								queryProvinces();
							} else if("City".equals(type)){
								queryCities();
							} else if("County".equals(type)){
								queryCounties();
							}
						}
						
					});
				}
				
			}

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT);
					}//run
					
				});
			}//onError

		});
		
	}


	/*
	 * 显示进度对话框
	 */
	private void showProgressDialog() {
		// TODO Auto-generated method stub
		if(progressDialog == null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	
	/*
	 * 关闭进度对话框
	 */
	private void closeProgressDialog(){
		if(progressDialog != null){
			progressDialog.dismiss();
		}
	}
	
	/*
	 * 捕获back键，根据当前的级别来判断，此时应该退出或者返回市列表、省列表。
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(currentLevel == LEVEL_COUNTY){
			queryCities();
		} else if(currentLevel == LEVEL_CITY){
			queryProvinces();
		} else {
			finish();
		}
	}
	
}
