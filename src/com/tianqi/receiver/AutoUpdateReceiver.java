package com.tianqi.receiver;

import com.tianqi.service.AutoUpdateService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AutoUpdateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO �Զ����ɵķ������
		Intent i = new Intent(context, AutoUpdateService.class);
		context.startService(i);
	}

}
