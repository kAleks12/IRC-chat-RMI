package com.client;

import com.client.gui.ClientFrame;
import com.client.services.Client;

import java.rmi.RemoteException;

public class ClientLauncher {
    public static void main(String[] args) throws RemoteException {
        Client client = new Client();
        ClientFrame frame = new ClientFrame(client);
        frame.addTextToOutputArea("Please enter host name of the server you would like to join! (probably \"localhost\")");
        client.setFrame(frame);
    }
}