package org.jundeng.performance.service;


import org.jundeng.performance.pojo.User;
import org.jundeng.srpc.common.extension.SRpcSPI;

import java.util.List;

@SRpcSPI("default")
public interface UserService {

    boolean exitsUser(String name);

    boolean createUser(User user);

    User findUser(Integer userId);

    List<User> getUserList();
}
