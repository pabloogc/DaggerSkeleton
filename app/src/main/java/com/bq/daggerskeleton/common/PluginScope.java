package com.bq.daggerskeleton.common;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

@Scope
@Retention(RetentionPolicy.SOURCE)
public @interface PluginScope {
}
