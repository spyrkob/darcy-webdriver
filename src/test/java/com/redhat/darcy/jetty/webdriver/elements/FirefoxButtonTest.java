package com.redhat.darcy.jetty.webdriver.elements;

import com.redhat.darcy.web.api.Browser;
import org.junit.Rule;

/**
 * Created by spyrkob on 20/11/2014.
 */
public class FirefoxButtonTest extends WebDriverButtonTest {

    @Rule
    public FirefoxBrowser firefox = new FirefoxBrowser();

    @Override
    protected Browser getBrowser() {
        return firefox.instance();
    }
}
