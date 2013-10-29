package com.jdm.common;

public class Constants
{
   public static final String VERSION = "3.1g";
   public static final String NOT_CONNECTED_MSG = ":0000";
   public static final String CONNECTED_MSG = ":0001";
   public static final String LOCKOUT_MSG = ":0002";
   public static final String ERROR_MSG = ":-500";
   
   public static final int STATE_NOT_CONNECTED = 0;
   public static final int STATE_CONNECTED = 1;
   public static final int STATE_LOCKOUT = 2;
   public static final int STATE_ERROR = 3;
}
