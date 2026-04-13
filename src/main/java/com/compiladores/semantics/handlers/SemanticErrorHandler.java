package com.compiladores.semantics.handlers;

import com.compiladores.handlers.CustomErrorHandler;
import com.compiladores.models.ErrorModel;
import com.compiladores.models.ErrorType;

public class SemanticErrorHandler extends CustomErrorHandler{

    public SemanticErrorHandler(ErrorType defaultErrorType) { super(defaultErrorType); }

    public void addSemanticError(String message, int line, int column) {
        this.errorList.add(new ErrorModel(
                this.defaultErrorType,
                line,
                column,
                message
        ));
    }
}
