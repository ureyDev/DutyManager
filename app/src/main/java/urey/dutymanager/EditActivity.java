package urey.dutymanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by urey on 08.02.2016.
 */
public class EditActivity extends Activity implements View.OnClickListener {

    EditText etPayed, etDuty;
    TextView tvName;
    InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dm_screen_edit);

        etDuty = (EditText)findViewById(R.id.etDuty);
        etPayed = (EditText)findViewById(R.id.etPayed);
        tvName = (TextView)findViewById(R.id.tvEditName);

        Intent intent = getIntent();

        double duty = intent.getDoubleExtra("Duty", 0);
        double payed = intent.getDoubleExtra("Payed", 0);
        tvName.setText(intent.getStringExtra("Name")+"\nОстаток: "+
                String.format("%,.2f",duty-payed));

        etDuty.setText(String.valueOf(duty));
        etPayed.setText(String.valueOf(payed));

        etPayed.setSelectAllOnFocus(true);
        etDuty.setSelectAllOnFocus(true);

        /*InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        etPayed.focu
        imm.showSoftInput(etPayed, 0);*/
        etPayed.requestFocus();
       /* imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput (InputMethodManager.SHOW_FORCED, InputMethodManager.RESULT_HIDDEN);
         /*InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(etPayed, 0);*/
    }

    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btnMemOK:
                SaveAndExit();
                break;
            case R.id.btnMemCancel:
                finish();
                break;
            case R.id.btnPayAll:
                etPayed.setText(etDuty.getText().toString());
                SaveAndExit();
                break;
        }
    }

    public void SaveAndExit()
    {
        try {
            Intent intent = new Intent();
            if (etPayed.getText().toString() == "")
                intent.putExtra("Payed", 0);
            else
                intent.putExtra("Payed", Double.parseDouble(etPayed.getText().toString()));
            if (etDuty.getText().toString() == "")
                intent.putExtra("Duty", 0);
            else
                intent.putExtra("Duty", Double.parseDouble(etDuty.getText().toString()));
            setResult(RESULT_OK, intent);
            finish();
        }
        catch(Exception e){Log.d("Test", e.getMessage());
            finish();
        }
    }


    protected void onDestroy() {
        super.onDestroy();
        Log.d("Test", "onDestroy");
        imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }



}
