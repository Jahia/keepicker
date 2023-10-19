<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="utility" uri="http://www.jahia.org/tags/utilityLib" %>

<c:set var="keepeekNode" value="${currentNode.properties['j:node'].node}"/>
<c:set var="referenceView" value="${not empty currentNode.properties['j:referenceView'] ?
    currentNode.properties['j:referenceView'].string :
    'default'}"/>

<%--<c:set var="width" value="${currentNode.properties['cloudy:width']}"/>--%>
<%--<c:set var="height" value="${currentNode.properties['cloudy:height']}"/>--%>

<template:module node="${keepeekNode}" editable="false" view="${referenceView}"/>
<%--    <template:param name="width" value="${width}"/>--%>
<%--</template:module>--%>
