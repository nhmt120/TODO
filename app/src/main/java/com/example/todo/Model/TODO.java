package com.example.todo.Model;

public class TODO {
    private String id, title, note;

    public TODO() {
    }

    public String getId() {
        return id;
    }

    public TODO(String id, String title, String note) {
        this.id = id;
        this.title = title;
        this.note = note;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
