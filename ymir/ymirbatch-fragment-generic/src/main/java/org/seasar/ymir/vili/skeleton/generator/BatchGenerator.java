package org.seasar.ymir.vili.skeleton.generator;

import org.seasar.ymir.vili.skeleton.generator.impl.GenericGenerator;
import org.seasar.ymir.vili.skeleton.generator.util.StringUtils;

public class BatchGenerator extends GenericGenerator<BatchGenerator.Parameters> {
    public static class Parameters extends ParametersBase {
        private String batchName;

        private String batchClassName;

        private boolean generateTestClass;

        private boolean generateBat;

        private boolean generateSh;

        private boolean overwrite;

        public String getBatchName() {
            return batchName;
        }

        public void setBatchName(String batchName) {
            this.batchName = batchName;
        }

        public String getBatchClassName() {
            return batchClassName;
        }

        public void setBatchClassName(String batchClassName) {
            this.batchClassName = batchClassName;
        }

        public boolean shouldGenerateTestClass() {
            return generateTestClass;
        }

        public void setGenerateTestClass(boolean generateTestClass) {
            this.generateTestClass = generateTestClass;
        }

        public boolean shouldGenerateBat() {
            return generateBat;
        }

        public void setGenerateBat(boolean generateBat) {
            this.generateBat = generateBat;
        }

        public boolean shouldGenerateSh() {
            return generateSh;
        }

        public void setGenerateSh(boolean generateSh) {
            this.generateSh = generateSh;
        }

        public boolean shouldOverwrite() {
            return overwrite;
        }

        public void setOverwrite(boolean overwrite) {
            this.overwrite = overwrite;
        }
    }

    private static final String PACKAGE_BATCH = "batch";

    private static final String SUFFIX_BATCH = "Batch";

    private static final String DIR_SRC_MAIN_BATCH = "src/main/batch";

    private static final String SUFFIX_BAT = ".bat";

    private static final String SUFFIX_SH = ".sh";

    private static final String SUFFIX_TEST = "Test";

    private static final String SUFFIX_JAVA = ".java";

    public Class<Parameters> getParametersClass() {
        return Parameters.class;
    }

    public GeneratedResult generate(Parameters parameters) {
        String batchName = StringUtils.capitalize(parameters.getBatchName());
        String packageName;
        String className;
        int dot = batchName.indexOf('.');
        if (dot >= 0) {
            packageName = PACKAGE_BATCH + "." + batchName.substring(0, dot);
            className = batchName.substring(dot + 1);
        } else {
            packageName = PACKAGE_BATCH;
            className = batchName;
        }
        if (!className.endsWith(SUFFIX_BATCH)) {
            className += SUFFIX_BATCH;
        }

        String batchClassName = getRootPackageName() + "." + packageName + "."
                + className;
        String resourcePath = DIR_SRC_MAIN_JAVA + "/"
                + batchClassName.replace('.', '/') + SUFFIX_JAVA;
        String launcherName = StringUtils.decapitalize(className.substring(0,
                className.length() - SUFFIX_BATCH.length()));

        parameters.setBatchClassName(batchClassName);
        parameters.setBatchName(className);

        boolean overwrite = parameters.shouldOverwrite();
        generateClass(DIR_SRC_MAIN_JAVA, "." + packageName, className,
                "Batch.ftl", parameters, overwrite);
        if (parameters.shouldGenerateTestClass()) {
            generateClass(DIR_SRC_TEST_JAVA, "." + packageName, className
                    + SUFFIX_TEST, "BatchTest.ftl", parameters, overwrite);
        }
        if (parameters.shouldGenerateBat()) {
            generateFile(DIR_SRC_MAIN_BATCH, launcherName + SUFFIX_BAT,
                    "BatchBat.ftl", parameters, overwrite);
        }
        if (parameters.shouldGenerateSh()) {
            generateFile(DIR_SRC_MAIN_BATCH, launcherName + SUFFIX_SH,
                    "BatchSh.ftl", parameters, overwrite);
        }

        return new GeneratedResult().addActiveResourcePath(resourcePath);
    }
}
