package com.example.mp1tipcalculator;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import java.text.NumberFormat;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
    private static final NumberFormat percentFormat = NumberFormat.getPercentInstance();
    private String beforeTextChanged= "";
    private String onTextChanged= "";
    private double percent = 0.15;
    private double billAmount = 0.0;
    private TextView percentView;
    private TextView amountTextView;
    private TextView tipTextView;
    private TextView totalTextView;
    private EditText editTxt;
    private TextView totalPPer;
    private boolean radioTotal;
    private boolean radioTip;
    Toolbar toolbar;
    public int spinnerLabel = 1;
    private static final String SAVED_VAL = "safe";
    private static final String SAVED_VAL2 = "safe2";
    private static final String SAVED_VAL3 = "safe3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Spinner spinner = findViewById(R.id.spinner);
        if (spinner != null) { spinner.setOnItemSelectedListener(this);
        }
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinner_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if(spinner != null) { spinner.setAdapter(adapter);
        }

        editTxt = findViewById(R.id.editEnterAmount);
        percentView = findViewById(R.id.percentTextView);
        amountTextView = findViewById(R.id.amountTextView);
        tipTextView = findViewById(R.id.tipTextView);
        totalTextView = findViewById(R.id.totalTextView);
        totalPPer = findViewById(R.id.totalPerPersonView);
        tipTextView.setText(currencyFormat.format(0));
        totalTextView.setText(currencyFormat.format(0));
        totalPPer.setText(currencyFormat.format(0));
        SeekBar percentSeekBar = findViewById(R.id.percentSeekBar);
        percentSeekBar.setOnSeekBarChangeListener(seekBarListener);

        if(savedInstanceState != null){
            boolean b1 = savedInstanceState.getBoolean(SAVED_VAL);
            boolean b2 = savedInstanceState.getBoolean(SAVED_VAL2);
            int i1 = savedInstanceState.getInt(SAVED_VAL3);
            radioTip = b1;
            radioTotal = b2;
            spinnerLabel = i1;
        }

        editTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                onTextChanged = editTxt.getText().toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                beforeTextChanged = editTxt.getText().toString();
                if(!beforeTextChanged.equals("")) {
                    billAmount = Double.parseDouble(s.toString());
                    calculateBill();
                }
                if(beforeTextChanged.equals("")){
                    tipTextView.setText(currencyFormat.format(0));
                    totalTextView.setText(currencyFormat.format(0));
                    totalPPer.setText(currencyFormat.format(0));
                } }

            @Override
            public void afterTextChanged(Editable s) {
            }});
    }

    private void calculateBill() {
        percentView.setText(percentFormat.format(percent));
        double tip = billAmount * percent;
        if(radioTip){
            tip = Math.ceil(tip);
            tipTextView.setText(currencyFormat.format(tip));
        }
        double total = billAmount + tip;
        tipTextView.setText(currencyFormat.format(tip));
        totalTextView.setText(currencyFormat.format(total));
        totalPPer.setText(currencyFormat.format(total/spinnerLabel));
        if(radioTotal){
            totalTextView.setText(currencyFormat.format(Math.ceil(total)));
            totalPPer.setText(currencyFormat.format(Math.ceil(total/spinnerLabel)));
        }
    }

    private final OnSeekBarChangeListener seekBarListener = new OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
            percent = progress / 100.0;
            calculateBill();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.info:
                AlertDialog.Builder alertInfo = new AlertDialog.Builder(MainActivity.this);
                alertInfo.setTitle("Spinner Info");
                alertInfo.setMessage("The spinner is used to split the bill among friends.");
                alertInfo.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }); alertInfo.show();
                return true;
            case R.id.share:
                Uri uri = Uri.parse("smsto:7777777777");
                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                intent.putExtra("sms_body", "Bill: $"+billAmount+ ", Tip: "+tipTextView.getText()+", Total: "+totalTextView.getText()+", Total: "+totalPPer.getText()+" for "
                        +spinnerLabel+" people.");
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

//Spinner Methods
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
         spinnerLabel = Integer.valueOf(parent.getItemAtPosition(position).toString());
         calculateBill();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    //Radio Method
    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton)view).isChecked();
        switch (view.getId()){
            case R.id.rNo:
                if(checked){
                    radioTotal = false;
                    radioTip = false;
                    calculateBill();
                }
                break;
            case R.id.rTip:
                if(checked){
                    radioTip = true;
                    radioTotal = false;
                    calculateBill();
                }
                break;
            case R.id.rTotal:
                if(checked){
                    radioTotal = true;
                    radioTip = false;
                    calculateBill();
                }
                break;
            default:
                radioTotal = false;
                radioTip = false;
                break;
        }
    }

    public void onSaveInstanceState(Bundle outState){
        outState.putBoolean(SAVED_VAL, radioTip);
        outState.putBoolean(SAVED_VAL2, radioTotal);
        outState.putInt(SAVED_VAL3, spinnerLabel);
        super.onSaveInstanceState(outState);
    }
}