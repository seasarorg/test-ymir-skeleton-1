package org.seasar.ymir.skeleton;

import org.seasar.ymir.vili.ViliBehavior;

public interface Globals {
    String PREFIX_DBFLUTE = "dbflute_";

    String PATH_MYDBFLUTE = "mydbflute";

    String PATHPREFIX_MYDBFLUTE_DBFLUTE = PATH_MYDBFLUTE + "/dbflute-";

    String PATHPREFIX_DBFLUTE = "dbflute_${dbfluteProjectName}/";

    String PATH_PROJECT_BAT = PATHPREFIX_DBFLUTE + "_project.bat";

    String PATH_PROJECT_SH = PATHPREFIX_DBFLUTE + "_project.sh";

    String PATH_YMIRDAODICON = "src/main/resources/ymir-dao.dicon";

    String BATCH_INITIALIZE = "_initialize";

    String SUFFIX_BAT = ".bat";

    String SUFFIX_SH = ".sh";

    String NATURE_ID_YMIRPROJECT = "org.seasar.ymir.eclipse.ymirProjectNature";

    String PARAM_DBFLUTE_VERSION = "dbflute.version";

    String PARAM_DBFLUTEPROJECTNAME = "dbfluteProjectName";

    String PARAM_UPGRADEDBFLUTE = "upgradeDbflute";

    String PARAM_ISDELETEOLDVERSION = "isDeleteOldVersion";

    String PARAM_UPDATEBATCHFILES = "updateBatchFiles";

    String PARAM_DBFLUTEPROJECTNAME_DEFAULT = ViliBehavior.PREFIX_TEMPLATE_PARAMETER
            + PARAM_DBFLUTEPROJECTNAME + ".default";
}
