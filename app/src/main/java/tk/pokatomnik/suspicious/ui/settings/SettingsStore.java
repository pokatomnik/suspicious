package tk.pokatomnik.suspicious.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

public class SettingsStore {
    private final static String KEY_USE_DIGITS = "preferences:useDigits";

    private final static String KEY_USE_SYMBOLS = "preferences:useSymbols";

    private final static String KEY_SMART_SEARCH = "preferences:useSmartSearch";

    private final static String KEY_PASSWORD_LENGTH = "preferences:passwordLength";

    private final static String KEY_MASTER_PASSWORD_HASH = "preferences:masterPassword";

    private final SharedPreferences sharedPreferences;

    public SettingsStore(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean isUseDigits() {
        return sharedPreferences.getBoolean(KEY_USE_DIGITS, false);
    }

    public void setIsUseDigits(boolean useDigits) {
        sharedPreferences.edit().putBoolean(KEY_USE_DIGITS, useDigits).apply();
    }

    public boolean isUseSymbols() {
        return sharedPreferences.getBoolean(KEY_USE_SYMBOLS, false);
    }

    public void setIsUseSymbols(boolean isUseSymbols) {
        sharedPreferences.edit().putBoolean(KEY_USE_SYMBOLS, isUseSymbols).apply();
    }

    public boolean isUseSmartSearch() {
        return sharedPreferences.getBoolean(KEY_SMART_SEARCH, false);
    }

    public void setIsUseSmartSearch(boolean useSmartSearch) {
        sharedPreferences.edit().putBoolean(KEY_SMART_SEARCH, useSmartSearch).apply();
    }

    public int passwordLength() {
        return sharedPreferences.getInt(KEY_PASSWORD_LENGTH, 12);
    }

    public void setPasswordLength(int passwordLength) {
        sharedPreferences.edit().putInt(KEY_PASSWORD_LENGTH, passwordLength).apply();
    }

    @Nullable
    public String getMasterPasswordHash() {
        return sharedPreferences.getString(KEY_MASTER_PASSWORD_HASH, null);
    }

    public void setMasterPasswordHash(String masterPasswordHash) {
        sharedPreferences.edit().putString(KEY_MASTER_PASSWORD_HASH, masterPasswordHash).apply();
    }
}
