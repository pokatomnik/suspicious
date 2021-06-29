package tk.pokatomnik.suspicious.ui.home;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import tk.pokatomnik.suspicious.Entities.Password;
import tk.pokatomnik.suspicious.Storage.PersistentStorage;
import tk.pokatomnik.suspicious.Utils.ToastError;

public class PasswordsExtractor implements Disposable {
    private final Context context;

    private final FragmentActivity fragmentActivity;

    private final BehaviorSubject<List<Password>> publishSubject = BehaviorSubject.createDefault(new ArrayList<>());

    @Nullable
    private Disposable fetchPasswordsSubscription;

    private boolean isDisposed = false;

    public PasswordsExtractor(
            Context initialContext,
            @Nullable FragmentActivity initialFragmentActivity
    ) {
        context = initialContext;
        fragmentActivity = initialFragmentActivity;
    }

    public void extract() {
        fetchPasswordsSubscription = PersistentStorage
                .getInstance(context)
                .getPasswordDatabase()
                .passwordDAO()
                .getAll()
                .subscribeOn(Schedulers.newThread())
                .subscribe((result) -> {
                    Optional.ofNullable(fragmentActivity).ifPresent((activity) -> {
                        activity.runOnUiThread(() -> {
                            publishSubject.onNext(result);
                        });
                    });
                }, new ToastError<>(fragmentActivity, "Error getting passwords"));
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
