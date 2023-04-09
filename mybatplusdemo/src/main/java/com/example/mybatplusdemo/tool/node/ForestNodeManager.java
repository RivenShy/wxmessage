package com.example.mybatplusdemo.tool.node;

import com.example.mybatplusdemo.utils.INode;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ForestNodeManager<T extends INode<T>> {
//    ImmutableMap：一个不可变集合
//    线程安全、更有效的利用内存、可作为常量
    private final ImmutableMap<Long, T> nodeMap;
    private final Map<Long, Object> parentIdMap = Maps.newHashMap();

    public ForestNodeManager(List<T> nodes) {
        // 得到以 id 为key，node 为值的一个map
        this.nodeMap = Maps.uniqueIndex(nodes, INode::getId);
    }

    public INode<T> getTreeNodeAt(Long id) {
        return this.nodeMap.containsKey(id) ? (INode)this.nodeMap.get(id) : null;
    }

    public void addParentId(Long parentId) {
        this.parentIdMap.put(parentId, "");
    }

    public List<T> getRoot() {
        List<T> roots = new ArrayList();
        this.nodeMap.forEach((key, node) -> {
            if (node.getParentId() == 0L || this.parentIdMap.containsKey(node.getId())) {
                roots.add(node);
            }

        });
        return roots;
    }

//    Maps.uniqueIndex(Iterable,Function)通常针对的场景是：有一组对象，它们在某个属性上分别有独一无二的值，
//    而我们希望能够按照这个属性值查找对象
}