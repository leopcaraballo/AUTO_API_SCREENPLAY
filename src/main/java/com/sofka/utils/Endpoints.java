package com.sofka.utils;

public class Endpoints {
    public static final String AUTH_LOGIN = "/api/staff/auth/login";
    public static final String RECEPTION_REGISTER = "/api/reception/register";
    public static final String CASHIER_CALL_NEXT = "/api/cashier/call-next";
    public static final String CASHIER_VALIDATE_PAYMENT = "/api/cashier/validate-payment";
    public static final String MEDICAL_ACTIVATE_ROOM = "/api/medical/consulting-room/activate";
    public static final String MEDICAL_CALL_NEXT = "/api/medical/call-next";
    public static final String MEDICAL_START_CONSULTATION = "/api/medical/start-consultation";
    public static final String MEDICAL_FINISH = "/api/medical/finish-consultation";

    public static final String TRAJECTORY_DISCOVER = "/api/patient-trajectories";
    public static final String TRAJECTORY_GET_BY_ID = "/api/patient-trajectories/{trajectoryId}";
    public static final String TRAJECTORY_REBUILD = "/api/patient-trajectories/rebuild";
}
