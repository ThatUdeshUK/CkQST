package org.example.helpers;

import org.example.base.DataObject;
import org.example.base.Point;

import java.util.Comparator;

public class EuclideanComparator implements Comparator<DataObject> {
    private final Point point;

    public EuclideanComparator(Point point) {
        this.point = point;
    }

    @Override
    public int compare(DataObject o1, DataObject o2) {
        double val1 = Math.pow(point.x - o1.location.x, 2) + Math.pow(point.y - o1.location.y, 2);
        double val2 = Math.pow(point.x - o2.location.x, 2) + Math.pow(point.y - o2.location.y, 2);

        return Double.compare(val2, val1);
    }
}