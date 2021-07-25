package tk.pokatomnik.suspicious.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Optional;

import tk.pokatomnik.suspicious.SuspiciousApplication;
import tk.pokatomnik.suspicious.databinding.FragmentSettingsBinding;
import tk.pokatomnik.suspicious.services.settings.Settings;

public class SettingsFragment extends Fragment {
    private FragmentSettingsBinding binding;

    private Settings settingsStore;

    @Override
    public View onCreateView(
        @NonNull LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    ) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentSettingsBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(
        @NotNull View view,
        @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);
        settingsStore = Optional.ofNullable(getActivity()).map((activity) -> {
            final SuspiciousApplication application = (SuspiciousApplication) activity.getApplication();
            return application.getSettingsService();
        }).orElse(null);

        if (settingsStore != null) {
            binding.useDigits.setChecked(settingsStore.isUseDigits());
            binding.useSymbols.setChecked(settingsStore.isUseSymbols());
            binding.useSimpleSearch.setChecked(!settingsStore.isUseSmartSearch());
            binding.useFuzzySearch.setChecked(settingsStore.isUseSmartSearch());
            binding.passwordLength.setText(String.format(Locale.US, "%d", settingsStore.passwordLength()));
        }

        binding.useDigits.setOnCheckedChangeListener(this::handleUseDigitsChange);
        binding.useSymbols.setOnCheckedChangeListener(this::handleUseSymbolsChange);
        binding.useSimpleSearch.setOnCheckedChangeListener(this::handleUseSimpleSearchChange);
        binding.useFuzzySearch.setOnCheckedChangeListener(this::handleUseFuzzySearchChange);
        binding.passwordLength.setOnFocusChangeListener(this::handlePasswordLengthFocusChange);
    }

    private void handleUseDigitsChange(View v, boolean checked) {
        settingsStore.setIsUseDigits(checked);
    }

    private void handleUseSymbolsChange(View v, boolean checked) {
        settingsStore.setIsUseSymbols(checked);
    }

    private void handleUseSimpleSearchChange(View v, boolean checked) {
        if (checked) {
            settingsStore.setIsUseSmartSearch(false);
        }
    }

    private void handleUseFuzzySearchChange(View v, boolean checked) {
        if (checked) {
            settingsStore.setIsUseSmartSearch(true);
        }
    }

    private void handlePasswordLengthFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            final EditText passwordLengthInput = (EditText) v;
            final int passwordLength = Integer.parseInt(passwordLengthInput.getText().toString());
            settingsStore.setPasswordLength(passwordLength);
        }
    }
}
