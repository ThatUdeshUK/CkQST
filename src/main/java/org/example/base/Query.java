package org.example.base;

import java.util.List;

public abstract class Query {
    public int id;
    public List<String> keywords;
    public long st;
    public long et;

    public Query(int id, List<String> keywords, long st, long et) {
        this.id = id;
        this.keywords = keywords;
        this.st = st;
        this.et = et;
    }

    /**
     * Get the spatial range of the query as a rectangle
     * @return Minimal covering rectangle for the range.
     */
    public abstract Rectangle spatialBox();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Query query = (Query) o;
        return id == query.id;
    }
}
