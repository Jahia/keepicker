<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="utility" uri="http://www.jahia.org/tags/utilityLib" %>

<c:url var="poster" value="${currentNode.properties['kpk:poster'].string}"/>
<c:choose>
    <c:when test="${not empty currentResource.moduleParams.quality}">
        <template:include templateType="txt" var="stream" view="hidden.getURL">
            <template:param name="quality" value="${currentResource.moduleParams.quality}"/>
        </template:include>
        <utility:logger level="DEBUG" value="*** keepeek video stream : ${stream}"/>

        <video poster="${poster}" controls muted width="100%">
            <source src="${stream}">
        </video>
    </c:when>
    <c:otherwise>
        <c:url var="stream" value="${currentNode.properties['kpk:preview'].string}"/>
        <c:url var="stream480" value="${currentNode.properties['kpk:480p'].string}"/>
        <c:url var="stream1080" value="${currentNode.properties['kpk:1080p'].string}"/>

        <video poster="${poster}" controls muted width="100%">
            <source src="${stream}">
            <source src="${stream480}" media="(min-width: 480px)" />
            <source src="${stream1080}" media="(min-width: 960px)" />
        </video>
    </c:otherwise>
</c:choose>






