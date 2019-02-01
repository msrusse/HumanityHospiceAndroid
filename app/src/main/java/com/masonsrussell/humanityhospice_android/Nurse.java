package com.masonsrussell.humanityhospice_android;

import java.util.HashMap;

public class Nurse {

    private String id;
    private String FacetimeID;
    private String FirstName;
    private String HangoutID;
    private String LastName;
    private HashMap<String, Boolean> Patients;
    private String Team;
    private String firstName;
    private Boolean isOnCall;
    private String lastName;
    private String token;

    private Nurse() {

    }

    public Nurse(String id, String facetimeID, String firstName, String hangoutID, String lastName, HashMap<String, Boolean> patients, String team, String firstName1, Boolean isOnCall, String lastName1, String token) {
        this.id = id;
        FacetimeID = facetimeID;
        FirstName = firstName;
        HangoutID = hangoutID;
        LastName = lastName;
        Patients = patients;
        Team = team;
        this.firstName = firstName1;
        this.isOnCall = isOnCall;
        this.lastName = lastName1;
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFacetimeID() {
        return FacetimeID;
    }

    public void setFacetimeID(String facetimeID) {
        FacetimeID = facetimeID;
    }

    public String getfirstName() {
        return FirstName;
    }

    public void setfirstName(String firstName) {
        FirstName = firstName;
    }

    public Boolean getIsOnCall() {
        return isOnCall;
    }

    public void setOnCall(Boolean isOnCall) {
        isOnCall = isOnCall;
    }

    public String getHangoutID() {
        return HangoutID;
    }

    public void setHangoutID(String hangoutID) {
        HangoutID = hangoutID;
    }

    public String getlastName() {
        return LastName;
    }

    public void setlastName(String lastName) {
        LastName = lastName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public HashMap<String, Boolean> getPatients() {
        return Patients;
    }

    public void setPatients(HashMap<String, Boolean> patients) {
        Patients = patients;
    }

    public String getTeam() {
        return Team;
    }

    public void setTeam(String team) {
        Team = team;
    }
}