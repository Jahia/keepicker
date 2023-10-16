<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="utility" uri="http://www.jahia.org/tags/utilityLib" %>

<utility:logger level="INFO" value="*** keepeek hidden url moduleParams size : ${currentResource.moduleParams.size}"/>

<c:set var="params" value="kpk:${not empty currentResource.moduleParams.size ?
    currentResource.moduleParams.size : 'large'}"/>

<c:set var="url" value="${currentNode.properties[params].string}"/>

<c:url value="${url}"/>
<%--<c:out value="${url}" />--%>
