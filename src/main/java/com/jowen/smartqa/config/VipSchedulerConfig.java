package com.jowen.smartqa.config;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
@Data
public class VipSchedulerConfig {

    @Bean
    public Scheduler vipScheduler() {
        ThreadFactory threadFactory = new ThreadFactory() {
            private final AtomicInteger threadNumber = new AtomicInteger(1);

            @Override
            public Thread newThread(@NotNull Runnable r) {
                Thread thread = new Thread(r, "vip-scheduler-" + threadNumber.getAndIncrement());
                // 设置为非守护线程
                thread.setDaemon(false);
                return thread;
            }
        };
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10, threadFactory);
        return Schedulers.fromExecutor(executorService);
    }
}
