<%@ page language="java" contentType="text/javascript" %>
<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="utility" uri="http://www.jahia.org/tags/utilityLib" %>
<%@ taglib prefix="functions" uri="http://www.jahia.org/tags/functions"%>

<c:set var="keepeekConfig" value="${functions:getConfigValues('org.jahia.se.modules.keepicker_credentials')}"/>

<c:choose>
    <c:when test="${! empty keepeekConfig}">
        window.contextJsParameters.config.keepeek={
            keycloakUrl:"${keepeekConfig['keepeek_provider.front.keycloakUrl']}",
            keycloakRealm:"${keepeekConfig['keepeek_provider.front.keycloakRealm']}",
            keycloakClientId:"${keepeekConfig['keepeek_provider.front.keycloakClientId']}",
            apiEndPoint:"${keepeekConfig['keepeek_provider.front.apiEndPoint']}",
            pickerCdn:"${keepeekConfig['keepeek_provider.front.pickerCdn']}",
            applyOnPickers:"${keepeekConfig['keepeek_provider.front.applyOnPickers']}",
            mountPoint:"/sites/systemsite/contents/dam-keepeek"
        }
        console.debug("%c Keepeek config is added to contextJsParameters.config", 'color: #3c8cba');
    </c:when>
    <c:otherwise>
        <utility:logger level="warn" value="no content of keepeeknt:mountPoint available"/>
        console.warn("no content of keepeeknt:mountPoint available");
    </c:otherwise>
</c:choose>
