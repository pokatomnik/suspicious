package tk.pokatomnik.suspicious.ui.home;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import tk.pokatomnik.suspicious.Entities.Password;
import tk.pokatomnik.suspicious.Storage.PersistentStorage;

public class PasswordsExtractor {
    private final Context context;

    private final FragmentActivity fragmentActivity;

    public PasswordsExtractor(
            Context initialContext,
            @Nullable FragmentActivity initialFragmentActivity
    ) {
        context = initialContext;
        fragmentActivity = initialFragmentActivity;
    }

    public Disposable extract(Consumer<List<Password>> consumer) {
        return PersistentStorage
                .getInstance(context)
                .getPasswordDatabase()
                .passwordDAO()
                .getAll()
                .subscribeOn(Schedulers.newThread())
                .subscribe((result) -> {
                    Optional.ofNullable(fragmentActivity).ifPresent((activity) -> {
                        activity.runOnUiThread(() -> {
                            consumer.accept(result);
                        });
                    });
                });
    }
}
