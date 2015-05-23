package com.nightwind.contacts.model;

/**
 * Created by nightwind on 15/5/20.
 */
public class GroupSummary {
    private long id;//组的ID
    private String title;//组名
    private int count;//统计某个组的人数

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
