/*
 * Copyright (c) 2009-2012 TouchType Ltd. All Rights Reserved.
 */

package com.touchtype.chromebug;

public class ChromeBugFailedAssertion extends Exception {

    private String expected, actual;

    public ChromeBugFailedAssertion(String expected, String actual) {
        super("Failed assertion! expected: " + expected + " - actual: " + actual);
        this.expected = expected;
        this.actual = actual;
    }

    public String getExpected() {
        return expected;
    }

    public String getActual() {
        return actual;
    }
}
