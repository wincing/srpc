package org.jundeng.example.provider.service.impl;

import org.jundeng.example.provider.service.HelloService;

public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello() {
        return "hello, world!";
    }
}
