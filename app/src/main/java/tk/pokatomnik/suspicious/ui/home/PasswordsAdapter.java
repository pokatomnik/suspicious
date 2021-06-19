package tk.pokatomnik.suspicious.ui.home;

import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.function.Consumer;

import tk.pokatomnik.suspicious.Entities.Password;
import tk.pokatomnik.suspicious.GenericRecycleViewAdapter;
import tk.pokatomnik.suspicious.R;

public class PasswordsAdapter extends GenericRecycleViewAdapter<Password> {
    public PasswordsAdapter(List<Password> passwordsData, Consumer<Password> onPasswordClick) {
        super(passwordsData, onPasswordClick);
    }

    @Override
    protected void fillLayout(Password item, LinearLayout linearLayout) {
        final TextView domain = linearLayout.findViewById(R.id.domain);
        final TextView userName = linearLayout.findViewById(R.id.user_name);

        domain.setText(item.getDomain());
        userName.setText(item.getUserName());
    }
}
