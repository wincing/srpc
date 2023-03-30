package org.jundeng.performance.service.impl;


import org.jundeng.performance.pojo.User;
import org.jundeng.performance.service.UserService;

import java.util.ArrayList;
import java.util.List;

public class UserServiceImpl implements UserService {

    @Override
    public boolean exitsUser(String name) {
        return true;
    }

    @Override
    public boolean createUser(User user) {
        return true;
    }

    @Override
    public User findUser(Integer userId) {
        User user = new User();
        user.setName("kk");
        user.setPassword("1222222222222");
        return user;
    }

    @Override
    public List<User> getUserList() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            users.add(new User());
        }
        return users;
    }
}
