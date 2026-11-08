package com.philosophizer.demeter.SearchTermNormalizer;

import com.philosophizer.demeter.utils.SearchTermNormalizer;
import org.junit.jupiter.api.Test;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class SearchTermNormalizerTest {


    @Test
    public void searchTermFormation() throws Exception {
        SearchTermNormalizer stn = new SearchTermNormalizer();
        String[] items = {" 2 packs  chicken breast ", "raspberries", "", " ", "turkey Breast"};
        int counter = 0;
        HashMap<Integer, String> resultsMap = new HashMap<>();

        resultsMap.put(0, "chicken breast");
        resultsMap.put(1, "raspberries");
        resultsMap.put(2, "");
        resultsMap.put(3, "");
        resultsMap.put(4, "turkey breast");
        for(String s : items) {
            assertEquals(resultsMap.get(counter), stn.standardize(items[counter]));
            counter++;
        }




    }
    @Test
    public void cacheTokenFormation() throws Exception {
        SearchTermNormalizer stn = new SearchTermNormalizer();
        String[] items = {" 2 packs  chicken breast ", "raspberries", "", " ", "turkey Breast"};
        int counter = 0;
        HashMap<Integer, String> resultsMap = new HashMap<>();

        resultsMap.put(0, "v1#breast#chicken");
        resultsMap.put(1, "v1#raspberri");
        resultsMap.put(2, "v1#");
        resultsMap.put(3, "v1#");
        resultsMap.put(4, "v1#breast#turkei");
        for(String s : items) {
            assertEquals(resultsMap.get(counter), stn.tokenize(items[counter]));
            counter++;
        }




    }
}
