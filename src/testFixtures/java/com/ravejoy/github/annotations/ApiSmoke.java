package com.ravejoy.github.annotations;

import java.lang.annotation.*;
import org.junit.jupiter.api.Tag;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@ApiBase
@Tag("smoke")
public @interface ApiSmoke {}
