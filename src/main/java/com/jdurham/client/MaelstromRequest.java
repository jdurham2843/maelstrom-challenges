package com.jdurham.client;

import com.jdurham.Message;

public record MaelstromRequest(String src, String dest, Message request) {
}
