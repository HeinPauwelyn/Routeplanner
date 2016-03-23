package be.howest.nmct.roeteplanner;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;

import be.howest.nmct.roeteplanner.classes.INieweLocatieFragment;
import be.howest.nmct.roeteplanner.classes.Locatie;
import be.howest.nmct.roeteplanner.classes.OnFragementReplaceListener;
import be.howest.nmct.roeteplanner.classes.OnNieuweLocatieCreatieListener;
import be.howest.nmct.roeteplanner.repositories.GoogleLocationRepo;
import butterknife.Bind;
import butterknife.ButterKnife;


public class SmartNieuwLocatieFragement extends Fragment implements INieweLocatieFragment {

    @Bind(R.id.btnRefresh) ImageButton btnRefresh;
    @Bind(R.id.btnAnnuleren) Button btnAnnuleren;
    @Bind(R.id.btnToevoegen) Button btnToevoegen;
    @Bind(R.id.txvBedoeling) TextView txvBedoeling;
    @Bind(R.id.edtLocatie) EditText edtLocatie;

    private GoogleLocationRepo _googleLocatieRepo;
    private OnNieuweLocatieCreatieListener _nieuweLocatieCreatieListener;
    private OnFragementReplaceListener _fragmentReplaceListener;

    public SmartNieuwLocatieFragement() {
    }

    public static SmartNieuwLocatieFragement newInstance() {
        return new SmartNieuwLocatieFragement();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_smart_nieuw_locatie_fragement, container, false);

        ButterKnife.bind(this, v);

        _fragmentReplaceListener = (StartActivity) getActivity();
        _nieuweLocatieCreatieListener = (StartActivity) getActivity();
        _googleLocatieRepo = new GoogleLocationRepo();

        btnAnnuleren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _fragmentReplaceListener.newFragment(LocatiesFragment.newInstance());
            }
        });

        btnToevoegen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Locatie locatie = new Locatie(edtLocatie.getText().toString(), "");

                _nieuweLocatieCreatieListener.onNieuweLocatieCreeerd(locatie);
                _fragmentReplaceListener.newFragment(LocatiesFragment.newInstance());

                Snackbar.make(v, locatie.toString() + "is toegevoegd", Snackbar.LENGTH_LONG).show();
            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leesData(v);
            }
        });

        return v;
    }

    private void leesData(View v) {
        Locatie locatie = null;

        try {
             locatie = new GoogleLocationRepo().execute(edtLocatie.getText().toString()).get();
        }
        catch (ExecutionException | InterruptedException ex){
            Snackbar.make(v, "Fout tijdens het verwerken van de aanvraag", Snackbar.LENGTH_LONG).show();
            return;
        }

        if (locatie == null) {
            Snackbar.make(v, "Controleer uw internetverbinding", Snackbar.LENGTH_LONG).show();
        } else {
            txvBedoeling.setText(locatie.toString());
        }
    }
}
