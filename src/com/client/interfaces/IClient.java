package com.client.interfaces;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public interface IClient {
    void setHost(String host);
    void setServerPort(String serverPort);
    void startConnection() throws RemoteException, NotBoundException;
    void negotiateNickname(String nickname) throws IOException;
    void updateChannelList() throws IOException ;
    void joinChannel(String channelName) throws IOException;
    void sendMessageToChannel(String message) throws IOException ;
    void terminateConnection() throws IOException ;
    void disconnectFromChannel() throws IOException;
}