<%@page import="customer.CustomerBean"%>
<%@page import="customer.Customer"%>
<%@page import="customer.CustomerBeanLocal"%>
<%@page import="javax.ejb.EJB"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<html>
    <head>
        <title></title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" href="project2.css" type="text/css">
    </head>
    <body>
        <%
        // Couldnt figure out how to get EJB annotations into JSP pages and google was no help so we forwarded to Homepage instead.
        // We of course know how to use JSP pages as we showed in Project 1
        response.sendRedirect("/Project2Final-war/homePage");
        %>
    </body>
</html>

