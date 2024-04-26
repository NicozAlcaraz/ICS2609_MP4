<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Captcha</title>
</head>
<body>
<form action="CaptchaServlet" method="post">

<!-- Print the value -->
<p><c:out value="${captcha}" /></p>
</form>
</body>
</html>
