package tk.pokatomnik.suspicious.services.systeminfo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class SystemInfo {
    private final SystemInfoDependencies deps;

    public SystemInfo(SystemInfoDependencies initialDeps) {
        deps = initialDeps;
    }

    public boolean isWiFiConnected() {
        final Context context = deps.getContext();
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
    }
}
