package tk.pokatomnik.suspicious.services.settings;

import android.content.SharedPreferences;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import java.util.Optional;

public class Settings {
    private final static String KEY_USE_DIGITS = "preferences:useDigits";

    private final static String KEY_USE_SYMBOLS = "preferences:useSymbols";

    private final static String KEY_SMART_SEARCH = "preferences:useSmartSearch";

    private final static String KEY_PASSWORD_LENGTH = "preferences:passwordLength";

    private final static String KEY_MASTER_PASSWORD_HASH = "preferences:masterPassword";

    private final SettingsDependencies deps;

    private SharedPreferences prefs;

    public Settings(SettingsDependencies initialDeps) {
        deps = initialDeps;
    }

    private SharedPreferences getSharedPreferences() {
        if (prefs != null) {
            return prefs;
        }

        prefs = Optional
            .ofNullable(deps.getContext())
            .map(PreferenceManager::getDefaultSharedPreferences)
            .orElseThrow(() -> new RuntimeException("Context isn't ready"));

        return prefs;
    }

    public boolean isUseDigits() {
        return getSharedPreferences().getBoolean(KEY_USE_DIGITS, false);
    }

    public void setIsUseDigits(boolean useDigits) {
        getSharedPreferences().edit().putBoolean(KEY_USE_DIGITS, useDigits).apply();
    }

    public boolean isUseSymbols() {
        return getSharedPreferences().getBoolean(KEY_USE_SYMBOLS, false);
    }

    public void setIsUseSymbols(boolean isUseSymbols) {
        getSharedPreferences().edit().putBoolean(KEY_USE_SYMBOLS, isUseSymbols).apply();
    }

    public boolean isUseSmartSearch() {
        return getSharedPreferences().getBoolean(KEY_SMART_SEARCH, false);
    }

    public void setIsUseSmartSearch(boolean useSmartSearch) {
        getSharedPreferences().edit().putBoolean(KEY_SMART_SEARCH, useSmartSearch).apply();
    }

    public int passwordLength() {
        return getSharedPreferences().getInt(KEY_PASSWORD_LENGTH, 12);
    }

    public void setPasswordLength(int passwordLength) {
        getSharedPreferences().edit().putInt(KEY_PASSWORD_LENGTH, passwordLength).apply();
    }

    @Nullable
    public String getMasterPasswordHash() {
        return getSharedPreferences().getString(KEY_MASTER_PASSWORD_HASH, null);
    }

    public void setMasterPasswordHash(String masterPasswordHash) {
        getSharedPreferences().edit().putString(KEY_MASTER_PASSWORD_HASH, masterPasswordHash).apply();
    }
}
