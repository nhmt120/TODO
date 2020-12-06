package com.example.todo.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.MainActivity;
import com.example.todo.Model.TODO;
import com.example.todo.R;

import java.util.List;

class ListItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    ItemClickListener itemClickListener;
    TextView item_title, item_note, item_radio;

    public ListItemViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        item_title = (TextView) itemView.findViewById(R.id.item_title);
        item_note = (TextView) itemView.findViewById(R.id.item_note);
        item_radio = (RadioButton) itemView.findViewById(R.id.item_radio);

    }


    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }
}

public class ListItemAdapter extends RecyclerView.Adapter<ListItemViewHolder> {

    MainActivity mainActivity;
    Context context;
    List<TODO> todoList;

    public ListItemAdapter(MainActivity mainActivity, List<TODO> todoList) {
        this.mainActivity = mainActivity;
        this.todoList = todoList;
    }

    @NonNull
    @Override
    public ListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mainActivity.getBaseContext());
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new ListItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListItemViewHolder holder, final int position) {

        // set data for items
        holder.item_title.setText(todoList.get(position).getTitle());
        holder.item_note.setText(todoList.get(position).getNote());

        holder.item_radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mainActivity instanceof MainActivity){
                    ((MainActivity)mainActivity).deleteItem(position);
                }
            }
        });

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                // When user select item, data auto set for Edit Text View
                mainActivity.title.setText(todoList.get(position).getTitle());
                mainActivity.note.setText(todoList.get(position).getNote());

                mainActivity.isUpdate = true; // set flag isUpdate true
                mainActivity.idUpdate = todoList.get(position).getId();
            }
        });
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }


}
