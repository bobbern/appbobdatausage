package pl.rzagorski.networkstatsmanager;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.DecimalFormat;
import android.view.View;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Bundle;

/**
 * Created by bob on 2017-08-14.
 */

public class DialogActivity extends Activity implements View.OnClickListener{
    public static final String PREFS = "PREFS";
    public static final String PREF_KEY_DATA_LIMITE_MAX = "PREF_KEY_DATA_LIMITE_MAX";
    public static final String PREF_KEY_DEBUT_CYCLE = "PREF_KEY_DEBUT_CYCLE";
    public static final String PREF_KEY_DATA_LIMITE_MAX_FLOAT = "PREF_KEY_DATA_LIMITE_MAX_FLOAT";
    Button ok_btn, cancel_btn;
    TextView tvJourDebutPeriode ;
    NumberPicker numberPickerDebutPeriode;
    EditText etDatalimite;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_activity);
        ok_btn = (Button) findViewById(R.id.ok_btn_id);
        cancel_btn = (Button) findViewById(R.id.cancel_btn_id);
        tvJourDebutPeriode = (TextView) findViewById(R.id.EtDebutPeriode);
        numberPickerDebutPeriode = (NumberPicker) findViewById(R.id.numberPickerDebutPeriode) ;
        etDatalimite = (EditText)   findViewById(R.id.EtDataLimite);
        ok_btn.setOnClickListener(this);
        cancel_btn.setOnClickListener(this);
        numberPickerDebutPeriode.setMinValue(1);
        numberPickerDebutPeriode.setMaxValue(31);
        numberPickerDebutPeriode.setValue(getIntent().getIntExtra( PREF_KEY_DEBUT_CYCLE,6));
        etDatalimite.setText(String.valueOf(getIntent().getIntExtra( PREF_KEY_DATA_LIMITE_MAX,66)));
        etDatalimite.setText(String.valueOf(getIntent().getFloatExtra( PREF_KEY_DATA_LIMITE_MAX_FLOAT,67)));
        tvJourDebutPeriode.setText( String.valueOf( numberPickerDebutPeriode.getValue()));
        numberPickerDebutPeriode.setOnValueChangedListener(new NumberPicker.OnValueChangeListener()
        {
            public void onValueChange(NumberPicker picker, int oldVal, int newVal)
            {
                tvJourDebutPeriode.setText( String.valueOf( newVal));
            }



        }
        );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok_btn_id:
                Intent iOut = new Intent();
                float limite =  Float.valueOf(etDatalimite.getText().toString());
                int cycle = Integer.valueOf(tvJourDebutPeriode.getText().toString());
                float ff  = Float.valueOf( etDatalimite.getText().toString());
                iOut.putExtra(PREF_KEY_DATA_LIMITE_MAX_FLOAT, ff);
                iOut.putExtra(PREF_KEY_DEBUT_CYCLE, Integer.valueOf(tvJourDebutPeriode.getText().toString()));
                setResult(Activity.RESULT_OK, iOut);
                this.finish();
                break;
            case R.id.cancel_btn_id:
                setResult(Activity.RESULT_CANCELED);
                this.finish();
                break;
        }
    }

    void showToastMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
                .show();
    }

}
