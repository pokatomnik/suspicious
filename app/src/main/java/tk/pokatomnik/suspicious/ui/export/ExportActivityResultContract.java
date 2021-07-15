package tk.pokatomnik.suspicious.ui.export;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Optional;

public class ExportActivityResultContract extends ActivityResultContract<String, Uri> {
    private final String mime;

    public ExportActivityResultContract(String initialMime) {
        mime = initialMime;
    }

    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, String fileName) {
        final Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_CREATE_DOCUMENT);
        shareIntent.setType(mime);
        shareIntent.putExtra(Intent.EXTRA_TITLE, fileName);
        return shareIntent;
    }

    @Nullable
    @Override
    public Uri parseResult(int resultCode, @Nullable Intent intent) {
        if (resultCode == Activity.RESULT_OK) {
            return Optional.ofNullable(intent).map(Intent::getData).orElse(null);
        }
        return null;
    }
}
