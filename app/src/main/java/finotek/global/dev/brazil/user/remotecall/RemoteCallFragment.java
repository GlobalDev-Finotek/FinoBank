package finotek.global.dev.brazil.user.remotecall;


import android.app.Fragment;
import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import finotek.global.dev.brazil.R;
import finotek.global.dev.brazil.databinding.FragmentRemoteCallBinding;

public class RemoteCallFragment extends Fragment {
	private FragmentRemoteCallBinding binding;
	private String videoUrl;
	private Runnable stopListener;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		this.binding = DataBindingUtil.inflate(inflater, R.layout.fragment_remote_call, container, false);
		this.binding.videoView.setVideoURI(Uri.parse(videoUrl));
		this.binding.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				mp.setLooping(true);
				binding.videoView.start();
			}
		});

		RxView.clicks(this.binding.confirmButton)
				.throttleFirst(200, TimeUnit.MILLISECONDS)
				.subscribe(aVoid -> {
					binding.videoView.stopPlayback();
					stopListener.run();
				});

		return binding.getRoot();
	}

	public void setStopListener(Runnable stopListener) {
		this.stopListener = stopListener;
	}

	public void setVideoURL(String videoUrl) {
		this.videoUrl = videoUrl;
	}
}