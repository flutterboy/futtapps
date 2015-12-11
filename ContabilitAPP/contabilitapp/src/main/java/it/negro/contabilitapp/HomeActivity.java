package it.negro.contabilitapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class HomeActivity extends MainActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setTitle("Home");
        getSupportActionBar().setTitle(getTitle());
        Intent intent = getIntent();
        ((TextView)findViewById(R.id.tvHome)).setText("Ciao, sono la Home");
    }
}
