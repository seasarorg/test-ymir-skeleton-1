package org.seasar.ymir.vili.skeleton.generator.ui;

import static org.seasar.ymir.vili.skeleton.generator.GeneratorUtils.newInstance;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.seasar.ymir.vili.skeleton.generator.IParameters;
import org.seasar.ymir.vili.skeleton.generator.annotation.GUI;
import org.t2framework.vili.ViliContext;

public class SpecifyParametersPage extends GeneratorWizardPage {
    private static class Parameter {
        private String name;

        private String displayName;

        private String description;

        private Parameter(String name, String displayName, String description) {
            this.name = name;
            this.displayName = displayName;
            this.description = description;
        }
    }

    private Class<? extends IParameters> parametersClass;

    private List<Text> parameters;

    protected SpecifyParametersPage() {
        super("SpecifyParametersPage");

        setTitle("パラメータの指定");
        setDescription("自動生成のためのパラメータを指定して下さい。");
    }

    public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NULL);
        composite.setFont(parent.getFont());
        composite.setLayout(new GridLayout(2, false));

        parameters = new ArrayList<Text>();
        parametersClass = newInstance(
                getGeneratorWizard().getSelectedGeneratorClass())
                .getParametersClass();
        for (Parameter parameter : buildParameters(parametersClass)) {
            new Label(composite, SWT.NONE).setText(parameter.displayName + ":");
            Text text = new Text(composite, SWT.SINGLE | SWT.BORDER);
            GridData gridData = new GridData(GridData.FILL, GridData.CENTER,
                    true, false);
            gridData.widthHint = 250;
            if (parameter.description.length() > 0) {
                text.setToolTipText(parameter.description);
            }
            text.setLayoutData(gridData);
            text.setData(parameter);
            text.addModifyListener(new ModifyListener() {
                public void modifyText(ModifyEvent event) {
                    updatePageComplete();
                }
            });
            parameters.add(text);
        }
        updatePageComplete();

        setControl(composite);
    }

    private List<Parameter> buildParameters(
            Class<? extends IParameters> parametersClass) {
        List<Parameter> parameters = new ArrayList<Parameter>();

        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(parametersClass);
        } catch (IntrospectionException ex) {
            ViliContext.getVili().log("Can't get BeanInfo: " + parametersClass,
                    ex);
            return parameters;
        }

        for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
            Method method = pd.getReadMethod();
            if (method == null) {
                method = pd.getWriteMethod();
            }
            GUI gui = method.getAnnotation(GUI.class);
            if (gui == null) {
                continue;
            }
            parameters.add(new Parameter(pd.getName(), gui.displayName(), gui
                    .description()));
        }

        return parameters;
    }

    private void updatePageComplete() {
        boolean complete = true;
        for (Text parameter : parameters) {
            if (parameter.getText().length() == 0) {
                complete = false;
                break;
            }
        }
        setPageComplete(complete);
    }

    public IParameters getParameters() {
        IParameters instance = newInstance(parametersClass);
        for (Text parameter : parameters) {
            try {
                BeanUtils.setProperty(instance, ((Parameter) parameter
                        .getData()).name, parameter.getText());
            } catch (IllegalAccessException ex) {
                ViliContext.getVili().log(ex);
            } catch (InvocationTargetException ex) {
                ViliContext.getVili().log(ex);
            }
        }
        return instance;
    }
}
