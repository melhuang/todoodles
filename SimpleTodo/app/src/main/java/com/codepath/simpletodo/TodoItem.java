package com.codepath.simpletodo;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by melissahuang on 6/26/16.
 */

public class TodoItem implements Serializable {
    String title;
    Calendar dueDate;
    Priority priority;
    boolean completed;

    TodoItem(String title, Calendar dueDate, Priority priority, boolean completed) {
        this.title = title;
        this.dueDate = dueDate;
        this.priority = priority;
        this.completed = completed;
    }
}
