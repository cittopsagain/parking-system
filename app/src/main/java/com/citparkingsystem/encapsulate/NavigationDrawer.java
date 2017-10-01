package com.citparkingsystem.encapsulate;

/**
 * Created by Walter Ybanez on 7/24/2017.
 */

public class NavigationDrawer {

    private String title;

    public NavigationDrawer() {

    }

    public NavigationDrawer(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
