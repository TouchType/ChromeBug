/*
 * Copyright (c) 2009-2012 TouchType Ltd. All Rights Reserved.
 */

package com.touchtype.chromebug;

import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class LoggingInputConnection extends InputConnectionWrapper {

    private final InputConnection conn;

    private List<String> inputSequence = new ArrayList<String>();

    public LoggingInputConnection(InputConnection conn) {
        super(conn, true);
        this.conn = conn;
    }

    public void reset() {
        conn.deleteSurroundingText(10, 0);
        inputSequence = new ArrayList<String>();
    }

    public ArrayList<String> getInputSequence() {
        return new ArrayList<String>(inputSequence);
    }

    @Override
    public boolean setComposingText(CharSequence charSequence, int i) {
        inputSequence.add("setComposingText(\"" + charSequence + "\"," + i + ")");
        return conn.setComposingText(charSequence, i);
    }

    @Override
    public boolean finishComposingText() {
        inputSequence.add("finishComposingText()");
        return conn.finishComposingText();
    }

    @Override
    public boolean commitText(CharSequence charSequence, int i) {
        inputSequence.add("commitText(\"" + charSequence + "\"," + i + ")");
        return conn.commitText(charSequence, i);
    }

    @Override
    public boolean beginBatchEdit() {
        inputSequence.add("beginBatchEdit()");
        return conn.beginBatchEdit();
    }

    @Override
    public boolean endBatchEdit() {
        inputSequence.add("endBatchEdit()");
        return conn.endBatchEdit();
    }
}
