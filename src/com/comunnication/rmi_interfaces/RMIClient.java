package com.comunnication.rmi_interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIClient extends Remote {
    void displayMessage(String message) throws RemoteException;
    void receiveContextualAreaUpdate(String text) throws RemoteException;
    void updateContextualLabel(String text) throws RemoteException;
}
