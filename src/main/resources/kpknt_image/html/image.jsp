<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="utility" uri="http://www.jahia.org/tags/utilityLib" %>

<c:set var="alt" value="${fn:escapeXml(currentNode.displayableName)}"/>

<template:include templateType="txt" var="url" view="hidden.getURL">
    <template:param name="size" value="${currentResource.moduleParams.size}"/>
</template:include>

<utility:logger level="DEBUG" value="*** keepeek asset url : ${url}"/>

<img src="${url}" width="100%"
     class="${currentResource.moduleParams.class}"
     alt="${alt}"
/>
