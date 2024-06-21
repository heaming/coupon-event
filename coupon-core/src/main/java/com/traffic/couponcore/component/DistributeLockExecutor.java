package com.traffic.couponcore.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Component
@Slf4j
public class DistributeLockExecutor {
    private final RedissonClient redissonClient;

    public void execute(String lockName, long waitMilliSecond, long releaseMilliSecond, Runnable logic) {
        RLock lock = redissonClient.getLock(lockName);

        try {
            boolean isLocked = lock.tryLock(waitMilliSecond, releaseMilliSecond, TimeUnit.MICROSECONDS);
            if(!isLocked) {
                throw new IllegalStateException("[ %s ] lock 획득 실패".formatted(lockName));
            }
            logic.run();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            if(lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

    }
}
