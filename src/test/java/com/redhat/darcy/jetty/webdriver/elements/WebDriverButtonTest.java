package com.redhat.darcy.jetty.webdriver.elements;

import com.redhat.darcy.ui.By;
import com.redhat.darcy.ui.Elements;
import com.redhat.darcy.ui.api.HasElementContext;
import com.redhat.darcy.ui.api.elements.Button;
import com.redhat.darcy.web.SimpleUrlView;
import com.redhat.darcy.web.api.Browser;
import com.redhat.darcy.webdriver.FirefoxBrowserFactory;
import com.redhat.synq.Synq;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.Assert.assertTrue;

/**
 * Created by spyrkob on 14/11/2014.
 */

public class WebDriverButtonTest {
    private final Server server = new Server(8080);

    private Supplier<String> htmlContent;

    private Browser browser = null;

    @Before
    public void setUp() throws Exception {
        // Jetty wants the handler to be set before server is started... but we can change what the handler is returning
        server.setHandler(new AbstractHandler() {
            @Override
            public void handle(String s, Request request, HttpServletRequest httpServletRequest, HttpServletResponse response) throws IOException, ServletException {
                response.getWriter().print(htmlContent.get());
                request.setHandled(true);
            }
        });

        // the server needs to be running before we can do any testing
        Synq.after(()->{
            try {
                server.start();
            } catch (Exception e) {
                //TODO: handle this :)
                e.printStackTrace();
            }
        }).expect(server::isStarted).waitUpTo(5, ChronoUnit.SECONDS); // TODO: CountDownLatch?

        // lets fire up the browser
        browser = new FirefoxBrowserFactory().newBrowser();
    }

    @After
    public void tearDown() throws Exception {
        if (browser != null) {
            browser.close();
        }

        if (server.isRunning()) {
            server.stop();
            server.join();
        }
    }

    @Test
    public void findButton() throws Exception {
        htmlContent = () -> "<html><body><button id='mybutton'>Click me</button></body></html>";

        Button mybutton = Elements.button(By.id("mybutton"));
        SimpleUrlView testView = new SimpleUrlView("http://localhost:8080/") {
            public Button _mybutton = mybutton;
        };
        browser.open(testView).waitUpTo(1, ChronoUnit.SECONDS);

        assertTrue(mybutton.isDisplayed());
    }

    @Test
    public void findDifferentButton() throws Exception {
        htmlContent = () -> "<html><body><button>Now click me!</button></body></html>";

        Button mybutton = Elements.button(By.textContent("Now click me!"));
        SimpleUrlView testView = new SimpleUrlView("http://localhost:8080/") {
            public Button _mybutton = mybutton;
        };
        browser.open(testView).waitUpTo(1, ChronoUnit.SECONDS);

        assertTrue(mybutton.isDisplayed());
    }
}
