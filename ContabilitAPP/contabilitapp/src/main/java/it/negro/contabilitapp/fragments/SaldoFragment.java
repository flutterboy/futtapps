package it.negro.contabilitapp.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import it.negro.contabilitapp.R;
import it.negro.contabilitapp.WhereWeAre;
import it.negro.contabilitapp.activities.BaseContabilitaActivity;

public class SaldoFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saldo, container, false);
        ((BaseContabilitaActivity)getActivity()).where(WhereWeAre.SALDO, this);
        getActivity().setTitle("Saldo");
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Saldo");
        ((TextView)view.findViewById(R.id.tvSaldo)).setText("Io sono ilò saldo");
        return view;
    }
}
