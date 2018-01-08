package us.spencer.habittracker.utility;

import android.support.test.espresso.idling.CountingIdlingResource;

public class EspressoCoutingIdlingResource {

    private static final CountingIdlingResource mCountingIdlingResource =
            new CountingIdlingResource("ALL");

    public static CountingIdlingResource getIdlingResource() {
        return mCountingIdlingResource;
    }
}
