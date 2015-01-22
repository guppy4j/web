package org.guppy4j.web.html.content;

import org.guppy4j.web.html.marker.BodyContent;
import org.guppy4j.web.html.marker.SpanContent;
import org.guppy4j.web.html.marker.TitleContent;
import org.guppy4j.web.html.render.Renderer;

import java.util.function.Function;

/**
 * Text nodes
 */
public class Text<M> implements TitleContent<M>, BodyContent<M>, SpanContent<M> {

    private final Function<M, ?> value;

    public Text(Function<M, ?> value) {
        this.value = value;
    }

    @Override
    public void render(Renderer renderer, M model) {
        final Object o = value.apply(model);
        renderer.render(o == null ? "" : o.toString());
    }
}