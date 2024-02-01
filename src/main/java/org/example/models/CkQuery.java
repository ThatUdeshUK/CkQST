package org.example.models;

import org.example.base.Point;
import org.example.base.Query;
import org.example.base.Rectangle;

import java.util.List;

public class CkQuery extends Query {
    public Point location;
    public double sr;
    public int k;

    public CkQuery(int id, List<String> keywords, double x, double y, int k, long st, long et) {
        super(id, keywords, st, et);
        this.location = new Point(x, y);
        this.sr = Double.MAX_VALUE;
        this.k = k;
    }

    public boolean containsPoint(Point p) {
        boolean isInRectangle = p.x >= location.x - sr && p.x <= location.x + sr &&
                p.y >= location.y - sr && p.y <= location.y + sr;

        if (isInRectangle) {
            return (p.x - location.x) * (p.x - location.x) + (p.y - location.y) * (p.y - location.y) <= sr * sr;
        }
        return false;
    }

    @Override
    public String toString() {
        return "CkQST{" +
                "id=" + id +
                ", keywords=" + keywords +
                ", (x, y)=(" + location.x + ", " + location.y + ")" +
                ", k=" + k +
                ", sr=" + sr +
                '}';
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Rectangle spatialBox() {
        throw new RuntimeException("ERROR!!! THIS SHOULD NEVER HAPPEN!");
    }

    @Override
    public boolean equals(Object other) {
        if (other == this)
            return true;
        if (!(other instanceof CkQuery))
            return false;
        return (this.id == ((CkQuery) other).id);
    }
}
