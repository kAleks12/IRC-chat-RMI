package com.client.interfaces;


import com.client.enums.ClientState;

public interface IClientFrame {
    void addTextToOutputArea(String message);
    void updateContextualArea(String text);
    void changeState (ClientState state);
    void clearOutputArea();
    void updateContextualAreaLabel(String text);
    void updateOutputAreaLabel(String text);
}