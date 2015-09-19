package com.tianqi.db;

import java.util.ArrayList;
import java.util.List;

import com.tianqi.model.City;
import com.tianqi.model.County;
import com.tianqi.model.Province;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CoolWeatherDB {
	/**
	 * ���ݿ���
	 */
    public static final String DB_NAME="cool_weather";
    /**
     * ���ݿ�汾
     */
    public static final int VERSION=1;
    private static CoolWeatherDB coolWeatherDB;
    private SQLiteDatabase db;
    /**
     * ���췽��˽�л�
     */
    private CoolWeatherDB(Context context){
    	CoolWeatherOpenHelper dbHelper=new CoolWeatherOpenHelper(context, DB_NAME, null, VERSION);
        db=dbHelper.getWritableDatabase();
    }
    /**
     * ��ȡCoolWeatherDBʵ��
     */
    public synchronized static CoolWeatherDB getInstance (Context context) {
    	if(coolWeatherDB==null){
    		coolWeatherDB=new CoolWeatherDB(context);
    	}
		return coolWeatherDB;
	}
    /**
     * �����й�ʡ�����ݵ����ݿ�
     */
    public void saveProvince(Province province) {
		if(province!=null){
			ContentValues values=new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			db.insert("province", null, values);
		}
	}
    /**
     * �����ݿ��м����й�ʡ������
     */
    public List<Province> loadProvince() {
    	List<Province> list=new ArrayList<Province>();
    	Cursor cursor=db.query("province", null, null, null, null, null, null);
    	if(cursor.moveToFirst()){
    		do{
    			Province province=new Province();
    			province.setId(cursor.getInt(cursor.getColumnIndex("id")));
    			province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
    			province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
    			list.add(province);
    		}while(cursor.moveToNext());
    	}
    	return list;
	}
    /**
     * ����ĳʡ�м����ݵ����ݿ�
     */
    public void saveCity(City city) {
    	if(city!=null){
    		ContentValues values=new ContentValues();
    		values.put("city_name", city.getCityName());
    		values.put("city_code", city.getCityCode());
    		values.put("province_id", city.getProvinceId());
    		db.insert("city", null, values);
    	}
    }
    /**
     * �����ݿ��м���ĳʡ�м�����
     */
    public List<City> loadCity(int provinceId) {
    	List<City> list=new ArrayList<City>();
    	Cursor cursor=db.query("city", null, "province_id=?", new String[]{String.valueOf(provinceId)}, null, null, null);
    	if(cursor.moveToFirst()){
    		do{
    			City city=new City();
    			city.setId(cursor.getInt(cursor.getColumnIndex("id")));
    			city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
    			city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
    			city.setProvinceId(provinceId);
    			list.add(city);
    		}while(cursor.moveToNext());
    	}
    	return list;
    }
    /**
     * ����ĳʡ�м����ݵ����ݿ�
     */
    public void saveCounty(County county) {
    	if(county!=null){
    		ContentValues values=new ContentValues();
    		values.put("county_name", county.getCountyName());
    		values.put("county_code", county.getCountyCode());
    		values.put("city_id", county.getCityId());
    		db.insert("county", null, values);
    	}
    }
    /**
     * �����ݿ��м���ĳ���ؼ�����
     */
    public List<County> loadCounty(int cityId) {
    	List<County> list=new ArrayList<County>();
    	Cursor cursor=db.query("county", null, "city_id=?", new String[]{String.valueOf(cityId)}, null, null, null);
    	if(cursor.moveToFirst()){
    		do{
    			County county=new County();
    			county.setId(cursor.getInt(cursor.getColumnIndex("id")));
    			county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
    			county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
    			county.setCityId(cityId);
    			list.add(county);
    		}while(cursor.moveToNext());
    	}
    	return list;
    }
}