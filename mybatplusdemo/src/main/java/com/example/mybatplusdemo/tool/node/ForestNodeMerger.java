package com.example.mybatplusdemo.tool.node;

import com.example.mybatplusdemo.utils.INode;

import java.util.List;

public class ForestNodeMerger {
    public ForestNodeMerger() {
    }

    public static <T extends INode<T>> List<T> merge(List<T> items) {
        ForestNodeManager<T> forestNodeManager = new ForestNodeManager(items);
        items.forEach((forestNode) -> {
            if (forestNode.getParentId() != 0L) {
                INode<T> node = forestNodeManager.getTreeNodeAt(forestNode.getParentId());
                if (node != null) {
                    // 将子菜单放到父菜单的children列表
                    node.getChildren().add(forestNode);
                } else {
                    System.out.println(node);
                    forestNodeManager.addParentId(forestNode.getId());
                }
            }

        });
        return forestNodeManager.getRoot();
    }
}