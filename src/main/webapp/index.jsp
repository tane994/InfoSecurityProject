<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
   <head>
      <title>Page Redirection</title>
   </head>
   <body>
      <h1>Page Redirection</h1>
      <%
        String path;
        if (request.getAttribute("email")!=null)
            path = "home.jsp";
        else
            path = "login.jsp";

        response.setStatus(response.SC_MOVED_TEMPORARILY);
        response.setHeader("Location", path);
      %>
   </body>
</html>