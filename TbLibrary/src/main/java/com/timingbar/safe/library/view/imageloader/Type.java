package com.timingbar.safe.library.view.imageloader;

import javax.inject.Qualifier;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Type
 * -----------------------------------------------------------------------------------------------------------------------------------
 *
 * @author rqmei on 2018/1/25
 */
@Qualifier
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Type {
    String value() default "";
}
