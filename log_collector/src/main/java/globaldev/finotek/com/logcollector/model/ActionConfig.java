package globaldev.finotek.com.logcollector.model;

/**
 * Created by magyeong-ug on 04/05/2017.
 */

public class ActionConfig<T> {
	private int actionType;
	private long period;
	private long deadline;
	private int requiredNetworkType;
	private boolean requiresCharging;
	private boolean requiresDeviceIdle;
	private boolean persisted;
	private T option;
	private long minimumLatency;

	public ActionConfig(int actionType) {
		this.actionType = actionType;
	}

	public T getOption() {
		return option;
	}

	public void setOption(T option) {
		this.option = option;
	}

	public int getActionType() {
		return actionType;
	}


	public long getPeriod() {
		return period;
	}

	public void setPeriod(long period) {
		this.period = period;
	}

	public long getDeadline() {
		return deadline;
	}

	public void setDeadline(long deadline) {
		this.deadline = deadline;
	}

	public int getRequiredNetworkType() {
		return requiredNetworkType;
	}

	public void setRequiredNetworkType(int requiredNetworkType) {
		this.requiredNetworkType = requiredNetworkType;
	}

	public boolean isRequiresCharging() {
		return requiresCharging;
	}

	public void setRequiresCharging(boolean requiresCharging) {
		this.requiresCharging = requiresCharging;
	}

	public boolean isRequiresDeviceIdle() {
		return requiresDeviceIdle;
	}

	public void setRequiresDeviceIdle(boolean requiresDeviceIdle) {
		this.requiresDeviceIdle = requiresDeviceIdle;
	}

	public boolean isPersisted() {
		return persisted;
	}

	public void setPersisted(boolean persisted) {
		this.persisted = persisted;
	}

	public long getMinimumLatency() {
		return minimumLatency;
	}


	public void setMinimumLatency(long minimumLatency) {
		this.minimumLatency = minimumLatency;
	}
}
