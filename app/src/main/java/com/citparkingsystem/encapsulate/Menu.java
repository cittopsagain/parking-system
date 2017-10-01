package com.citparkingsystem.encapsulate;

/**
 * Created by Walter Ybanez on 8/13/2017.
 */

public class Menu {
    private String name;
    private int slots;
    private int maxSlots;

    public int getMaxSlots() {
        return maxSlots;
    }

    public void setMaxSlots(int maxSlots) {
        this.maxSlots = maxSlots;
    }

    public int getSlots() {
        return slots;
    }

    public void setSlots(int slots) {
        this.slots = slots;
    }

    private int thumbnail;

        public Menu() {
        }

        public Menu(String name, int slots, int thumbnail, int maxSlots) {
            this.name = name;
            this.slots = slots;
            this.thumbnail = thumbnail;
            this.maxSlots = maxSlots;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(int thumbnail) {
            this.thumbnail = thumbnail;
        }
}
