package com.tools.sockettools.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BaseTree {
    private List children;
    private Boolean leaf;

    public void setChildren(List<? extends BaseTree> children) {
        this.children = children;
    }

    public void addChildren(BaseTree item) {
        if(children == null){
            children = new ArrayList();
        }
        children.add(item);
    }

    public void removeChildren(BaseTree item) {
        children.remove(item);
    }

    public boolean hasChildren(){
        return (children != null) && (!children.isEmpty());
    }

    public Boolean getLeaf() {
        return (leaf == null) ? (!hasChildren()) : leaf;
    }
}
