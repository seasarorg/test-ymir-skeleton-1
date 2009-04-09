package org.seasar.ymir.vili.skeleton;

public enum HotdeployType {
    S2("S2"), JAVAREBEL("JavaRebel"), VOID("void"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    private String name;

    private HotdeployType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static HotdeployType enumOf(String name) {
        for (HotdeployType type : values()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }
        return null;
    }
}
