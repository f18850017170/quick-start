package com.vonfly;

import com.vonfly.actuator.ActuatorProcessDelegate;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(RootConfig.class);
        context.start();
        ActuatorProcessDelegate delegate = context.getBean(ActuatorProcessDelegate.class);
        delegate.run();
    }
}
