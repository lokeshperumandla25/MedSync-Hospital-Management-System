<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>


<h2>Reset Password (First Login)</h2>

<form action="/MajorProject1/customer/reset-password" method="post">
    <label>New Password:</label>
    <input type="password" name="newPassword" required><br><br>

    <button type="submit">Reset Password</button>
</form>

<p class="error">${error}</p>

</body>
</html>