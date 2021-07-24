package tk.pokatomnik.suspicious.ui.home;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import tk.pokatomnik.suspicious.entities.Password;
import tk.pokatomnik.suspicious.SuspiciousApplication;
import tk.pokatomnik.suspicious.utils.ToastError;

public class PasswordsExtractor implements Disposable {
    private final FragmentActivity fragmentActivity;

    private final BehaviorSubject<List<Password>> publishSubject = BehaviorSubject.createDefault(new ArrayList<>());

    @Nullable
    private Disposable fetchPasswordsSubscription;

    private boolean isDisposed = false;

    public PasswordsExtractor(@Nullable FragmentActivity initialFragmentActivity) {
        fragmentActivity = initialFragmentActivity;
    }

    public void extract() {
        fetchPasswordsSubscription = Optional.ofNullable(fragmentActivity).map((activity) -> {
            final SuspiciousApplication application = (SuspiciousApplication) activity.getApplication();
            return application
                .getPasswordDatabaseService()
                .getAll()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(publishSubject::onNext, new ToastError<>(fragmentActivity, "Error getting passwords"));
        }).orElse(Disposable.empty());
    }

    public BehaviorSubject<List<Password>> getPasswordsObservable() {
        return publishSubject;
    }

    @Override
    public void dispose() {
        isDisposed = true;
        Optional.ofNullable(fetchPasswordsSubscription).ifPresent(Disposable::dispose);
    }

    @Override
    public boolean isDisposed() {
        return isDisposed;
    }
}
