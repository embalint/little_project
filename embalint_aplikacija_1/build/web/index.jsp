<%-- 
    Document   : index
    Created on : Jun 9, 2016, 4:04:46 AM
    Author     : Emil
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>

        <%
            request.setCharacterEncoding("UTF-8");
            response.setCharacterEncoding("UTF-8");
        %>

        <section id="body" class="width">
            <aside id="sidebar" class="column-left">
                <header>
      
                </header>
                <nav id="mainnav">
                    <ul>
                        <li class="selected-item"><a href="${pageContext.servletContext.contextPath}/Kontrola">Početna</a></li>
                        <li><a href="${pageContext.servletContext.contextPath}/Prijava">Prijava korisnika</a></li><br/>
                    </ul>
                </nav>
            </aside>
    

            <div class="clear"></div>

        </section>

    </body>
</html>
