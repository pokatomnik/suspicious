package tk.pokatomnik.suspicious.utils.matchers;

import org.apache.commons.text.similarity.FuzzyScore;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class FuzzyMatcher<T> implements Matcher<T> {
    protected final FuzzyScore fuzzyScore = new FuzzyScore(Locale.US);

    private final TriFunction<String, T, FuzzyScore, Integer> computeScore;

    public FuzzyMatcher(TriFunction<String, T, FuzzyScore, Integer> initialComputeScore) {
        computeScore = initialComputeScore;
    }

    @Override
    public List<T> getResults(String searchString, List<T> source) {
        if (searchString.equals("")) {
            return source;
        }

        return source.stream().sorted((a, b) -> {
            final int scoreA = computeScore.apply(searchString, a, fuzzyScore);
            final int scoreB = computeScore.apply(searchString, b, fuzzyScore);
            return scoreB - scoreA;
        }).collect(Collectors.toList());
    }
}
