package com.tianqi.activity;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.tianqi.R;
import com.tianqi.R.drawable;
import com.tianqi.service.AutoUpdateService;
import com.tianqi.util.HttpCallbackListener;
import com.tianqi.util.HttpUtil;
import com.tianqi.util.HttpUtil2;
import com.tianqi.util.Utility;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity implements OnClickListener{
	private LinearLayout weatherInfoLayout;

	private TextView cityNameText;

	private TextView timeText;

	private TextView stateText;
	private ImageView imageView; 
	private TextView tempText;
	private TextView forecast1;
	private TextView forecast2;
	private TextView forecast3;
	private TextView fore;

	private TextView windText;	
	private Button switchCity;

	private Button refreshWeather;
	 Bitmap bitmap;  
	/*Handler handler=new Handler(){  
        @Override  
        public void handleMessage(Message msg) {  
            if (msg.what==12321) {  
                imageView.setImageBitmap(bitmap);  
            }  
        }            
    }; */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		//weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		tempText = (TextView)findViewById(R.id.tem);
		stateText = (TextView)findViewById(R.id.state);
		cityNameText = (TextView)findViewById(R.id.cityname);
		timeText = (TextView)findViewById(R.id.time);
		windText =(TextView)findViewById(R.id.wind);
		forecast1 = (TextView)findViewById(R.id.forecast1);
		forecast2 = (TextView)findViewById(R.id.forecast2);
		forecast3 = (TextView)findViewById(R.id.forecast3);
		fore = (TextView)findViewById(R.id.fore);
		switchCity = (Button) findViewById(R.id.change);
		refreshWeather = (Button) findViewById(R.id.refresh);
		
		imageView = (ImageView) findViewById(R.id.pic);  		

		String cityName = getIntent().getStringExtra("city_name");
		if (!TextUtils.isEmpty(cityName)) {
			// 有县级代号时就去查询天气
			timeText.setText("同步中...");
			//weatherInfoLayout.setVisibility(View.INVISIBLE);
			//cityNameText.setVisibility(View.INVISIBLE);
			queryFromServer(cityName);
		} else {
			// 没有县级代号时就直接显示本地天气
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
			timeText.setText("同步中...");
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
	 * 向服务器查询城市名所对应的天气。
	 */
	private void queryFromServer(final String cityName) {
		String address = null;
		address = "https://api.heweather.com/x3/weather?city="
				+java.net.URLEncoder.encode(cityName)+"&key=07da7c0bd34d4fe4a519df49c7063cd1";
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			@Override
			public void onFinish(final String response) {
				// 处理服务器返回的天气信息
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
						timeText.setText("同步失败");
					}
				});
			}
		});
	}
	/**
	 * 从SharedPreferences文件中读取存储的天气信息，并显示到界面上。
	 */
	private void showWeather() {//忘记初始化控件报错没注意到很蛋疼的哦
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		cityNameText.setText( prefs.getString("cityname", ""));
		tempText.setText(prefs.getString("tem", "")+"°C"+" ");
		stateText.setText(prefs.getString("state", ""));
		timeText.setText("更新时间：" + prefs.getString("time", ""));
		windText.setText(prefs.getString("wind1", "")+"，风力"+prefs.getString("wind2", "")+"级");
		fore.setText("天气预报");
		forecast1.setText(prefs.getString("fore0", ""));
		forecast2.setText(prefs.getString("fore1", ""));
		forecast3.setText(prefs.getString("fore2", ""));
		String picCode = "q"+prefs.getString("picCode", "999");
		Class<drawable> cls = R.drawable.class;
		Integer value = null;
        try {
            value = cls.getDeclaredField(picCode).getInt(null);
                    //    Log.v("value",value.toString());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		imageView.setImageResource(value);
      //"http://files.heweather.com/cond_icon/"+prefs.getString("picCode", "")+".png" 

		Intent intent = new Intent(this, AutoUpdateService.class);
		startService(intent);
		//currentDateText.setText(prefs.getString("current_date", ""));
		//weatherInfoLayout.setVisibility(View.VISIBLE);
		//cityNameText.setVisibility(View.VISIBLE);
	}
	//网络下载天气图片
	/*void showPic(){
		final SharedPreferences prefs1 = PreferenceManager.getDefaultSharedPreferences(this);
	    new Thread(){  
	        @Override  
	        public void run() {  
	            try {  	                
	                URL url=new URL("http://files.heweather.com/cond_icon/"+
	                		prefs1.getString("picCode", "999")+".png" );  
	                InputStream is= url.openStream();  
	                bitmap = BitmapFactory.decodeStream(is);  
	              //  handler.sendEmptyMessage(12321);  
	                is.close();  
	            } catch (Exception e) {  
	                e.printStackTrace();  
	            }  
	        }            
	    }.start();  
	    runOnUiThread(new Runnable() {
			@Override
			public void run() {
		        imageView.setImageBitmap(bitmap);  
			}
		});
	}*/
}
