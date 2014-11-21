package com.redhat.darcy.jetty.webdriver.elements;

import com.redhat.darcy.web.api.Browser;
import com.redhat.darcy.webdriver.FirefoxBrowserFactory;
import org.junit.rules.ExternalResource;

/**
 * Created by spyrkob on 20/11/2014.
 */
public class FirefoxBrowser extends ExternalResource {
    protected Browser browser;

    public Browser instance() {
        return browser;
    }

    @Override
    protected void before() throws Throwable {
        this.browser = new FirefoxBrowserFactory().newBrowser();
    }

    @Override
    protected void after() {
        browser.close();
    }
}
