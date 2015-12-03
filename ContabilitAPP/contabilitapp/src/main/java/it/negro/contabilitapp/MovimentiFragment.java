package it.negro.contabilitapp;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import it.negro.contabilitapp.entity.MovimentoContabile;
import it.negro.contabilitapp.remote.RemoteContabService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


public class MovimentiFragment extends Fragment {

    private TableLayout movimentiTable;
    private static final int REQ_CODE_SPEECH_INPUT = 100;

    public static MovimentiFragment newInstance() {
        MovimentiFragment fragment = new MovimentiFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movimenti, container, false);
        this.movimentiTable = (TableLayout) view.findViewById(R.id.movimentiGrid);
        //new GetMovimentiAsyncToken(this).execute(null, null);
        return view;
    }

    public void promptSpeech (){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getView().getContext(), getString(R.string.speech_not_supported), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getView().getContext(), "Hai detto: " + string, Toast.LENGTH_LONG).show();
                }
                break;
            }
            default:
                break;
        }
    }

    private static class GetMovimentiAsyncToken extends AsyncTask<Object, List<MovimentoContabile>, List<MovimentoContabile>> {

        private ProgressDialog progressDialog;

        private MovimentiFragment movimentiFragment;
        private RemoteContabService contabService;

        public GetMovimentiAsyncToken(MovimentiFragment mf){
            this.movimentiFragment = mf;
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
            progressDialog = ProgressDialog.show(movimentiFragment.getActivity(), "Caricamento movimenti!", "Sto recuperando i movimenti...");
        }

        @Override
        protected void onPostExecute(List<MovimentoContabile> result) {
            progressDialog.dismiss();
            movimentiFragment.onMovimentiTrovati(result);
        }
    }

    public void onMovimentiTrovati(List<MovimentoContabile> m){
        if (m == null || m.size() == 0)
            return;
        buildTable(getView(), m);
    }

    private void buildTable(View view, List<MovimentoContabile> ms){
        movimentiTable.removeAllViews();
        TableRow tr = new TableRow(view.getContext());
        tr.setBackgroundColor(Color.parseColor("#ff800a"));
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

        TextView data = buildMovimentiGridCell("Data", 270, 40, true, view.getContext());
        tr.addView(data, 750, 100);

        TextView descrizione = buildMovimentiGridCell("Descrizione", -1, -1, true, view.getContext());
        tr.addView(descrizione);

        TextView importo = buildMovimentiGridCell("Importo", 250, 40, true, view.getContext());
        tr.addView(importo, 710, 100);

        movimentiTable.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

        int i = 0;
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        for (MovimentoContabile m : ms){
            tr = new TableRow(view.getContext());
            if (i % 2 == 0)
                tr.setBackgroundColor(Color.parseColor("#b4ff800a"));
            else
                tr.setBackgroundColor(Color.parseColor("#64ff800a"));
            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

            data = buildMovimentiGridCell(format.format(m.getData()), 270, 40, false, view.getContext());
            tr.addView(data, 750, 100);

            descrizione = buildMovimentiGridCell(m.getDescrizione().trim(), -1, -1, false, view.getContext());
            tr.addView(descrizione);

            importo = buildMovimentiGridCell(String.valueOf(m.getImporto()), 250, 40, false, view.getContext());
            tr.addView(importo, 710, 100);

            movimentiTable.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
            i += 1;
        }

    }

    private TextView buildMovimentiGridCell(String title, int w, int h, boolean header, Context c){
        TextView tv = new TextView(c);
        tv.setText(title);
        tv.setTextColor(Color.BLACK);
        tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        if (w != -1)
            tv.setMinimumWidth(w);
        if (h != -1)
            tv.setMinimumHeight(h);
        tv.setTextSize(15);
        if (header)
            tv.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return tv;
    }

}
