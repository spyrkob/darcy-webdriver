package com.redhat.darcy.jetty.webdriver.elements;

import com.redhat.synq.Synq;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.rules.ExternalResource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.function.Supplier;

/**
 * Created by spyrkob on 20/11/2014.
 */
public class JettyServer extends ExternalResource {
    //TODO: fallback to different port if 8080 is taken
    private final Server server = new Server(8080);

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

        // the server needs to be running before we can do any testing
        Synq.after(() -> {
            try {
                server.start();
            } catch (Exception e) {
                //TODO: handle this :)
                e.printStackTrace();
            }
        }).expect(server::isStarted).waitUpTo(5, ChronoUnit.SECONDS); // TODO: would CountDownLatch be better?
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
        return "http://localhost:8080" + path;
    }
}
