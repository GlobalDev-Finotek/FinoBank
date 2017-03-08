package finotek.global.dev.talkbank_ca.base.mvp;

/**
 * Created by mario on 2017-01-11.
 */

public interface IPresenter<V extends IMvpView> {
  void attachView(V mvpView);

  void detachView();
}
