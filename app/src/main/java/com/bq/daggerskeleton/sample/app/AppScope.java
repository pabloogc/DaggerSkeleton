package com.bq.daggerskeleton.sample.app;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

@Scope
@Retention(RetentionPolicy.SOURCE)
public @interface AppScope {
}
