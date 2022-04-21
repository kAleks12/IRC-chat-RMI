package com.server.models;

public enum EventType {
    ServerAlive, ServerDead, NegotiatedNick, LeftServer, JoinedChannel, LeftChannel, SentMessage
}