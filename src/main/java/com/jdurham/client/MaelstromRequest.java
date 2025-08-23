package com.jdurham.client;

import com.jdurham.Request;

public record MaelstromRequest(String src, String dest, Request request) {
}
