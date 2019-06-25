package com.example.candogan.todoexercise;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ToDoViewHolder> {

    List<TodoModel> dataObjects = new ArrayList<>();

    public TodoAdapter() {
    }

    public void updateList(List<TodoModel> dataObjects) {
        this.dataObjects.clear();
        this.dataObjects.addAll(dataObjects);
        notifyDataSetChanged();
    }

    public void addTodo(TodoModel newItem) {
        this.dataObjects.add(newItem);
        notifyItemInserted(dataObjects.size());
    }

    @NonNull
    @Override
    public ToDoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_layout, viewGroup, false);
        return new ToDoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ToDoViewHolder toDoViewHolder, int position) {
        Boolean checked = dataObjects.get(position).getCheck();
        String workDescription = dataObjects.get(position).getWorkDescription();
        toDoViewHolder.setData(checked, workDescription);


    }

    @Override
    public int getItemCount() {
        return dataObjects.size();
    }

    class ToDoViewHolder extends RecyclerView.ViewHolder {
        private CheckBox isDone;
        private TextView workDescription;

        public ToDoViewHolder(@NonNull View itemView) {
            super(itemView);
            isDone = itemView.findViewById(R.id.isWorkDone);
            workDescription = itemView.findViewById(R.id.workDescription);

        }

        private void setData(Boolean check, String workDesc) {
            isDone.setChecked(check);
            workDescription.setText(workDesc);
        }

    }

}
