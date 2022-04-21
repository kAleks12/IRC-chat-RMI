package com.server.services;

import com.comunnication.flagsresponses.Flags;
import com.comunnication.rmi_interfaces.RMIClient;
import com.server.models.Message;

import java.rmi.RemoteException;
import java.util.*;

public class Channel {
    private final String name;
    private final List<Message> messages;
    private final Map <String, RMIClient> users;

    public Channel(String name, String nickname, RMIClient client) {
        this.name = name;
        users = new TreeMap<>();
        users.putIfAbsent(nickname, client);
        messages = new LinkedList<>();
        sendUserList(generateUserList());
    }

    public String getName() {
        return name;
    }
    public Integer getSize(){
        return users.size();
    }
    public void addUser(String nickname, RMIClient client) {
        users.putIfAbsent(nickname, client);
        sendUserList(generateUserList());
        sendMessage(new Message("SERVER" , "User [" + nickname + "] joined the chat"));
    }
    public void removeUser(String nickname) {
        users.remove(nickname);
        sendUserList(generateUserList());
        sendMessage(new Message("SERVER" , "User [" + nickname + "] left the chat"));
    }
    public void sendMessage(Message message) {
        messages.add(message);
        for(Map.Entry <String, RMIClient> entry : users.entrySet()){
            try {
                entry.getValue().displayMessage(message.toString());
            } catch (RemoteException e) {
                System.out.println("Could not send a message to the client" + entry.getKey());
            }
        }

    }
    public void sendUserList(String userList){
        for(Map.Entry <String, RMIClient> entry : users.entrySet()){
            try {
                entry.getValue().receiveContextualAreaUpdate(userList);
            } catch (RemoteException e) {
                System.out.println("Could not send an user list to the client" + entry.getKey());
            }
        }
    }
    public String generateUserList(){
        StringBuilder userList = new StringBuilder();
        for(Map.Entry <String, RMIClient> entry : users.entrySet()){
            userList.append("[*] ").append(entry.getKey()).append(Flags.Separator);
        }
        return userList.toString();
    }
}