package com.jdurham;

public interface NodeHandler<T extends Request, R extends Response> {
    Class<T> getRequestType();
    Class<R> getResponseType();
    R handle(MessageContext messageContext, T request);
}
