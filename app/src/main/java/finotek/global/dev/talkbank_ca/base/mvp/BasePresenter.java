package finotek.global.dev.talkbank_ca.base.mvp;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by mario on 2017-01-11.
 */

public abstract class BasePresenter<V extends IMvpView>  implements IPresenter<V> {
  private V mvpView;
  protected CompositeSubscription viewSubscription;

  @Override
  public void attachView(V mvpView) {
    viewSubscription = new CompositeSubscription();
    this.mvpView = mvpView;
  }

  @Override
  public void detachView() {
    if (viewSubscription != null && !viewSubscription.isUnsubscribed()) {
      viewSubscription.unsubscribe();;
    }
    mvpView = null;
  }

  public boolean isViewAttached() {
    return mvpView != null;
  }

  public V getMvpView() {
    checkViewAttached();
    return mvpView;
  }

  public void checkViewAttached() {
    if (!isViewAttached()) throw new MvpViewNotAttachedException();
  }

  public static class MvpViewNotAttachedException extends RuntimeException {
    public MvpViewNotAttachedException() {
      super("Please call Presenter.attachView(MvpView) before" +
          " requesting data to the Presenter");
    }
  }
}
