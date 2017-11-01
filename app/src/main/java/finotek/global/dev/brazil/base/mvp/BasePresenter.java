package finotek.global.dev.brazil.base.mvp;

import io.reactivex.disposables.CompositeDisposable;

public abstract class BasePresenter<V extends IMvpView> implements IPresenter<V> {
	protected CompositeDisposable viewSubscription;
	private V mvpView;

	/**
	 * @param mvpView View 인터페이스
	 *                View 인터페이스를 설정
	 */
	@Override
	public void attachView(V mvpView) {
		viewSubscription = new CompositeDisposable();
		this.mvpView = mvpView;
	}

	/**
	 * View를 해제하고 Subscription 들 해제
	 */
	@Override
	public void detachView() {
		if (viewSubscription != null && !viewSubscription.isDisposed()) {
			viewSubscription.dispose();
			;
		}
		mvpView = null;
	}

	/**
	 * @return view 가 설정되어있는지 여부 리턴
	 */
	public boolean isViewAttached() {
		return mvpView != null;
	}

	/**
	 * @return view 인터페이스 리턴
	 */
	public V getMvpView() {
		checkViewAttached();
		return mvpView;
	}

	/**
	 * view 가 설정되었는 지 확인
	 * 설정이 안되있을 경우 {@link finotek.global.dev.brazil.base.mvp.BasePresenter.MvpViewNotAttachedException} 발생
	 */
	public void checkViewAttached() {
		if (!isViewAttached()) throw new MvpViewNotAttachedException();
	}


	/**
	 * view 가 설정되지 않을 경우 발생하는 예외
	 */
	public static class MvpViewNotAttachedException extends RuntimeException {
		public MvpViewNotAttachedException() {
			super("Please call Presenter.attachView(MvpView) before" +
					" requesting data to the Presenter");
		}
	}
}
