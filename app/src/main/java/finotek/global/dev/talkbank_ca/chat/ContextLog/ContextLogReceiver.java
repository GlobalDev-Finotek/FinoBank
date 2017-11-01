package finotek.global.dev.talkbank_ca.chat.ContextLog;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import finotek.global.dev.talkbank_ca.chat.ChatActivity;
import finotek.global.dev.talkbank_ca.chat.MessageBox;
import finotek.global.dev.talkbank_ca.chat.messages.context.ContextScoreReceived;
import finotek.global.dev.talkbank_ca.util.ContextAuthPref;
import globaldev.finotek.com.logcollector.Finopass;
import globaldev.finotek.com.logcollector.model.ApplicationLog;
import globaldev.finotek.com.logcollector.model.CallHistoryLog;
import globaldev.finotek.com.logcollector.model.LocationLog;
import globaldev.finotek.com.logcollector.model.MessageLog;
import globaldev.finotek.com.logcollector.model.ValueQueryGenerator;
import globaldev.finotek.com.logcollector.util.AesInstance;
import globaldev.finotek.com.logcollector.util.userinfo.UserInfoGetter;
import globaldev.finotek.com.logcollector.util.userinfo.UserInfoGetterImpl;

/**
 * Created by idohyeon on 2017. 10. 25..
 */

public class ContextLogReceiver extends BroadcastReceiver {
    private Application app;
    private Context context;
    private Requester requester;

    public ContextLogReceiver(Application app, Context context, Requester requester){
        this.app = app;
        this.context = context;
        this.requester = requester;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ContextLogType askType = (ContextLogType) intent.getSerializableExtra("askType");
        ArrayList<ValueQueryGenerator> queryMaps = new ArrayList<>();

        if (askType == ContextLogType.sms || askType == ContextLogType.total) {
            List<MessageLog> smsLogData = intent.getParcelableArrayListExtra("smsLog");
            queryMaps.addAll(smsLogData);
            Log.d("FINOPASS", "sms logs: " + smsLogData);
        }

        if (askType == ContextLogType.call || askType == ContextLogType.total) {
            List<CallHistoryLog> callLogData = intent.getParcelableArrayListExtra("callLog");
            queryMaps.addAll(callLogData);
            Log.d("FINOPASS", "call logs: " + callLogData);
        }

        if (askType == ContextLogType.location || askType == ContextLogType.total) {
            List<LocationLog> locationLogData = intent.getParcelableArrayListExtra("locationLog");
            queryMaps.addAll(locationLogData);
            Log.d("FINOPASS", "location logs: " + locationLogData);
        }

        if (askType == ContextLogType.app || askType == ContextLogType.total) {
            List<ApplicationLog> appLogData = intent.getParcelableArrayListExtra("appLog");
            if (appLogData != null) {
                int skyHomeAppId = 0;
                int size = appLogData.size();
                for (int i = 0; i < size; i++) {
                    ApplicationLog log = appLogData.get(i);
                    try {
                        UserInfoGetter uig = new UserInfoGetterImpl(app, context.getSharedPreferences("prefs", Context.MODE_PRIVATE));
                        AesInstance aes = AesInstance.getInstance(uig.getUserKey().substring(0, 16).getBytes());

                        if (aes.decText(log.appName).equals("SKY 홈")) {
                            skyHomeAppId = i;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (!appLogData.isEmpty()) {
                    appLogData.remove(skyHomeAppId);
                }

                queryMaps.addAll(appLogData);
            }

            Log.d("FINOPASS", "app logs: " + appLogData);
        }

        requester.request(queryMaps);
    }

    public interface Requester {
        void request(List<ValueQueryGenerator> queryMaps);
    }
}
