package org.guppy4j.html.attribute;

import org.guppy4j.html.attribute.type.LanguageCode;
import org.guppy4j.html.marker.HtmlAttribute;

import java.util.function.Function;

/**
 * HTML "lang" attribute
 */
public class Lang<M> extends AttributeBase<M> implements HtmlAttribute<M> {

    public Lang(Function<M, LanguageCode> value) {
        super("lang", value);
    }
}
