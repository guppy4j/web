package org.guppy4j.web.html.render;

import java.io.IOException;

/**
 * Renders by appending to an Appendable
 */
public class AppendingRenderer implements Renderer {

    private final Appendable a;

    public AppendingRenderer(Appendable appendable) {
        this.a = appendable;
    }

    @Override
    public void render(String s) {
        append(s);
    }

    private void append(String s) {
        try {
            a.append(s);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}