package ${rootPackageName};

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Locale;

import org.seasar.kvasir.util.io.IORuntimeException;
import org.seasar.kvasir.util.io.IOUtils;
import org.seasar.ymir.vili.skeleton.generator.IGenerator;
import org.seasar.ymir.vili.skeleton.generator.IParameters;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateException;

abstract public class GenericGenerator<P extends IParameters> implements
        IGenerator<P> {
    private static final String PATHPREFIX_TEMPLATE = "/template/";

    private static final String ENCODING = "UTF-8";

    protected static final String DIR_SRC_MAIN_JAVA = "src/main/java";

    protected static final String DIR_SRC_MAIN_RESOURCES = "src/main/resources";

    protected static final String DIR_SRC_TEST_JAVA = "src/test/java";

    protected static final String DIR_SRC_TEST_RESOURCES = "src/test/resources";

    private File projectDirectory;

    private String rootPackageName;

    public void initialize(String targetProjectPath,
            String targetRootPackageName) {
        projectDirectory = new File(targetProjectPath);
        rootPackageName = targetRootPackageName;
    }

    public final void generateClass(String dir, String packageName,
            String className, String templateName, IParameters root) {
        generateClass(dir, packageName, className, templateName, root, true);
    }

    public final void generateClass(String dir, String packageName,
            String className, String templateName, IParameters root,
            boolean overwrite) {
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
                + ".java", templateName, root, overwrite);
    }

    public final void generateResource(String dir, String pathName,
            String templateName, Object root) {
        generateResource(dir, pathName, templateName, root, true);
    }

    public final void generateResource(String dir, String pathName,
            String templateName, Object root, boolean overwrite) {
        writeString(evaluate(templateName, root), new File(new File(
                projectDirectory, dir), pathName), overwrite);
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
        writeString(string, file, true);
    }

    protected final void writeString(String string, File file, boolean overwrite) {
        if (string == null) {
            return;
        }
        if (!overwrite && file.exists()) {
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
