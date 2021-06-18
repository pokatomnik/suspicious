package tk.pokatomnik.suspicious.ui.home;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import tk.pokatomnik.suspicious.Entities.Password;

public class PasswordsRecyclerViewManager {
    private final PasswordsAdapter adapter;

    private final List<Password> passwords;

    private final PublishSubject<Password> clickSubject = PublishSubject.create();

    public PasswordsRecyclerViewManager(
            RecyclerView initialRecyclerView,
            Context context
    ) {
        passwords = new ArrayList<>();
        adapter = new PasswordsAdapter(passwords, this::onPasswordClick);

        initialRecyclerView.setHasFixedSize(true);
        initialRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        initialRecyclerView.setAdapter(adapter);
    }

    private void onPasswordClick(Password password) {
        clickSubject.onNext(password);
    }

    public void updatePasswords(List<Password> newPasswords) {
        passwords.clear();
        passwords.addAll(newPasswords);
        adapter.notifyDataSetChanged();
    }

    public Observable<Password> getClickSubject() {
        return clickSubject.hide();
    }
}
