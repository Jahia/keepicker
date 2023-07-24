<%@ page language="java" contentType="text/javascript" %>
<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="utility" uri="http://www.jahia.org/tags/utilityLib" %>
<%--<%@ taglib prefix="keepeek" uri="http://www.jahia.org/se/keepeek" %>--%>

<%--<c:set var="keepeekConfig" value="${keepeek:config()}"/>--%>

<c:choose>
    <c:when test="${! empty keepeekConfig}">
        window.contextJsParameters.config.keepeek={
            <%--Not need anymore due to proxy usage --%>
<%--            <c:if test="${! empty keepeekConfig['keepeek_provider.apiSchema']}">--%>
<%--                apiSchema:"${keepeekConfig['keepeek_provider.apiSchema']}",--%>
<%--            </c:if>--%>
<%--            <c:if test="${! empty keepeekConfig['keepeek_provider.apiEndPoint']}">--%>
<%--                apiEndPoint:"${keepeekConfig['keepeek_provider.apiEndPoint']}",--%>
<%--            </c:if>--%>
<%--            <c:if test="${! empty keepeekConfig['keepeek_provider.apiVersion']}">--%>
<%--                apiVersion:"${keepeekConfig['keepeek_provider.apiVersion']}",--%>
<%--            </c:if>--%>
<%--            <c:if test="${! empty keepeekConfig['keepeek_provider.apiSecret']}">--%>
<%--                apiSecret:"${keepeekConfig['keepeek_provider.apiSecret']}",--%>
<%--            </c:if>--%>
            <%--  ---  --%>
<%--            <c:if test="${! empty keepeekConfig['keepeek_provider.apiKey']}">--%>
<%--                apiKey:"${keepeekConfig['keepeek_provider.apiKey']}",--%>
<%--            </c:if>--%>
<%--            <c:if test="${! empty keepeekConfig['keepeek_provider.cloudName']}">--%>
<%--                cloudName:"${keepeekConfig['keepeek_provider.cloudName']}",--%>
<%--            </c:if>--%>
            mountPoint:"/sites/systemsite/contents/dam-keepeek"
        }
        console.debug("%c Keepeek config is added to contextJsParameters.config", 'color: #3c8cba');
    </c:when>
    <c:otherwise>
        <utility:logger level="warn" value="no content of keepeeknt:mountPoint available"/>
        console.warn("no content of keepeeknt:mountPoint available");
    </c:otherwise>
</c:choose>
