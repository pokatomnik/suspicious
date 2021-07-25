package tk.pokatomnik.suspicious.services.systeminfo;

import android.content.Context;

import java.util.function.Supplier;

public class SystemInfoDependencies {
    private final Supplier<Context> getContext;

    public SystemInfoDependencies(Supplier<Context> initialGetContext) {
        getContext = initialGetContext;
    }

    public Context getContext() {
        return getContext.get();
    }
}
