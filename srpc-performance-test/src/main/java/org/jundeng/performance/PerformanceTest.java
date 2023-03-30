package org.jundeng.performance;


import org.jundeng.performance.pojo.User;
import org.jundeng.performance.service.UserService;
import org.jundeng.performance.service.impl.UserServiceImpl;
import org.jundeng.srpc.core.reflect.invoke.RpcProxy;
import org.junit.Test;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * 远程调用性能测试
 */
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 5)
@State(value = Scope.Benchmark)
@Fork(0)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class PerformanceTest {
    UserService userService = RpcProxy.invoke(UserService.class);

    @Test
    @Benchmark
    public void exitsUserTest() {
        userService.exitsUser("jundeng");
    }

    @Test
    @Benchmark
    public void createUserTest() {
        userService.createUser(new User());
    }

    @Test
    @Benchmark
    public void findUserTest() {
        userService.findUser(2);
    }

    @Test
    @Benchmark
    public void getUserListTest() {
        userService.getUserList();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(PerformanceTest.class.getSimpleName())
                .result("result.json")
                .resultFormat(ResultFormatType.JSON).build();
        new Runner(opt).run();
    }

}
