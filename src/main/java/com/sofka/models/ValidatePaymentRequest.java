package com.sofka.models;

public class ValidatePaymentRequest {
    private String turnId;
    private String queueId;
    private String patientId;
    private String paymentReference;
    private double validatedAmount;

    public ValidatePaymentRequest() {}

    public ValidatePaymentRequest(String turnId, String queueId, String patientId, String paymentReference, double validatedAmount) {
        this.turnId = turnId;
        this.queueId = queueId;
        this.patientId = patientId;
        this.paymentReference = paymentReference;
        this.validatedAmount = validatedAmount;
    }

    public String getTurnId() { return turnId; }
    public void setTurnId(String turnId) { this.turnId = turnId; }
    public String getQueueId() { return queueId; }
    public void setQueueId(String queueId) { this.queueId = queueId; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getPaymentReference() { return paymentReference; }
    public void setPaymentReference(String paymentReference) { this.paymentReference = paymentReference; }
    public double getValidatedAmount() { return validatedAmount; }
    public void setValidatedAmount(double validatedAmount) { this.validatedAmount = validatedAmount; }

    public static ValidatePaymentRequestBuilder builder() { return new ValidatePaymentRequestBuilder(); }

    public static class ValidatePaymentRequestBuilder {
        private String turnId;
        private String queueId;
        private String patientId;
        private String paymentReference;
        private double validatedAmount;
        public ValidatePaymentRequestBuilder turnId(String turnId) { this.turnId = turnId; return this; }
        public ValidatePaymentRequestBuilder queueId(String queueId) { this.queueId = queueId; return this; }
        public ValidatePaymentRequestBuilder patientId(String patientId) { this.patientId = patientId; return this; }
        public ValidatePaymentRequestBuilder paymentReference(String paymentReference) { this.paymentReference = paymentReference; return this; }
        public ValidatePaymentRequestBuilder validatedAmount(double validatedAmount) { this.validatedAmount = validatedAmount; return this; }
        public ValidatePaymentRequest build() { return new ValidatePaymentRequest(turnId, queueId, patientId, paymentReference, validatedAmount); }
    }
}
