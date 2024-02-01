package org.example;

import org.example.base.DataObject;
import org.example.base.Query;

import java.util.Collection;

public interface SpatialKeywordIndex<Q extends Query, O extends DataObject> {
    default void preloadObject(O object) {};
    default void preloadQuery(Q query) {};
    Collection<O> insertQuery(Q query);
    Collection<Q> insertObject(O dataObject);
}
