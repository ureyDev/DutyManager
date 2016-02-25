package urey.dutymanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener{
    String[] periods;
    String[] addrem = {"Добавить", "Удалить", "Отмена"};
    String[] delcon = {"Да", "Нет", "Отмена"};
    final int DIALOG_ADDPERIOD = 1;
    final int DIALOG_ADDTYPE = 2;
    final int DIALOG_DELCONSIST = 3;
    final int DIALOG_ADDCONSIST = 4;
    final int DIALOG_DELMEMBER = 5;
    final int CURSOR_CONSIST = 0;
    private boolean bOK = false;
    DB db;
    int conID=0;
    SelectableCursorAdapter scConsistAdapter;
    Cursor cursorPeriods, cursorConsist, cursorChooseConsist, cursorAllMembers;
    Spinner spinPeriods;
    ListView lvConsist;
    View vHeader, vFooter;
    ImageView btnDelConsist;
    ImageView btnAddConsist;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dm_screen_main);

        Log.d("TestLog", "Start!");
        Global.iCurrentType = 0;//sic! TEST!
        // открываем подключение к БД
        db = new DB(this);
        db.open();

        btnDelConsist = (ImageView) findViewById(R.id.btnDelConsist);
        btnDelConsist.setOnClickListener(this);
        btnAddConsist = (ImageView) findViewById(R.id.btnAddConsist);
        // получаем курсор
        cursorPeriods = db.getAllPeriods();


        startManagingCursor(cursorPeriods);
        spinPeriods = (Spinner) findViewById(R.id.spinPeriods);
        lvConsist = (ListView) findViewById(R.id.lvConsist);
        lvConsist.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        RefreshPeriods();
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("Test", "onItemClick position=" + position + " id=" + id);
        lvConsist.setItemChecked(position, true);
        cursorConsist.moveToPosition(position);
        Log.d("Test", "Name=" + cursorConsist.getInt(0) + " id=" + cursorConsist.getInt(0));
    }


    public void onItemSelected(AdapterView<?> parent, View view,
                               int position, long id) {
        Log.d("TestLog", "onItemSelected");
        if (parent.getClass() == lvConsist.getClass()) {
        } else {
            if(cursorPeriods.getCount()>0) {
                Log.d("TestLog", "cursorPeriods.moveToPosition(position);");
                cursorPeriods.moveToPosition(position);
                RefreshConsist(cursorPeriods.getInt(0), 0);
            }
            else
            {
                RefreshConsist(0,0);
            }

        }
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        if (arg0.getClass() == lvConsist.getClass()) {
            Log.d("TestLog", "lvConsist onNothingSelected ID=" + cursorConsist.getInt(0));
        }
    }

    public void onClick(View v) {
        Log.d("TestLog", "onClick start");
        switch (v.getId()) {
            case R.id.btnAddPeriod:
                showDialog(DIALOG_ADDPERIOD);
                break;
            case R.id.btnAddMember:
                Log.d("TestLog", "btnAddMember start");
                Intent intent = new Intent(this, AddMemberActivity.class);
                startActivityForResult(intent, 2);
                Log.d("TestLog", "btnAddMember end");
                break;
            case R.id.btnDelConsist:
                if(cursorConsist.getCount()>0)
                    showDialog(DIALOG_DELCONSIST);
                break;
            case R.id.btnAddConsist:
                showDialog(DIALOG_ADDCONSIST);
                break;
            case R.id.btnEdit:
                if(cursorConsist.getCount()>0) {
                    Log.d("TestLog", "btnEdit start");
                    cursorConsist.moveToPosition(lvConsist.getCheckedItemPosition());
                    Intent intent1 = new Intent(this, EditActivity.class);
                    intent1.putExtra("Name",cursorConsist.getString(1));
                    intent1.putExtra("Duty",cursorConsist.getDouble(cursorConsist.getColumnIndex(DB.COL_CON_DUTY)));
                    intent1.putExtra("Payed",cursorConsist.getDouble(cursorConsist.getColumnIndex(DB.COL_CON_PAYED)));
                    conID = cursorConsist.getInt(0);
                    startActivityForResult(intent1, 3);
                    Log.d("TestLog", "btnEdit end");
                }
                break;
            case R.id.btnExit:
                finish();
                break;
            case R.id.btnDelMember:
                showDialog(DIALOG_DELMEMBER);
                break;
        }



        Log.d("TestLog", "onClick end");
    }

    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        switch (id) {
            case DIALOG_ADDPERIOD:
                adb.setTitle(R.string.title_choose);
                adb.setItems(addrem, mainDialogListener);
                break;
            case DIALOG_DELCONSIST:
                adb.setTitle("Предупреждение!");
                cursorConsist.moveToPosition(lvConsist.getCheckedItemPosition());
                adb.setMessage("Удалить участника " + cursorConsist.getString(1) + "?");
                adb.setIcon(android.R.drawable.ic_dialog_info);
                adb.setPositiveButton("Да", delConDialogListener);
                adb.setNegativeButton("Нет", delConDialogListener);
                break;
            case DIALOG_ADDCONSIST:
                Log.d("TestLog", "getChooseConsist start");
                cursorPeriods.moveToPosition(spinPeriods.getSelectedItemPosition());
                if (cursorConsist != null && cursorConsist.getCount() > 0)
                    cursorChooseConsist = db.getChooseConsist(cursorPeriods.getInt(0));
                else
                    cursorChooseConsist = db.getChooseConsist(0);
                Log.d("TestLog", "getChooseConsist end");
                int count = cursorChooseConsist.getCount();
                String[] data = new String[count];
                boolean[] chkd = new boolean[count];
                cursorChooseConsist.moveToFirst();
                for (int i = 0; i < count; i++) {
                    data[i] = cursorChooseConsist.getString(1);
                    chkd[i] = false;
                    cursorChooseConsist.moveToNext();
                }
                adb.setTitle("Имена");
                adb.setMultiChoiceItems(data, chkd, null);
                adb.setPositiveButton("OK", ChooseConBtnClickListener);
                adb.setNegativeButton("Отмена", ChooseConBtnClickListener);
                break;
            case DIALOG_DELMEMBER:
                Log.d("TestLog", "btnDelMember start");
                cursorAllMembers = db.getAllMembers();
                int count1 = cursorAllMembers.getCount();
                String[] data1 = new String[count1];
                boolean[] chkd1 = new boolean[count1];
                cursorAllMembers.moveToFirst();
                for (int i = 0; i < count1; i++) {
                    data1[i] = cursorAllMembers.getString(1);
                    chkd1[i] = false;
                    cursorAllMembers.moveToNext();
                }
                adb.setTitle("Имена");
                adb.setMultiChoiceItems(data1, chkd1, null);
                adb.setPositiveButton("OK", DelMemClickListener);
                adb.setNegativeButton("Отмена", DelMemClickListener);
                break;

        }
        return adb.create();
    }

    // обработчик нажатия на кнопку
    DialogInterface.OnClickListener ChooseConBtnClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            Log.d("qwe", "which: " + which);
            if (which == -1) {
                SparseBooleanArray sbArray = ((AlertDialog) dialog).getListView().getCheckedItemPositions();
                for (int i = 0; i < sbArray.size(); i++) {
                    int key = sbArray.keyAt(i);
                    if (sbArray.get(key)) {
                        Log.d("qwe", "checked: " + key);
                        cursorChooseConsist.moveToPosition(key);
                        InsertConsist(cursorPeriods.getInt(0), cursorChooseConsist.getInt(0), cursorPeriods.getDouble(3), 0);
                    }
                }
            }
            removeDialog(DIALOG_ADDCONSIST);
        }
    };

    DialogInterface.OnClickListener DelMemClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            Log.d("qwe", "which: " + which);
            if (which == -1) {
                SparseBooleanArray sbArray = ((AlertDialog) dialog).getListView().getCheckedItemPositions();
                for (int i = 0; i < sbArray.size(); i++) {
                    int key = sbArray.keyAt(i);
                    if (sbArray.get(key)) {
                        Log.d("qwe", "checked: " + key);
                        cursorAllMembers.moveToPosition(key);
                        DelMember(cursorAllMembers.getInt(0));
                    }
                }
            }
            removeDialog(DIALOG_DELMEMBER);
        }
    };

    // обработчик нажатия на пункт списка диалога
    DialogInterface.OnClickListener mainDialogListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {

            if (cursorPeriods.getCount() > 0) {
                cursorPeriods.moveToPosition(spinPeriods.getSelectedItemPosition());
                Global.iCurrentPeriod = cursorPeriods.getInt(0);
            } else {
                Global.iCurrentPeriod = 0;
            }

            switch (which) {
                case 0:
                    AddPeriod();
                    break;
                case 1:
                    if (cursorPeriods.getCount() > 0) {
                        int index = spinPeriods.getSelectedItemPosition();
                        cursorPeriods.moveToPosition(index);
                        DelPeriod(cursorPeriods.getInt(0));
                    }
                    break;
            }
            Log.d("TestLog", "which = " + which);
        }
    };

    DialogInterface.OnClickListener delConDialogListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            if (cursorConsist.getCount() > 0) {
                if (which == Dialog.BUTTON_POSITIVE) {
                    cursorConsist.moveToPosition(lvConsist.getCheckedItemPosition());
                    db.delRec(DB.DB_TABLE_CONSIST, cursorConsist.getInt(0));
                    cursorConsist.requery();
                    if(cursorConsist.getCount()>0) {
                        cursorConsist.moveToFirst();
                    }
                    cursorPeriods.moveToPosition(spinPeriods.getSelectedItemPosition());
                    RefreshConsist(cursorPeriods.getInt(0), 0);
                }
            }
            removeDialog(DIALOG_DELCONSIST);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("TestLog", "onActivityResult start");
        Log.d("TestLog", "requestCode=" + requestCode);
        if (resultCode == RESULT_OK) {
            Log.d("TestLog", "resultCode=OK");
            switch (requestCode) {

                case 1:
                    if (data == null) {
                        return;
                    }
                    String sMonth = data.getStringExtra("Month");
                    String sYear = data.getStringExtra("Year");
                    String sNotes = data.getStringExtra("Notes");
                    double dSum = data.getDoubleExtra("Sum", 0);
                    InsertPeriod(sYear, sMonth, sNotes, dSum, Global.iCurrentType);
                    break;
                case 2:
                    if (data == null) {
                        return;
                    }
                    String sName = data.getStringExtra("Name");
                    String sPass = data.getStringExtra("Pass");
                    InsertMember(sName, sPass, 0);
                    break;
                case 3:
                    double dPayed = data.getDoubleExtra("Payed",0);
                    double dDuty = data.getDoubleExtra("Duty",0);
                    UpdateConsist(dDuty,dPayed);

                    break;
            }
        }
    }

    //===========КОНЕЦ ОБРАБОТЧИКОВ====МОИ ФУНКЦИИ===========================================================================

    public void RefreshPeriods() {
        Log.d("TestLog", "RefreshPeriods start");
        cursorPeriods.requery();
        Log.d("TestLog", "cursorPeriods.requery();");
        Log.d("TestLog", "cursorPeriods.getCount()=" + cursorPeriods.getCount());
        ArrayAdapter<String> adapter;
        if (cursorPeriods.getCount() > 0) {
            btnAddConsist.setEnabled(true);
            List<String> periods = new ArrayList<String>();
            cursorPeriods.moveToFirst();
            while (true) {
                periods.add(cursorPeriods.getString(2) + " " + cursorPeriods.getString(1) +
                        " " + cursorPeriods.getString(4));

                if (!cursorPeriods.moveToNext()) {
                    cursorPeriods.moveToPosition(cursorPeriods.getCount() - 1);
                    break;
                }
            }
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                    periods);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinPeriods.setAdapter(adapter);
            spinPeriods.setPrompt("Title");
            spinPeriods.setSelection(periods.size() - 1);
            spinPeriods.setOnItemSelectedListener(this);

        } else {
            btnAddConsist.setEnabled(false);
            RefreshConsist(0, 0);
            String[] periods = new String[]{" "};
            Log.d("TestLog", "if (cursorPeriods.getCount()>0)  else");
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, periods);
            Log.d("TestLog", "ArrayAdapter<String> adapter = new");
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Log.d("TestLog", "adapter.setDropDownViewResource");
            spinPeriods.setAdapter(adapter);
            Log.d("TestLog", "spinPeriods.setAdapter(adapter);");
        }
        Log.d("TestLog", "RefreshPeriods end");
    }

    public void RefreshConsist(int period, int type) {
        Log.d("TestLog", "RefreshConsist start");
        //btnDelConsist.setEnabled(false);
        cursorConsist = db.getConsist(period, type);
        Log.d("TestLog", "cursorConsist.getCount()=" + cursorConsist.getCount());
        if (cursorConsist.getCount() > 0) {
            String[] s = cursorConsist.getColumnNames();
            for (int i = 0; i < s.length; i++) {
                Log.d("TestLog", s[i]);
            }
            startManagingCursor(cursorConsist);
            // формируем столбцы сопоставления
            String[] from = new String[]{DB.COL_MEM_NAME, DB.COL_CON_DUTY, DB.COL_CON_PAYED};
            int[] to = new int[]{R.id.tvConName, R.id.tvConDuty, R.id.tvConPayed};

            // создааем адаптер и настраиваем список
            scConsistAdapter = new SelectableCursorAdapter(this, R.layout.item_main, cursorConsist, from, to,
                    R.id.item_bckg, Color.GRAY, Color.BLACK, Color.BLACK, Color.WHITE,1);

            lvConsist.setAdapter(scConsistAdapter);
            lvConsist.setOnItemClickListener(this);

            lvConsist.setOnItemSelectedListener(this);
            lvConsist.setItemsCanFocus(true);
            lvConsist.setItemChecked(0,true);

        }else {
            lvConsist.setAdapter(null);
            lvConsist.setOnItemClickListener(null);
            lvConsist.setOnItemSelectedListener(null);
        }
    }

    public void AddPeriod() {
        Log.d("TestLog", "AddPeriod start");
        Intent intent = new Intent(this, AddPeriodActivity.class);
        startActivityForResult(intent, 1);
        Log.d("TestLog", "AddPeriod end");
    }

    public void DelPeriod(int index) {
        Log.d("TestLog", "DelPeriod start");
        if (cursorPeriods.getCount() > 0) {
            db.delRec(DB.DB_TABLE_PERIODS, index);
            RefreshPeriods();
        }
        Log.d("TestLog", "DelPeriod end");
    }

    public void DelMember(int index) {
        Log.d("TestLog", "DelMember start");
        if (cursorAllMembers.getCount() > 0) {
            db.delRec(DB.DB_TABLE_MEMBERS, index);
            RefreshPeriods();
        }
        Log.d("TestLog", "DelMember end");
    }

    public void InsertPeriod(String year, String month, String notes, double sum, int type) {
        Log.d("TestLog", "InsertPeriod start");
        db.addPeriod(year, month, sum, notes, type);
        Log.d("TestLog", "InsertPeriod end");
        RefreshPeriods();
    }

    public void InsertMember(String Name, String Pass, int Flags) {
        Log.d("TestLog", "InsertMember start");
        db.addMember(Name, Pass, Flags);
        Log.d("TestLog", "InsertMember end");
        // RefreshPeriods();
    }

    public void InsertConsist(int period, int member, double duty, double payed) {
        Log.d("TestLog", "InsertConsist start");
        db.addConsist(period, member, duty, payed);
        Log.d("TestLog", "InsertConsist end");
        RefreshConsist(period, 0);
    }

    public void UpdateConsist(double duty, double payed) {
        Log.d("TestLog", "UpdateConsist start");
        cursorConsist.moveToPosition(lvConsist.getCheckedItemPosition());
        Log.d("TestLog", "getInt start");
        //int id = cursorConsist.getInt(cursorConsist.getColumnIndex(DB.COL_ID));
        Log.d("TestLog", "UpConsist start");
        db.UpConsist(conID, duty, payed);
        Log.d("TestLog", "UpdateConsist end");
        //RefreshConsist(period, 0);
    }

    protected void onDestroy() {
        super.onDestroy();
        // закрываем подключение при выходе
        db.close();
    }

}
