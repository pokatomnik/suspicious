package tk.pokatomnik.suspicious.Utils.Matchers;

import java.util.List;

public interface Matcher<T> {
    List<T> getResults(String searchString, List<T> source);
}
