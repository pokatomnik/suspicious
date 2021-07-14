package tk.pokatomnik.suspicious.ui.export;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Optional;


public class ImportActivityResultContract extends ActivityResultContract<String[], Uri> {
    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, String[] mimes) {
        final Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("file/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimes);

        return intent;
    }

    @Override
    public Uri parseResult(int resultCode, @Nullable Intent intent) {
        if (resultCode == Activity.RESULT_OK) {
            return Optional.ofNullable(intent).map(Intent::getData).orElse(null);
        }
        return null;
    }
}
