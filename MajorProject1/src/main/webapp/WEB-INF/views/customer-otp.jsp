<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>

<!-- Header -->
<div class="nav-bar">
    Smart Bank - OTP Verification
</div>

<div class="container">

<h2>Enter OTP</h2>

<form action="/MajorProject1/customer/otp" method="post">
    <input type="text" name="otp" placeholder="Enter OTP" required><br><br>
    <button type="submit">Verify</button>
</form>

<p class="error">${error}</p>

</div>

</body>
</html>