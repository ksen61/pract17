package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import io.paperdb.Paper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText titleText, authorText;
    private Button addButton, updateButton, deleteButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private String selectedBookTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Paper.init(this);

        titleText = findViewById(R.id.titleText);
        authorText = findViewById(R.id.AuthorText);
        addButton = findViewById(R.id.addButton);
        updateButton = findViewById(R.id.updateButton);
        deleteButton = findViewById(R.id.deleteButton);
        listView = findViewById(R.id.listView);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getBooksTitles());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedBookTitle = adapter.getItem(position);
                Book book = Paper.book().read(selectedBookTitle, null);
                if (book != null) {
                    titleText.setText(book.getTitle());
                    authorText.setText(book.getAuthor());
                }
            }
        });

        addButton.setOnClickListener(v -> {
            String title = titleText.getText().toString();
            String author = authorText.getText().toString();
            if (!title.isEmpty() && !author.isEmpty()) {
                Book book = new Book(title, author);
                Paper.book().write(title, book);
                updateBookList();
                clearInputs();
            }
        });

        updateButton.setOnClickListener(v -> {
            if (selectedBookTitle == null) {
                Toast.makeText(MainActivity.this, "Пожалуйста, сначала выберите книгу", Toast.LENGTH_SHORT).show();
                return;
            }
            String title = titleText.getText().toString();
            String author = authorText.getText().toString();
            if (!title.isEmpty() && !author.isEmpty()) {
                Book updatedBook = new Book(title, author);
                Paper.book().write(selectedBookTitle, updatedBook);
                updateBookList();
                clearInputs();
            }
        });

        deleteButton.setOnClickListener(v -> {
            if (selectedBookTitle == null) {
                Toast.makeText(MainActivity.this, "Пожалуйста, сначала выберите книгу", Toast.LENGTH_SHORT).show();
                return;
            }
            Paper.book().delete(selectedBookTitle);
            updateBookList();
            clearInputs();
        });
    }

    private void updateBookList() {
        adapter.clear();
        adapter.addAll(getBooksTitles());
        adapter.notifyDataSetChanged();
    }

    private List<String> getBooksTitles() {
        return new ArrayList<>(Paper.book().getAllKeys());
    }

    private void clearInputs() {
        titleText.setText("");
        authorText.setText("");
        selectedBookTitle = null;
    }
}
