import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchWorker extends SwingWorker<Map<Integer, SearchResult>, SearchResult> {

    private final JTextArea textArea;
    private final String pattern;
    private final SearchResultList resultList;
    private final boolean useRegex;
    private volatile boolean isValid;

    SearchWorker(JTextArea textArea, SearchResultList resultList, String pattern, boolean useRegex) {
        this.textArea = textArea;
        this.resultList = resultList;
        this.pattern = pattern;
        this.useRegex = useRegex;
        this.isValid = true;
    }

    @Override
    protected Map<Integer, SearchResult> doInBackground() {
        while (true) {
            isValid = true;
            Map<Integer, SearchResult> result = fetchTextAndSearch();
            if (isValid) {
                return result;
            } else {
                resultList.clear();
            }
        }
    }

    @Override
    protected void done() {
        try {
            if (isValid) {
                if (get().size() > 0) {
                    textArea.setCaretPosition(get().get(0).end);
                    textArea.select(get().get(0).start, get().get(0).end);
                    textArea.grabFocus();
                }
                resultList.update(get());
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    protected void restart() {
        isValid = false;
    }

    private Map<Integer, SearchResult> fetchTextAndSearch() {
        Map<Integer, SearchResult> result;
        if (useRegex) {
            result = regexSearch(textArea.getText(), pattern);
        } else {
            result = plainSearch(textArea.getText(), pattern);
        }
        return result;
    }

    private Map<Integer, SearchResult> regexSearch(String text, String pattern) {
        HashMap<Integer, SearchResult> resultMap = new HashMap<>();
        Pattern regexPattern = Pattern.compile(pattern);
        Matcher matcher = regexPattern.matcher(text);
        int count = 0;
        while (matcher.find() && isValid) {
            SearchResult result = new SearchResult(count, matcher.start(), matcher.end());
            resultMap.put(count, result);
            count++;
        }
        return resultMap;
    }

    private Map<Integer, SearchResult> plainSearch(String text, String pattern) {
        HashMap<Integer, SearchResult> resultMap = new HashMap<>();
        int count = 0;
        for (int k = 0; isValid && k < text.length() - pattern.length() + 1; k++) {
            for (int i = 0; i < pattern.length(); i++) {
                if (text.charAt(k + i) != pattern.charAt(i)) {
                    break;
                }
                if (i == pattern.length() - 1) {
                    SearchResult result = new SearchResult(count, k, k + i + 1);
                    resultMap.put(count, result);
                    count++;
                }
            }
        }
        return resultMap;
    }
}
