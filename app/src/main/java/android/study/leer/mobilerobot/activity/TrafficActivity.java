package android.study.leer.mobilerobot.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.study.leer.mobilerobot.R;
import android.study.leer.mobilerobot.utils.ContantValues;

/**
 * Created by Leer on 2017/3/16.
 */
public class TrafficActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic);
    }
    public static Intent newIntent(Context context){
        Intent i = new Intent(context,TrafficActivity.class);
        return i;
    }
}
