package tk.pokatomnik.suspicious.customfragments;

import java.net.URI;

public abstract class DomainCaptureFragment extends CaptureFragment<String> {
    @Override
    protected String map(String input) throws Exception {
        return new URI(input).getHost();
    }
}
