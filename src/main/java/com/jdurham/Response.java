package com.jdurham;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Response {
    @JsonProperty
    String type;
    @JsonProperty("msg_id")
    int msgId;
    @JsonProperty("in_reply_to")
    int inReplyTo;

    public Response(String type, int msgId, int inReplyTo) {
        this.type = type;
        this.msgId = msgId;
        this.inReplyTo = inReplyTo;
    }
}
