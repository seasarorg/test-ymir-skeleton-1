package org.seasar.ymir.vili.skeleton.dbflute_fragment_generic;

import org.t2framework.vili.ViliBehavior;

public interface Globals {
    String NAME_REPLACESCHEMASQL = "replace-schema.sql";

    String NAME_BUILDPROPERTIES = "build.properties";

    String NAME_BASICINFOMAPDFPROP = "basicInfoMap.dfprop";

    String PREFIX_DBFLUTE = "dbflute_";

    String PATH_MYDBFLUTE = "mydbflute";

    String PATHPREFIX_MYDBFLUTE_DBFLUTE = PATH_MYDBFLUTE + "/dbflute-";

    String PATHPREFIX_DBFLUTE = "dbflute_${dbfluteProjectName}/";

    String PATHPREFIX_BUILDPROPERTIES = PATHPREFIX_DBFLUTE + "build";

    String PATH_PROJECT_BAT = PATHPREFIX_DBFLUTE + "_project.bat";

    String PATH_PROJECT_SH = PATHPREFIX_DBFLUTE + "_project.sh";

    String PATH_DBFLUTEDICON = "src/main/resources/dbflute.dicon";

    String PATH_YMIRDAODICON = "src/main/resources/ymir-dao.dicon";

    String BATCH_JDBC = "jdbc";

    String BATCH_GENERATE = "generate";

    String BATCH_SQL2ENTITY = "sql2entity";

    String BATCH_DOC = "doc";

    String SUFFIX_BAT = ".bat";

    String SUFFIX_SH = ".sh";

    String NATURE_ID_YMIRPROJECT = "org.seasar.ymir.eclipse.ymirProjectNature";

    String PARAM_DBFLUTE_VERSION = "dbflute.version";

    String PARAM_DBFLUTEPROJECTNAME = "dbfluteProjectName";

    String PARAM_UPGRADEDBFLUTE = "upgradeDbflute";

    String PARAM_ISDELETEOLDVERSION = "isDeleteOldVersion";

    String PARAM_UPDATEBATCHFILES = "updateBatchFiles";

    String PARAM_DATABASE = "database";

    String PARAM_PACKAGEBASE = "packageBase";

    String PARAM_EXECUTEGENERATEBATCH = "executeGenerateBatch";

    String PARAM_DBFLUTEPROJECTNAME_DEFAULT = ViliBehavior.PREFIX_TEMPLATE_PARAMETER
            + PARAM_DBFLUTEPROJECTNAME + ".default";

    String PARAM_UPGRADEDBFLUTE_LABEL = ViliBehavior.PREFIX_TEMPLATE_PARAMETER
            + PARAM_UPGRADEDBFLUTE + ".label";

    String PARAM_UPGRADEDBFLUTE_DEFAULT = ViliBehavior.PREFIX_TEMPLATE_PARAMETER
            + PARAM_UPGRADEDBFLUTE + ".default";

    String PARAM_RESOURCESYNCHRONIZERURL = "resourceSynchronizerURL";

    String PATH_APP_PROPERTIES = "src/main/resources/app.properties";

    String PROP_RESOURCESYNCHRONIZERURL = "extension.sourceCreator.eclipse.resourceSynchronizerURL";

    String PROP_ECLIPSE_ENABLE = "extension.sourceCreator.eclipse.enable";

    String DEFAULT_PARAM_RESOURCESYNCHRONIZERURL = "http://localhost:8386/";
}
