package com.redhat.darcy.jetty.webdriver.elements;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.rules.ExternalResource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Created by spyrkob on 20/11/2014.
 */
public class JettyServer extends ExternalResource {
    private final Server server = new Server(0);
    private int connectorPort;

    protected Supplier<String> htmlContent;

    public void before() throws Exception {
        // Jetty wants the handler to be set before server is started... but we can change what the handler is returning
        server.setHandler(new AbstractHandler() {
            @Override
            public void handle(String target, Request request, HttpServletRequest httpServletRequest, HttpServletResponse response) throws IOException, ServletException {
                response.getWriter().print(htmlContent.get());
                request.setHandled(true);
            }
        });

        // jetty needs to be started in a separate thread before we can do any testing
        startJettyThread();

        connectorPort = ((ServerConnector)server.getConnectors()[0]).getLocalPort();
    }

    private void startJettyThread() throws InterruptedException {
        CountDownLatch isJettyStarted = new CountDownLatch(1);

        new Thread(() -> {
            try {
                server.start();
                isJettyStarted.countDown();
            } catch (Exception e) {
                // we couldn't start Jetty - no point in going further with tests
                throw new RuntimeException("Failed to start Jetty server", e);
            }
        }).start();

        isJettyStarted.await(10, TimeUnit.SECONDS); // wait until jetty is up
    }

    @Override
    public void after() {
        if (server.isRunning()) {
            try {
                server.stop();
                server.join();
            }catch(Exception e) {
                // we can't close jetty... probably should crash
                throw new RuntimeException("Failed to stop embedded Jetty server", e);
            }

        }
    }

    // TODO: do we need more complicated stuff - access to request etc?
    // TODO: would be good to be able to call .serve("/", "foo").serve("/lib.js", "bar")
    public void serve(Supplier<String> func) {
        htmlContent = func;
    }

    public String url(String path) {
        return "http://localhost:" + connectorPort + path;
    }
}
