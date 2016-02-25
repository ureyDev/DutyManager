package urey.dutymanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

/**
 * Created by urey on 08.02.2016.
 */
public class AddMemberActivity extends Activity implements View.OnClickListener {

    EditText etMemName, etMemPass, etMemPassConfirm;
    public final int DIALOG_NAME_EMPTY = 1;
    public final int DIALOG_PASS_INCORECT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addmember_act_screen);

        etMemName = (EditText)findViewById(R.id.etMemName);
        etMemPass = (EditText)findViewById(R.id.etMemPass);
        etMemPassConfirm = (EditText)findViewById(R.id.etMemPassConfirm);
    }

    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btnMemOK:
                if (etMemName.getText().toString()!="") {
                    if ((etMemPass.getText().toString()!="") && etMemPass.getText().toString().equals(etMemPassConfirm.getText().toString())) {
                        Intent intent = new Intent();
                        intent.putExtra("Name", etMemName.getText().toString());
                        intent.putExtra("Pass", etMemPass.getText().toString());

                        setResult(RESULT_OK, intent);
                        finish();
                    }
                    else
                    {
                        showDialog(DIALOG_PASS_INCORECT);
                        //message
                    }
                }
                else
                {
                    //message
                    showDialog(DIALOG_NAME_EMPTY);
                }
                break;
            case R.id.btnMemCancel:
                finish();
                break;
        }
    }

    protected Dialog onCreateDialog(int id) {

            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            // заголовок
            adb.setTitle(R.string.title_error);
            // сообщение
            switch (id) {
                case DIALOG_NAME_EMPTY:
                adb.setMessage(R.string.mes_name_empty);
                 break;
                case DIALOG_PASS_INCORECT:
                    adb.setMessage(R.string.mes_pass_incorrect);
                    break;
            }
            // иконка
            adb.setIcon(android.R.drawable.ic_dialog_info);
            // кнопка положительного ответа
            adb.setPositiveButton("OK",ErrorClickListener);
            // создаем диалог
            return adb.create();

       // return super.onCreateDialog(id);
    }
    DialogInterface.OnClickListener ErrorClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                // положительная кнопка
                case Dialog.BUTTON_POSITIVE:
                    break;
                // негаитвная кнопка
                case Dialog.BUTTON_NEGATIVE:
                    break;
                // нейтральная кнопка
                case Dialog.BUTTON_NEUTRAL:
                    break;
            }
        }
    };
}
