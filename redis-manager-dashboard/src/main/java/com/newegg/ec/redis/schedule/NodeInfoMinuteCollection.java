package com.newegg.ec.redis.schedule;

import com.newegg.ec.redis.entity.Cluster;
import com.newegg.ec.redis.entity.TimeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Jay.H.Zou
 * @date 2019/7/22
 */
@Component
public class NodeInfoMinuteCollection extends NodeInfoCollectionAbstract {

    private static final Logger logger = LoggerFactory.getLogger(NodeInfoMinuteCollection.class);

    /**
     * 一分钟收集一次 RedisNode 数据，并计算以 MINUTE 为单位的 avg, max, min
     */
    @Async
    @Scheduled(cron = "0 0/1 * * * ? ")
    @Override
    public void collect() {
        logger.info("Start collecting node info (minute)...");
        try {
            List<Cluster> allClusterList = clusterService.getAllClusterList();
            if (allClusterList == null || allClusterList.isEmpty()) {
                return;
            }
            for (Cluster cluster : allClusterList) {
                threadPool.submit(new CollectNodeInfoTask(cluster, TimeType.MINUTE));
            }
        } catch (Exception e) {
            logger.error("Collect node info data failed.", e);
        }
    }

}
