<%-- 
    Document   : dnevnik
    Created on : Jul 11, 2016, 3:29:19 AM
    Author     : Emil
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Dnevnik</title>
    </head>
    <body>
        <nav id="mainnav">
                    <ul>
                        <li class="selected-item"><a href="${pageContext.servletContext.contextPath}/Kontrola">Početna</a></li>
                        <li><a href="${pageContext.servletContext.contextPath}/Prijava">Prijava korisnika</a></li><br/>
                    </ul>
                </nav>
                    ADMIN navigacija
                    <nav id="mainnav"  >
                    <ul>
                        <li class="selected-item"><a href="${pageContext.servletContext.contextPath}/Dnevnik">Dnevnik</a></li>
                        <li class="selected-item"><a href="${pageContext.servletContext.contextPath}/PregledKorisnika">Pregled Korisnika</a></li>
                    </ul>
                </nav>
                    
                    <table border="1" cellspacing='0'>
            <thead>
                <tr>
                    <th>IPadresa</th>
                    <th>Korisnik</th>
                    <th>Operacija</th>
                    <th>Rezultat</th>
                    <th>Trajanje</th>
                    <th>Vrijeme</th>
                </tr>
            </thead>
            
            <c:forEach items="${dnevnik}" var="dnevnik">
            <tr>
                <td><c:out value="${dnevnik.ipAdresa}"/></td>
                <td><c:out value="${dnevnik.korisnik}"/></td>
                <td><c:out value="${dnevnik.operacija}"/></td>
                <td><c:out value="${dnevnik.rezultat}"/></td>
                <td><c:out value="${dnevnik.trajanje}"/></td>
                <td><c:out value="${dnevnik.vrijeme}"/></td>

            </tr>
            </c:forEach>
        </table>
    </body>
</html>
