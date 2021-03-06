package com.tianqi.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil2 {
	/**
	 * 
	 */
	public static void sendHttpRequest(final String address,final HttpCallbackListener listener){
		new Thread(new Runnable() {
			@Override
			public void run() {
				HttpURLConnection connection=null;
				try{
					URL url=new URL(address);
					connection=(HttpURLConnection)url.openConnection();
					connection.setRequestMethod("GET");
			        connection.setRequestProperty("apikey",  "83b2954e4be42c63d59c05dd2e0d1ecc");
					connection.setConnectTimeout(2000);
					connection.setReadTimeout(2000);
					InputStream in=connection.getInputStream();
					BufferedReader reader=new BufferedReader(new InputStreamReader(in));
					StringBuilder response=new StringBuilder();
					String line;
					while((line=reader.readLine())!=null){
						response.append(line);
					}
					if(listener!=null){
						//回调ononFinish函数
						listener.onFinish(response.toString());
					}
				}catch(Exception e){
					if(listener!=null){
						//回调onError函数
						listener.onError(e);
					}
				}finally{
					if(connection!=null){
						connection.disconnect();
					}
				}
			}
		}).start();
	}

}
