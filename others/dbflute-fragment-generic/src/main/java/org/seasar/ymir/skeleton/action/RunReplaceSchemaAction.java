package org.seasar.ymir.skeleton.action;

public class RunReplaceSchemaAction extends AbstractRunAction {
    @Override
    public String getProgramName() {
        return "replace-schema";
    }

    @Override
    protected boolean isConfirmBeforeExecution() {
        return true;
    }

    @Override
    protected String getCofirmationMessage() {
        return "スキーマを初期化してDDLを実行します。よろしいですか？";
    }
}
