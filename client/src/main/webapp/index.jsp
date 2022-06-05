<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
  <head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <% it.unibz.emails.client.Headers.set(response); %>
    <title>Page Redirection</title>
  </head>
  <body>
    <h1>Page Redirection</h1>
    <%
      String path;
      if (request.getSession().getAttribute("email")!=null)
          path = "home.jsp";
      else
          path = "login.jsp";

      response.setStatus(301);
      response.setHeader("Location", path);
    %>
  </body>
</html>