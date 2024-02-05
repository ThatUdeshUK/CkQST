package org.example.structures;

import org.example.base.Query;

import java.util.*;

public class Block {
    String minw;
    String maxw;
    private final LinkedList<Query> queries;
    private final LinkedList<String> keywords;

    public Block() {
        this.queries = new LinkedList<>();
        this.keywords = new LinkedList<>();
    }

    public Block(Query query) {
        if (query.keywords.size() > 2) {
            this.minw = query.keywords.get(2);
            this.maxw = query.keywords.get(query.keywords.size() - 1);
        }

        this.keywords = new LinkedList<>();
        if (query.keywords.size() > 2) {
            this.keywords.add(query.keywords.get(2));
        }

        this.queries = new LinkedList<>();
        this.queries.add(query);
    }

    public void add(Query query) {
        queries.add(query);
    }

    public void add(int i, Query query) {
        queries.add(i, query);
    }

    public double probBVbr(Map<String, Double> probWV) {
        double max = 0;
        double sum = 0;
        for (String wj : keywords) {
            double prob = probWV.get(wj);
            if (prob > max)
                max = prob;

            sum += prob;
        }
        double out = max;
        if (keywords.size() > 1)
            out = max + (1.0 / (keywords.size()-1)) * (sum - max);
        return Math.min(out, 1);
    }

    public double probBVbr_c(Map<String, Double> probWV, String w3) {
        double max = probWV.get(w3);
        double sum = max;
        for (String wj : keywords) {
            double prob = probWV.get(wj);
            if (prob > max)
                max = prob;

            sum += prob;
        }
        double out = max + (1.0 / (keywords.size())) * (sum - max);
        return Math.min(out, 1);
    }

    public LinkedList<String> getKeywords() {
        return keywords;
    }
    public LinkedList<Query> getQueries() {
        return queries;
    }

    public int size() {
        return queries.size();
    }

    @Override
    public String toString() {
        return "Block{" +
                ", minw='" + minw + '\'' +
                ", maxw='" + maxw + '\'' +
                ", queries=" + queries +
                ", keywords=" + keywords +
                '}';
    }
}
