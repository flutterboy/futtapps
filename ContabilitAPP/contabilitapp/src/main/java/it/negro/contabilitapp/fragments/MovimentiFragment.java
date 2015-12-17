package it.negro.contabilitapp.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import it.negro.contabilitapp.R;
import it.negro.contabilitapp.WhereWeAre;
import it.negro.contabilitapp.activities.BaseContabilitaActivity;
import it.negro.contabilitapp.entity.MovimentoContabile;
import it.negro.contabilitapp.remote.AsyncTaskDelegate;
import it.negro.contabilitapp.remote.AsyncTaskExecutor;
import it.negro.contabilitapp.remote.RemoteContabService;
import it.negro.contabilitapp.widget.InteractiveScrollView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class MovimentiFragment extends Fragment {

    private InteractiveScrollView scrollView;
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private int page = 1;
    private int elems = 10;
    private boolean allMovsLoaded;

    public static MovimentiFragment newInstance(){
        MovimentiFragment fragment = new MovimentiFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movimenti, container, false);
        ((BaseContabilitaActivity)getActivity()).where(WhereWeAre.MOVIMENTI, this);
        getActivity().setTitle("Movimenti");
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Movimenti");
        this.scrollView = (InteractiveScrollView) view.findViewById(R.id.movimentiView);
        this.scrollView.setOnBottomReachedListener(new InteractiveScrollView.OnBottomReachedListener() {
            @Override
            public void onBottomReached() {
                if (!allMovsLoaded)
                    executeTask("Caricamento movimenti!", "Sto recuperando i movimenti...", page, elems, new Date());
            }
        });
        executeTask("Caricamento movimenti!", "Sto recuperando i movimenti...", page, elems, new Date());
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
            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.speech_not_supported), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getActivity().getApplicationContext(), "Hai detto: " + string, Toast.LENGTH_LONG).show();
                }
                break;
            }
            default:
                break;
        }
    }

    protected void executeTask(String title, String message, Object... params){
        AsyncTaskDelegate asyncTaskDelegate = new AsyncTaskDelegate() {
            @Override
            public Object executeTask(Object... params) {
                return new RemoteContabService().getMovimenti((Integer) params[0], (Integer) params[1], (Date) params[2]);
            }

            @Override
            public void onResult(Object result) {
                page += 1;
                buildView((List<MovimentoContabile>) result);
            }
        };
        new AsyncTaskExecutor(getActivity(), asyncTaskDelegate, title, message).executeTask(params);
    }

    private void buildView(List<MovimentoContabile> movimenti){
        Context ctx = getActivity().getApplicationContext();
        LinearLayout mainBox = (LinearLayout) getActivity().findViewById(R.id.movimentiViewMainBox);
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        MovimentoContabile movimento = null;
        for (int i = 0; i < movimenti.size(); i++){
            movimento = movimenti.get(i);
            int textColor = Color.parseColor("#FF232323");
            LinearLayout box = new LinearLayout(ctx);
            mainBox.addView(box);
            box.setOnClickListener(new DettaglioMovimentoListener(movimento, this, getActivity()));
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
        if (movimenti.size() < elems)
            allMovsLoaded = true;
        if(scrollView.getMeasuredHeight() > mainBox.getHeight() && !allMovsLoaded)
            executeTask("Caricamento movimenti!", "Sto recuperando i movimenti...", page, elems, new Date());
    }

    public static class DettaglioMovimentoListener implements View.OnClickListener {

        private MovimentoContabile movimentoContabile;
        private MovimentiFragment fragment;
        private Activity activity;

        public DettaglioMovimentoListener(MovimentoContabile movimento, MovimentiFragment fragment, Activity activity){
            this.movimentoContabile = movimento;
            this.fragment = fragment;
            this.activity = activity;
        }

        @Override
        public void onClick(View v) {
            Fragment fragment = DettaglioMovimentoFragment.newInstance(movimentoContabile.getId());
            this.activity.getFragmentManager().beginTransaction().add(R.id.container, fragment, WhereWeAre.DETTAGLIO_MOVIMENTO).commit();
            this.activity.getFragmentManager().beginTransaction().hide(this.fragment).commit();
        }
    }
}
