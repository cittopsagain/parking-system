package com.citparkingsystem.lib;

/**
 * Created by Walter Ybanez on 8/9/2017.
 */

public class StringHelper {

    // It is like implode in PHP
    public static String implode(String separator, String... data) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length - 1; i++) {
            // data.length - 1 => to not add separator at the end
            if (!data[i].matches(" *")) { // empty string are ""; " "; "  "; and so on
                sb.append(data[i]);
                sb.append(separator);
            }
        }
        sb.append(data[data.length - 1].trim());
        return sb.toString();
    }

    // It is like uc_first in PHP
    public static String toTheUpperCaseSingle(String givenString) {
        String example = givenString;

        example = example.substring(0, 1).toUpperCase() + example.substring(1, example.length());

        return example;
    }

    public static String toTheUpperCase(String givenString) {
        String[] arr = givenString.split(" ");
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < arr.length; i++) {
            sb.append(Character.toUpperCase(arr[i].charAt(0)))
                    .append(arr[i].substring(1)).append(" ");
        }
        return sb.toString().trim();
    }
}
