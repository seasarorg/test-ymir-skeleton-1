package ${rootPackageName}.dto.${entityName_uncapFirst}.register;

import java.io.Serializable;

@org.seasar.ymir.annotation.Meta(name="conversion",value="${rootPackageName}.dbflute.exentity.${entityName}")
public class InputFormDtoBase
    implements Serializable
{
    private static final long serialVersionUID = 1L;

<#list table.columns as column>
    protected String ${column.name};
</#list>

<#if table.columns?size != 0> 
    public InputFormDtoBase()
    {
    }
</#if>

    public InputFormDtoBase(<#list table.columns as column>String ${column.name}<#if column_has_next>, </#if></#list>)
    {
<#list table.columns as column>
        this.${column.name} = ${column.name};
</#list>
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder().append('(');<#list table.columns as column>
        append(sb.append("${column.name}="), ${column.name})<#if column_has_next>.append(", ")</#if>;</#list>
        sb.append(')');
        return toString(sb);
    }

    protected StringBuilder append(StringBuilder sb, Object obj)
    {
        if (obj != null && obj.getClass().isArray()) {
            sb.append('{');
            int len = java.lang.reflect.Array.getLength(obj);
            String delim = "";
            for (int i = 0; i < len; i++) {
                sb.append(delim);
                delim = ", ";
                append(sb, java.lang.reflect.Array.get(obj, i));
            }
            sb.append('}');
        } else {
            sb.append(obj);
        }
        return sb;
    }

    protected String toString(StringBuilder sb)
    {
        return sb.toString();
    }

<#list table.columns as column>
    public String get${column.name?cap_first}()
    {
        return ${column.name};
    }

    public void set${column.name?cap_first}(String ${column.name})
    {
        this.${column.name} = ${column.name};
    }
</#list>
}
