/*
 * Copyright (c) 2009-2012 TouchType Ltd. All Rights Reserved.
 */

package com.touchtype.chromebug;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class BugExposedActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bug_exposed);

        handleExpectedActual();
        handleInputSequence();
    }

    private void handleExpectedActual() {
        Intent intent = getIntent();

        String expected = intent.getStringExtra("expected");
        String actual = intent.getStringExtra("actual");

        String templateExpectedActual = getString(R.string.expected_actual);
        TextView tvExpectedActual = (TextView) findViewById(R.id.expected_actual);
        tvExpectedActual.setText(String.format(templateExpectedActual, expected, actual));
    }

    private void handleInputSequence() {
        Intent intent = getIntent();

        ArrayList<String> sequence = intent.getStringArrayListExtra("sequence");
        ListView lvSequence = (ListView) findViewById(R.id.input_sequence);
        lvSequence.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, sequence));
    }
}
