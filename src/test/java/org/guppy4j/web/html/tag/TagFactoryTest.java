package org.guppy4j.web.html.tag;

import org.guppy4j.web.html.attribute.type.LanguageCode;
import org.guppy4j.web.html.marker.BodyContent;
import org.guppy4j.web.html.model.Variable;
import org.guppy4j.web.html.model.VariableImpl;
import org.guppy4j.web.html.render.AppendingRenderer;
import org.guppy4j.web.html.render.Renderer;
import org.guppy4j.web.samples.Model;
import org.junit.Test;

import java.util.Arrays;
import java.util.function.Function;

import static org.guppy4j.web.html.attribute.AttributeFactory.$;
import static org.guppy4j.web.html.attribute.AttributeFactory.lang;
import static org.guppy4j.web.html.logic.LogicFactory.forEach;
import static org.guppy4j.web.html.tag.TagFactory.body;
import static org.guppy4j.web.html.tag.TagFactory.head;
import static org.guppy4j.web.html.tag.TagFactory.html;
import static org.guppy4j.web.html.tag.TagFactory.span;
import static org.guppy4j.web.html.tag.TagFactory.text;
import static org.guppy4j.web.html.tag.TagFactory.title;

/**
 * TODO: Document this!
 */
public class TagFactoryTest {

    @Test
    public void test() {

        final Function<Model, String> name = m -> m.name().get();

        final Html<Model> html =

            html(
                $(lang(Model::lang)),
                head(
                    title(text(Model::title))
                ),
                body(
                    forEach(Model::names, Model::name, BodyContent.class,
                        span(
                            text(name),
                            text(" "),
                            text(name)
                        )
                    )
                )
            );

        final StringBuilder sb = new StringBuilder();
        final Renderer renderer = new AppendingRenderer(sb);

        html.render(renderer, new Model() {

            private final Variable<String> name = new VariableImpl<>();

            @Override
            public LanguageCode lang() {
                return LanguageCode.de;
            }

            @Override
            public String title() {
                return "Cool page";
            }

            @Override
            public Iterable<String> names() {
                return Arrays.asList("a", "b", "c");
            }

            @Override
            public Variable<String> name() {
                return name;
            }
        });

        System.out.print(sb);

    }
}