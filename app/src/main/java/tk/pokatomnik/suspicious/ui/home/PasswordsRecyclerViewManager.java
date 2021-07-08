package tk.pokatomnik.suspicious.ui.home;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.PublishSubject;
import tk.pokatomnik.suspicious.Entities.Password;

public class PasswordsRecyclerViewManager implements Disposable {
    private final PasswordsAdapter adapter;

    private final BehaviorSubject<List<Password>> passwordsSubject;

    private final Disposable passwordsSubscription;

    private final PublishSubject<Password> clickSubject = PublishSubject.create();

    private final PublishSubject<Password> longClickSubject = PublishSubject.create();

    private final PublishSubject<Password> clickRemoveSubject = PublishSubject.create();

    private boolean isDisposed = false;

    public PasswordsRecyclerViewManager(
            RecyclerView initialRecyclerView,
            Context context
    ) {
        final List<Password> data = new ArrayList<>();
        passwordsSubject = BehaviorSubject.createDefault(data);
        adapter = new PasswordsAdapter(
            data,
            this::onPasswordClick,
            this::onLongPasswordClick,
            this::onPasswordRemoveClick
        );

        passwordsSubscription = passwordsSubject.subscribe((newPasswordsRaw) -> {
            final List<Password> newPasswords = new ArrayList<>(newPasswordsRaw);
            data.clear();
            data.addAll(newPasswords);
            adapter.notifyDataSetChanged();
        });

        initialRecyclerView.setHasFixedSize(true);
        initialRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        initialRecyclerView.setAdapter(adapter);
    }

    private void onPasswordClick(Password password) {
        clickSubject.onNext(password);
    }

    private void onLongPasswordClick(Password password) {
        longClickSubject.onNext(password);
    }

    private void onPasswordRemoveClick(Password password) {
        clickRemoveSubject.onNext(password);
    }

    public void updatePasswords(List<Password> newPasswords) {
        passwordsSubject.onNext(newPasswords);
    }

    public Observable<Password> getClickSubject() {
        return clickSubject.hide();
    }

    public Observable<Password> getLongClickSubject() {
        return longClickSubject.hide();
    }

    public Observable<Password> getClickRemoveSubject() {
        return clickRemoveSubject.hide();
    }

    @Override
    public void dispose() {
        isDisposed = true;
        passwordsSubscription.dispose();
    }

    @Override
    public boolean isDisposed() {
        return isDisposed;
    }
}
