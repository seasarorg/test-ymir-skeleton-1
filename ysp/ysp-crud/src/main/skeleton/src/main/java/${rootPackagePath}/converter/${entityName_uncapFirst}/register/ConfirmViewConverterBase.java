package ${rootPackageName}.converter.${entityName_uncapFirst}.register;

import java.util.ArrayList;
import java.util.List;

import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.seasar.ymir.converter.TypeConversionManager;
import org.seasar.ymir.message.Messages;

import ${rootPackageName}.dbflute.exentity.${entityName};
import ${rootPackageName}.dto.${entityName_uncapFirst}.register.ConfirmViewDto;

public class ConfirmViewConverterBase
{
    protected TypeConversionManager typeConversionManager;

    protected Messages messages;

    @Binding(bindingType = BindingType.MUST)
    final public void setConversionManager(
        TypeConversionManager typeConversionManager) {
        this.typeConversionManager = typeConversionManager;
    }

    @Binding(bindingType = BindingType.MUST)
    final public void setMessages(Messages messages) {
        this.messages = messages;
    }

    final protected TypeConversionManager getTypeConversionManager() {
        return typeConversionManager;
    }

    final protected Messages getMessages() {
        return messages;
    }

    final protected <T> T convert(Object value, Class<T> type) {
        return typeConversionManager.convert(value, type);
    }

    final protected String valueOf(Object obj) {
        if (obj == null) {
            return null;
        }
        return String.valueOf(obj);
    }

    final protected boolean isEmpty(Object obj) {
        return (obj == null || obj instanceof String
            && ((String) obj).trim().length() == 0);
    }

    final protected <T> T emptyToNull(T obj) {
        if (isEmpty(obj)) {
            return null;
        } else {
            return obj;
        }
    }

    public ConfirmViewDto copyTo(ConfirmViewDto dto, ${entityName} entity)
    {
<#list table.columns as column>
        copy${column.name?cap_first}To(dto, entity);
</#list>

        return dto;
    }

    public ConfirmViewDto[] copyTo(${entityName}[] entities)
    {
        ConfirmViewDto[] dtos = new ConfirmViewDto[entities.length];
        for (int i = 0; i < entities.length; i++) {
            dtos[i] = copyTo(new ConfirmViewDto(), entities[i]);
        }
        return dtos;
    }

    public List<ConfirmViewDto> copyToDtoList(List<${entityName}> entityList)
    {
        List<ConfirmViewDto> dtoList = new ArrayList<ConfirmViewDto>();
        for (${entityName} entity : entityList) {
            dtoList.add(copyTo(new ConfirmViewDto(), entity));
        }
        return dtoList;
    }

<#list table.columns as column>
    protected void copy${column.name?cap_first}To(ConfirmViewDto dto, ${entityName} entity)
    {
        dto.set${column.name?cap_first}(convert(entity.get${column.name?cap_first}(), String.class));
    }

</#list>

    public ${entityName} copyTo(${entityName} entity, ConfirmViewDto dto)
    {
<#list table.columns as column>
        copy${column.name?cap_first}To(entity, dto);
</#list>
        return entity;
    }

    public ${entityName}[] copyTo(ConfirmViewDto[] dtos)
    {
        ${entityName}[] entities = new ${entityName}[dtos.length];
        for (int i = 0; i < dtos.length; i++) {
            entities[i] = copyTo(new ${entityName}(), dtos[i]);
        }
        return entities;
    }

    public List<${entityName}> copyToEntityList(List<ConfirmViewDto> dtoList)
    {
        List<${entityName}> entityList = new ArrayList<${entityName}>();
        for (ConfirmViewDto dto : dtoList) {
            entityList.add(copyTo(new ${entityName}(), dto));
        }
        return entityList;
    }

<#list table.columns as column>
    protected void copy${column.name?cap_first}To(${entityName} entity, ConfirmViewDto dto)
    {
        entity.set${column.name?cap_first}(convert(dto.get${column.name?cap_first}(), ${column.type}.class));
    }

</#list>
}
