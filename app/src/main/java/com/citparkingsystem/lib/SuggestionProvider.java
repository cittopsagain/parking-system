package com.citparkingsystem.lib;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by Dave Tolentin on 7/28/2017.
 */

public class SuggestionProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "com.citparkingsystem.lib.SuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES | DATABASE_MODE_2LINES;
    public SuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
