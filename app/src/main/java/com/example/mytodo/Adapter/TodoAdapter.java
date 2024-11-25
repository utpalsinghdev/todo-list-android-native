package com.example.mytodo.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytodo.AddNewTask;
import com.example.mytodo.MainActivity;
import com.example.mytodo.ModelTodoList.TodoModel;
import com.example.mytodo.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.MyViewHolder> {

    private List<TodoModel> todoList;
    private MainActivity activity;
    private FirebaseFirestore firestore;

    public TodoAdapter(MainActivity mainActivity, List<TodoModel> todoList){
        this.todoList = todoList;
        activity = mainActivity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(activity).inflate(R.layout.each_task, parent, false);

       firestore = FirebaseFirestore.getInstance();

       return new MyViewHolder(view);
    }

    public Context getContext(){
        return activity;
    }


    public void deleteTask(int position){
        TodoModel todoModel = todoList.get(position);
        firestore.collection("task").document(todoModel.TaskId).delete();
        todoList.remove(position);
        notifyItemRemoved(position);
    }

    public void editTask(int position){
        TodoModel todoModel = todoList.get(position);
        Bundle bundle = new Bundle();

        bundle.putString("task", todoModel.getTask());
        bundle.putString("due", todoModel.getDue());
        bundle.putString("id",todoModel.TaskId);

        AddNewTask addNewTask = new AddNewTask();
        addNewTask.setArguments(bundle);
        addNewTask.show(activity.getSupportFragmentManager(), addNewTask.getTag());


    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TodoModel todoModel = todoList.get(position);
        holder.mCheckBox.setText(todoModel.getTask());

        if(todoModel.getDue() != null){
            holder.mDueDateTv.setText("Due on " + todoModel.getDue());
        } else {
            holder.mDueDateTv.setText("No due date");
        }

        holder.mCheckBox.setChecked(toBoolean(todoModel.getStatus()));

        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    firestore.collection("task").document(todoModel.TaskId).update("status", 1);
                } else {
                    firestore.collection("task").document(todoModel.TaskId).update("status", 0);
                }
            }
        });
    }

    private boolean toBoolean(int status){
        return status != 0;
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView mDueDateTv;
        CheckBox mCheckBox;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            mDueDateTv = itemView.findViewById(R.id.due_date_tv);
            mCheckBox = itemView.findViewById(R.id.mcheckbox);
        }
    }
}
