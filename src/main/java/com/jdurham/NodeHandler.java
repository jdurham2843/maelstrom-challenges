package com.jdurham;

public interface NodeHandler<T extends Request, R extends Response> {
    Class<T> getRequestType();
    Class<R> getResponseType();
    R handle(T request);
}
