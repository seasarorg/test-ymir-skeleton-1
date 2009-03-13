package org.seasar.ymir.skeleton.ysp;

import static org.seasar.ymir.vili.ViliBehavior.PREFIX_TEMPLATE_PARAMETER;
import static org.seasar.ymir.vili.ViliBehavior.SUFFIX_TEMPLATE_PARAMETER_CANDIDATES;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.seasar.kvasir.util.PropertyUtils;
import org.seasar.ymir.vili.AbstractConfigurator;
import org.seasar.ymir.vili.Activator;
import org.seasar.ymir.vili.ViliBehavior;
import org.seasar.ymir.vili.ViliProjectPreferences;

public class Configurator extends AbstractConfigurator {
    @Override
    public void start(IProject project, ViliBehavior behavior,
            ViliProjectPreferences preferences) {
        String className = preferences.getRootPackageName()
                + ".dbflute.allcommon.DBMetaInstanceHandler";
        Class<?> dbMetaInstanceHandlerClass = null;
        try {
            dbMetaInstanceHandlerClass = Class.forName(className);
        } catch (ClassNotFoundException ignore) {
            log("Can't find Class: " + className, ignore);
        }
        if (dbMetaInstanceHandlerClass != null) {
            behavior.getProperties().setProperty(
                    PREFIX_TEMPLATE_PARAMETER + "tableName"
                            + SUFFIX_TEMPLATE_PARAMETER_CANDIDATES,
                    PropertyUtils
                            .join(getTableNames(dbMetaInstanceHandlerClass)));
            behavior.notifyPropertiesChanged();
        }
    }

    @Override
    public void processBeforeExpanding(IProject project, ViliBehavior behavior,
            ViliProjectPreferences preferences, Map<String, Object> parameters,
            IProgressMonitor monitor) {
        String tableName = (String) parameters.get("tableName");
        String entityName = toEntityName(tableName);
        parameters.put("entityName", entityName);
        parameters.put("entityName_uncapFirst", uncapFirst(entityName));

        String className = preferences.getRootPackageName()
                + ".dbflute.exentity." + entityName;
        List<Column> columnList = new ArrayList<Column>();
        try {
            Class<?> entityClass = Class.forName(className);
            gatherColumns(entityClass, columnList);
        } catch (ClassNotFoundException ignore) {
            log("Can't find Class: " + className, ignore);
        }

        Table table = new Table();
        table.setColumns(columnList.toArray(new Column[0]));
        parameters.put("table", table);
    }

    @SuppressWarnings("unchecked")
    void gatherColumns(Class<?> entityClass, List<Column> columnList) {
        List<Object> columnInfoList;
        try {
            Object entity = entityClass.newInstance();
            Object dbMeta = entity.getClass().getMethod("getDBMeta").invoke(
                    entity);
            columnInfoList = (List<Object>) dbMeta.getClass().getMethod(
                    "getColumnInfoList").invoke(dbMeta);
        } catch (Throwable t) {
            log("Can't get or invoke : " + entityClass.getName()
                    + "#getDBMeta() or DBMeta#getColumnInfoList()", t);
            return;
        }
        for (Object columnInfo : columnInfoList) {
            try {
                columnList.add(new Column(((Class<?>) columnInfo.getClass()
                        .getMethod("getPropertyType").invoke(columnInfo))
                        .getName(), (String) columnInfo.getClass().getMethod(
                        "getPropertyName").invoke(columnInfo)));
            } catch (Throwable ignore) {
                log("Can't invoke ColumnInfo#getPropertyType()"
                        + " or ColumnInfo#getPropertyName()", ignore);
            }
        }
        Collections.sort(columnList, new Comparator<Column>() {
            public int compare(Column o1, Column o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
    }

    private void log(String message, Throwable cause) {
        Activator.getLog().log(
                new Status(IStatus.ERROR, Globals.ID, message, cause));
    }

    String toEntityName(String tableName) {
        if (tableName == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (StringTokenizer st = new StringTokenizer(tableName.toLowerCase(),
                "_"); st.hasMoreTokens();) {
            sb.append(capFirst(st.nextToken()));
        }
        return sb.toString();
    }

    String capFirst(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    String uncapFirst(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }

    @SuppressWarnings("unchecked")
    String[] getTableNames(Class<?> dbMetaInstanceHandlerClass) {
        Method method;
        try {
            method = dbMetaInstanceHandlerClass.getMethod("getDBMetaMap");
        } catch (Throwable t) {
            log("Can't get Method object: "
                    + "DBMetaInstanceHandler#getDBMetaMap()", t);
            return new String[0];
        }

        Map<String, Object> map;
        try {
            map = (Map<String, Object>) method.invoke(null);
        } catch (Throwable t) {
            log("Can't invoke DBMetaInstanceHandler#getDBMetaMap()", t);
            return new String[0];
        }

        String[] tableNames = map.keySet().toArray(new String[0]);
        Arrays.sort(tableNames);

        return tableNames;
    }
}
