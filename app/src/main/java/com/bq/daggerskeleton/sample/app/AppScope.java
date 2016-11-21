package com.bq.daggerskeleton.sample.app;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Root scope used by Stores.
 */
@Scope
@Retention(RetentionPolicy.SOURCE)
public @interface AppScope {
}
