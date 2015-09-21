package com.tianqi.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import com.tianqi.db.*;
import com.tianqi.model.*;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

public class Utility {
	/**
	 * 解析处理服务器返回的省级数据
	 */
	public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB,String response) {
		if(!TextUtils.isEmpty(response)){
			String []allProvinces=response.split(",");
			if(allProvinces!=null&&allProvinces.length>0){
				for(String p:allProvinces){
					String[] array=p.split("\\|");
					Province province=new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					coolWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	/**
	 * 解析处理服务器返回的市级数据
	 */
	public synchronized static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,String response,int provinceId) {
		if(!TextUtils.isEmpty(response)){
			String []allCitys=response.split(",");
			if(allCitys!=null&&allCitys.length>0){
				for(String p:allCitys){
					String[] array=p.split("\\|");
					City city=new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					coolWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}
	/**
	 * 解析处理服务器返回的县级数据
	 */
	public synchronized static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB,String response,int CityId) {
		if(!TextUtils.isEmpty(response)){
			String []allCountys=response.split(",");
			if(allCountys!=null&&allCountys.length>0){
				for(String p:allCountys){
					String[] array=p.split("\\|");
					County county=new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(CityId);
					coolWeatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}
	/**
	 * 解析处理服务器返回的天气数据
	 */
	public static void handleWeatherResponse(Context context,String response) {
		try{
			JSONObject jsonObject=new JSONObject(response);
			String neirong = jsonObject.getString("HeWeather data service 3.0");
			JSONArray jsonArray = new JSONArray(neirong);
	        JSONObject jsonObject1 = jsonArray.getJSONObject(0);	
			JSONObject now=jsonObject1.getJSONObject("now");
			JSONObject cond = now.getJSONObject("cond");
			JSONObject basic=jsonObject1.getJSONObject("basic");
			JSONObject update = basic.getJSONObject("update");
			JSONObject wind = now.getJSONObject("wind");
			String tem = now.getString("fl");
			String state = cond.getString("txt");
			String picCode = cond.getString("code");
			String cityName = basic.getString("city");
			String time = update.getString("loc");
			String wind1 = wind.getString("dir");
			String wind2 = wind.getString("sc");
			//
			String neirong2 = jsonObject1.getString("daily_forecast");
			JSONArray jsonArray2 = new JSONArray(neirong2);
			List<String> fore = new ArrayList<String>();
			for(int i=0; i<3;i++){
		        JSONObject jsonObject21 = jsonArray2.getJSONObject(i);	
		        String date = jsonObject21.getString("date").substring(5);
		        JSONObject cond2 = jsonObject21.getJSONObject("cond");
		        String state1 = cond2.getString("txt_d");
		        String state2 = cond2.getString("txt_n");
		        JSONObject tmp = jsonObject21.getJSONObject("tmp");
		        String tmp_max = tmp.getString("max");
		        String tmp_min = tmp.getString("min");
		        if(state1.equals(state2))
		        	fore.add(date+" "+tmp_min+"°C~"+tmp_max+"°C"+" "+state1);
		        else fore.add(date+" "+tmp_min+"°C~"+tmp_max+"°C"+" "+state1+"转"+state2);
			}
			saveWeatherInfo(context,cityName,tem,state,picCode,time,wind1,wind2,fore);
		}catch(Exception e){
			Log.v("crb", "handleWeatherResponse的错误报告：  "+e.toString());
		}
	}
	/**
	 * 保存天气数据
	 * @param time 
	 * @param wind2 
	 * @param wind1 
	 * @param foreDate 
	 */
	private static void saveWeatherInfo(Context context, String cityName,String tem,String state, 
			String picCode,String time, String wind1, String wind2, List<String> fore) {
		//SimpleDateFormat sdf=new SimpleDateFormat("yyyy年M月d日",Locale.CHINA);
		SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("tem",tem);
		editor.putString("state",state);
		editor.putString("picCode", picCode);
		editor.putString("cityname",cityName );
		editor.putString("time", time);
		editor.putString("wind1",wind1 );
		editor.putString("wind2",wind2 );
		editor.putString("fore0", fore.get(0));
		editor.putString("fore1", fore.get(1));
		editor.putString("fore2", fore.get(2));
		
		//editor.putString("", );

		editor.commit();
	}
}
