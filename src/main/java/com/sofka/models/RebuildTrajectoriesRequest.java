package com.sofka.models;

public class RebuildTrajectoriesRequest {
    private String queueId;
    private String patientId;
    private boolean dryRun;

    public RebuildTrajectoriesRequest() {}

    public RebuildTrajectoriesRequest(String queueId, String patientId, boolean dryRun) {
        this.queueId = queueId;
        this.patientId = patientId;
        this.dryRun = dryRun;
    }

    public String getQueueId() { return queueId; }
    public void setQueueId(String queueId) { this.queueId = queueId; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public boolean isDryRun() { return dryRun; }
    public void setDryRun(boolean dryRun) { this.dryRun = dryRun; }

    public static RebuildTrajectoriesRequestBuilder builder() {
        return new RebuildTrajectoriesRequestBuilder();
    }

    public static class RebuildTrajectoriesRequestBuilder {
        private String queueId;
        private String patientId;
        private boolean dryRun;

        public RebuildTrajectoriesRequestBuilder queueId(String queueId) { this.queueId = queueId; return this; }
        public RebuildTrajectoriesRequestBuilder patientId(String patientId) { this.patientId = patientId; return this; }
        public RebuildTrajectoriesRequestBuilder dryRun(boolean dryRun) { this.dryRun = dryRun; return this; }

        public RebuildTrajectoriesRequest build() {
            return new RebuildTrajectoriesRequest(queueId, patientId, dryRun);
        }
    }
}
