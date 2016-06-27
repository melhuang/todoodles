package com.codepath.simpletodo;

import java.util.Date;

/**
 * Created by melissahuang on 6/26/16.
 */

public class TodoItem {
    String title;
    Date dueDate;
    Priority priority;
    boolean completed;

    TodoItem(String title, Date dueDate, Priority priority, boolean completed) {
        this.title = title;
        this.dueDate = dueDate;
        this.priority = priority;
        this.completed = completed;
    }
}
