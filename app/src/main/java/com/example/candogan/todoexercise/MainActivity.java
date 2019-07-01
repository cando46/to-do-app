package com.example.candogan.todoexercise;

import android.content.Intent;

import androidx.annotation.Nullable;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private RecyclerView recyclerView;
    private TodoAdapter adapter;
    private FloatingActionButton addButton;

    // CTRL + ALT + L -> Code Formatter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

        List<TodoModel> toDoListWorkDescriptionDataObjectList = new ArrayList<>();
        toDoListWorkDescriptionDataObjectList.add(new TodoModel(true, "deneme1"));
        toDoListWorkDescriptionDataObjectList.add(new TodoModel(false, "deneme2"));
        toDoListWorkDescriptionDataObjectList.add(new TodoModel(true, "deneme3"));
        toDoListWorkDescriptionDataObjectList.add(new TodoModel(false, "deneme4"));

        adapter.updateList(toDoListWorkDescriptionDataObjectList);
        events();
    }

    private void initViews() {
        addButton = findViewById(R.id.floatAB);
        recyclerView = findViewById(R.id.toDoRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        adapter = new TodoAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void events() {
        addButton.setOnClickListener(this);
    }

    Intent serviceIntent;

    @Override
    protected void onPause() {
        super.onPause();
        serviceIntent = new Intent();
        serviceIntent.putExtra("asd","asd");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floatAB:
                onClickAddButton();
                break;

        }

    }

    private void onClickAddButton() {
        startActivityForResult(new Intent(this, AddTodoActivity.class), Constants.IntentRequestCode.REQUEST_ADD_TODO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.IntentRequestCode.REQUEST_ADD_TODO:
                if (data != null) {
                    String data1 = data.getExtras().getString(Constants.IntentData.AddTodoData);
                    adapter.addTodo(new TodoModel(false, data1));
                }
                break;
        }
    }
}
