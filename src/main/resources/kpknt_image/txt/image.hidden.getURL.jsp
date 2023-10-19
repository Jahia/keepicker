<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="utility" uri="http://www.jahia.org/tags/utilityLib" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<utility:logger level="DEBUG" value="*** keepeek hidden url moduleParams width : ${currentResource.moduleParams.width}"/>
<utility:logger level="DEBUG" value="*** keepeek node id : ${currentNode.identifier}"/>

<%--
props= kpk:['xlarge','large','medium','small','whr','poster']
--%>

<%-- When content is optimized use this --%>
<%--<fmt:parseNumber var="width" value="${currentResource.moduleParams.width}" integerOnly="true"/>--%>
<%--<c:choose>--%>
<%--    <c:when test="${width >= 1400}">--%>
<%--        <c:set var="params" value="kpk:xlarge"/>--%>
<%--    </c:when>--%>
<%--    <c:when test="${width <= 960}">--%>
<%--        <c:set var="params" value="kpk:large"/>--%>
<%--    </c:when>--%>
<%--    <c:when test="${width <= 780}">--%>
<%--        <c:set var="params" value="kpk:medium"/>--%>
<%--    </c:when>--%>
<%--    <c:when test="${width <= 256}">--%>
<%--        <c:set var="params" value="kpk:small"/>--%>
<%--    </c:when>--%>
<%--    <c:otherwise>--%>
<%--        <c:set var="params" value="kpk:whr"/>--%>
<%--    </c:otherwise>--%>
<%--</c:choose>--%>

<c:set var="params" value="kpk:whr"/>

<c:set var="url" value="${currentNode.properties[params].string}"/>

<c:url value="${url}"/>
<%--<c:out value="${url}" />--%>
