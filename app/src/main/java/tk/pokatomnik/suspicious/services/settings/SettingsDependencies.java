package tk.pokatomnik.suspicious.services.settings;

import android.content.Context;

import java.util.function.Supplier;

public class SettingsDependencies {
    private final Supplier<Context> getContext;

    public SettingsDependencies(Supplier<Context> initialGetContext) {
        getContext = initialGetContext;
    }

    public Context getContext() {
        return getContext.get();
    }
}
