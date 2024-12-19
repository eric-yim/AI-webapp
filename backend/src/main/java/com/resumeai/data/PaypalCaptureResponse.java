package com.resumeai.data;

import java.util.List;
import java.util.Map;

public class PaypalCaptureResponse {
    private boolean error;
    private int statusCode;
    private Map<String, Object> body;
    private String passthrough;
    private TransactionUpdate transactionUpdate;

    // success
    public PaypalCaptureResponse(TransactionUpdate transactionUpdate, String passthrough, int statusCode) {
        this.statusCode = statusCode;
        this.error = false;
        this.transactionUpdate = transactionUpdate;
        this.passthrough = passthrough;
    }

    public PaypalCaptureResponse(Map<String, Object> body, int statusCode) {
        this.statusCode = statusCode;
        this.error = true;
        this.body = body;
    }

    public boolean hasError() {
        return error;
    }

    public Map<String, Object> getBody() {
        return body;
    }

    public TransactionUpdate getTransactionUpdate() {
        return transactionUpdate;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getPassthrough() {
        return passthrough;
    }
}