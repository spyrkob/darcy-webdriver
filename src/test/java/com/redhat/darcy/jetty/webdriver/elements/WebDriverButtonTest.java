package com.redhat.darcy.jetty.webdriver.elements;

import com.redhat.darcy.ui.By;
import com.redhat.darcy.ui.api.elements.Button;
import com.redhat.darcy.web.SimpleUrlView;
import com.redhat.darcy.web.api.Browser;
import org.junit.*;

import java.time.temporal.ChronoUnit;

import static com.redhat.darcy.ui.Elements.*;
import static org.junit.Assert.assertTrue;

/**
 * Created by spyrkob on 14/11/2014.
 */

public abstract class WebDriverButtonTest {

    @Rule
    public JettyServer server = new JettyServer();

    protected abstract Browser getBrowser();

    @Test
    public void findButton() throws Exception {
        server.serve(() -> "<html><body><button id='mybutton'>Click me</button></body></html>");

        TestView testView = new TestView(
                server.url("/"),
                button(By.id("mybutton")));
        getBrowser().open(testView).waitUpTo(1, ChronoUnit.SECONDS);

        assertTrue(testView.button.isDisplayed());
    }

    @Test
    public void findDifferentButton() throws Exception {
        server.serve(() -> "<html><body><button>Now click me!</button></body></html>");

        TestView testView = new TestView(
                server.url("/"),
                button(By.textContent("Now click me!")));
        getBrowser().open(testView).waitUpTo(1, ChronoUnit.SECONDS);

        assertTrue(testView.button.isDisplayed());
    }

    class TestView extends SimpleUrlView {
        Button button;

        TestView(String url, Button button) {
            super(url);
            this.button = button;
        }
    }
}
