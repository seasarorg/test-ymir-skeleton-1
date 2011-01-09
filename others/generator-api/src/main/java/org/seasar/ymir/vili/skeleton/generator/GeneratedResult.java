package org.seasar.ymir.vili.skeleton.generator;

import java.util.ArrayList;
import java.util.List;

public class GeneratedResult {
    private List<String> activeResourcePaths = new ArrayList<String>();

    public List<String> getActiveResourcePaths() {
        return new ArrayList<String>(activeResourcePaths);
    }

    public GeneratedResult setActiveResourcePath(String activeResourcePath) {
        activeResourcePaths.clear();
        return addActiveResourcePath(activeResourcePath);
    }

    public GeneratedResult addActiveResourcePath(String activeResourcePath) {
        activeResourcePaths.add(activeResourcePath);
        return this;
    }
}
