package tk.pokatomnik.suspicious.ui.newpassword;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import io.reactivex.rxjava3.core.Completable;
import tk.pokatomnik.suspicious.Entities.Password;
import tk.pokatomnik.suspicious.Storage.PersistentStorage;
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
        return PersistentStorage
            .getInstance(getContext())
            .getPasswordDatabase()
            .passwordDAO()
            .insert(password);
    }
}