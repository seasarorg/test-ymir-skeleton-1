package org.seasar.ymir.vili.skeleton.generator;

public interface Globals {
    String GROUPID = "org.seasar.ymir.vili.skeleton.generator";

    String ARTIFACTID = "generator-skeleton-generic";

    String PARAM_TARGETPROJECTNAME = "targetProjectName";

    String PARAM_TARGETROOTPACKAGENAME = "targetRootPackageName";

    String PARAM_TARGETPROJECTSUFFIX = "targetProjectSuffix";

    String PARAM_GENERATESAMPLEGENERATOR = "generateSampleGenerator";

    String PATH_SAMPLEGENERATOR_PACKAGE = "src/main/java/${rootPackagePath}/generator/";

    String PATH_SAMPLEGENERATOR_TEMPLATE = "src/main/resources/template/";
}
