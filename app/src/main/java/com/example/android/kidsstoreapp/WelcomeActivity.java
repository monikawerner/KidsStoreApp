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
        TextView products = (TextView) findViewById(R.id.products);
        products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent productsIntent = new Intent(WelcomeActivity.this, CatalogActivity.class);
                startActivity(productsIntent);
            }
        });

        /** Set OnClickListener on the TextView to start another activity */
        TextView add = (TextView) findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editorIntent = new Intent(WelcomeActivity.this, EditorActivity.class);
                startActivity(editorIntent);
            }
        });
    }
}
