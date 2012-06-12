/*
 * Copyright (c) 2009-2012 TouchType Ltd. All Rights Reserved.
 */

package com.touchtype.chromebug;

import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.InputMethodService;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class ChromeBugInputMethodService extends InputMethodService {

    private static final String TAG = ChromeBugInputMethodService.class.getSimpleName();
    private Button startButton;

    @Override
    public View onCreateInputView() {
        View view = getLayoutInflater().inflate(R.layout.chromebug, null);
        startButton = (Button) view.findViewById(R.id.start);
        Button changeButton = (Button) view.findViewById(R.id.change);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startButton.setEnabled(false);
                startInputSequence();
            }
        });

        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputMethodPicker();
            }
        });
        return view;
    }

    private void startInputSequence() {
        new BugExposingSequence().run();
    }

    private void showInputMethodPicker() {
        InputMethodManager imeManager = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
        if (imeManager != null) {
            imeManager.showInputMethodPicker();
        }
    }

    @Override
    public LoggingInputConnection getCurrentInputConnection() {
        final InputConnection conn = super.getCurrentInputConnection();
        if (conn == null) {
            return null;
        }

        return new LoggingInputConnection(conn);
    }

    private class BugExposingSequence implements Runnable {

        private static final int RETRIES = 1000;

        private int count = RETRIES;
        private Handler handler = new Handler();

        @Override
        public void run() {
            LoggingInputConnection lic = getCurrentInputConnection();
            if (lic != null) {
                if (count % 100 == 0)
                    Log.d(TAG, "Test " + (RETRIES - count) + "...");
                try {
                    lic.reset();

                    lic.beginBatchEdit();
                    lic.setComposingText("I", 1);
                    lic.finishComposingText();
                    lic.commitText(" ", 1);
                    lic.endBatchEdit();
                    assertExtractedText("I ");

                    lic.beginBatchEdit();
                    lic.setComposingText("a", 1);
                    lic.endBatchEdit();
                    assertExtractedText("I a");

                    lic.beginBatchEdit();
                    lic.setComposingText("am", 1);
                    lic.endBatchEdit();
                    assertExtractedText("I am");

                    lic.beginBatchEdit();
                    lic.setComposingText("am", 1);
                    lic.finishComposingText();
                    lic.commitText(" ", 1);
                    lic.endBatchEdit();
                    assertExtractedText("I am ");

                    schedule();
                } catch (ChromeBugFailedAssertion e) {
                    String expected = e.getExpected();
                    String actual = e.getActual();
                    ArrayList<String> inputSequence = lic.getInputSequence();

                    Log.d(TAG, "Expected input '" + expected + "' differs from actual '" + actual
                            + "' \n\nThe sequence that exposed the bug is:\n " + inputSequence);

                    launchDetailsActivity(expected, actual, inputSequence);

                    startButton.setEnabled(true);
                }
            } else {
                startButton.setEnabled(true);
            }
        }

        private void launchDetailsActivity(String expected, String actual, ArrayList<String> inputSequence) {
            Intent intent = new Intent(getApplicationContext(), BugExposedActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("expected", expected);
            intent.putExtra("actual", actual);
            intent.putStringArrayListExtra("sequence", inputSequence);
            startActivity(intent);
        }

        public void schedule() {
            if (count-- > 0) {
                handler.post(this);
            } else {
                makeToast(R.string.bug_not_exposed);
                startButton.setEnabled(true);
            }
        }

        private void assertExtractedText(String expected) throws ChromeBugFailedAssertion {
            InputConnection lic = getCurrentInputConnection();
            if (lic != null) {
                ExtractedText extractedText = lic.getExtractedText(new ExtractedTextRequest(), 0);
                CharSequence seq = extractedText != null ? extractedText.text : null;
                String actual = seq != null ? seq.toString() : "";
                if (!expected.equals(actual)) {
                    throw new ChromeBugFailedAssertion(expected, actual);
                }
            }
        }
    }

    private void makeToast(int stringId) {
        Context context = this.getApplicationContext();
        Toast.makeText(context, context.getString(stringId), Toast.LENGTH_LONG).show();
    }
}