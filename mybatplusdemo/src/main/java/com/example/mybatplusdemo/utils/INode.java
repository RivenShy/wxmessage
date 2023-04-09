package com.example.mybatplusdemo.utils;

import java.io.Serializable;
import java.util.List;

public interface INode<T> extends Serializable {
    Long getId();

    Long getParentId();

    List<T> getChildren();

    default Boolean getHasChildren() {
        return false;
    }
}