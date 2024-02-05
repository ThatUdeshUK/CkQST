package org.example.structures;

import org.example.CkQST;
import org.example.base.BaseQuadTree;
import org.example.base.DataObject;
import org.example.base.Point;
import org.example.base.Query;
import org.example.models.CkQuery;

import java.util.*;

public class CostBasedQuadTree extends BaseQuadTree<DataObject, Query> {
    private final CostBasedQuadNode root;

    public CostBasedQuadTree(double x, double y, double width, double height, int maxHeight) {
        Point point = new Point(x, y);
        AxisAlignedBoundingBox aabb = new AxisAlignedBoundingBox(point, width, height);
        CostBasedQuadNode.maxHeight = maxHeight;
        root = new CostBasedQuadNode(aabb);
    }

    @Override
    protected BaseQuadNode<DataObject, Query> getRoot() {
        return root;
    }

    @Override
    public Collection<Query> search(DataObject dataObject) {
        Set<Query> results = new HashSet<>();
        this.root.search(dataObject, results);
        return results;
    }

    @Override
    public boolean insert(Query object) {
        return this.root.insert(object);
    }

    @Override
    public boolean remove(Query object) {
        return this.root.remove(object);
    }

    protected static class CostBasedQuadNode extends BaseQuadNode<DataObject, Query> {
        protected static int maxHeight = 0;
        protected OrderedInvertedIndex textualIndex = new OrderedInvertedIndex();
        protected int height = 1;

        protected CostBasedQuadNode(AxisAlignedBoundingBox aabb) {
            super(aabb);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean insert(Query query) {
            // Ignore objects which do not belong in this quad tree
            if (!aabb.containsQuery(query) && height > 1)
                return false; // object cannot be added

            if (height == maxHeight) {
                textualIndex.insertQueryPL(query);
                return true;
            } else if (isMinimal(query)) {
                if (query.id == 4188) {
                    System.out.println("Minimal at " + height);
                }
                double nodeVCost = verifyCost(query);
                double nodeUCost = updateCost(query);

                double childrenVCost = 0;
                double childrenUCost = 0;
                if (isLeaf())
                    subdivide();
                childrenVCost += ((CostBasedQuadNode) northEast).verifyCost(query);
                childrenVCost += ((CostBasedQuadNode) northWest).verifyCost(query);
                childrenVCost += ((CostBasedQuadNode) southEast).verifyCost(query);
                childrenVCost += ((CostBasedQuadNode) southWest).verifyCost(query);

                childrenUCost += ((CostBasedQuadNode) northEast).updateCost(query);
                childrenUCost += ((CostBasedQuadNode) northWest).updateCost(query);
                childrenUCost += ((CostBasedQuadNode) southEast).updateCost(query);
                childrenUCost += ((CostBasedQuadNode) southWest).updateCost(query);

                if (childrenVCost + CkQST.thetaU * childrenUCost < nodeVCost + CkQST.thetaU * nodeUCost) {
                    ((CostBasedQuadNode) northEast).textualIndex.insertQueryPL(query);
                    ((CostBasedQuadNode) northWest).textualIndex.insertQueryPL(query);
                    ((CostBasedQuadNode) southEast).textualIndex.insertQueryPL(query);
                    ((CostBasedQuadNode) southWest).textualIndex.insertQueryPL(query);
                    return true;
                }

                textualIndex.insertQueryPL(query);
                return true;
            }

            // Otherwise, we need to subdivide then add the point to whichever node will accept it
            if (isLeaf())
                subdivide();
            return insertIntoChildren(query);
        }

        @Override
        public boolean remove(Query o) {
            throw new RuntimeException("Not implemented!");
        }

        private boolean isMinimal(Query q) {
            if (aabb.quadContainsQuery(q, 0)) return false;
            if (aabb.quadContainsQuery(q, 1)) return false;
            if (aabb.quadContainsQuery(q, 2)) return false;
            return !aabb.quadContainsQuery(q, 3);
        }

        private double verifyCost(Query q) {
            double num_o_N = 1; // the number of objects falling to this node in a unit time

            double p_V_q = textualIndex.verifyProb(q);

            double e_V_q = 1;
            if (q.keywords.size() > 2) {
                e_V_q = textualIndex.estVerifyCost(q);
            }

            return num_o_N * p_V_q * e_V_q;
        }

        private double updateCost(Query q) {
            return textualIndex.updateCost(q);
        }

        private void subdivide() {
            double h = aabb.height / 2d;
            double w = aabb.width / 2d;

            AxisAlignedBoundingBox aabbNW = new AxisAlignedBoundingBox(aabb, w, h);
            northWest = new CostBasedQuadNode(aabbNW);
            ((CostBasedQuadNode) northWest).height = height + 1;

            Point xyNE = new Point(aabb.x + w, aabb.y);
            AxisAlignedBoundingBox aabbNE = new AxisAlignedBoundingBox(xyNE, w, h);
            northEast = new CostBasedQuadNode(aabbNE);
            ((CostBasedQuadNode) northEast).height = height + 1;

            Point xySW = new Point(aabb.x, aabb.y + h);
            AxisAlignedBoundingBox aabbSW = new AxisAlignedBoundingBox(xySW, w, h);
            southWest = new CostBasedQuadNode(aabbSW);
            ((CostBasedQuadNode) southWest).height = height + 1;

            Point xySE = new Point(aabb.x + w, aabb.y + h);
            AxisAlignedBoundingBox aabbSE = new AxisAlignedBoundingBox(xySE, w, h);
            southEast = new CostBasedQuadNode(aabbSE);
            ((CostBasedQuadNode) southEast).height = height + 1;
        }

        private boolean insertIntoChildren(Query q) {
            // A point can only live in one child.
            if (northWest.insert(q)) return true;
            if (northEast.insert(q)) return true;
            if (southWest.insert(q)) return true;
            return southEast.insert(q);
        }

        /**
         * Remove object from tree.
         *
         * @param q Query object to remove from tree.
         * @return True if successfully removed.
         */
        protected boolean remove(CkQuery q) {
            // If not in this AABB, don't do anything
            if (!aabb.containsPoint(q.location))
                return false;

            // If in this AABB and in this node
            if (textualIndex.remove(q))
                return true;

            // If this node has children
            if (!isLeaf()) {
                // If in this AABB but in a child branch
                return removeFromChildren(q);
            }

            return false;
        }

        private boolean removeFromChildren(CkQuery q) {
            // A point can only live in one child.
            if (northWest.remove(q)) return true;
            if (northEast.remove(q)) return true;
            if (southWest.remove(q)) return true;
            return southEast.remove(q);
        }

        /**
         * Find all objects which appear within a range.
         *
         * @param object  Object to be matched with the queries contained in this node.
         * @param results Queries matching with the streamed object.
         */
        @Override
        protected void search(DataObject object, Collection<Query> results) {
            // Automatically abort if the range does not collide with this quad
            if (!aabb.containsPoint(object.location))
                return;

            textualIndex.searchObject(object, results);

            // Otherwise, add the points from the children
            if (!isLeaf()) {
                ((CostBasedQuadNode) northWest).search(object, results);
                ((CostBasedQuadNode) northEast).search(object, results);
                ((CostBasedQuadNode) southWest).search(object, results);
                ((CostBasedQuadNode) southEast).search(object, results);
            }
        }

        @Override
        protected int size() {
            throw new RuntimeException("Not implemented!");
        }
    }
}
