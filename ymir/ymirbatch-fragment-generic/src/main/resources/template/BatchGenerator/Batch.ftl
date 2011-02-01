package ${packageName};

import org.seasar.ymir.batch.Batch;

public class ${className} implements Batch {
    @Override
    public boolean init(String[] args) throws Exception {
        return true;
    }

    @Override
    public int execute() throws Exception {
        // TODO 実装してください。
        return 0;
    }

    @Override
    public void destroy() throws Exception {
    }
}
