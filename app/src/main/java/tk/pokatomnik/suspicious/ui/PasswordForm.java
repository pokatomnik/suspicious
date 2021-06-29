package tk.pokatomnik.suspicious.ui;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import tk.pokatomnik.suspicious.CustomFragments.DomainCaptureFragment;
import tk.pokatomnik.suspicious.Entities.Password;
import tk.pokatomnik.suspicious.R;
import tk.pokatomnik.suspicious.Utils.PasswordGenerator;
import tk.pokatomnik.suspicious.Utils.TextViewOnChangeListener;
import tk.pokatomnik.suspicious.Utils.ToastError;
import tk.pokatomnik.suspicious.databinding.FragmentPasswordFormBinding;

public abstract class PasswordForm extends DomainCaptureFragment {
    private final Iterator<String> passwordGenerator = new PasswordGenerator(true, true, 12).iterator();

    private FragmentPasswordFormBinding binding;

    @Nullable
    private Disposable saveSubscription;

    @CallSuper
    public View onCreateView(
        @NonNull LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    ) {
        binding = FragmentPasswordFormBinding.inflate(inflater, container, false);
        binding.passwordFormTitle.setText(getTitle());
        setupListeners();
        return binding.getRoot();
    }

    protected abstract String getTitle();

    private void setupListeners() {
        binding.editDomain.addTextChangedListener(new TextViewOnChangeListener((view) -> {
            binding.editDomain.setError(null);
        }));

        binding.editUserName.addTextChangedListener(new TextViewOnChangeListener((view) -> {
            binding.editUserName.setError(null);
        }));

        binding.editPassword.addTextChangedListener(new TextViewOnChangeListener((view) -> {
            binding.editPassword.setError(null);
        }));

        binding.passwordHidden.setOnCheckedChangeListener((button, isChecked) -> {
            final int passwordInputType = isChecked
                ? InputType.TYPE_TEXT_VARIATION_PASSWORD
                : InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
            binding.editPassword.setInputType(InputType.TYPE_CLASS_TEXT | passwordInputType);
        });

        binding.generatePassword.setOnClickListener((view) -> {
            binding.editPassword.setText(passwordGenerator.next());
        });

        binding.save.setOnClickListener(this::onClickSave);
        binding.newPasswordCapture.setOnClickListener((View view) -> capture());
    }

    private boolean applyValidation() {
        boolean result = true;

        if (binding.editDomain.getText().toString().equals("")) {
            result = false;
            binding.editDomain.setError("Please specify domain");
        }

        if (binding.editUserName.getText().toString().equals("")) {
            result = false;
            binding.editUserName.setError("Please specify user name");
        }

        if (binding.editPassword.getText().toString().equals("")) {
            result = false;
            binding.editPassword.setError("Please specify password");
        }

        return result;
    }

    private void onClickSave(View view) {
        final boolean isValid = applyValidation();
        if (!isValid) {
            return;
        }

        final Password password = new Password(
            binding.editDomain.getText().toString(),
            binding.editUserName.getText().toString(),
            binding.editPassword.getText().toString(),
            binding.editComment.getText().toString()
        );

        saveSubscription = postPassword(password)
            .subscribeOn(Schedulers.newThread())
            .subscribe(() -> {
                Optional.ofNullable(getActivity()).ifPresent((activity) -> {
                    activity.runOnUiThread(this::navigateHome);
                });
            }, new ToastError<>(getActivity(), "Can't save password because of error"));
    }

    protected abstract Completable postPassword(Password password);

    protected void onGetPassword(Password password) {
        binding.editDomain.setText(password.getDomain());
        binding.editUserName.setText(password.getUserName());
        binding.editPassword.setText(password.getPassword());
        binding.editComment.setText(password.getComment());
    }

    private void navigateHome() {
        Optional.ofNullable(getActivity()).ifPresent((activity) -> {
            Toast.makeText(activity.getApplicationContext(), "Saved!", Toast.LENGTH_LONG).show();
            Navigation
                .findNavController(activity, R.id.nav_host_fragment_content_main)
                .navigate(R.id.nav_home);
        });
    }

    @Override
    protected void onCapture(String result) {
        binding.editDomain.setText(result);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        Optional.ofNullable(saveSubscription).ifPresent(Disposable::dispose);
    }
}