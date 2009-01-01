<c:if test="${r"$"}{f:containsKey(flash, 'notice')}">
<div id="notice" class="notice">${r"$"}{f:out(flash['notice'])}</div>
</c:if>
