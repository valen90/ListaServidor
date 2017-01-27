package com.example.valen.listaservidor;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by Valen on 27/01/2017.
 */

public class main_preferences extends PreferenceActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferencias);
    }
}
