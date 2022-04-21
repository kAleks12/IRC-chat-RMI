package com.comunnication.rmi_interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIServer extends Remote {
    String nicknameCheck(String nickname, RMIClient client) throws RemoteException;
    String generateChannelList() throws  RemoteException;
    String assignUserToChannel(String nickname, String channelName ) throws RemoteException;
    void sendChannelMessage(String nickname, String messageBody) throws RemoteException;
    void disconnectUser(String nickname) throws RemoteException;
    void deleteUser(String nickname) throws RemoteException;
}
