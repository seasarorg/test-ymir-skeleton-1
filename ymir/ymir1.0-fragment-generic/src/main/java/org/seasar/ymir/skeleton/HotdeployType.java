package org.seasar.ymir.skeleton;

public enum HotdeployType {
    S2("S2"), JAVAREBEL("JavaRebel"), VOID("void"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    private String name;

    private HotdeployType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
