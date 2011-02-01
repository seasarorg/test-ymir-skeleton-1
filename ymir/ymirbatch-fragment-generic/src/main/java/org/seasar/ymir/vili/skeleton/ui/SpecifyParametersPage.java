package org.seasar.ymir.vili.skeleton.ui;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class SpecifyParametersPage extends WizardPage {
    private Text batchNameControl;

    private Button generateTestClassControl;

    private Button generateBatControl;

    private Button generateShControl;

    private Button overwriteControl;

    protected SpecifyParametersPage() {
        super("SpecifyParametersPage");

        setTitle("バッチの追加");
        setDescription("バッチを追加します。");
    }

    public void createControl(Composite parent) {
        Composite control = new Composite(parent, SWT.NULL);
        control.setFont(parent.getFont());
        control.setLayout(new GridLayout(2, false));

        new Label(control, SWT.NONE).setText("バッチクラス名" + ":");
        batchNameControl = new Text(control, SWT.SINGLE | SWT.BORDER);
        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true,
                false);
        gridData.widthHint = 250;
        batchNameControl
                .setToolTipText("バッチクラスの名前を指定して下さい。バッチクラスは<ルートパッケージ>.batchパッケージ以下に作成されます。");
        batchNameControl.setLayoutData(gridData);
        batchNameControl.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                setPageComplete(validatePage());
            }
        });

        overwriteControl = new Button(control, SWT.CHECK | SWT.LEFT);
        GridData data = new GridData();
        data.horizontalSpan = 2;
        overwriteControl.setLayoutData(data);
        overwriteControl.setText("既にファイルが存在する場合は上書きする");
        overwriteControl.setSelection(false);

        generateTestClassControl = new Button(control, SWT.CHECK | SWT.LEFT);
        data = new GridData();
        data.horizontalSpan = 2;
        generateTestClassControl.setLayoutData(data);
        generateTestClassControl.setText("テストクラスを生成する");
        generateTestClassControl.setSelection(true);

        generateBatControl = new Button(control, SWT.CHECK | SWT.LEFT);
        data = new GridData();
        data.horizontalSpan = 2;
        generateBatControl.setLayoutData(data);
        generateBatControl.setText("実行ファイル（*.bat）を生成する");
        generateBatControl.setSelection(true);

        generateShControl = new Button(control, SWT.CHECK | SWT.LEFT);
        data = new GridData();
        data.horizontalSpan = 2;
        generateShControl.setLayoutData(data);
        generateShControl.setText("実行ファイル（*.sh）を生成する");
        generateShControl.setSelection(true);

        setControl(control);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            setPageComplete(validatePage());
        }
    }

    private boolean validatePage() {
        if (batchNameControl.getText().length() == 0) {
            return false;
        }

        return true;
    }

    public String getBatchName() {
        return batchNameControl.getText().trim();
    }

    public boolean shouldGenerateTestClass() {
        return generateTestClassControl.getSelection();
    }

    public boolean shouldGenerateBat() {
        return generateBatControl.getSelection();
    }

    public boolean shouldGenerateSh() {
        return generateShControl.getSelection();
    }

    public boolean shouldOverwrite() {
        return overwriteControl.getSelection();
    }
}
