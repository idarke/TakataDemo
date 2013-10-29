package com.jdm.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.jdm.takatademo.R;

public class LockoutActivity extends Activity
{

   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_lockout);
      registerReceiver(finishReceiver, new IntentFilter("com.jdm.FinishLockout"));
   }
   
   private final BroadcastReceiver finishReceiver = new BroadcastReceiver() 
   {
      @Override
      public void onReceive(Context context, Intent intent) 
      {
         finish();                                   
      }
   };
}
