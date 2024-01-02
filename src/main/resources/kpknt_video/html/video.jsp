<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="utility" uri="http://www.jahia.org/tags/utilityLib" %>

<c:url var="poster" value="${currentNode.getThumbnailUrl()}"/>
<c:choose>
    <c:when test="${not empty currentResource.moduleParams.quality}">
        <video poster="${poster}" controls muted width="100%">
            <source src="${currentNode.getUrl(['quality:'.concat(currentResource.moduleParams.quality)])}">
        </video>
    </c:when>
    <c:otherwise>
        <video poster="${poster}" controls muted width="100%">
            <source src="${currentNode.getUrl(['quality:preview'])}">
            <source src="${currentNode.getUrl(['quality:480p'])}" media="(min-width: 480px)" />
            <source src="${currentNode.getUrl(['quality:1080p']}" media="(min-width: 960px)" />
        </video>
    </c:otherwise>
</c:choose>






