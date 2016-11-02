package com.bq.daggerskeleton.common;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.MotionEvent;



public interface Plugin {

    void onCreate(@Nullable Bundle savedInstanceState);

    /**
     * Called after {@link #onCreate(Bundle)} or during a configuration change.
     * Most components can ignore this method if they ignore configuration changes and
     * do every logic during {@link #onCreate(Bundle)}
     */
    void onCreateView();

    void onPostCreate();

    /**
     * All the components have completed their onCreate method, it's safe to reference external views.
     */
    void onComponentsReady();

    void onSaveInstanceState(@NonNull Bundle outState);

    void onDestroy();

    void onStart();

    void onResume();

    void onPause();

    void onStop();

    void onBackPressed(SharedEvent<Void> ev);

    void onDispatchTouchEvent(SharedEvent<MotionEvent> ev);

    void onKeyDown(SharedEvent<KeyEvent> ev);

    void onKeyUp(SharedEvent<KeyEvent> ev);

    void onConfigurationChanged(@NonNull Configuration newConfig);

    /**
     * Called during {@link #onConfigurationChanged(Configuration)}, this call is always
     * followed by a {@link #onCreateView()}
     */
    void onDestroyView();
}
