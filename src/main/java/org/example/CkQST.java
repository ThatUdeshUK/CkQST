package org.example;

import org.example.base.DataObject;
import org.example.base.Query;
import org.example.helpers.SpatialHelper;
import org.example.models.CkQuery;
import org.example.structures.CostBasedQuadTree;
import org.example.structures.IQuadTree;

import java.util.Collection;
import java.util.PriorityQueue;

/**
 * Reproduction of CkQST
 */
public class CkQST implements SpatialKeywordIndex<Query, DataObject> {
    public static int xRange = 10;
    public static int yRange = 10;
    public static int maxHeight = 9;
    public static int maxLeafCapacity = 5;

    public static double thetaU;
    private final IQuadTree objectIndex;
    private final CostBasedQuadTree queryIndex;
    private int timestamp = 0;

    public CkQST() {
        objectIndex = new IQuadTree(0, 0, xRange, yRange, maxLeafCapacity, maxHeight);
        queryIndex = new CostBasedQuadTree(0, 0, xRange, yRange, maxHeight);
        thetaU = 0.5;
    }

    public CkQST(int xRange, int yRange, int maxHeight) {
        CkQST.xRange = xRange;
        CkQST.yRange = yRange;
        CkQST.maxHeight = maxHeight;
        objectIndex = new IQuadTree(0, 0, xRange, yRange, maxLeafCapacity, maxHeight);
        queryIndex = new CostBasedQuadTree(0, 0, xRange, yRange, maxHeight);
        thetaU = 0.5;
    }

    @Override
    public void preloadObject(DataObject object) {
        objectIndex.insert(object);
    }

    @Override
    public Collection<DataObject> insertQuery(Query query) {
        timestamp++;
        if (query.getClass() == CkQuery.class) {
            PriorityQueue<DataObject> objResults = (PriorityQueue<DataObject>) objectIndex.search(query);

            if (objResults.size() >= ((CkQuery) query).k) {
                DataObject o = objResults.peek();
                assert o != null;
                ((CkQuery) query).sr = SpatialHelper.getDistanceInBetween(((CkQuery) query).location, o.location);
            }

            queryIndex.insert(query);
        } else
            throw new RuntimeException("CkQST only support KNNQueries!");
        return null;
    }

    @Override
    public Collection<Query> insertObject(DataObject dataObject) {
        timestamp++;

        objectIndex.insert(dataObject);
        Collection<Query> queryResults = queryIndex.search(dataObject);
        for (Query query : queryResults) {
            if (query instanceof CkQuery) {
                PriorityQueue<DataObject> objResults = (PriorityQueue<DataObject>) objectIndex.search(query);

                if (objResults.size() >= ((CkQuery) query).k) {
                    DataObject o = objResults.peek();
                    assert o != null;
                    ((CkQuery) query).sr = SpatialHelper.getDistanceInBetween(((CkQuery) query).location, o.location);
                }
            }
        }
        return queryResults;
    }

    public void printIndex() {
        System.out.println(queryIndex);
    }
}
