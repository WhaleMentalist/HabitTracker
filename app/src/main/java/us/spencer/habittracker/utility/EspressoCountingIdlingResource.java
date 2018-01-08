package us.spencer.habittracker.utility;

public class EspressoCountingIdlingResource {

    private static final SimpleCountingIdlingResource mCountingIdlingResource =
            new SimpleCountingIdlingResource("ALL");

    public static SimpleCountingIdlingResource getIdlingResource() {
        return mCountingIdlingResource;
    }
}
