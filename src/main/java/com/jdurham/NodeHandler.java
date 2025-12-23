package com.jdurham;

public interface NodeHandler<T extends Message, R extends Message> {
    Class<T> getRequestType();
    Class<R> getResponseType();
    R handle(MessageContext messageContext, T request);
}
