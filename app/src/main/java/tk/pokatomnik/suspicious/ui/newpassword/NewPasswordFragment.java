package tk.pokatomnik.suspicious.ui.newpassword;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import java.util.Optional;

import io.reactivex.rxjava3.core.Completable;
import tk.pokatomnik.suspicious.entities.Password;
import tk.pokatomnik.suspicious.SuspiciousApplication;
import tk.pokatomnik.suspicious.ui.PasswordForm;

public class NewPasswordFragment extends PasswordForm {
    @Override
    public View onCreateView(
        @NonNull LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    ) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected String getTitle() {
        return "New Password";
    }

    @Override
    protected Completable postPassword(Password password) {
        return Optional.ofNullable(getActivity()).map((activity) -> {
            final SuspiciousApplication application = (SuspiciousApplication) activity.getApplication();
            return application.getPasswordDatabaseService().insert(password);
        }).orElse(Completable.never());
    }
}