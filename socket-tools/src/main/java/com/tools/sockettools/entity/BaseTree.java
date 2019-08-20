package com.tools.sockettools.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BaseTree {
    private String parentId;
    private List child;
    private Boolean leaf;

    public void setChildren(List<? extends BaseTree> children) {
        this.child = children;
    }

    public void addChildren(BaseTree item) {
        if(child == null){
            child = new ArrayList();
        }
        child.add(item);
    }

    public void removeChildren(BaseTree item) {
        child.remove(item);
    }

    public boolean hasChildren(){
        return (child != null) && (!child.isEmpty());
    }

    public Boolean getLeaf() {
        return (leaf == null) ? (!hasChildren()) : leaf;
    }
}
