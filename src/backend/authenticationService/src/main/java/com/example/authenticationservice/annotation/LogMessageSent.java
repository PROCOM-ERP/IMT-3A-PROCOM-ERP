package com.example.authenticationservice.annotation;

public @interface LogMessageSent {

    String description();
    String tag() default "";

}
