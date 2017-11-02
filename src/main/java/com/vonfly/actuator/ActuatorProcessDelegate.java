package com.vonfly.actuator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ActuatorProcessDelegate {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired(required = false)
    private List<Actuator> actuators;

    /**
     * 执行执行器
     */
    public void run() {
        if (null != actuators) {
            for (Actuator actuator : actuators) {
                try {
                    actuator.process();
                } catch (Exception e) {
                    logger.error("===当前执行器{},执行异常====", actuator.getClass().getSimpleName(), e);
                }
            }
        } else {
            logger.info("===当前不存在执行器====");
        }
    }
}
