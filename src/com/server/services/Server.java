package com.server.services;


import com.comunnication.flagsresponses.Flags;
import com.comunnication.flagsresponses.ServerResponse;
import com.comunnication.rmi_interfaces.RMIClient;
import com.comunnication.rmi_interfaces.RMIServer;
import com.server.models.EventType;
import com.server.models.Message;
import com.server.models.User;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.List;

public class Server implements RMIServer {
    private final Logger serverLogger = new Logger();
    private final List<User> users;
    private final List<Channel> channels;


    public Server() throws RemoteException {
        this.users = new LinkedList<>();
        this.channels = new LinkedList<>();
        UnicastRemoteObject.exportObject(this, 0);
    }

    public void start() {
        serverLogger.logEvent(EventType.ServerAlive);
    }
    public void stop()  {
        serverLogger.logEvent(EventType.ServerDead);
    }
    public void sendUpdatedChannelList() throws RemoteException {
        for(User user:users){
            if(user.getCurrChannel() == null)
                user.getClient().receiveContextualAreaUpdate(generateChannelList());
        }
    }

    private User findUserByNick(String nickname){
        return  users.stream()
                .filter(x -> x.getName().equals(nickname))
                .findFirst()
                .orElse(null);
    }
    private Channel findChannelByName(String name){
        return  channels.stream()
                .filter(x -> x.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String nicknameCheck(String nickname, RMIClient client) {
        boolean isTaken = users.stream().
                anyMatch(x -> x.getName()
                .equals(nickname));

        if (isTaken) {
            return ServerResponse.Rejected;
        }

        users.add(new User(nickname, client));

        serverLogger.logEvent(EventType.NegotiatedNick, nickname);
        return ServerResponse.Accepted;
    }
    @Override
    public String assignUserToChannel(String nickname, String channelName) throws RemoteException {
        Channel channel = findChannelByName(channelName);
        User user = findUserByNick(nickname);

        if(channel == null){
            channels.add(new Channel(channelName, nickname, user.getClient()));
        }
        else {
            channel.addUser(nickname, user.getClient());
        }

        user.setCurrChannel(channelName);
        user.getClient().updateContextualLabel("Channel's \" " + channelName + " \" user list");

        sendUpdatedChannelList();
        serverLogger.logEvent(EventType.JoinedChannel, nickname, channelName);
        return ServerResponse.JoinedChannel;
    }
    @Override
    public void disconnectUser(String nickname) throws RemoteException {
        User user = findUserByNick(nickname);
        if(user == null){
            return;
        }

        Channel channel = findChannelByName(user.getCurrChannel());
        if(channel == null)
            return;

        user.getClient().updateContextualLabel("Channels list");
        user.setCurrChannel(null);

        channel.removeUser(user.getName());

        sendUpdatedChannelList();
        serverLogger.logEvent(EventType.LeftChannel, nickname, channel.getName());
    }
    @Override
    public void deleteUser(String nickname) throws RemoteException {
        disconnectUser(nickname);

        users.removeIf(x -> x.getName().equals(nickname));

        serverLogger.logEvent(EventType.LeftServer, nickname);
    }
    @Override
    public void sendChannelMessage(String nickname, String messageBody){
        User user = findUserByNick(nickname);
        if(user == null){
            return;
        }

        Channel channel = findChannelByName(user.getCurrChannel());
        if(channel == null)
            return;

        channel.sendMessage(new Message(user.getName(), messageBody));
        serverLogger.logEvent(EventType.SentMessage, nickname, channel.getName());
    }
    @Override
    public String generateChannelList() {
        if (channels.size() == 0) {
            return "There are no active channels";
        }

        StringBuilder channelList = new StringBuilder();
        for (Channel channel : channels) {
            channelList.append("[*] ").append(channel.getName()).append(" => users [").append(channel.getSize()).append("]" + Flags.Separator);
        }
        return channelList.toString();
    }
}