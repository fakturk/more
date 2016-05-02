package com.unist.netlab.fakturk.more;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver
{
   static final String Log_Tag = "MyReceiver";
   @Override
   public void onReceive(Context arg0, Intent arg1){
      // Log.d(LOG_TAG, "onReceive");
       String measurement = arg1.getStringExtra("measurement");
       System.out.println("I am here");
   }

}
