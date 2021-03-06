package org.guppy4j.html.tag;

import org.guppy4j.html.Tag;
import org.guppy4j.html.content.type.Metadata;
import org.guppy4j.html.marker.BaseAttribute;
import org.guppy4j.html.marker.BaseContent;

import static org.guppy4j.html.Element.base;

/**
 * The 'base' tag
 */
public class Base<M> extends Tag<M, BaseAttribute<M>, BaseContent<M>>
    implements Metadata<M> {

    public Base(Iterable<BaseAttribute<M>> attributes,
                Iterable<BaseContent<M>> contents) {
        super(base, attributes, contents);
    }
}
