package com.cj.cn.task;

import com.cj.cn.service.IOrderService;
import com.cj.cn.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CloseOrderTask {
    @Autowired
    private IOrderService iOrderService;

    @Scheduled(cron = "0 0/5 * * * ?")
    public void closeOrderTask() {
        //设置定时任务为五分钟执行一次
        log.info("关闭订单定时任务启动");
        iOrderService.closeOrder(Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour", "2")));
        log.info("关闭订单定时任务关闭");
    }
}
