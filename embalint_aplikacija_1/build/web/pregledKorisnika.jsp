<%-- 
    Document   : pregledKorisnika
    Created on : Jul 11, 2016, 12:38:07 AM
    Author     : Emil
--%>

<%@page import="org.foi.nwtis.embalint.web.Registry"%>
<%@page import="org.foi.nwtis.embalint.web.Korisnik"%>
<%@page import="java.util.List"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <% List<Korisnik> userList = (List<Korisnik>) Registry.getInstance().get("users"); %>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Pregled korisnika</title>
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
                    <th>Ime</th>
                    <th>Prezime</th>
                    <th>KorIme</th>
                    <th>Kategorija</th>
                    <th>Uloga</th>
                    <th>Password</th>
                </tr>
            </thead>
            
            <c:forEach items="${users}" var="person">
            <tr>
                <td><c:out value="${person.ime}"/></td>
                <td><c:out value="${person.prezime}"/></td>
                <td><c:out value="${person.korIme}"/></td>
                <td><c:out value="${person.kategorija}"/></td>
                <td><c:out value="${person.uloga}"/></td>
                <td><c:out value="${person.password}"/></td>

            </tr>
            </c:forEach>
        </table>
    </body>
</html>
