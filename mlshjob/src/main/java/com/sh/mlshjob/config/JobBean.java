package com.sh.mlshjob.config;

import com.tuan.job.client.service.ZkDockerService;
import com.tuan.job.util.JobContextUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author
 * @version 2019/5/23.
 */
@Component
public class JobBean {

    @Bean
    public JobContextUtil getJobContextUtil() {
        return new JobContextUtil();
    }

    @Bean("zkDockerService")
    public ZkDockerService getZkDockerService() {
        return new ZkDockerService();
    }

}
