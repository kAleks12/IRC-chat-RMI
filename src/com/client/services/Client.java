package com.client.services;


import com.client.enums.ClientState;
import com.client.interfaces.IClient;
import com.client.interfaces.IClientFrame;
import com.comunnication.flagsresponses.Flags;
import com.comunnication.flagsresponses.ServerResponse;
import com.comunnication.rmi_interfaces.RMIClient;
import com.comunnication.rmi_interfaces.RMIServer;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Client implements IClient, RMIClient {
    private String host;
    private int serverPort;
    private String nickname;
    IClientFrame clientFrame;
    private RMIServer server;

    public Client() throws RemoteException {
        UnicastRemoteObject.exportObject(this, 0);
    }

    public void setFrame(IClientFrame clientFrame) {
        this.clientFrame = clientFrame;
    }

    @Override
    public void setHost(String host) {
        this.host = host;

        clientFrame.changeState(ClientState.SetPort);
        clientFrame.clearOutputArea();
        clientFrame.addTextToOutputArea("Enter port of the server! (number [1024 - 40 000])");
    }
    @Override
    public void setServerPort(String serverPort) {
        int port;

        try {
            port = Integer.parseInt(serverPort);
            if (port <= 40000 && port >= 1024) {
                this.serverPort = port;

                clientFrame.changeState(ClientState.SetNickName);
                clientFrame.clearOutputArea();
                clientFrame.addTextToOutputArea("Please enter your nickname");
            } else {
                clientFrame.addTextToOutputArea("Invalid port!");
            }
        } catch (NumberFormatException e) {
            clientFrame.addTextToOutputArea("Not a number!");
        }
    }
    @Override
    public void startConnection() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(this.host, this.serverPort);
        server = (RMIServer) registry.lookup("myIrcServer");
    }
    @Override
    public void negotiateNickname(String nickname) throws RemoteException {
        if (nickname.isEmpty() || nickname.isBlank()) {
            clientFrame.addTextToOutputArea("Bad nickname");
            return;
        }

        if (this.server.nicknameCheck(nickname, this).equals(ServerResponse.Accepted)) {
            this.nickname = nickname;

            clientFrame.updateOutputAreaLabel("Chat messages (" + this.nickname + ")");
            clientFrame.changeState(ClientState.JoinChannel);
            clientFrame.clearOutputArea();
            clientFrame.addTextToOutputArea("Enter name of the channel you would like to join (if the channel doesn't exist it will be created automatically):");

            updateChannelList();
            return;
        }
        clientFrame.addTextToOutputArea(ServerResponse.Rejected);
    }
    @Override
    public void updateChannelList() throws RemoteException {
        String channels = this.server.generateChannelList();
        clientFrame.updateContextualArea( channels.replace(Flags.Separator, "\n") );
    }
    @Override
    public void joinChannel(String channelName) throws RemoteException {
        if (channelName.isBlank() || channelName.isEmpty()){
            clientFrame.addTextToOutputArea("Bad channel name");
            return;
        }
        String response = this.server.assignUserToChannel(this.nickname, channelName);

        if (response.equals(ServerResponse.JoinedChannel)) {
            clientFrame.changeState(ClientState.OnChannel);
            clientFrame.clearOutputArea();
            clientFrame.addTextToOutputArea("This is the beginning of chat :) \n\n");
        }
    }
    @Override
    public void sendMessageToChannel(String message) throws RemoteException {
        if (message.isEmpty() || message.isBlank()){
            return;
        }
        this.server.sendChannelMessage(this.nickname, message);
    }
    @Override
    public void disconnectFromChannel() throws RemoteException {
        this.server.disconnectUser(this.nickname);
        clientFrame.changeState(ClientState.JoinChannel);
    }
    @Override
    public void terminateConnection() throws RemoteException{
        if(this.server == null){
            System.exit(0);
        }
        this.server.deleteUser(this.nickname);
        System.exit(0);
    }


    @Override
    public void updateContextualLabel(String text) throws RemoteException{
        clientFrame.updateContextualAreaLabel(text);
    }
    @Override
    public void receiveContextualAreaUpdate(String text) throws RemoteException{
        clientFrame.updateContextualArea(text.replace(Flags.Separator,"\n"));
    }
    @Override
    public void displayMessage(String message) throws RemoteException {
        clientFrame.addTextToOutputArea(message);
    }
}