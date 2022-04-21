package com.server.models;

import com.comunnication.rmi_interfaces.RMIClient;


public class User {
    private final String userName;
    private String currChannel;
    private RMIClient client;


    public User(String name, RMIClient client) {
        this.userName = name;
        this.client = client;
    }

    public RMIClient getClient() {
        return client;
    }

    public String getCurrChannel() { return currChannel; }

    public void setCurrChannel(String currChannel) { this.currChannel = currChannel; }

    public String getName(){ return this.userName; }

}