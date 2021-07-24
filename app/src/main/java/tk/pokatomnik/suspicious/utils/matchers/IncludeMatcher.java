package tk.pokatomnik.suspicious.utils.matchers;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class IncludeMatcher<T> implements Matcher<T> {
    private final BiFunction<String, T, Integer> computeScore;

    public IncludeMatcher(BiFunction<String, T, Integer> initialComputeScore) {
        computeScore = initialComputeScore;
    }

    @Override
    public List<T> getResults(String searchString, List<T> source) {
        if (searchString.equals("")) {
            return source;
        }

        return source.stream().filter((item) -> {
            return computeScore.apply(searchString, item) > 0;
        }).collect(Collectors.toList());
    }
}
