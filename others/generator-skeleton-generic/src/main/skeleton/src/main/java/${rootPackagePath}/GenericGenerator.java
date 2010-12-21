package ${rootPackageName};

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Locale;

import org.seasar.kvasir.util.ClassUtils;
import org.seasar.kvasir.util.io.IORuntimeException;
import org.seasar.kvasir.util.io.IOUtils;
import org.seasar.ymir.vili.skeleton.generator.IParameters;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateException;

abstract public class GenericGenerator {
    private static final String NAME_POM_XML = "pom.xml";

    private static final String PATHPREFIX_TEMPLATE = "/template/";

    private static final String ENCODING = "UTF-8";

    protected static final String DIR_SRC_MAIN_JAVA = "src/main/java";

    protected static final String DIR_SRC_MAIN_RESOURCES = "src/main/resources";

    protected static final String DIR_SRC_TEST_JAVA = "src/test/java";

    protected static final String DIR_SRC_TEST_RESOURCES = "src/test/resources";

    private File projectDirectory;

    private String rootPackageName;

    public GenericGenerator(Class<?> landmark, String rootPackageName) {
        this.rootPackageName = rootPackageName;
        File baseDirectory = ClassUtils.getBaseDirectory(landmark);
        if (baseDirectory == null) {
            throw new RuntimeException(
                    "指定されたクラス（"
                            + landmark.getName()
                            + "）から生成先フォルダを見つけることができませんでした。クラスのソースが存在するプロジェクトに対してこのプロジェクトからプロジェクト参照を作成するようにして下さい。");
        }
        while (baseDirectory != null
                && !new File(baseDirectory, NAME_POM_XML).exists()) {
            baseDirectory = baseDirectory.getParentFile();
        }
        if (baseDirectory == null) {
            throw new RuntimeException("クラスのソースが存在するプロジェクトに" + NAME_POM_XML
                    + "が存在しないため、指定されたクラス（" + landmark.getName()
                    + "）から生成先フォルダを見つけることができませんでした。");
        }
        projectDirectory = baseDirectory;
    }

    public final void generateClass(String dir, String packageName,
            String className, String templateName, IParameters root) {
        String pkgName;
        if (packageName.startsWith(".")) {
            pkgName = rootPackageName + packageName;
        } else if (packageName.isEmpty()) {
            pkgName = rootPackageName;
        } else {
            pkgName = packageName;
        }

        root.setRootPackageName(rootPackageName);
        root.setPackageName(pkgName);
        root.setClassName(className);

        generateResource(dir, pkgName.replace('.', '/') + "/" + className
                + ".java", templateName, root);
    }

    public final void generateResource(String dir, String pathName,
            String templateName, Object root) {
        writeString(evaluate(templateName, root), new File(new File(
                projectDirectory, dir), pathName));
    }

    protected final String evaluate(String templateName, Object root) {
        Configuration cfg = new Configuration();
        cfg.setEncoding(Locale.getDefault(), ENCODING);
        cfg.setTemplateLoader(new ClassTemplateLoader(getClass(),
                PATHPREFIX_TEMPLATE + getClass().getSimpleName()));
        cfg.setObjectWrapper(new DefaultObjectWrapper());

        StringWriter sw = new StringWriter();
        try {
            cfg.getTemplate(templateName).process(root, sw);
            return sw.toString();
        } catch (TemplateException ex) {
            throw new IORuntimeException(ex);
        } catch (IOException ex) {
            throw new IORuntimeException(ex);
        }
    }

    protected final void writeString(String string, File file) {
        if (string == null) {
            return;
        }

        file.getParentFile().mkdirs();
        try {
            IOUtils.writeString(new FileOutputStream(file), string, ENCODING,
                    true);
        } catch (FileNotFoundException ex) {
            throw new IORuntimeException(ex);
        }
    }
}
