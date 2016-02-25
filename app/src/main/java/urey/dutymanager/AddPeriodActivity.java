package urey.dutymanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

/**
 * Created by urey on 04.02.2016.
 */
public class AddPeriodActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemSelectedListener{

    Button addperOK;
    Button addperCancel;
    String[] month = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь"
            , "Октябрь", "Ноябрь", "Декабрь"};
    String[] year = {"2016", "2017", "2018", "2019", "2020"};

    String sMonth  = "Январь";
    String sYear  = "2016";
    Spinner spinMonth,spinYear;
    EditText etNotes;
    EditText etSum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dm_screen_addper);

        etNotes = (EditText)findViewById(R.id.etSetNotes);
        etSum = (EditText)findViewById(R.id.etSetSum);

        addperOK = (Button)findViewById(R.id.addperOK);
        addperCancel = (Button)findViewById(R.id.addperCancel);

        addperCancel.setOnClickListener(this);
        addperOK.setOnClickListener(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, month);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinMonth = (Spinner) findViewById(R.id.spinSetMonth);
        spinMonth.setAdapter(adapter);
        spinMonth.setPrompt("Месяц");
        spinMonth.setSelection(0);
        spinMonth.setOnItemSelectedListener(this);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, year);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinYear = (Spinner) findViewById(R.id.spinSetYear);
        spinYear.setAdapter(adapter1);
        spinYear.setPrompt("Год");
        spinYear.setSelection(0);
        spinYear.setOnItemSelectedListener(this);

    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int position, long id) {
        switch ((int)id)
        {
            case R.id.spinSetMonth:
                sMonth = view.toString();
                break;
            case R.id.spinSetYear:
                sYear = view.toString();
                break;

        }
        sYear = spinYear.getSelectedItem().toString();
        sMonth = spinMonth.getSelectedItem().toString();


        Toast.makeText(getBaseContext(), sYear+" "+sMonth, Toast.LENGTH_SHORT).show();
    }

    public void onNothingSelected(AdapterView<?> arg0) {
    }

    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.addperOK:
                Intent intent = new Intent();
                intent.putExtra("Month", sMonth);
                intent.putExtra("Year", sYear);
                intent.putExtra("Notes",etNotes.getText().toString());
               try {
                   intent.putExtra("Sum", Double.parseDouble(etSum.getText().toString()));
               }
               catch(Exception e)
               {
                   intent.putExtra("Sum", 0);
               }

                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.addperCancel:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }

    }

}
