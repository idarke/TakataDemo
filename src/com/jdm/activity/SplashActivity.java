package com.jdm.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.jdm.takatademo.R;

public class SplashActivity extends Activity
{
   private SplashActivity thisActivity = this;
   
   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      this.requestWindowFeature(Window.FEATURE_NO_TITLE);
      setContentView(R.layout.activity_splash);
      
      pauseAndLaunchApp();
   }

   private void pauseAndLaunchApp()
   {
      Thread splashTimer = new Thread() {
         public void run()
         {
            try
            {
               sleep(3000);
            }
            catch (InterruptedException e)
            {
            }
            finally
            {
               Intent intent = new Intent(thisActivity, MainActivity.class);
               startActivity(intent);
               finish();
            }
         }
      };
      splashTimer.start();
   }

}
