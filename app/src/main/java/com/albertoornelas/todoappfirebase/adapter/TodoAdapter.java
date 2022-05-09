package com.albertoornelas.todoappfirebase.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.albertoornelas.todoappfirebase.HomeActivity;
import com.albertoornelas.todoappfirebase.R;
import com.albertoornelas.todoappfirebase.domain.Todo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder> {
    private List<Todo> todosList;
    private LayoutInflater inflater;
    private Context context;

    private Button btnDelete;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private HomeActivity homeActivity;

    public TodoAdapter(HomeActivity homeActivity, List<Todo> todosList, Context context) {
        this.homeActivity = homeActivity;
        this.inflater = LayoutInflater.from(context);
        this.todosList = todosList;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return todosList.size();
    }

    @Override
    public TodoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_element, null);
        return new TodoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TodoAdapter.ViewHolder holder, final int position) {
        holder.bindData(todosList.get(position));
    }

    public void setItem(List<Todo> todos) { todosList = todos; }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTodo, descTodo, idTodo;

        ViewHolder(View itemView){
            super(itemView);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            idTodo = itemView.findViewById(R.id.idTodo);
            titleTodo = itemView.findViewById(R.id.titleTodo);
            descTodo = itemView.findViewById(R.id.descTodo);

            mAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();

            btnDelete.setOnClickListener(view -> {
                db.collection("users").document(mAuth.getCurrentUser().getUid())
                        .collection("todos").document(idTodo.getText().toString())
                        .delete()
                        .addOnCompleteListener(task -> {
                            Toast.makeText(
                                    homeActivity,
                                    String.format("Todo %s eliminado", idTodo.getText().toString()),
                                    Toast.LENGTH_SHORT
                            ).show();

                            todosList.clear();
                            homeActivity.fetchTodos();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(homeActivity, "Algo salio mal", Toast.LENGTH_SHORT).show();
                        });
            });
        }


        void bindData(final Todo todo) {
            idTodo.setText(todo.getIdTodo());
            titleTodo.setText(todo.getTitleTodo());
            descTodo.setText(todo.getDescriptionTodo());
        }
    }
}
