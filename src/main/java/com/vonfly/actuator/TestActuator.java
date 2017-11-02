package com.vonfly.actuator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class TestActuator implements Actuator{
    @Autowired
    private JdbcOperations jdbcOperations;
    @Override
    public void process() {
        List<Map<String, Object>> maps = jdbcOperations.queryForList("select * from t_template_api_merchant_enter_xmdzf");
        System.out.println(maps);
    }
}
