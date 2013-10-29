package com.jdm.common;

public class SetupBean
{
   private static boolean showNotifications = true;
   private static boolean showActivity = true;
   private static boolean beepOnTransition = true;
   private static boolean echoDebugOnSerial = true;
   private static int pollingMillis = 1000;
   private static String deviceDescription = "";
   
   public static boolean isShowNotifications()
   {
      return showNotifications;
   }
   public static void setShowNotifications(boolean showNotifications)
   {
      SetupBean.showNotifications = showNotifications;
   }
   public static boolean isBeepOnTransition()
   {
      return beepOnTransition;
   }
   public static void setBeepOnTransition(boolean beepOnTransition)
   {
      SetupBean.beepOnTransition = beepOnTransition;
   }
   public static int getPollingMillis()
   {
      return pollingMillis;
   }
   public static void setPollingMillis(int pollingMillis)
   {
      SetupBean.pollingMillis = pollingMillis;
   }
   public static boolean isEchoDebugOnSerial()
   {
      return echoDebugOnSerial;
   }
   public static void setEchoDebugOnSerial(boolean echoDebugOnSerial)
   {
      SetupBean.echoDebugOnSerial = echoDebugOnSerial;
   }
   public static boolean isShowActivity()
   {
      return showActivity;
   }
   public static void setShowActivity(boolean showActivity)
   {
      SetupBean.showActivity = showActivity;
   }
   public static String getDeviceDescription()
   {
      return deviceDescription;
   }
   public static void setDeviceDescription(String deviceDescription)
   {
      SetupBean.deviceDescription = deviceDescription;
   }
}
