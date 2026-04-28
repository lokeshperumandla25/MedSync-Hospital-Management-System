<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>

<h2>Fund Transfer</h2>

<form action="/MajorProject1/customer/transfer" method="post">

    <label>To Account Number:</label>
    <input type="text" name="toAccount" required /><br><br>

    <label>Amount:</label>
    <input type="number" name="amount" step="0.01" required /><br><br>

    <button type="submit">Transfer</button>
</form>

<p class="error">${error}</p>
<p class="message">${message}</p>


</body>
</html>