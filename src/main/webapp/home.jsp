<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    if (request.getSession().getAttribute("email")==null) {
        response.setStatus(301);
        response.setHeader("Location", "login.jsp");
    }
  %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <% it.unibz.emails.server.Headers.set(response); %>
  <link rel="stylesheet" href="style.css">
  <title>Home</title>
</head>
<body>
<<<<<<< HEAD
	<nav class="navbar">
	  <div class="box">
	  	<div>
			<img src="images/email_icon.jpg" align="left" />
			<p>E-MAIL CLIENT
				<br><% out.println(request.getAttribute("email")); %>
			</p>
	  	</div>
	  	<div id="right"><a href="login.html">Logout</a></div>
	  </div>
	</nav>
	
	<div class="grid-container">
		<form class="btn-group" action="NavigationServlet" method="post">
			<input type="hidden" name="email" value="<%= request.getAttribute("email") %>">
			<input type="hidden" name="password" value="<%= request.getAttribute("password") %>">
			<input type="submit" name="newMail" value="New Mail">
			<input type="submit" name="inbox" value="Inbox">
			<input type="submit" name="sent" value="Sent">
			<input type = "submit" name="refresh" value ="Refresh">
		</form>
		
		<%= request.getAttribute("content")!=null ? request.getAttribute("content") : "" %>
	</div>
=======
  <navbar class='flex-row'>
    <img src="images/email_icon.jpg" align="left" />
    <div class='flex-col'>
    <p>E-MAIL CLIENT
      <br><% out.println(request.getSession().getAttribute("email")); %>
    </p>
    </div>
    <div class='flex-grow'></div>
    <a href="logout">Logout</a>
  </navbar>
  
  <main>
    <form class="btn-group" action="navigation" method="post">
      <input type="hidden" name="email" value="<%= request.getAttribute("email") %>">
      <input type="hidden" name="password" value="<%= request.getAttribute("password") %>">
      <input type="submit" name="newMail" value="New Mail">
      <input type="submit" name="inbox" value="Inbox">
      <input type="submit" name="sent" value="Sent">
    </form>
    
    <%= request.getAttribute("content")!=null ? request.getAttribute("content") : "" %>
    <% if(request.getAttribute("error")!=null) { %>
        <p class='error'>Error: <%= request.getAttribute("error")%></p>
    <% } %>
  </main>
>>>>>>> b2bef8e00bb040dd335d73b7b9374b26a059f882
</body>
</html>