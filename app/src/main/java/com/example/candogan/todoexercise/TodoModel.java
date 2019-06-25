package com.example.candogan.todoexercise;

import android.widget.CheckBox;
import android.widget.TextView;

public class TodoModel {

    //CheckBox checkBox;
    //TextView textView;

    Boolean check;
    String workDescription;

    public Boolean getCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }

    public String getWorkDescription() {
        return workDescription;
    }

    public void setWorkDescription(String workDescription) {
        this.workDescription = workDescription;
    }

    public TodoModel(Boolean check, String workDescription) {
        this.check = check;
        this.workDescription = workDescription;
    }

}
