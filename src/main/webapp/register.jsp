<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <% it.unibz.emails.server.Headers.set(response); %>
  <title>Register</title>
  <link rel="stylesheet" href="style.css">
</head>
<body class="section">
  <h1>Register</h1>
  <form action="register" method="post">
  <table>
    <tr>
      <td><label for="name">Name:</label></td>
      <td><input type="text" name="name" id="name" required></td>
    </tr>
    <tr>
      <td><label for="surname">Surname:</label></td>
      <td><input type="text" name="surname" id="surname" required></td>
    </tr>
    <tr>
      <td><label for="email">Email:</label></td>
      <td><input type="text" name="email" id="email" required></td>
    </tr>
    <tr>
      <td><label for="email">Password:</label></td>
      <td><input type="password" name="password" id="password" required></td>
    </tr>
    <tr>
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