package finotek.global.dev.brazil.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import globaldev.finotek.com.logcollector.api.score.BaseScoreParam;
import globaldev.finotek.com.logcollector.api.score.ContextScoreResponse;
import globaldev.finotek.com.logcollector.model.ActionType;

public class ContextAuthPref {
	private final String SCORE_APP_LOG = "score_app_log";
	private final String SCORE_FINAL_SCORE_KEY = "final_score";
	private final String SCORE_APP_USAGE_KEY = "score_app_usage";
	private final String SCORE_CALL_KEY = "score_call";
	private final String SCORE_MESSAGE_KEY = "score_message";
	private final String SCORE_LOCATION_KEY = "score_location";
	private SharedPreferences pref;

	public ContextAuthPref(Context context) {
		pref = context.getSharedPreferences("context_auth_pref", Context.MODE_PRIVATE);
	}

	public void save(ContextScoreResponse response) {
		SharedPreferences.Editor editor = pref.edit();
		int size = 0;
		if (response.messages != null)
			size = response.messages.size();

		int total_app_usage_log = 0;
		int total_call_log = 0;
		int total_message_log = 0;
		int total_location_log = 0;

		float app_usage_score = 0.0f;
		float app_call_score = 0.0f;
		float app_message_score = 0.0f;
		float app_location_score = 0.0f;

		for (int i = 0; i < size; i++) {
			BaseScoreParam msg = response.messages.get(i);
			switch (msg.type) {
				case ActionType.GATHER_APP_USAGE_LOG:
					app_usage_score += msg.score;
					total_app_usage_log++;
					break;
				case ActionType.GATHER_CALL_LOG:
					app_call_score += msg.score;
					total_call_log++;
					break;
				case ActionType.GATHER_MESSAGE_LOG:
					app_message_score += msg.score;
					total_message_log++;
					break;
				case ActionType.GATHER_LOCATION_LOG:
					app_location_score += msg.score;
					total_location_log++;
					break;
			}
		}

		app_usage_score = (total_app_usage_log == 0) ? 0 : app_usage_score / total_app_usage_log;
		app_call_score = (total_call_log == 0) ? 0 : app_call_score / total_call_log;
		app_location_score = (total_location_log == 0) ? 0 : app_location_score / total_location_log;
		app_message_score = (total_message_log == 0) ? 0 : app_message_score / total_message_log;

		Gson gson = new Gson();
		editor.putString(SCORE_APP_LOG, gson.toJson(response));
		editor.putFloat(SCORE_APP_USAGE_KEY, app_usage_score);
		editor.putFloat(SCORE_CALL_KEY, app_call_score);
		editor.putFloat(SCORE_LOCATION_KEY, app_location_score);
		editor.putFloat(SCORE_MESSAGE_KEY, app_message_score);
		editor.putFloat(SCORE_FINAL_SCORE_KEY, (float) response.finalScore);
		editor.apply();
	}

	public float getAppUsageScore() {
		return pref.getFloat(SCORE_APP_USAGE_KEY, 0.0f);
	}

	public float getCallScore() {
		return pref.getFloat(SCORE_CALL_KEY, 0.0f);
	}

	public float getLocationScore() {
		return pref.getFloat(SCORE_LOCATION_KEY, 0.0f);
	}

	public float getMessageScore() {
		return pref.getFloat(SCORE_MESSAGE_KEY, 0.0f);
	}

	public float getTotalScore() {
		float total = 0.0f;
		total += getAppUsageScore();
		total += getCallScore();
		total += getLocationScore();
		total += getMessageScore();
		return total;
	}

	public float getFinalScore() {
		return pref.getFloat(SCORE_FINAL_SCORE_KEY, 0.0f);
	}

	public ContextScoreResponse getScoreParams() {
		try {
			Gson gson = new Gson();
			Log.d("FINOTEK", "context score response stored as : " + pref.getString(SCORE_APP_LOG, "none"));

			String prefString = pref.getString(SCORE_APP_LOG, "");
			if (prefString.isEmpty() || prefString.equals("")) {
				return null;
			} else {
				return gson.fromJson(pref.getString(SCORE_APP_LOG, ""), ContextScoreResponse.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
