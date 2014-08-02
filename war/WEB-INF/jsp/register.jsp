<%@ page import="com.minedme.social.util.RecaptchaUtil" %>
<html>
<head>
<title>Register</title>
<head>
<body>
<br />
<h1 align="center"><span style="color: gold">MINED!</span>Me</h1>
 <script type="text/javascript">
 var RecaptchaOptions = {
    theme : 'clean'
 };
 </script>
<form method="post" action="./register">
<h3><%= request.getAttribute("errors")==null?"":"Invalid: " + request.getAttribute("errors").toString() %></h3>
<table>
<tr>
<td>Username </td>
<td><input type="text" name="username" /></td>
</tr>
<tr>
<td>Password 1 </td>
<td><input type="password" name="password1" /></td>
</tr>
<tr>
<td>Password 2 </td>
<td><input type="password" name="password2" /></td>
</tr>
<tr>
<td>Nickname </td>
<td><input type="text" name="nickname" /></td>
</tr>
<tr>
<td>Keep Private </td>
<td><input type="checkbox" name="privacy" value="1" /></td>
</tr>
<tr><td colspan="2">   
<%
          out.print(RecaptchaUtil.newRecaptcha());
 %>
</td></tr>
<tr>
<td></td>
<td><input type="submit" name="submit" value="Register" /></td>
</tr>
</table>
</form>
</body>
</html>