import java.util.HashMap;
import java.util.Map;

public class SearchResultList {
    private Map<Integer, SearchResult> searchResultMap;
    private int currentSearchPosition;
    private int numberOfResults;

    SearchResultList() {
        searchResultMap = new HashMap<>();
        currentSearchPosition = 0;
        numberOfResults = 0;
    }

    protected void clear() {
        searchResultMap = new HashMap<>();
        currentSearchPosition = 0;
    }

    protected synchronized void update(Map<Integer, SearchResult> newResult) {
        searchResultMap = newResult;
        currentSearchPosition = 0;
        numberOfResults = newResult.size();
    }

    protected SearchResult next() {
        if (++currentSearchPosition == numberOfResults) {
            currentSearchPosition = 0;
        }
        return searchResultMap.get(currentSearchPosition);
    }

    protected SearchResult previous() {
        if (--currentSearchPosition < 0) {
            currentSearchPosition = numberOfResults - 1;
        }
        return searchResultMap.get(currentSearchPosition);
    }
}
