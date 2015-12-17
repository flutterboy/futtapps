package it.negro.contabilitapp.fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import it.negro.contabilitapp.R;
import it.negro.contabilitapp.WhereWeAre;
import it.negro.contabilitapp.activities.BaseContabilitaActivity;
import it.negro.contabilitapp.entity.MovimentoContabile;
import it.negro.contabilitapp.remote.AsyncTaskExecutor;
import it.negro.contabilitapp.remote.AsyncTaskDelegate;
import it.negro.contabilitapp.remote.RemoteContabService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;


public class DettaglioMovimentoFragment extends Fragment {

    private MovimentoContabile movimentoContabile;

    public static DettaglioMovimentoFragment newInstance(Integer id) {
        DettaglioMovimentoFragment fragment = new DettaglioMovimentoFragment();
        Bundle args = new Bundle();
        args.putInt("id", id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dettaglio_movimento, container, false);
        ((BaseContabilitaActivity)getActivity()).where(WhereWeAre.DETTAGLIO_MOVIMENTO, this);
        Integer idMovimento = getArguments().getInt("id");
        executeTask("Caricamento moviment0!", "Sto recuperando il movimento...", idMovimento);
        return view;
    }

    protected void executeTask(String title, String message, Object... params){
        AsyncTaskDelegate asyncTaskDelegate = new AsyncTaskDelegate() {
            @Override
            public Object executeTask(Object... params) {
                return new RemoteContabService().getMovimento((Integer)params[0]);
            }

            @Override
            public void onResult(Object result) {
                movimentoContabile = (MovimentoContabile)result;
                populateView();
            }
        };
        new AsyncTaskExecutor(getActivity(), asyncTaskDelegate, title, message).executeTask(params);
    }

    private void populateView(){
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        ((TextView)getView().findViewById(R.id.dataMovimento)).setText(format.format(movimentoContabile.getData()));
        ((TextView)getView().findViewById(R.id.direzioneMovimento)).setText(movimentoContabile.getDirezione());
        ((TextView)getView().findViewById(R.id.targetMovimento)).setText(movimentoContabile.getTarget());
        ((TextView)getView().findViewById(R.id.importoMovimento)).setText(String.valueOf(movimentoContabile.getImporto()) + " €");
        ((TextView)getView().findViewById(R.id.descrizioneMovimento)).setText(movimentoContabile.getDescrizione());
        if (movimentoContabile.getDocumento() != null){
            byte[] imageArray = Base64.decode(movimentoContabile.getDocumento().getBase64(), Base64.DEFAULT);
            Bitmap decodedImage = BitmapFactory.decodeByteArray(imageArray, 0, imageArray.length);
            ((ImageView)getView().findViewById(R.id.documentoImageView)).setImageBitmap(decodedImage);
        }else{
            getView().findViewById(R.id.documentoLabel).setVisibility(View.INVISIBLE);
        }
    }

}
