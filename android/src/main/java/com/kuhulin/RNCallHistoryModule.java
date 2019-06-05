package com.kuhulin;

import android.database.Cursor;
import android.provider.CallLog;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RNCallHistoryModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;

  public RNCallHistoryModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNCallHistory";
  }

  @ReactMethod
  public void getCallHistory(String date, Promise promise) {
    String[] projection = new String[]{
            CallLog.Calls.NUMBER,
            CallLog.Calls.TYPE,
            CallLog.Calls.DATE
    };
    String selection = CallLog.Calls.DATE + " >= ?";
    Long dateStr;
    try {
      dateStr = new SimpleDateFormat("dd/MM/yyyy").parse(date).getTime();
      Cursor cursor = reactContext.getContentResolver().query(
              CallLog.Calls.CONTENT_URI,
              null,
              CallLog.Calls.DATE + ">?",
              new String[]{"" + dateStr},
              CallLog.Calls.DATE + " DESC"
      );

      int number = cursor.getColumnIndex(CallLog.Calls.NUMBER);
      int type = cursor.getColumnIndex(CallLog.Calls.TYPE);
      int datee = cursor.getColumnIndex(CallLog.Calls.DATE);
      int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);

      WritableArray params = Arguments.createArray();
      params.pushInt(cursor.getCount());

      while (cursor.moveToNext()) {
        String phNumber = cursor.getString(number); // mobile number
        String callType = cursor.getString(type); // call type
        String callDate = cursor.getString(datee); // call date
        Date callDayTime = new Date(Long.valueOf(callDate));
        String callDuration = cursor.getString(duration);
        String dir = null;
        int dircode = Integer.parseInt(callType);
        switch (dircode) {
          case CallLog.Calls.OUTGOING_TYPE:
            dir = "OUTGOING";
            break;

          case CallLog.Calls.INCOMING_TYPE:
            dir = "INCOMING";
            break;

          case CallLog.Calls.MISSED_TYPE:
            dir = "MISSED";
            break;
        }
        WritableMap tmpMap = Arguments.createMap();
        tmpMap.putString("number", phNumber);
        tmpMap.putString("type", dir);
        tmpMap.putString("date", callDayTime.toString());
        tmpMap.putString("rawDate", callDate);
        tmpMap.putString("duration", callDuration);
        params.pushMap(tmpMap);
      }
      promise.resolve(params);
    } catch (ParseException e) {
      promise.reject("Incorrect date");
    }
    //String[] selectionArgs = {dateStr};

    //Log.i("CALL_HISTORY", "" + dateStr);


  }
}
