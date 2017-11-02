package globaldev.finotek.com.aws_tts;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.polly.AmazonPollyPresigningClient;
import com.amazonaws.services.polly.model.DescribeVoicesRequest;
import com.amazonaws.services.polly.model.DescribeVoicesResult;
import com.amazonaws.services.polly.model.LanguageCode;
import com.amazonaws.services.polly.model.OutputFormat;
import com.amazonaws.services.polly.model.SynthesizeSpeechPresignRequest;
import com.amazonaws.services.polly.model.Voice;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import io.reactivex.Observable;

import static android.content.ContentValues.TAG;

/**
 * Created by magyeong-ug on 2017. 11. 1..
 */

public class TTSPlayer {

	private static final String COGNITO_POOL_ID = "ap-northeast-2:02da37b8-e554-430b-8a2b-c9ae56f992d0";
	private static final Regions MY_REGION = Regions.AP_NORTHEAST_2;
	private MediaPlayer mediaPlayer;
	private AmazonPollyPresigningClient client;
	private Context context;

	public TTSPlayer(Context appContext) {
		this.context = appContext;
		this.mediaPlayer = new MediaPlayer();
		initPollyClient();
	}

	private void initPollyClient() {
		CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
				context,
				COGNITO_POOL_ID,
				MY_REGION
		);

		// Create a client that supports generation of presigned URLs.
		client = new AmazonPollyPresigningClient(credentialsProvider);
	}

	public void requestPollyVoice(final String textToRead, final String languageCode) {

		new Thread(new Runnable() {
			@Override
			public void run() {

				// Create describe voices request.
				DescribeVoicesRequest describeVoicesRequest = new DescribeVoicesRequest();
				describeVoicesRequest.setLanguageCode(languageCode);

				DescribeVoicesResult describeVoicesResult = null;
				try {
					// Synchronously ask the Polly Service to describe available TTS voices.
					describeVoicesResult = client.describeVoices(describeVoicesRequest);
				} catch (RuntimeException e) {
					Log.e(TAG, "Unable to get available voices. " + e.getMessage());
				}

				// Get list of voices from the result.
				List<Voice> voices = describeVoicesResult.getVoices();

				// Create speech synthesis request.
				SynthesizeSpeechPresignRequest synthesizeSpeechPresignRequest =
						new SynthesizeSpeechPresignRequest()
								.withText(textToRead)
								.withVoiceId(voices.get(0).getId())
								.withOutputFormat(OutputFormat.Mp3);

				// Get the presigned URL for synthesized speech audio stream.
				URL presignedSynthesizeSpeechUrl =
						client.getPresignedSynthesizeSpeechUrl(synthesizeSpeechPresignRequest);

				Log.i(TAG, "Playing speech from presigned URL: " + presignedSynthesizeSpeechUrl);

				// Create a media player to play the synthesized audio stream.

				setupNewMediaPlayer();

				mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

				try {
					// Set media player's data source to previously obtained URL.
					mediaPlayer.setDataSource(presignedSynthesizeSpeechUrl.toString());
				} catch (IOException e) {
					Log.e(TAG, "Unable to set data source for the media player! " + e.getMessage());
				}

				// Start the playback asynchronously (since the data source is a network stream).
				mediaPlayer.prepareAsync();
			}
		}).start();


	}

	private void setupNewMediaPlayer() {
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				mp.release();
				setupNewMediaPlayer();
			}
		});
		mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				mp.start();
			}
		});
		mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				return false;
			}
		});

	}
}
