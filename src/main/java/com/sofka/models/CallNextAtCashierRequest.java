package com.sofka.models;

public class CallNextAtCashierRequest {
    private String queueId;
    private String cashierStationId;

    public CallNextAtCashierRequest() {}

    public CallNextAtCashierRequest(String queueId, String cashierStationId) {
        this.queueId = queueId;
        this.cashierStationId = cashierStationId;
    }

    public String getQueueId() { return queueId; }
    public void setQueueId(String queueId) { this.queueId = queueId; }
    public String getCashierStationId() { return cashierStationId; }
    public void setCashierStationId(String cashierStationId) { this.cashierStationId = cashierStationId; }

    public static CallNextAtCashierRequestBuilder builder() { return new CallNextAtCashierRequestBuilder(); }

    public static class CallNextAtCashierRequestBuilder {
        private String queueId;
        private String cashierStationId;
        public CallNextAtCashierRequestBuilder queueId(String queueId) { this.queueId = queueId; return this; }
        public CallNextAtCashierRequestBuilder cashierStationId(String cashierStationId) { this.cashierStationId = cashierStationId; return this; }
        public CallNextAtCashierRequest build() { return new CallNextAtCashierRequest(queueId, cashierStationId); }
    }
}
