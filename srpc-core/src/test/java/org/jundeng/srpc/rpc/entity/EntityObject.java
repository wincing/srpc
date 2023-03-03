package org.jundeng.srpc.rpc.entity;

import java.util.ArrayList;
import java.util.List;

public class EntityObject {

    public int filed1 = 2;
    public String filed2 = "EntityObject";
    public long filed3 = 100L;
    public List<Integer> list = new ArrayList<>();

    public EntityObject() {
        for (int i = 1; i < 10; i++) {
            list.add(i);
        }
    }

    @Override
    public String toString() {
        return "EntityObject{" + "filed1=" + filed1 + ", filed2='" + filed2 + '\'' + ", filed3=" + filed3 + ", list=" + list + '}';
    }
}
