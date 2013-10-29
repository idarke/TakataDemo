package com.jdm.common;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;

import com.jdm.activity.MainActivity;
import com.jdm.takatademo.R;

public class Notifications
{
   private static Notification notifyNotConnected;
   private static Notification notifyConnected;
   private static Notification notifyLockout;
   private static Notification notifyNothing;
   private static Notification notifyError;
   private static Notification notifyStartup;
   
   public static void createNotifications(Activity thisActivity)
   {
      Intent intent = new Intent(thisActivity, MainActivity.class);
      PendingIntent pIntent = PendingIntent.getActivity(thisActivity, 0, intent, 0);

      notifyNotConnected = new Notification.Builder(thisActivity)
                                     .setContentTitle("Takata Demo")
                                     .setContentText("Not Connected")
                                     .setSmallIcon(R.drawable.ic_notification_grey)
                                     .setContentIntent(pIntent)
                                     .setAutoCancel(true)
                                     .build();

      notifyConnected = new Notification.Builder(thisActivity)
                                     .setContentTitle("Takata Demo")
                                     .setContentText("Running")
                                     .setSmallIcon(R.drawable.ic_notification_blue)
                                     .setContentIntent(pIntent)
                                     .setAutoCancel(true)
                                     .build();

      notifyError = new Notification.Builder(thisActivity)
                                     .setContentTitle("Takata Demo")
                                     .setContentText("Error")
                                     .setSmallIcon(R.drawable.ic_notification_red)
                                     .setContentIntent(pIntent)
                                     .setAutoCancel(true)
                                     .build();

      notifyLockout = new Notification.Builder(thisActivity)
                                     .setContentTitle("Takata Demo")
                                     .setContentText("Locked Out")
                                     .setSmallIcon(R.drawable.ic_notification_yellow)
                                     .setContentIntent(pIntent)
                                     .setAutoCancel(true)
                                     .build();
      
      notifyNothing = new Notification.Builder(thisActivity)
                                      .setContentTitle("Takata Demo")
                                      .setContentText("Nothing Received")
                                      .setSmallIcon(R.drawable.ic_notification_yellow)
                                      .setContentIntent(pIntent)
                                      .setAutoCancel(true)
                                      .build();
      
      Intent mainIntent = new Intent(MainActivity.thisActivity, MainActivity.class);
      PendingIntent pIntentMain = PendingIntent.getActivity(thisActivity, 0, mainIntent, 0);
      notifyStartup = new Notification.Builder(thisActivity)
                                      .setContentTitle("Takata Demo")
                                      .setContentText("Tap for Setup")
                                      .setSmallIcon(R.drawable.ic_launcher)
                                      .setContentIntent(pIntentMain)
                                      .setAutoCancel(true)
                                      .build();      
   }

   public static Notification getNotifyNotConnected()
   {
      return notifyNotConnected;
   }

   public static Notification getNotifyConnected()
   {
      return notifyConnected;
   }

   public static Notification getNotifyLockout()
   {
      return notifyLockout;
   }

   public static Notification getNotifyError()
   {
      return notifyError;
   }
   
   public static Notification getNotifyStartup()
   {
      return notifyStartup;
   }  
   
   public static Notification getNotifyNothing()
   {
      return notifyNothing;
   } 
}
