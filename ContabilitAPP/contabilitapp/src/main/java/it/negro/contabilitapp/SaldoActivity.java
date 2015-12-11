package it.negro.contabilitapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class SaldoActivity extends MainActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saldo);
        setTitle("Saldo");
        getSupportActionBar().setTitle(getTitle());
        Intent intent = getIntent();
        ((TextView)findViewById(R.id.tvSaldo)).setText("Ciao, sono il Saldo");
    }
}
