package com.compiladores.semantics;

import com.compiladores.models.CallModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CallManager {
    private static CallManager instance;
    private final List<CallModel> calls;

    private CallManager() {
        this.calls = new ArrayList<>();
    }

    public static CallManager getInstance() {
        if(instance == null) {
            instance = new CallManager();
        }
        return instance;
    }

    public void add(String caller, String callee, int line) {
        calls.add(new CallModel(caller, callee, line));
    }

    public Map<String,List<CallModel>> getCalls() {
        return calls.stream()
                .collect(Collectors.groupingBy(
                        CallModel::getCallerContext,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
    }

    public void reset() {
        this.calls.clear();
    }
}
