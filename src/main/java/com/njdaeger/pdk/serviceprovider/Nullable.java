package com.njdaeger.pdk.serviceprovider;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a constructor parameter as an optional dependency. If the service is not registered, {@code null} is passed
 * instead of failing. Unlike {@code org.jetbrains.annotations.Nullable} this has runtime retention, which is required
 * because the service provider detects it through reflection.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD})
public @interface Nullable {
}
