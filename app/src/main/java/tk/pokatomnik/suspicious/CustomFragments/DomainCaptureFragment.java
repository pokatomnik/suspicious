package tk.pokatomnik.suspicious.CustomFragments;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

public abstract class DomainCaptureFragment extends CaptureFragment<String> {
    // TODO add more if needed
    private static final List<String> excludeSubdomains = Arrays.asList("www", "m");

    @Override
    protected String map(String input) throws Exception {
        final String host = new URI(input).getHost();
        final String[] hostPartsAsList = host.split("\\.");

        if (hostPartsAsList.length <= 1) {
            return host;
        }

        if (excludeSubdomains.contains(hostPartsAsList[0])) {
            return host.replace(hostPartsAsList[0], "");
        }

        return host;
    }
}
