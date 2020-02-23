package com.example.anote.adapter.item.item;

import java.util.List;

import io.realm.RealmObject;

public class Memo extends RealmObject {
    private String id;
    private String title;
    private String imageContents;
    private String textContents;
    private String date;

    public Memo() {
        this.id = "";
        this.title = "";
        this.imageContents = "";
        this.textContents = "";
        this.date = "";
    }

    public Memo(String id, String title, String imageContents, String textContents, String date) {
        this.id = id;
        this.title = title;
        this.imageContents = imageContents;
        this.textContents = textContents;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImageContents() {
        return imageContents;
    }

    public void setImageContents(String imageContents) {
        this.imageContents = imageContents;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTextContents() {
        return textContents;
    }

    public void setTextContents(String textContents) {
        this.textContents = textContents;
    }
}
