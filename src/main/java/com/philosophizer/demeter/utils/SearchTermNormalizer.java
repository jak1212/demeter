package com.philosophizer.demeter.utils;

import jakarta.annotation.PreDestroy;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Component
public class SearchTermNormalizer {
    private static final String version = "v1";
    private static final Pattern QTY = Pattern.compile("\\d+(\\.\\d+)?(packs|bags|packages|boxes|containers|jars|tubs|oz|lb|lbs|g|kg|ct|ml|l|gal|box|pack|package|tub|jar|can|p|gallon|quart||pint|ml)?");
    public static EnglishAnalyzer englishAnalyzer;
    public static StandardAnalyzer krogerApiAnalyzer;


    public SearchTermNormalizer() {
        CharArraySet stop = new CharArraySet(EnglishAnalyzer.getDefaultStopSet(), true);
        stop.addAll(List.of("tbsp", "tsp", "tablespoon", "cup", "lb", "packs", "bag", "set", "g", "bottle", "box", "liter", "can", "package", "oz", "ounce", "pound", "jar", "gallon", "gal", "pint", "quart", "tub", "jug", "container", "bunch", "kilogram", "kg", "pack", "cups"));
        EnglishAnalyzer englishAnalyzer = new EnglishAnalyzer(stop);
        StandardAnalyzer standardAnalyzer = new StandardAnalyzer(stop);
        this.englishAnalyzer = englishAnalyzer;
        this.krogerApiAnalyzer = standardAnalyzer;
    }

private static List<String> analyze(Analyzer analyzer, String input) {
    List<String> tokens = new ArrayList<>();
    try (TokenStream ts = analyzer.tokenStream("q", input)) {
        CharTermAttribute term = ts.addAttribute(CharTermAttribute.class);
        ts.reset();
        while (ts.incrementToken()) {
            tokens.add(term.toString());
        }
        ts.end();
    } catch (IOException e) {
        throw new UncheckedIOException(e);
    }
    tokens.removeIf(t -> QTY.matcher(t).matches());
    return tokens;
}

    /** Cache key: stemmed, deduped, sorted. */
    public String tokenize(String input) {
        List<String> sorted = analyze(englishAnalyzer, input).stream()
                .filter(s -> !s.isEmpty())
                .distinct()          // optional: "eggs, large eggs" -> one "egg"
                .sorted()
                .toList();
        return version + "#" + String.join("#", sorted);
    }

    public Set<String> stemmedTokens(String input) {
        return new HashSet<>(analyze(englishAnalyzer, input));
    }

    /** Query string for Kroger: unstemmed, original order. */
    public String standardize(String input) {
        return String.join(" ", analyze(krogerApiAnalyzer, input));
    }

    @PreDestroy
    public void shutdown() {
        englishAnalyzer.close();
        krogerApiAnalyzer.close();
    }
}
