package org.example.structures;

import org.example.base.Point;
import org.example.base.Query;
import org.example.models.CkQuery;

/**
 * A quadtree is a tree data structure in which each internal node has exactly four children. Quadtrees
 * are most often used to partition a two dimensional space by recursively subdividing it into four
 * quadrants or regions. The regions may be square or rectangular, or may have arbitrary shapes.
 * <a href="http://en.wikipedia.org/wiki/Quadtree">...</a>
 *
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class AxisAlignedBoundingBox extends Point implements Comparable<Object> {

    public double height;
    public double width;

    private final double minX;
    private final double minY;
    private final double maxX;
    private final double maxY;

    public AxisAlignedBoundingBox(Point upperLeft, double width, double height) {
        super(upperLeft.x, upperLeft.y);
        this.width = width;
        this.height = height;

        minX = upperLeft.x;
        minY = upperLeft.y;
        maxX = upperLeft.x + width;
        maxY = upperLeft.y + height;
    }

    public boolean containsPoint(Point p) {
        if (p.x >= maxX) return false;
        if (p.x < minX) return false;
        if (p.y >= maxY) return false;
        return !(p.y < minY);
    }

    /**
     * Is the inputted AxisAlignedBoundingBox completely inside this AxisAlignedBoundingBox.
     *
     * @param b AxisAlignedBoundingBox to test.
     * @return True if the AxisAlignedBoundingBox is completely inside this AxisAlignedBoundingBox.
     */
    public boolean insideThis(AxisAlignedBoundingBox b) {
        return b.minX >= minX && b.maxX <= maxX && b.minY >= minY && b.maxY <= maxY;
    }

    /**
     * Is the inputted CkQST completely inside this AxisAlignedBoundingBox.
     *
     * @param query CkQST query to test.
     * @return True if the query is completely inside this AxisAlignedBoundingBox.
     */
    public boolean containsQuery(Query query) {
        if (query instanceof CkQuery) {
            CkQuery q = (CkQuery) query;
            return q.location.x - q.sr >= minX && q.location.x + q.sr <= maxX &&
                    q.location.y - q.sr >= minY && q.location.y + q.sr <= maxY;
        }
        return false;
    }

    /**
     * Is the inputted CkQST completely inside specified quad of this AxisAlignedBoundingBox.
     *
     * @param query CkQST query to test.
     * @param quad  Quad of the AABB (0-NW, 1-NE, 2-SW, 3-SE)
     * @return True if the query is completely inside this AxisAlignedBoundingBox.
     */
    public boolean quadContainsQuery(Query query, int quad) {
        double h = height / 2d;
        double w = width / 2d;

        double sx = minX;
        double sy = minY;
        double ex = minX + w;
        double ey = minY + h;

        if (quad == 1) {
            sx = ex;
            ex = maxX;
        } else if (quad == 2) {
            sy = ey;
            ey = maxY;
        } else if (quad == 3) {
            sx = ex;
            sy = ey;
            ex = maxX;
            ey = maxY;
        }

        if (query instanceof CkQuery) {
            CkQuery q = (CkQuery) query;
            return q.location.x - q.sr >= sx && q.location.x + q.sr <= ex &&
                    q.location.y - q.sr >= sy && q.location.y + q.sr <= ey;
        }
        return false;
    }

    /**
     * Is the inputted AxisAlignedBoundingBox intersecting this AxisAlignedBoundingBox.
     *
     * @param b AxisAlignedBoundingBox to test.
     * @return True if the AxisAlignedBoundingBox is intersecting this AxisAlignedBoundingBox.
     */
    public boolean intersectsBox(AxisAlignedBoundingBox b) {
        if (insideThis(b) || b.insideThis(this)) {
            // INSIDE
            return true;
        }

        // OUTSIDE
        if (maxX < b.minX || minX > b.maxX) return false;

        // INTERSECTS
        return !(maxY < b.minY) && !(minY > b.maxY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = hash * 13 + (int) height;
        hash = hash * 19 + (int) width;
        return hash;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof AxisAlignedBoundingBox))
            return false;

        AxisAlignedBoundingBox aabb = (AxisAlignedBoundingBox) obj;
        return compareTo(aabb) == 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(Object o) {
        if (!(o instanceof AxisAlignedBoundingBox))
            throw new RuntimeException("Cannot compare object.");

        AxisAlignedBoundingBox a = (AxisAlignedBoundingBox) o;
        int p = super.compareTo(a);
        if (p != 0) return p;

        if (height > a.height) return 1;
        if (height < a.height) return -1;

        return Double.compare(width, a.width);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "(" +
                super.toString() + ", " +
                "height" + "=" + height + ", " +
                "width" + "=" + width +
                ")";
    }
}