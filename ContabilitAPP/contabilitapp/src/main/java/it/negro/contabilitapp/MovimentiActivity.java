package it.negro.contabilitapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import it.negro.contabilitapp.entity.MovimentoContabile;
import it.negro.contabilitapp.remote.RemoteContabService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MovimentiActivity extends MainActivity {

    private ScrollView scrollView;
    private static final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movimenti);
        setTitle("Movimenti");
        getSupportActionBar().setTitle(getTitle());
        Intent intent = getIntent();
        this.scrollView = (ScrollView) findViewById(R.id.movimentiView);
        new GetMovimentiAsyncToken(this).execute(null, null);
    }

    public void promptSpeech (){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), getString(R.string.speech_not_supported), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String string = result.get(0);
                    Toast.makeText(getApplicationContext(), "Hai detto: " + string, Toast.LENGTH_LONG).show();
                }
                break;
            }
            default:
                break;
        }
    }

    private static class GetMovimentiAsyncToken extends AsyncTask<Object, List<MovimentoContabile>, List<MovimentoContabile>> {

        private ProgressDialog progressDialog;

        private MovimentiActivity movimentiActivity;
        private RemoteContabService contabService;

        public GetMovimentiAsyncToken(MovimentiActivity ma){
            this.movimentiActivity = ma;
            this.contabService = new RemoteContabService();
        }

        @Override
        protected List<MovimentoContabile> doInBackground(Object... params) {
            try {
                return contabService.getMovimenti((Date)params[0], (Date)params[1]);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(movimentiActivity, "Caricamento movimenti!", "Sto recuperando i movimenti...");
        }

        @Override
        protected void onPostExecute(List<MovimentoContabile> result) {
            progressDialog.dismiss();
            movimentiActivity.onMovimentiTrovati(result);
        }
    }

    public void onMovimentiTrovati(List<MovimentoContabile> m){
        if (m == null || m.size() == 0)
            return;
        //buildTable(getView(), m);
        buildView(m);
    }

    private void buildView(List<MovimentoContabile> movimenti){
        scrollView.removeAllViews();
        Context ctx = getApplicationContext();
        LinearLayout mainBox = new LinearLayout(ctx);
        scrollView.addView(mainBox);
        mainBox.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        mainBox.setOrientation(LinearLayout.VERTICAL);
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        MovimentoContabile movimento = null;
        for (int i = 0; i < movimenti.size(); i++){
            movimento = movimenti.get(i);
            int textColor = Color.parseColor("#FF232323");
            LinearLayout box = new LinearLayout(ctx);
            mainBox.addView(box);
            box.setOnClickListener(new DettaglioMovimentoListener(movimento, this));
            box.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            box.setOrientation(LinearLayout.VERTICAL);
            if (i % 2 == 0)
                box.setBackgroundColor(Color.parseColor("#d7d7d7"));
            else
                box.setBackgroundColor(Color.parseColor("#9b9b9b"));

            LinearLayout row = new LinearLayout(ctx);
            box.addView(row);
            row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setWeightSum(100);

            TextView dataTv = new TextView(ctx);
            row.addView(dataTv);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 28);
            layoutParams.gravity = Gravity.CENTER_VERTICAL;
            dataTv.setLayoutParams(layoutParams);
            dataTv.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
            dataTv.setTextSize(14);
            dataTv.setTextColor(textColor);
            dataTv.setText(format.format(movimento.getData()));
            dataTv.setPadding(10,10,10,10);

            TextView descrizioneTv = new TextView(ctx);
            row.addView(descrizioneTv);
            layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 45);
            layoutParams.gravity = Gravity.CENTER_VERTICAL;
            descrizioneTv.setLayoutParams(layoutParams);
            descrizioneTv.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            descrizioneTv.setTextSize(15);
            descrizioneTv.setTextColor(textColor);
            descrizioneTv.setText(movimento.getDescrizione());
            descrizioneTv.setPadding(10,10,10,10);

            String importo = String.valueOf(movimento.getImporto()) + " €";
            if (movimento.getDirezione().equals("USCITA"))
                importo = "- " + importo;
            TextView importoTv = new TextView(ctx);
            row.addView(importoTv);
            layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 28);
            layoutParams.gravity = Gravity.CENTER_VERTICAL;
            importoTv.setLayoutParams(layoutParams);
            importoTv.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
            importoTv.setTextSize(15);
            importoTv.setTextColor(textColor);
            importoTv.setText(importo);
            importoTv.setPadding(10,10,10,10);

        }
    }

    public static class DettaglioMovimentoListener implements View.OnClickListener {

        private MovimentoContabile movimentoContabile;
        private Activity activity;

        public DettaglioMovimentoListener(MovimentoContabile movimento, Activity activity){
            this.movimentoContabile = movimento;
            this.activity = activity;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(activity, MovimentoActivity.class);
            intent.putExtra("movimento", movimentoContabile);
            activity.startActivity(intent);
        }
    }
}
