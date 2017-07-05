package android.study.leer.mobilerobot.engine;

import android.test.ActivityTestCase;

import junit.framework.TestCase;

/**
 * Created by Leer on 2017/3/10.
 */
public class ProcessInfoProviderTest extends ActivityTestCase {
    public void testGetAppInfos() throws Exception {
        ProcessInfoProvider.getAppInfos(getActivity());
    }

}