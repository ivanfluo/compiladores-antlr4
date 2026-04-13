package com.compiladores.models;

import com.compiladores.io.IReportable;

import java.util.Arrays;
import java.util.List;

public class CallModel implements IReportable {
    private String callerContext;
    private String calleeName;
    private int line;

    public CallModel(String callerContext, String calleeName, int line) {
        this.callerContext = callerContext;
        this.calleeName = calleeName;
        this.line = line;
    }

    @Override
    public List<String> getHeaders() { return Arrays.asList("funcion invocada","linea"); }

    @Override
    public List<String> toRow() {
        return Arrays.asList(
            this.calleeName,
            Integer.toString(this.line)
        );
    }

    public String getCallerContext() { return callerContext; }
}
