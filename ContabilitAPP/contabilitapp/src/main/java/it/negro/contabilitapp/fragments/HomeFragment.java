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

public class HomeFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ((BaseContabilitaActivity)getActivity()).where(WhereWeAre.HOME, this);
        getActivity().setTitle("Home");
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Home");
        ((TextView)view.findViewById(R.id.tvHome)).setText("Io sono la home");
        return view;
    }
}
