package com.vonfly.common;

/**
 * 指定锁的key
 */
public interface RedisLockKey {
    String lockKey();
}
