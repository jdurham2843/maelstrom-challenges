package com.jdurham;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Message {
    @JsonProperty
    public String type;
    @JsonProperty("msg_id")
    public int msgId;
    @JsonProperty("in_reply_to")
    public int inReplyTo;

    public Message() {
    }

    public Message(String type, int msgId, int inReplyTo) {
        this.type = type;
        this.msgId = msgId;
        this.inReplyTo = inReplyTo;
    }
}
