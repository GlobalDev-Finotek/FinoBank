package finotek.global.dev.talkbank_ca.base.mvp.event;

/**
 * Created by magyeong-ug on 2017. 3. 31..
 */

public class AccuracyMeasureEvent implements IEvent {
	private double accuracy;

	public AccuracyMeasureEvent(double accuracy) {
		this.accuracy = accuracy;
	}

	public double getAccuracy() {
		return accuracy;
	}
}
