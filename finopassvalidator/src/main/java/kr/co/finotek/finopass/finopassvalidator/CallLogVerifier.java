package kr.co.finotek.finopass.finopassvalidator;

import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by KoDeokyoon on 2017. 3. 24..
 */

public class CallLogVerifier {

    private static int MaxAvailableCalledMinute = 10;

    private static Cursor getCallLog(Context context) {
        Log.d("aa", "Permission opened");
        String[] projection;
        projection = new String[]{
            CallLog.Calls.NUMBER,
            CallLog.Calls.TYPE,
            CallLog.Calls.DURATION,
            CallLog.Calls.CACHED_NAME,
            CallLog.Calls.DATE,
            CallLog.Calls._ID
        };

        String beforeStr = getMinus10MinTimeStr();
        String nowStr = getNowTimeStr();

        try {
        /* 지금으로부터 10분 전의 통화목록만 얻어옴 */
            return context.getContentResolver().query(CallLog.Calls.CONTENT_URI, projection,
                CallLog.Calls.DATE + " BETWEEN ? AND ? ", new String[]{beforeStr, nowStr},
                CallLog.Calls._ID + " DESC");
        } catch (SecurityException e) {
        }
        return null;
    }

    @NonNull
    private static String getNowTimeStr() {
        Calendar now = Calendar.getInstance();
        now.setTime(new Date());
        return String.valueOf(now.getTimeInMillis());
    }


    @NonNull
    private static String getMinus10MinTimeStr() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MINUTE, -10);
        return String.valueOf(c.getTimeInMillis());
    }


    public static double getCallLogPassRate(Context context) {

        Cursor cursor = getCallLog(context);
        ArrayList<CallDetailRecord> records = parseCallLog(cursor);
        Long now = new Date().getTime();
        double totalWeight = 0.0;
        int size = 0;
        for(CallDetailRecord cdr : records)
        {
            Long diff = now - Long.parseLong(cdr.getFinishTime());

            if(diff < MaxAvailableCalledMinute * 60 * 1000){
                size++;
                //totalWeight = totalWeight + getCDRWeight(cdr.getName(), diff/60000.0, Long.parseLong(cdr.getDuration())/60.0);
                //데모에서는 10분 통화 한 것으로 가정함
                totalWeight = totalWeight + getCDRWeight(cdr.getName(), diff/60000.0, 10);
            }
        }
        Log.d("total",totalWeight+"/"+size);

        if(size == 0)
            return 0.5;
        else
            return totalWeight / size;


    }

    private static double getCDRWeight(String name, double finishedPeriod, double duration){
        double cdrWeight = 0.0;
        //통화 완료 후 시간이 흐를수록 낮은 점수를 획득
        double timeWeight = getNegativeWeight(finishedPeriod);
        //통화 시간이 길 수록 높은 점수를 획득
        double durationWeight = getPositiveWeight(duration);
        double avgWeight = (timeWeight + durationWeight) /2;
        //지인 통화가 아닌 경우에는 끊은 후 흐른 시간에 따라 점수가 결정됨
        if(name == null){
            cdrWeight = 0.5 - timeWeight;
        }
        //지인 통화의 경우 끊은 후 흐른 시간과 통화시간을 함께 고려함
        else{
            cdrWeight = 0.5 + avgWeight;
        }

        Log.d("Calc","N:"+name+"/F:"+finishedPeriod+"/D:"+duration+"=>"+cdrWeight);
        return cdrWeight;
    }

    private static double getNegativeWeight(double x){
        //double exp = (1.0/2.0) * (1 / (1 + Math.exp(-x+4)));
        double result = ((-1.0/2.0) * (1.0/(1.0 + Math.exp(-x+4))))+(1.0/2.0);
        return result;
    }

    private static double getPositiveWeight(double x){
        //double exp = (1.0/2.0) * (1 / (1 + Math.exp(-x+4)));
        double result = ((1.0/2.0) * (1.0/(1.0 + Math.exp(-x+4))));
        return result;
    }

    private static ArrayList<CallDetailRecord> parseCallLog(Cursor cursor) {
        ArrayList<CallDetailRecord> records = new ArrayList<CallDetailRecord>();

        try {

            if(cursor.getCount() != 0) {
                cursor.moveToFirst();

                do {
                    String callNumber = cursor.getString(0);
                    int type          = Integer.parseInt(cursor.getString(1)); // type, 3: missing call, 2: incoming call, 1: outgoing call
                    String duration   = cursor.getString(2);
                    String name       = cursor.getString(3);

                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(Long.parseLong(cursor.getString(4)));
                    String startTime = cal.getTimeInMillis()+"";
                    //String date = DateFormat.format("yyyy-MM-dd H:m:s", cal).toString();
                    String finishTime = (cal.getTimeInMillis() + (Integer.parseInt(duration)*1000))+"";


                    records.add(new CallDetailRecord(type, callNumber, duration, name, startTime,finishTime));
                    //logString = logString + "," + callLog.toString();
                    //Log.d("",callLog.toString());
               } while(cursor.moveToNext());
            }
        } catch(SecurityException e){
            e.printStackTrace();
        } finally {
            if(cursor != null) cursor.close();
        }

        return records;
    }
}

