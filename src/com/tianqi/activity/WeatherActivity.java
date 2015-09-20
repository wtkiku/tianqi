package com.tianqi.activity;

import com.tianqi.R;
import com.tianqi.service.AutoUpdateService;
import com.tianqi.util.HttpCallbackListener;
import com.tianqi.util.HttpUtil;
import com.tianqi.util.HttpUtil2;
import com.tianqi.util.Utility;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity implements OnClickListener{
	private LinearLayout weatherInfoLayout;
	/**
	 * ������ʾ������
	 */
	private TextView cityNameText;
	/**
	 * ������ʾ����ʱ��
	 */
	private TextView timeText;
	/**
	 * ������ʾ����������Ϣ
	 */
	private TextView stateText;

	private TextView tempText;
	private TextView forecast1;
	private TextView forecast2;
	private TextView forecast3;

	private TextView windText;
	/**
	 * �л����а�ť
	 */
	private Button switchCity;
	/**
	 * ����������ť
	 */
	private Button refreshWeather;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		// ��ʼ�����ؼ�
		//weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		tempText = (TextView)findViewById(R.id.tem);
		stateText = (TextView)findViewById(R.id.state);
		cityNameText = (TextView)findViewById(R.id.cityname);
		timeText = (TextView)findViewById(R.id.time);
		windText =(TextView)findViewById(R.id.wind);
		forecast1 = (TextView)findViewById(R.id.forecast1);
		forecast2 = (TextView)findViewById(R.id.forecast2);
		forecast3 = (TextView)findViewById(R.id.forecast3);
		switchCity = (Button) findViewById(R.id.change);
		refreshWeather = (Button) findViewById(R.id.refresh);

		String cityName = getIntent().getStringExtra("city_name");
		if (!TextUtils.isEmpty(cityName)) {
			// ���ؼ�����ʱ��ȥ��ѯ����
			timeText.setText("ͬ����...");
			//weatherInfoLayout.setVisibility(View.INVISIBLE);
			//cityNameText.setVisibility(View.INVISIBLE);
			queryFromServer(cityName);
		} else {
			// û���ؼ�����ʱ��ֱ����ʾ��������
			showWeather();
		}
		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.change:
			Intent intent = new Intent(this, ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh:
			timeText.setText("ͬ����...");
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			String cityName = prefs.getString("cityname", "");
			if (!TextUtils.isEmpty(cityName)) {
				queryFromServer(cityName);
			}
			break;
		default:
			break;
		}
	}
	/**
	 * ���������ѯ����������Ӧ��������
	 */
	private void queryFromServer(final String cityName) {
		String address = "http://apis.baidu.com/heweather/pro/weather?city="+cityName;
		HttpUtil2.sendHttpRequest(address, new HttpCallbackListener() {
			@Override
			public void onFinish(final String response) {
				// ������������ص�������Ϣ
				Utility.handleWeatherResponse(WeatherActivity.this,response);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						showWeather();
					}
				});
			}
			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						timeText.setText("ͬ��ʧ��");
					}
				});
			}
		});
	}
	/**
	 * ��SharedPreferences�ļ��ж�ȡ�洢��������Ϣ������ʾ�������ϡ�
	 */
	private void showWeather() {//���ǳ�ʼ���ؼ�����ûע�⵽�ܵ��۵�Ŷ
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		cityNameText.setText( prefs.getString("cityname", ""));
		tempText.setText(prefs.getString("tem", "")+"��C"+" ");
		stateText.setText(prefs.getString("state", ""));
		timeText.setText("����ʱ�䣺" + prefs.getString("time", ""));
		windText.setText(prefs.getString("wind1", "")+"������"+prefs.getString("wind2", "")+"��");
		forecast1.setText(prefs.getString("fore0", ""));
		forecast2.setText(prefs.getString("fore1", ""));
		forecast3.setText(prefs.getString("fore2", ""));
		Intent intent = new Intent(this, AutoUpdateService.class);
		startService(intent);
		//currentDateText.setText(prefs.getString("current_date", ""));
		//weatherInfoLayout.setVisibility(View.VISIBLE);
		//cityNameText.setVisibility(View.VISIBLE);
	}
}
