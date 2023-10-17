<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="utility" uri="http://www.jahia.org/tags/utilityLib" %>

<utility:logger level="DEBUG" value="*** keepeek hidden url moduleParams quality : ${currentResource.moduleParams.quality}"/>
<%--
quality = ['480p','1080p','preview']
--%>
<c:set var="params" value="kpk:${not empty currentResource.moduleParams.quality ?
    currentResource.moduleParams.quality : 'preview'}"/>

<c:url value="${currentNode.properties[params].string}"/>
<%--<c:out value="${url}" />--%>
