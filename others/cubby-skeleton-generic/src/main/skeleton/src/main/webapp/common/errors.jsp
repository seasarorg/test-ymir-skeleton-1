<c:if test="${r"$"}{!empty errors.all}">
  <div id="errors" class="errors">
    <ul>
    <c:forEach var="error" varStatus="s" items="${r"$"}{errors.all}">
      <li>${r"$"}{fn:replace(error, "
", "<br/>")}</li>
    </c:forEach>
    </ul>
  </div>
</c:if>
