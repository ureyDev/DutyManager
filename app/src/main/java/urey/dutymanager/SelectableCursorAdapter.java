package urey.dutymanager;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.ListView;

/**
 * Created by urey on 16.02.2016.
 */
public class SelectableCursorAdapter extends SimpleCursorAdapter {

    private int layout;
    Context ctx;
    String[] from;
    int[] to;
    LayoutInflater lInflater;
    Cursor cursor;
    int bckgView;
    int bckgColor;
    int bckgSelectedColor;
    int txtColor;
    int txtSelectedColor;

    boolean bDutyManager=false;

    public SelectableCursorAdapter(Context _context, int _layout, Cursor _cursor, String[] _from, int[] _to,
                                   int _bckgView, int _bckgColor, int _bckgSelectedColor,
                                   int _txtColor, int _txtSelectedColor, int type) {
        super(_context, _layout, _cursor, _from, _to);
        layout = _layout;
        ctx = _context;
        from = _from;
        to = _to;
        cursor = _cursor;
        lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        bckgView = _bckgView;
        bckgColor = _bckgColor;
        bckgSelectedColor = _bckgSelectedColor;
        txtColor = _txtColor;
        txtSelectedColor = _txtSelectedColor;

        switch (type)
        {
            case 1:
               bDutyManager=true;
                break;
        }
    }

    public SelectableCursorAdapter(Context _context, int _layout, Cursor _cursor, String[] _from, int[] _to,
                                   int _bckgView, int _bckgColor, int _bckgSelectedColor,
                                   int _txtColor, int _txtSelectedColor) {
        super(_context, _layout, _cursor, _from, _to);
        layout = _layout;
        ctx = _context;
        from = _from;
        to = _to;
        cursor = _cursor;
        lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        bckgView = _bckgView;
        bckgColor = _bckgColor;
        bckgSelectedColor = _bckgSelectedColor;
        txtColor = _txtColor;
        txtSelectedColor = _txtSelectedColor;
    }

    //связывает данные с view на которые указывает курсор
    @Override
    public void bindView(View view, Context _context, Cursor _cursor) {
       // Log.d("Test", "bindView");
        for (int i = 0;i<from.length;i++) {
            String text;
            if(bDutyManager) {
                if(i==1 || i==2) {
                    text = String.format("%,.2f", _cursor.getDouble(_cursor.getColumnIndex(from[i])));
                    if(i==1)
                        text="Долг: "+text;
                    else
                        text="Внесено: "+text;
                }
                else
                    text = (_cursor.getPosition()+1)+". "+_cursor.getString(_cursor.getColumnIndex(from[i]));
            }
            else
                text = _cursor.getString(_cursor.getColumnIndex(from[i]));

            TextView tvText = (TextView) view.findViewById(to[i]);
            tvText.setText(text);
        }
    }

    //сoздаёт нвоую view для хранения данных на которую указывает курсор
    @Override
    public View newView(Context _context, Cursor _cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) _context.getSystemService(_context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(layout, parent, false);
        return view;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       // Log.d("Test", "getView");
        View result;
        View bckg;
        TextView tvText;

        if (convertView == null) {
            result =  lInflater.inflate(R.layout.item_main, parent, false);
        } else {
            result = convertView;
        }
        cursor.moveToPosition(position);


        if(bckgView!=0) {
            bckg = result.findViewById(bckgView);
            ListView list = (ListView)parent;
            if (list.getCheckedItemPosition() == position) {
                bckg.setBackgroundColor(bckgSelectedColor);
                for (int i = 0; i < to.length; i++) {
                    tvText = (TextView) result.findViewById(to[i]);
                    tvText.setTextColor(txtSelectedColor);

                }
            } else {
                if((bDutyManager && cursor.getFloat(cursor.getColumnIndex(DB.COL_CON_DUTY))<=
                        cursor.getFloat(cursor.getColumnIndex(DB.COL_CON_PAYED)))
                        )
                {
                    bckg.setBackgroundColor(Color.CYAN);
                }
                else
                    bckg.setBackgroundColor(bckgColor);

                for (int i = 0; i < to.length; i++) {
                    tvText = (TextView) result.findViewById(to[i]);
                    tvText.setTextColor(txtColor);
                }
            }
        }
        bindView(result,ctx,cursor);

        return result;
    }
}
