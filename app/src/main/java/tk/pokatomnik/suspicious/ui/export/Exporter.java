package tk.pokatomnik.suspicious.ui.export;

import java.util.List;

public interface Exporter<T> {
    String export(List<T> input);
}
