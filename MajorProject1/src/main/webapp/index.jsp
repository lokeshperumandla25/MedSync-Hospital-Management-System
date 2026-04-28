<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" isELIgnored="false"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Smart Bank</title>
</head>

<body>
<div class="header">
    Smart Bank Management System
</div>

<div class="container">
    <h2>Welcome to Smart Bank</h2>
    <p>Please choose your login type</p>

    <a class="btn" href="${pageContext.request.contextPath}/admin/login">
        Admin Login
    </a>

    <a class="btn" href="${pageContext.request.contextPath}/customer/login">
        Customer Login
    </a>
</div>

</body>
</html>
