package com.sofka.models;

public class RegisterPatientRequest {
    private String queueId;
    private String patientId;
    private String patientName;
    private String appointmentReference;
    private String priority;
    private String notes;

    public RegisterPatientRequest() {}

    public RegisterPatientRequest(String queueId, String patientId, String patientName, String appointmentReference, String priority, String notes) {
        this.queueId = queueId;
        this.patientId = patientId;
        this.patientName = patientName;
        this.appointmentReference = appointmentReference;
        this.priority = priority;
        this.notes = notes;
    }

    public String getQueueId() { return queueId; }
    public void setQueueId(String queueId) { this.queueId = queueId; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    public String getAppointmentReference() { return appointmentReference; }
    public void setAppointmentReference(String appointmentReference) { this.appointmentReference = appointmentReference; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public static RegisterPatientRequestBuilder builder() { return new RegisterPatientRequestBuilder(); }

    public static class RegisterPatientRequestBuilder {
        private String queueId;
        private String patientId;
        private String patientName;
        private String appointmentReference;
        private String priority;
        private String notes;
        public RegisterPatientRequestBuilder queueId(String queueId) { this.queueId = queueId; return this; }
        public RegisterPatientRequestBuilder patientId(String patientId) { this.patientId = patientId; return this; }
        public RegisterPatientRequestBuilder patientName(String patientName) { this.patientName = patientName; return this; }
        public RegisterPatientRequestBuilder appointmentReference(String appointmentReference) { this.appointmentReference = appointmentReference; return this; }
        public RegisterPatientRequestBuilder priority(String priority) { this.priority = priority; return this; }
        public RegisterPatientRequestBuilder notes(String notes) { this.notes = notes; return this; }
        public RegisterPatientRequest build() { return new RegisterPatientRequest(queueId, patientId, patientName, appointmentReference, priority, notes); }
    }
}
