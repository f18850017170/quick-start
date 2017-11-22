package com.vonfly.actuator;

import com.vonfly.util.SpyMemcachedManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class TestActuator implements Actuator{
    @Autowired
    private JdbcOperations jdbcOperations;
    @Autowired
    private SpyMemcachedManager memcachedManager;
    @Override
    public void process() {
        String key = "API_WEB__Shiro_b2768ac5-58b1-4139-b390-bd90b79df5f3";
        Set<String> values = new HashSet<String>();
        values.add("APIWEB__SHIRO_SESSION_cda1a260-a0c8-46ea-9e20-c06fd8ad5974");
        values.add("SHIRO_SESSION_cda1a260-a0c8-46ea-9e20-c06fd8ad5974");
        values.add("SHIRO_SESSION_cda1a260-a0c8-46ea-9e20-");

        memcachedManager.set(key,values,1800000);
        Set<String> result = (Set<String>) memcachedManager.get(key);
        System.out.println(result);
        List<Map<String, Object>> maps = jdbcOperations.queryForList("select * from t_template_api_merchant_enter_xmdzf");
        System.out.println(maps);
    }
}
