package com.example.candogan.todoexercise;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddTodoActivity extends AppCompatActivity {

    Button saveButton;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo);
        initView();
    }

    private void initView() {
        saveButton = findViewById(R.id.btn_add_save);
        editText = findViewById(R.id.et_add_todo);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finihActivityForResult(editText.getText().toString().trim());
            }
        });
    }

    void finihActivityForResult(String data) {
        Intent intent = new Intent();
        intent.putExtra(Constants.IntentData.AddTodoData, data);
        setResult(Constants.IntentRequestCode.REQUEST_ADD_TODO, intent);
        finish();
    }
}
