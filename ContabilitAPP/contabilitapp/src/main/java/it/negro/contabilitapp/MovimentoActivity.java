package it.negro.contabilitapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import it.negro.contabilitapp.entity.MovimentoContabile;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class MovimentoActivity extends AppCompatActivity {

    private MovimentoContabile movimentoContabile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movimento);
        Intent intent = getIntent();
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        this.movimentoContabile = (MovimentoContabile) intent.getSerializableExtra("movimento");
        ((TextView)findViewById(R.id.dataMovimento)).setText(format.format(movimentoContabile.getData()));
        ((TextView)findViewById(R.id.direzioneMovimento)).setText(movimentoContabile.getDirezione());
        ((TextView)findViewById(R.id.targetMovimento)).setText(movimentoContabile.getTarget());
        ((TextView)findViewById(R.id.importoMovimento)).setText(String.valueOf(movimentoContabile.getImporto()) + " €");
        ((TextView)findViewById(R.id.descrizioneMovimento)).setText(movimentoContabile.getDescrizione());
        if (movimentoContabile.getDocumento() != null){
            byte[] imageArray = Base64.decode(movimentoContabile.getDocumento().getBase64(), Base64.DEFAULT);
            Bitmap decodedImage = BitmapFactory.decodeByteArray(imageArray, 0, imageArray.length);
            ((ImageView)findViewById(R.id.documentoImageView)).setImageBitmap(decodedImage);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_movimento, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings)
            return true;
        return super.onOptionsItemSelected(item);
    }


}
