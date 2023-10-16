<%@ page language="java" contentType="text/javascript" %>
<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="utility" uri="http://www.jahia.org/tags/utilityLib" %>
<%--<%@ taglib prefix="keepeek" uri="http://www.jahia.org/se/keepeek" %>--%>

<%--<c:set var="keepeekConfig" value="${keepeek:config()}"/>--%>

window.contextJsParameters.config.keepeek={
    mountPoint:"/sites/systemsite/contents/dam-keepeek"
}
console.debug("%c Keepeek config is added to contextJsParameters.config", 'color: #3c8cba');

<%--<c:choose>--%>
<%--    <c:when test="${! empty keepeekConfig}">--%>
<%--        window.contextJsParameters.config.keepeek={--%>
<%--            &lt;%&ndash;Not need anymore due to proxy usage &ndash;%&gt;--%>
<%--&lt;%&ndash;            <c:if test="${! empty keepeekConfig['keepeek_provider.apiSchema']}">&ndash;%&gt;--%>
<%--&lt;%&ndash;                apiSchema:"${keepeekConfig['keepeek_provider.apiSchema']}",&ndash;%&gt;--%>
<%--&lt;%&ndash;            </c:if>&ndash;%&gt;--%>
<%--&lt;%&ndash;            <c:if test="${! empty keepeekConfig['keepeek_provider.apiEndPoint']}">&ndash;%&gt;--%>
<%--&lt;%&ndash;                apiEndPoint:"${keepeekConfig['keepeek_provider.apiEndPoint']}",&ndash;%&gt;--%>
<%--&lt;%&ndash;            </c:if>&ndash;%&gt;--%>
<%--&lt;%&ndash;            <c:if test="${! empty keepeekConfig['keepeek_provider.apiVersion']}">&ndash;%&gt;--%>
<%--&lt;%&ndash;                apiVersion:"${keepeekConfig['keepeek_provider.apiVersion']}",&ndash;%&gt;--%>
<%--&lt;%&ndash;            </c:if>&ndash;%&gt;--%>
<%--&lt;%&ndash;            <c:if test="${! empty keepeekConfig['keepeek_provider.apiSecret']}">&ndash;%&gt;--%>
<%--&lt;%&ndash;                apiSecret:"${keepeekConfig['keepeek_provider.apiSecret']}",&ndash;%&gt;--%>
<%--&lt;%&ndash;            </c:if>&ndash;%&gt;--%>
<%--            &lt;%&ndash;  ---  &ndash;%&gt;--%>
<%--&lt;%&ndash;            <c:if test="${! empty keepeekConfig['keepeek_provider.apiKey']}">&ndash;%&gt;--%>
<%--&lt;%&ndash;                apiKey:"${keepeekConfig['keepeek_provider.apiKey']}",&ndash;%&gt;--%>
<%--&lt;%&ndash;            </c:if>&ndash;%&gt;--%>
<%--&lt;%&ndash;            <c:if test="${! empty keepeekConfig['keepeek_provider.cloudName']}">&ndash;%&gt;--%>
<%--&lt;%&ndash;                cloudName:"${keepeekConfig['keepeek_provider.cloudName']}",&ndash;%&gt;--%>
<%--&lt;%&ndash;            </c:if>&ndash;%&gt;--%>
<%--            mountPoint:"/sites/systemsite/contents/dam-keepeek"--%>
<%--        }--%>
<%--        console.debug("%c Keepeek config is added to contextJsParameters.config", 'color: #3c8cba');--%>
<%--    </c:when>--%>
<%--    <c:otherwise>--%>
<%--        <utility:logger level="warn" value="no content of keepeeknt:mountPoint available"/>--%>
<%--        console.warn("no content of keepeeknt:mountPoint available");--%>
<%--    </c:otherwise>--%>
<%--</c:choose>--%>
