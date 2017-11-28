package com.vonfly.annotation;


import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RedisLock {
    /**
     * 锁的key，未指定时，从参数中查询第一个实现RedisLockKey接口的参数，否则默认为请求参数类名组合
     *
     * @return
     */
    String value() default "";

    /**
     * 是否阻塞等待获取锁
     *
     * @return
     */
    boolean isBlock() default true;

    /**
     * （非阻塞模式）获取锁失败时，是否异常
     * @return
     */
    boolean lockFailExc() default true;
    /**
     * 阻塞情况下指定超时时间
     *
     * @return
     */
    long expireTime() default 180 * 1000;

    /**
     * 获取锁时的休眠随机因子
     *
     * @return
     */
    int sleepRandomFactor() default 100;

    /**
     * 获取锁后的，锁的过期时间
     *
     * @return
     */
    long lockHoldTime() default 300 * 1000;


}
