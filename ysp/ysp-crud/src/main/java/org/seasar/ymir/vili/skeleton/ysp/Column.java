package org.seasar.ymir.vili.skeleton.ysp;

public class Column {
    private String type;

    private String name;

    public Column(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
