package com.ravejoy.github.annotations;

import io.qameta.allure.Epic;
import java.lang.annotation.*;
import org.junit.jupiter.api.Tag;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Tag("infra")
@Tag("unit")
@Epic("Test Infrastructure")
public @interface InfraUnit {}
