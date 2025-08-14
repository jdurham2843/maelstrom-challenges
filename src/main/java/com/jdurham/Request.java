package com.jdurham;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Request {
    @JsonProperty
    String type;
    @JsonProperty
    @JsonAlias(value = "msg_id")
    int msgId;
    @JsonProperty
    @JsonAlias(value = "in_reply_to")
    int inReplyTo;
}
