package org.seasar.ymir.vili.skeleton;

public interface Globals {
    String PARAM_PRODUCTVERSION = "productVersion";

    String PARAM_AUTOGENERATIONENABLED = "autoGenerationEnabled";

    String PARAM_SPECIFYSUPERCLASS = "specifySuperclass";

    String PARAM_SUPERCLASS = "superclass";

    String PARAM_USINGFREYJARENDERCLASS = "usingFreyjaRenderClass";

    String PARAM_INPLACEEDITORENABLED = "inplaceEditorEnabled";

    String PARAM_CONTROLPANELENABLED = "controlPanelEnabled";

    String PARAM_FORMDTOCREATIONFEATUREENABLED = "formDtoCreationFeatureEnabled";

    String PARAM_DAOCREATIONFEATUREENABLED = "daoCreationFeatureEnabled";

    String PARAM_DXOCREATIONFEATUREENABLED = "dxoCreationFeatureEnabled";

    String PARAM_CONVERTERCREATIONFEATUREENABLED = "converterCreationFeatureEnabled";

    String PARAM_TRYTOUPDATECLASSESWHENTEMPLATEMODIFIED = "tryToUpdateClassesWhenTemplateModified";

    String PARAM_ECLIPSEENABLED = "eclipseEnabled";

    String PARAM_RESOURCESYNCHRONIZERURL = "resourceSynchronizerURL";

    String PARAM_HOTDEPLOYTYPE = "hotdeployType";

    String PARAM_BEANTABLEENABLED = "beantableEnabled";

    String PARAM_TOKENKEY = "tokenKey";

    String PARAM_WINDOWKEY = "windowKey";

    String PARAM_AUTORESETCHECKBOXENABLED = "autoResetCheckboxEnabled";

    String PARAM_CHECKBOXKEY = "checkboxKey";

    String PATH_SRC_MAIN_JAVA = "src/main/java"; //$NON-NLS-1$

    String PATH_SRC_MAIN_RESOURCES = "src/main/resources"; //$NON-NLS-1$

    String PATH_SRC_MAIN_WEBAPP = "src/main/webapp";

    String PATH_APP_PROPERTIES = PATH_SRC_MAIN_RESOURCES + "/app.properties"; //$NON-NLS-1$

    String PATH_PAGECUSTOMIZER = PATH_SRC_MAIN_RESOURCES
            + "/ymir-customizer+pageCustomizer.dicon";

    String PATH_XADATASOURCE = PATH_SRC_MAIN_RESOURCES
            + "/jdbc+xaDataSource.dicon";

    String KEY_BASEVERSION = "product.baseVersion";

    String KEY_PREREQUISITE = "product.prerequisite";

    String GROUPID = "org.seasar.ymir";

    String ARTIFACTID = "ymir-core";

    String GROUPID_SEASAR = "org.seasar.container";

    String ARTIFACTID_S2EXTENSION = "s2-extension";

    String ARTIFACTID_S2TIGER = "s2-tiger";

    String STABLE_VERSION_24 = "2.4.20";

    char PACKAGE_DELIMITER = '.';

    String CREATESUPERCLASS_KEY_PACKAGENAME = "packageName"; //$NON-NLS-1$

    String CREATESUPERCLASS_KEY_CLASSSHORTNAME = "classShortName"; //$NON-NLS-1$

    String TEMPLATEPATH_SUPERCLASS = "templates/Superclass.java.ftl"; //$NON-NLS-1$

    String PLUGINID_VE = "org.seasar.ymir.eclipse";

    String NATURE_ID_VEPROJECT = PLUGINID_VE + ".veProjectNature";

    String GROUP_MISC = "misc";
}
