package com.vonfly.aop;


import com.vonfly.annotation.RedisLock;
import com.vonfly.common.RedisLockKey;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
@Aspect
public class RedisLockAspect {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private RedisOperations redisOperations;

    /**
     * 指定切点
     */
    @Pointcut("@annotation(lock)")
    public void pointcut(RedisLock lock) {
    }

    /**
     * 环绕切入
     *
     * @param pjp
     * @return
     */
    @Around("pointcut(lock)")
    public Object around(ProceedingJoinPoint pjp, RedisLock lock) {
        Object[] args = pjp.getArgs();
        RedisLockInfo lockInfo = new RedisLockInfo(lock, args);
        //锁标识
        boolean isLocked = false;
        //返回结果
        Object result = null;
        long keyHoldTime = 0;//key持有的时间
        try {
            //超时时间
            long expireTime = System.currentTimeMillis() + lockInfo.expireTime;
            while (!isLocked) {
                isLocked = setIfAbsent(lockInfo.redisLockKey, expireTime, lockInfo.lockHoldTime);
                if (lockInfo.lockHoldTime > 0) {
                    keyHoldTime = System.currentTimeMillis() + lockInfo.lockHoldTime;
                }
                if (isLocked) {
                    result = pjp.proceed(args);
                } else {
                    //需要阻塞
                    if (lockInfo.isBlock) {
                        if (lockInfo.expireTime > 0 && System.currentTimeMillis() > expireTime) {
                            throw new TimeoutException("获取并发控制锁资源等待超时");
                        }
                        TimeUnit.MILLISECONDS.sleep(new Random().nextInt(lockInfo.sleepRandomFactor));
                    } else {
                        if (lock.lockFailExc()) {
                            throw new RuntimeException("获取并发控制锁,失败[MODEL-NO-BLOCK]");
                        } else {
                            return null;
                        }
                    }
                }
            }
        } catch (Throwable throwable) {
            logger.error("并发控制处理异常", throwable);
            throw new RuntimeException("并发控制处理异常", throwable);
        } finally {
            if (isLocked && (keyHoldTime == 0 || keyHoldTime > System.currentTimeMillis())) {
                redisOperations.delete(lockInfo.redisLockKey);
            }
        }
        return result;
    }

    /**
     * set是否加入key成功
     *
     * @param lockKey
     * @param expireTime
     * @param lockTime
     * @return
     */
    private boolean setIfAbsent(String lockKey, long expireTime, long lockTime) {
        BoundValueOperations ops = redisOperations.boundValueOps(lockKey);
        Boolean set = ops.setIfAbsent(expireTime);
        if (set && lockTime > 0) {
            ops.expire(lockTime, TimeUnit.MILLISECONDS);
        }
        return set;
    }


    /**
     * 锁信息
     */
    public class RedisLockInfo {
        private String redisLockKey;
        private boolean isBlock;
        private long expireTime;
        private int sleepRandomFactor;
        private long lockHoldTime;

        public RedisLockInfo(RedisLock lock, Object[] args) {
            if (StringUtils.isBlank(lock.value())) {
                if (null != args && args.length > 0) {
                    StringBuilder sb = new StringBuilder();
                    boolean exitLockKey = Boolean.FALSE;
                    for (Object arg : args) {
                        if (arg instanceof RedisLockKey) {
                            exitLockKey = Boolean.TRUE;
                            this.redisLockKey = ((RedisLockKey) arg).lockKey();
                            break;
                        }
                        sb.append(arg.getClass().getSimpleName()).append("_");
                    }
                    if (!exitLockKey) {
                        sb.setLength(sb.lastIndexOf("_"));
                        this.redisLockKey = sb.toString();
                    }
                }
            } else {
                this.redisLockKey = lock.value();
            }
            Assert.hasText(this.redisLockKey, "lock key配置不合法");
            this.isBlock = lock.isBlock();
            this.expireTime = lock.expireTime();
            this.sleepRandomFactor = lock.sleepRandomFactor();
            this.lockHoldTime = lock.lockHoldTime();
        }

        public String getRedisLockKey() {
            return redisLockKey;
        }

        public void setRedisLockKey(String redisLockKey) {
            this.redisLockKey = redisLockKey;
        }

        public boolean isBlock() {
            return isBlock;
        }

        public void setBlock(boolean block) {
            isBlock = block;
        }

        public long getExpireTime() {
            return expireTime;
        }

        public void setExpireTime(long expireTime) {
            this.expireTime = expireTime;
        }

        public int getSleepRandomFactor() {
            return sleepRandomFactor;
        }

        public void setSleepRandomFactor(int sleepRandomFactor) {
            this.sleepRandomFactor = sleepRandomFactor;
        }

        public long getLockHoldTime() {
            return lockHoldTime;
        }

        public void setLockHoldTime(long lockHoldTime) {
            this.lockHoldTime = lockHoldTime;
        }
    }
}
