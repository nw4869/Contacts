package com.nightwind.contacts.model;

import java.util.List;

/**
 * Created by nightwind on 15/5/20.
 */
public class Group {
    private long id;
    private String title;
    // contact id list
    private List<Long> members;

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

    public List<Long> getMembers() {
        return members;
    }

    public void setMembers(List<Long> members) {
        this.members = members;
    }
}
