package com.citparkingsystem.encapsulate;

/**
 * Created by Dave Tolentin on 8/13/2017.
 */

public class Menu {
        private String name;
        private int slots;

    public int getSlots() {
        return slots;
    }

    public void setSlots(int slots) {
        this.slots = slots;
    }

    private int thumbnail;

        public Menu() {
        }

        public Menu(String name, int slots, int thumbnail) {
            this.name = name;
            this.slots = slots;
            this.thumbnail = thumbnail;
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
