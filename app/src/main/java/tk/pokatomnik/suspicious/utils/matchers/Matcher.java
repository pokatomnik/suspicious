package tk.pokatomnik.suspicious.utils.matchers;

import java.util.List;

public interface Matcher<T> {
    List<T> getResults(String searchString, List<T> source);
}
