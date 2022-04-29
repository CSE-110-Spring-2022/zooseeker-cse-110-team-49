package com.example.cse110_team49;

import java.util.ArrayList;

public class Exhibit {
    private String id;
    private String itemType;
    private ArrayList<String> tags;

    Exhibit(String id, String itemType, ArrayList<String> tags) {
        this.id = id;
        this.itemType = itemType;
        this.tags = tags;
    }

    public String getItemType() {
        return itemType;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

}
