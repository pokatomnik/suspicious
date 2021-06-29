package tk.pokatomnik.suspicious.ui.editpassword;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;

import java.util.Optional;
import java.util.function.Consumer;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import tk.pokatomnik.suspicious.Entities.Password;
import tk.pokatomnik.suspicious.R;
import tk.pokatomnik.suspicious.Storage.PersistentStorage;
import tk.pokatomnik.suspicious.Utils.ToastError;
import tk.pokatomnik.suspicious.ui.PasswordForm;

public class EditPasswordFragment extends PasswordForm {
    public final static String PASSWORD_ID_KEY = "PASSWORD_ID";

    private Disposable loadSubscription;

    private void ifHasID(Consumer<Integer> consumer) {
        Optional.ofNullable(getArguments()).ifPresent((bundle) -> {
            final int passwordID = bundle.getInt(PASSWORD_ID_KEY);
            consumer.accept(passwordID);
        });
    }

    public View onCreateView(
        @NonNull LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    ) {
        final View root = super.onCreateView(inflater, container, savedInstanceState);
        ifHasID((passwordID) -> {
            loadSubscription = PersistentStorage
                .getInstance(getContext())
                .getPasswordDatabase()
                .passwordDAO()
                .getByUID(passwordID)
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::handlePassword, this::handleError);
        });

        return root;
    }

    @Override
    protected String getTitle() {
        return "Edit Password";
    }

    private void handlePassword(Password password) {
        Optional.ofNullable(getActivity()).ifPresent((activity) -> {
            activity.runOnUiThread(() -> {
                onGetPassword(password);
            });
        });
    }

    private void handleError(Throwable error) {
        final ToastError<Throwable> toastError =
            new ToastError<>(getActivity(), "Failed to fetch password");
        toastError.accept(error);

        Optional.ofNullable(getActivity()).ifPresent((activity) -> {
            Navigation
                .findNavController(getActivity(), R.id.nav_host_fragment_content_main)
                .navigate(R.id.nav_home);
        });
    }

    @Override
    protected Completable postPassword(Password password) {
        final Bundle bundle = getArguments();
        if (bundle == null) {
            return Observable.error(new Throwable("Can't update password")).ignoreElements().hide();
        }
        final int passwordID = bundle.getInt(PASSWORD_ID_KEY);
        password.setUid(passwordID);

        return PersistentStorage
            .getInstance(getContext())
            .getPasswordDatabase()
            .passwordDAO()
            .update(password);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Optional.ofNullable(loadSubscription).ifPresent(Disposable::dispose);
    }
}