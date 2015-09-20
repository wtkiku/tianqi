package com.tianqi.service;

import com.tianqi.receiver.AutoUpdateReceiver;
import com.tianqi.util.HttpCallbackListener;
import com.tianqi.util.HttpUtil2;
import com.tianqi.util.Utility;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

public class AutoUpdateService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO 自动生成的方法存根
		return null;
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				updateWeather();
			}
		}).start();
		AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
		int anHour = 60 * 60 * 1000; // 这是1小时的毫秒数
		long triggerAtTime = SystemClock.elapsedRealtime() + anHour*1;
		Intent i = new Intent(this, AutoUpdateReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
		return super.onStartCommand(intent, flags, startId);
	}
	/**
	 * 更新天气信息。
	 */
	private void updateWeather() {
		SharedPreferences prefs = PreferenceManager.
				getDefaultSharedPreferences(this);
		String cityName = prefs.getString("cityname", "");
		String address = "http://apis.baidu.com/heweather/pro/weather?city="+cityName;
		HttpUtil2.sendHttpRequest(address, new HttpCallbackListener() {
			@Override
			public void onFinish(String response) {
				Utility.handleWeatherResponse(AutoUpdateService.this,
						response);
			}
			@Override
			public void onError(Exception e) {
				e.printStackTrace();
			}
		});
	}
}
