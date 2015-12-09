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

    private ScrollView scrollView;
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
        this.scrollView = (ScrollView) view.findViewById(R.id.movimentiView);
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
        //buildTable(getView(), m);
        buildView(m);
    }

    private void buildView(List<MovimentoContabile> movimenti){
        scrollView.removeAllViews();
        Context ctx = getView().getContext();
        LinearLayout mainBox = new LinearLayout(ctx);
        scrollView.addView(mainBox);
        mainBox.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        mainBox.setOrientation(LinearLayout.VERTICAL);
        MovimentoContabile movimento = null;
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        for (int i = 0; i < movimenti.size(); i++){
            movimento = movimenti.get(i);
            int textColor = 0;
            if (movimento.getDirezione().equals("ENTRATA"))
                textColor = Color.parseColor("#C8008000");
            else
                textColor =  Color.parseColor("#C8800000");
            LinearLayout box = new LinearLayout(ctx);
            mainBox.addView(box);
            box.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            box.setOrientation(LinearLayout.VERTICAL);
            if (i % 2 == 0)
                box.setBackgroundColor(Color.parseColor("#82010101"));
            else
                box.setBackgroundColor(Color.parseColor("#A0010101"));

            LinearLayout row1 = new LinearLayout(ctx);
            box.addView(row1);
            row1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            row1.setOrientation(LinearLayout.HORIZONTAL);
            row1.setWeightSum(100);

            TextView dataTv = new TextView(ctx);
            row1.addView(dataTv);
            dataTv.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 35));
            dataTv.setTypeface(Typeface.DEFAULT, Typeface.BOLD_ITALIC);
            dataTv.setTextSize(20);
            dataTv.setTextColor(textColor);
            dataTv.setText(format.format(movimento.getData()));
            dataTv.setPadding(10,20,10,10);

            TextView direzioneTv = new TextView(ctx);
            row1.addView(direzioneTv);
            direzioneTv.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 65));
            direzioneTv.setTypeface(Typeface.DEFAULT, Typeface.BOLD_ITALIC);
            direzioneTv.setGravity(Gravity.RIGHT);
            direzioneTv.setTextSize(20);
            direzioneTv.setTextColor(textColor);
            direzioneTv.setText(movimento.getTarget());
            direzioneTv.setPadding(10,20,10,10);

            LinearLayout row2 = new LinearLayout(ctx);
            box.addView(row2);
            row2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            row2.setOrientation(LinearLayout.HORIZONTAL);
            row2.setWeightSum(100);

            TextView descrizioneTv = new TextView(ctx);
            row2.addView(descrizioneTv);
            descrizioneTv.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 60));
            descrizioneTv.setTypeface(Typeface.DEFAULT, Typeface.BOLD_ITALIC);
            descrizioneTv.setTextSize(18);
            descrizioneTv.setTextColor(textColor);
            descrizioneTv.setText(movimento.getDescrizione());
            descrizioneTv.setPadding(10,10,10,20);

            TextView importoTv = new TextView(ctx);
            row2.addView(importoTv);
            importoTv.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 40));
            importoTv.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            importoTv.setTypeface(Typeface.DEFAULT, Typeface.BOLD_ITALIC);
            importoTv.setTextSize(25);
            importoTv.setTextColor(textColor);
            importoTv.setText(String.valueOf(movimento.getImporto()));
            importoTv.setPadding(5,5,5,20);

        }
    }

}
