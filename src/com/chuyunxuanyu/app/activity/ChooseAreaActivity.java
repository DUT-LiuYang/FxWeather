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
	 * ʡ�б�
	 */
	private List<Province> provinceList;
	/*
	 * ���б�
	 */
	private List<City> cityList;
	/*
	 * ���б�
	 */
	private List<County> countyList;
	/*
	 * ѡ�е�ʡ��
	 */
	private Province selectedProvince;
	/*
	 * ѡ�еĳ���
	 */
	private City selectedCity;
	/*
	 * ��ǰѡ�еļ���
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
					Log.d("hehe", "ѡ�е�ʡ�� " + selectedProvince.getProvinceName() + " " + selectedProvince.getProvinceCode());
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
	 * ��ѯȫ�����е�ʡ�����ȴ����ݿ��ѯ�����û�дӷ�������ȡ
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
			titleText.setText("�й�");           
			currentLevel = LEVEL_PROVINCE;            //����ǰѡ�м����Ϊʡ��
		} else {
			queryFromServer(null, "Province");
		}
	}
	
	/*
	 * ��ѯѡ����ʡ�����г��У����ȴ����ݿ��ѯ�����û�дӷ�������ȡ
	 * ����ѡ����ʡ��selectedProvince�ṩ����������
	 * ���ﲻ���θ���Ŀ����ǻ�������ѡ�С����˲����ȽϷ���
	 * ���򴫲εĻ������˵�ʱ����ʵ�ǲ���֪����һ����id
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
			currentLevel = LEVEL_CITY;            //����ǰѡ�м����Ϊ���м�
		} else {
			Log.d("hehe", "��ѯʡ" + selectedProvince.getProvinceCode() + "�ĳ���");
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
			currentLevel = LEVEL_COUNTY;            //����ǰѡ�м����Ϊ���м�
		} else {
			queryFromServer(selectedCity.getCityCode(), "County");
		}
	}
	
	/*
	 *���ݴ���Ĵ��ź����ʹӷ������ϲ�ѯʡ��������
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
				//�ж϶�ȡ���ݽ��
				if("Province".equals(type)){
					result = Utility.handleProvinceResponse(fxWeatherDB, response);
				} else if("City".equals(type)){
					result = Utility.handleCityResponse(fxWeatherDB, response, selectedProvince.getId());
					Log.d("hehe", "41 " + result);
				} else if("County".equals(type)){
					result = Utility.handleCountyResponse(fxWeatherDB, response, selectedCity.getId());
				}
				if(result){
					//ͨ��runOnUiThread()�����ص����̴߳����߼�
					
					runOnUiThread(new Runnable(){
						@Override
						public void run() {
							// TODO Auto-generated method stub
							closeProgressDialog();     //�رս��ȶԻ���
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
						Toast.makeText(ChooseAreaActivity.this, "����ʧ��", Toast.LENGTH_SHORT);
					}//run
					
				});
			}//onError

		});
		
	}


	/*
	 * ��ʾ���ȶԻ���
	 */
	private void showProgressDialog() {
		// TODO Auto-generated method stub
		if(progressDialog == null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("���ڼ���");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	
	/*
	 * �رս��ȶԻ���
	 */
	private void closeProgressDialog(){
		if(progressDialog != null){
			progressDialog.dismiss();
		}
	}
	
	/*
	 * ����back�������ݵ�ǰ�ļ������жϣ���ʱӦ���˳����߷������б�ʡ�б�
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
