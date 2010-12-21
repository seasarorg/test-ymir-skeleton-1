package ${rootPackageName}.generator;

import ${rootPackageName}.GenericGenerator;

import org.seasar.ymir.vili.skeleton.generator.ParametersBase;
import org.seasar.ymir.vili.skeleton.generator.annotation.GUI;

@GUI(displayName = "サンプル", description = "サンプルのソースを生成します。")
public class SampleGenerator
        extends GenericGenerator<${rootPackageName}.generator.SampleGenerator.Parameters> {
    public Class<Parameters> getParametersClass() {
        return Parameters.class;
    }

    public void generate(Parameters parameters) {
        generateClass(DIR_SRC_MAIN_JAVA, "", "Sample", "Sample.ftl", parameters);
    }

    public static class Parameters extends ParametersBase {
        private String message;

        @GUI(displayName = "メッセージ", description = "メッセージを指定して下さい。")
        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
