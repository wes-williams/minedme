<html>
<head>
<title>Login</title>
<head>
<body>
<br />
<h1 align="center"><span style="color: gold">MINED!</span>Me</h1>
<form method="post" action="./login">
<h3><%= request.getAttribute("errors")==null?"":"Invalid: " + request.getAttribute("errors").toString() %></h3>
<table>
<tr>
<td>Username </td>
<td><input type="text" name="username" /></td>
</tr>
<tr>
<td>Password</td>
<td><input type="password" name="password" /></td>
</tr>
<tr>
<td></td>
<td><input type="submit" name="submit" value="Login" /></td>
</tr>
</table>
</form>
</body>
</html>