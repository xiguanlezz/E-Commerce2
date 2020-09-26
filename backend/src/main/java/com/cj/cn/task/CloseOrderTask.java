package com.cj.cn.task;

import com.cj.cn.common.Const;
import com.cj.cn.service.IOrderService;
import com.cj.cn.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CloseOrderTask {
    @Autowired
    private IOrderService iOrderService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    //    @Scheduled(cron = "0 0/5 * * * ?")
    public void closeOrderTaskV1() {
        //设置定时任务为五分钟执行一次
        log.info("关闭订单定时任务启动");
        iOrderService.closeOrder(Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour", "2")));
        log.info("关闭订单定时任务关闭");
    }

    @Scheduled(cron = "0 */1 * * * ?")
    public void closeOrderTaskV2() {
        log.info("关闭订单定时任务启动");
        long lockTimeout = Long.parseLong(PropertiesUtil.getProperty("lock.timeout", "5000"));
        Boolean flag = stringRedisTemplate.opsForValue()
                .setIfAbsent(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,    //key
                        String.valueOf(System.currentTimeMillis() + lockTimeout));   //过期的时间戳作为value
        if (BooleanUtils.isTrue(flag)) {
            log.info("{}获取到了分布式锁: {}", Thread.currentThread().getName(), Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
            iOrderService.closeOrder(Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour", "2")));
        } else {
            //这里需要用双重检测锁防分布式锁产生死锁
            String formerLockValueStr = stringRedisTemplate.opsForValue().get(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
            assert formerLockValueStr != null;
            long formerLockValue = Long.parseLong(formerLockValueStr);
            if (System.currentTimeMillis() > formerLockValue) {   //说明之前的锁已经过期, 可重新获得锁
                String latterLockValueStr = stringRedisTemplate.opsForValue()
                        .getAndSet(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,
                                String.valueOf(System.currentTimeMillis() + lockTimeout));
                if (latterLockValueStr == null  //调用getAndSet函数的时候锁已经过期, 并且被删除了
                        || StringUtils.equals(formerLockValueStr, latterLockValueStr)) {    //相等表明确实是这个tomcat进程获取到了分布式锁
                    log.info("{}获取到了分布式锁: {}", Thread.currentThread().getName(), Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
                    iOrderService.closeOrder(Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour", "2")));
                } else {
                    log.info("没有获取到分布式锁: {}", Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
                }
            } else {
                log.info("没有获取到分布式锁: {}", Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
            }
        }
        log.info("关闭订单定时任务关闭");
    }
}
