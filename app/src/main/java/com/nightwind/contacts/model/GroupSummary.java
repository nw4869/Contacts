package com.nightwind.contacts.model;

/**
 * Created by nightwind on 15/5/20.
 */
public class GroupSummary {
    private long id;
    private String title;
    private int count;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public GroupSummary() {
    }

    public GroupSummary(long id, String title, int count) {
        this.id = id;
        this.title = title;
        this.count = count;
    }
}
