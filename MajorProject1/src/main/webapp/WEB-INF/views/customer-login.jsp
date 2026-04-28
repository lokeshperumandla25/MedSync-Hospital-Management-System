<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>

<form action="/MajorProject1/customer/login" method="post">
	Account Number:<input type="text" name="accountNumber" required><br><br>
	Password:<input type="password" name="password"><br><br>
	
	<button type="submit">Login</button>
</form>
<div>
        <a href="/MajorProject1/">Back</a>
    </div>
<p class="error">${error}</p>

</div>

</body>
</html>