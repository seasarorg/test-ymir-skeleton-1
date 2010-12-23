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
import org.seasar.ymir.vili.skeleton.generator.util.StringUtils;

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

    /**
     * Javaクラスのソースコードを生成します。
     * <p>既にコードが存在する場合は上書きします。
     * </p>
     * <p>このメソッドは{@code #generateClass(dir, packageName, className, templateName, root, true)}と同じです。
     * </p>
     * 
     * @param dir 基点となる生成先ディレクトリ。「{@code src/main/java}」のようにプロジェクト相対で指定して下さい。
     * @param packageName 生成するJavaクラスが属するパッケージ名。
     * {@code .web}のように「{@code .}」で始まるパッケージ名を指定した場合は、ルートパッケージ相対と見なされます。
     * @param className Javaクラス名。
     * @param templateName 生成元となるテンプレート名。
     * テンプレートは{@code src/main/resources/template/<Generatorクラス名>/}から読み込まれます。
     * @param root テンプレートを評価する時に使用するパラメータを表すオブジェクト。
     */
    public final void generateClass(String dir, String packageName,
            String className, String templateName, IParameters root) {
        generateClass(dir, packageName, className, templateName, root, true);
    }

    /**
     * Javaクラスのソースコードを生成します。
     * 
     * @param dir 基点となる生成先ディレクトリ。「{@code src/main/java}」のようにプロジェクト相対で指定して下さい。
     * @param packageName 生成するJavaクラスが属するパッケージ名。
     * {@code .web}のように「{@code .}」で始まるパッケージ名を指定した場合は、ルートパッケージ相対と見なされます。
     * @param className Javaクラス名。
     * @param templateName 生成元となるテンプレート名。
     * テンプレートは{@code src/main/resources/template/<Generatorクラス名>/}から読み込まれます。
     * @param root テンプレートを評価する時に使用するパラメータを表すオブジェクト。
     * @param overwrite 既にソースコードが存在する場合に上書きするかどうか。
     */
    public final void generateClass(String dir, String packageName,
            String className, String templateName, IParameters root,
            boolean overwrite) {
        root.setClassName(className);
        generateResource(dir, packageName, className + ".java", templateName,
                root, overwrite);
    }

    /**
     * リソースファイルを生成します。
     * <p>既にリソースファイルが存在する場合は上書きします。
     * </p>
     * <p>このメソッドは{@code #generateResource(dir, packageName, className, templateName, root, true)}と同じです。
     * </p>
     * 
     * @param dir 基点となる生成先ディレクトリ。「{@code src/main/java}」のようにプロジェクト相対で指定して下さい。
     * @param packageName 生成するリソースが属するパッケージ名。
     * {@code .web}のように「{@code .}」で始まるパッケージ名を指定した場合は、ルートパッケージ相対と見なされます。
     * @param resourceName リソース名。
     * @param templateName 生成元となるテンプレート名。
     * テンプレートは{@code src/main/resources/template/<Generatorクラス名>/}から読み込まれます。
     * @param root テンプレートを評価する時に使用するパラメータを表すオブジェクト。
     */
    public final void generateResource(String dir, String packageName,
            String resourceName, String templateName, IParameters root) {
        generateResource(dir, packageName, resourceName, templateName, root,
                true);
    }

    /**
     * リソースファイルを生成します。
     * 
     * @param dir 基点となる生成先ディレクトリ。「{@code src/main/java}」のようにプロジェクト相対で指定して下さい。
     * @param packageName 生成するリソースが属するパッケージ名。
     * {@code .web}のように「{@code .}」で始まるパッケージ名を指定した場合は、ルートパッケージ相対と見なされます。
     * @param resourceName リソース名。
     * @param templateName 生成元となるテンプレート名。
     * テンプレートは{@code src/main/resources/template/<Generatorクラス名>/}から読み込まれます。
     * @param root テンプレートを評価する時に使用するパラメータを表すオブジェクト。
     * @param overwrite 既にリソースファイルが存在する場合に上書きするかどうか。
     */
    public final void generateResource(String dir, String packageName,
            String resourceName, String templateName, IParameters root,
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

        generateFile(dir, pkgName.replace('.', '/') + "/" + resourceName,
                templateName, root, overwrite);
    }

    /**
     * ファイルを生成します。
     * <p>既にファイルが存在する場合は上書きします。
     * </p>
     * <p>このメソッドは{@code #generateFile(dir, pathName, templateName, root, true)}と同じです。
     * </p>
     * 
     * @param dir 基点となる生成先ディレクトリ。「{@code src/main/java}」のようにプロジェクト相対で指定して下さい。
     * @param pathName 生成するファイルのパス名。
     * 生成先ディレクトリ相対で例えば「{@code a/b/c.txt}」のように指定して下さい。
     * @param templateName 生成元となるテンプレート名。
     * テンプレートは{@code src/main/resources/template/<Generatorクラス名>/}から読み込まれます。
     * @param root テンプレートを評価する時に使用するパラメータを表すオブジェクト。
     */
    public final void generateFile(String dir, String pathName,
            String templateName, Object root) {
        generateFile(dir, pathName, templateName, root, true);
    }

    /**
     * ファイルを生成します。
     * 
     * @param dir 基点となる生成先ディレクトリ。「{@code src/main/java}」のようにプロジェクト相対で指定して下さい。
     * @param pathName 生成するファイルのパス名。
     * 生成先ディレクトリ相対で例えば「{@code a/b/c.txt}」のように指定して下さい。
     * @param templateName 生成元となるテンプレート名。
     * テンプレートは{@code src/main/resources/template/<Generatorクラス名>/}から読み込まれます。
     * @param root テンプレートを評価する時に使用するパラメータを表すオブジェクト。
     * @param overwrite 既にファイルが存在する場合に上書きするかどうか。
     */
    public final void generateFile(String dir, String pathName,
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

    /**
     * 指定された文字列の先頭1文字を大文字にします。
     * <p>nullが指定された場合はnullを返します。
     * </p>
     * 
     * @param string 文字列。nullを指定することもできます。
     * @return 結果。
     */
    protected final String capitalize(String string) {
        return StringUtils.capitalize(string);
    }

    /**
     * 指定された文字列の先頭1文字をJavaBeansの名前規則に従って小文字にします。
     * <p>JavaBeansの名前規則に従うため、2文字目が大文字の時は小文字に変換しません。
     * </p>
     * <p>nullが指定された場合はnullを返します。
     * </p>
     * 
     * @param string 文字列。nullを指定することもできます。
     * @return 結果。
     */
    protected final String decapitalize(String string) {
        return StringUtils.decapitalize(string);
    }
}
