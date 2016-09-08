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
                        <li><a href="${pageContext.servletContext.contextPath}/Kontrola">Početna</a></li>
                        <li class="selected-item"><a href="${pageContext.servletContext.contextPath}/Prijava">Prijava korisnika</a></li><br/>
                    </ul>
                </nav>
            </aside>
            <section id="content" class="column-right">

                <article>
                    <h1>Prijava korisnika</h1>
                    <fieldset>
                        <form method="POST" action="${pageContext.servletContext.contextPath}/LoginConf">
                            <p><label for="user">Korisničko ime:</label>
                                <input id="korisnik" name="user"/><br /></p>	
                            <p><label for="pass">Lozinka:</label>
                                <input id="lozinka" name="pass" type="password"/><br /></p>
                            <p><input class="formbutton" name="akcija" value=" Prijavi se " type="submit"/></p>
                        </form>
                    </fieldset>
                </article>


                <footer class="clear">
                    <p>&copy; Emil Balint</p>
                </footer>

            </section>

            <div class="clear"></div>

        </section>

    </body>
</html>
