package org.seasar.ymir.vili.skeleton;

public interface Globals {
    String PARAM_PRODUCTVERSION = "productVersion";

    String PARAM_USESTABLES24CONTAINER = "useStableS24Container";

    String PATH_SRC_MAIN_JAVA = "src/main/java"; //$NON-NLS-1$

    String PATH_SRC_MAIN_RESOURCES = "src/main/resources"; //$NON-NLS-1$

    String PATH_APP_PROPERTIES = PATH_SRC_MAIN_RESOURCES + "/app.properties"; //$NON-NLS-1$

    String PATH_BATCHCUSTOMIZER = PATH_SRC_MAIN_RESOURCES
            + "/ymir-customizer+batchCustomizer.dicon";

    String PATH_XADATASOURCE = PATH_SRC_MAIN_RESOURCES
            + "/jdbc+xaDataSource.dicon";

    String KEY_BASEVERSION = "product.baseVersion";

    String KEY_PREREQUISITE = "product.prerequisite";

    String GROUPID = "org.seasar.ymir";

    String ARTIFACTID = "ymir-batch";

    String GROUPID_SEASAR = "org.seasar.container";

    String ARTIFACTID_S2EXTENSION = "s2-extension";

    String ARTIFACTID_S2TIGER = "s2-tiger";

    String GROUPID_SERVLETAPI = "javax.servlet";

    String ARTIFACTID_SERVLETAPI = "servlet-api";

    String SCOPE_COMPILE = "compile";

    char PACKAGE_DELIMITER = '.';

    String SUFFIX_SNAPSHOT = "-SNAPSHOT";
}
