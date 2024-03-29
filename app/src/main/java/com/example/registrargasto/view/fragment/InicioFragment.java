package com.example.registrargasto.view.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.registrargasto.DAOS.DAOAdeudoImp;
import com.example.registrargasto.DAOS.DAOGastoImp;
import com.example.registrargasto.DAOS.DAOPresupuestoIm;
import com.example.registrargasto.DAOS.IDAOS.IDAOAdeudo;
import com.example.registrargasto.R;
import com.example.registrargasto.adapter.AdapterListaGastos;
import com.example.registrargasto.entidades.AdeudoDTO;
import com.example.registrargasto.entidades.GastoDTO;
import com.example.registrargasto.entidades.PresupuestoDTO;
import com.example.registrargasto.view.activity.IActivity.IGastoActivityView;
import com.example.registrargasto.view.activity.PresupuestoActivity;
import com.example.registrargasto.view.fragment.IFragment.IAdeudoFragmentView;
import com.example.registrargasto.view.fragment.IFragment.IPresupuestoFragmentView;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class InicioFragment extends Fragment implements IGastoActivityView, IPresupuestoFragmentView, IDAOAdeudo {

    private CompactCalendarView compactCalendar;
    private final SimpleDateFormat dateFormatMonth = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
    private RecyclerView recyclerView;
    private TextView textView;
    private TextView mtxtPresupuesto;
    private TextView mPresupuesto;
    private Button buttonCam;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_inicio, container, false);
        compactCalendar = rootView.findViewById(R.id.calendar);
        recyclerView = rootView.findViewById(R.id.rviewGastos);
        textView =rootView.findViewById(R.id.txtHeader);
        mPresupuesto=rootView.findViewById(R.id.mosPresupuesto);
        //mtxtPresupuesto=rootView.findViewById(R.id.textPresupuesto);
        buttonCam = rootView.findViewById(R.id.buttonCambiar);

        //Adaptador de recyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        AdapterListaGastos adapterHome = new AdapterListaGastos(consultarGastos());
        recyclerView.setAdapter(adapterHome);

        compactCalendar.setUseThreeLetterAbbreviation(true);

        if(!consultarAdeudo().isEmpty()){
            agregarEventos();
        }


        cambiarPres();

        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener(){
            @Override
            public void onDayClick(Date dateClicked) {

                for (Event event:consultarEventos()) {
                    if (dateFormatMonth.format(dateClicked).compareTo(milliADate(event.getTimeInMillis()))==0) {
                        Toast.makeText(getContext(), Objects.requireNonNull(event.getData()).toString(), Toast.LENGTH_SHORT).show();

                    }else {
                        Toast.makeText(getContext(), "No contiene eventos", Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                textView.setText(dateFormatMonth.format(firstDayOfNewMonth));
            }
        });
        if(consultarPresupuesto().getCantidad()<=0){
            mPresupuesto.setText("0.0");
        }else {
            mPresupuesto.setText(String.valueOf( consultarPresupuesto().getCantidad()));

        }

        return  rootView;
    }

    public void cambiarPres(){
        if (consultarPresupuesto().getCantidad() != 0) {
            buttonCam.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         Intent intent;
                         intent = new Intent(getContext(), PresupuestoActivity.class);
                         startActivity(intent);
                     }
                 }
            );
        }else {
            Toast.makeText(getContext(), "Establesca un presupuesto primero", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public ArrayList<GastoDTO> consultarGastos() {
        IGastoActivityView iGastoActivityView=new DAOGastoImp(getContext());
        return iGastoActivityView.consultarGastos();
    }

    public String milliADate(Long milli){
        // Estableciendo formato
        DateFormat simple = new SimpleDateFormat("dd-MM-yyyy",Locale.getDefault());
        // Creando fecha de milliseconds
        // usando constructor de Date
        Date date = new Date(milli);
        return simple.format(date);
    }
    public Long fechaALong(String fecha){
        SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy",Locale.getDefault());
        Long timeInMillis = null;
        try {
            Date d = f.parse(fecha);
            assert d != null;
            timeInMillis = d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeInMillis;
    }


    public void agregarEventos(){
        Event evento;

        ArrayList<Event> eventos = new ArrayList<>();
        for (AdeudoDTO adeudoDTO:consultarAdeudo()) {
            evento=new Event(Color.rgb(26, 188, 156),fechaALong(adeudoDTO.getFechaLimite()),adeudoDTO.getNombreadeudo().toString());
            compactCalendar.addEvent(evento);
        }
    }
    public ArrayList<Event> consultarEventos(){

        Event evento;

        ArrayList<Event> eventos = new ArrayList<>();
        for (AdeudoDTO adeudoDTO:consultarAdeudo()) {
            evento=new Event(Color.RED,fechaALong(adeudoDTO.getFechaLimite()),adeudoDTO.getNombreadeudo().toString());
            eventos.add(evento);
        }
        return eventos;
    }

    @Override
    public PresupuestoDTO consultarPresupuestoDatos() {
        return null;
    }

    @Override
    public PresupuestoDTO consultarPresupuesto() {
        IPresupuestoFragmentView iPresupuestoFragmentView=new DAOPresupuestoIm(getContext());
        return iPresupuestoFragmentView.consultarPresupuesto();
    }


    @Override
    public long registarNuevoAdeudo(AdeudoDTO adeudoDTO) {
        return 0;
    }

    @Override
    public ArrayList<AdeudoDTO> consultarAdeudo() {
        IAdeudoFragmentView iAdeudoFragmentView = new DAOAdeudoImp(getContext());
        return iAdeudoFragmentView.consultarAdeudoDatos();
    }
}
