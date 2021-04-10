package com.timeline;

import java.util.ArrayList;
import java.util.HashSet;
import me.xdrop.fuzzywuzzy.*;

/**
 * Search class.
 * @author Susanna Persson
 * @author Elias Frigård
 */
public class Search {
    protected int searchRatio = 70;

    /**
     * Constructor
     */
    public Search () {}

    /**
     * Get search ratio value.
     * @return an integer with the current Search ratio.
     */
    public int getSearchRatio() {
        return searchRatio;
    }

    /**
     * Set the search ratio value (search accuracy).
     * @param searchRatio int
     */
    public void setSearchRatio(int searchRatio) {
        this.searchRatio = searchRatio;
    }

    /**
     * Searches the Timeline table in database for timelines with the specified name OR keyword.
     * @return  An ArrayList with Timeline objects.
     * @author Susanna Persson sp222xw
     * @author Elias Frigård ef222xf
     */
    public ArrayList<Timeline> searchNameAndKeywords(String searchString) {
        // Convert search string to ignore case.
        searchString = searchString.toLowerCase();

        // Get the list of global timelines.
        ArrayList<Timeline> timeLineList = new ArrayList<>(AppProperties.getAllTimelines());

        // Create a HashSet of timelines to avoid duplicates.
        HashSet<Timeline> resultList = new HashSet<>();

        // Loop through all global timelines and add matches to the
        for (Timeline tl : timeLineList) {
            // Add all timelines that contains the exact match of the string.
            if (tl.getName().contains(searchString)) {
                resultList.add(tl);
            }

            int ratio = FuzzySearch.weightedRatio(searchString, tl.getName().toLowerCase());

            // Add all timelines with matching names.
            if (ratio > searchRatio) {
                resultList.add(tl);
            }

            // Add all timelines with matching keywords.
            for (String keyword : tl.getKeywords()) {
                ratio = FuzzySearch.weightedRatio(searchString, keyword.toLowerCase());
                if (ratio > searchRatio) {
                    resultList.add(tl);
                }
            }
        }

        // Convert HashSet to ArrayList.
        ArrayList<Timeline> resultListAsArray = new ArrayList<>(resultList);

        // Sort and return ArrayList.
        resultListAsArray.sort(null);
        return resultListAsArray;
    }
}