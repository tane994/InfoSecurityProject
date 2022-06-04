<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <% it.unibz.emails.client.Headers.set(response); %>
  <% it.unibz.emails.client.BaseServlet.setCsrfToken(request,response); %>
  <title>Register</title>
  <link rel="stylesheet" href="style.css">
</head>
<body class="section">
  <h1>Register</h1>
  <form action="register" method="post">
  <table>
    <tr>
      <td><label for="name">Name:</label></td>
      <td><input type="text" name="name" id="name" minlength="3" required></td>
    </tr>
    <tr>
      <td><label for="surname">Surname:</label></td>
      <td><input type="text" name="surname" id="surname" minlength="3" required></td>
    </tr>
    <tr>
      <td><label for="email">Email:</label></td>
      <td><input type="email" name="email" id="email" minlength="5" required></td>
    </tr>
    <tr>
      <td><label for="password">Password:</label></td>
      <td><input type="password" name="password" id="password" minlength="8" required></td>
    </tr>
    <tr>
      <input type="hidden" name="csrf" value=<%=request.getSession().getAttribute("csrf")%>>
      <td><input type="submit" value="Register"></td>
    </tr>
    <% if(request.getAttribute("error")!=null) { %>
    <tr>
      <td class='error'>Error: <%= request.getAttribute("error")%></td>
    </tr>
    <% } %>
  </table>
  </form>
  <p>Or <a href="login.jsp">login</a></p>
</body>
</html>