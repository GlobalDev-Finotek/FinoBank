package globaldev.finotek.com.logcollector.model;

/**
 * Created by magyeong-ug on 04/05/2017.
 */

public class ActionConfig<T> {
	int actionType;
	long period;
	long deadline;
	int requiredNetworkType;
	boolean requiresCharging;
	boolean requiresDeviceIdle;
	boolean persisted;
	T option;

	public T getOption() {
		return option;
	}

	public void setOption(T option) {
		this.option = option;
	}

	public int getActionType() {
		return actionType;
	}

	public void setActionType(int actionType) {
		this.actionType = actionType;
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

	public static class CallHistoryLogOption {
		int elapsedTime;

		public int getElapsedTime() {
			return elapsedTime;
		}

		public void setElapsedTime(int elapsedTime) {
			this.elapsedTime = elapsedTime;
		}
	}


}
