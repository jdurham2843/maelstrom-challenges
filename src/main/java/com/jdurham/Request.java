package com.jdurham;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Request {
    @JsonProperty
    public String type;
    @JsonProperty
    @JsonAlias(value = "msg_id")
    public int msgId;
    @JsonProperty
    @JsonAlias(value = "in_reply_to")
    public int inReplyTo;
}
