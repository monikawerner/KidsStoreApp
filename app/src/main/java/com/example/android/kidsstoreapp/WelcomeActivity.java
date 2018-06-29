package com.example.android.kidsstoreapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        /** Set OnClickListener on the TextView to start another activity */
        TextView catalog = findViewById(R.id.catalog);
        catalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent catalogIntent = new Intent(WelcomeActivity.this, CatalogActivity.class);
                startActivity(catalogIntent);
            }
        });

        /** Set OnClickListener on the TextView to start another activity */
        TextView add = findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editorIntent = new Intent(WelcomeActivity.this, EditorActivity.class);
                startActivity(editorIntent);
            }
        });
    }
}
