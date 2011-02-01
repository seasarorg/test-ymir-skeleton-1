package ${packageName};

import org.seasar.ymir.testing.YmirTestCase;

public class ${className} extends YmirTestCase {
    private ${batchName} target;

    @Override
    public void setUp() {
        target = getComponent(${batchName}.class);
    }

    @Override
    protected void tearDown() throws Exception {
        target.destroy();
    }

    public void test() throws Exception {
        target.init(new String[] {});

        // TODO 実装して下さい。
        // assertEquals(0, target.execute());
        fail();
    }
}
