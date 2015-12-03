package it.negro.contabilitapp;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.style.TextAppearanceSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import it.negro.contabilitapp.entity.MovimentoContabile;
import it.negro.contabilitapp.remote.RemoteContabService;
import org.w3c.dom.Text;

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
        new GetMovimentiAsyncToken(this).execute(null, null);
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
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        tr.setGravity(Gravity.CENTER_VERTICAL);

        addToRow(tr, "Data", "header");
        addToRow(tr, "Descrizione", "header");
        addToRow(tr, "Importo", "header");

        movimentiTable.addView(tr);

        int i = 0;
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        for (MovimentoContabile m : ms){
            tr = new TableRow(view.getContext());
            /*if (i % 2 == 0)
                tr.setBackgroundColor(Color.parseColor("#b4ff800a"));
            else
                tr.setBackgroundColor(Color.parseColor("#64ff800a"));*/
            String tipo = "entrata";
            if (m.getDirezione().equals("USCITA"))
                tipo = "uscita";
            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            tr.setGravity(Gravity.CENTER_VERTICAL);

            addToRow(tr, format.format(m.getData()), tipo);
            addToRow(tr, m.getDescrizione(), tipo);
            addToRow(tr, String.valueOf(m.getImporto()), tipo);

            movimentiTable.addView(tr);
            i += 1;
        }
    }

    private void addToRow (TableRow row, String title, String tipo){
        TextView tv = new TextView(getView().getContext());
        row.addView(tv);
        tv.setText(title);
        tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        tv.setGravity(Gravity.CENTER_VERTICAL);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tv.setTextAppearance(getView().getContext(), android.R.style.TextAppearance_Medium);
        if (tipo.equals("header")) {
            tv.setTextColor(Color.GRAY);
            tv.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            tv.setHeight(100);
        }else{
            tv.setPadding(10, 10, 10, 10);
            tv.setTextSize(15);
            tv.setMinimumHeight(75);
            if (tipo.equals("entrata"))
                tv.setTextColor(Color.GREEN);
            else if (tipo.equals("uscita"))
                tv.setTextColor(Color.RED);
        }
    }

}
