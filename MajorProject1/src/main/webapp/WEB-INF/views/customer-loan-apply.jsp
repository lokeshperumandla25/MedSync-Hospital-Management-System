<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Loan Apply</title>
</head>
<body>

<!-- Navigation Bar -->
<div class="nav-bar">
    <div>Smart Bank - Customer</div>
    <div>
        <a href="${pageContext.request.contextPath}/customer/dashboard">Back to Dashboard</a>
        <a href="${pageContext.request.contextPath}/customer/logout">Logout</a> 
    </div>
</div>

<div class="container">

<h2>Apply for Loan</h2>

<form action="${pageContext.request.contextPath}/customer/loan/apply" method="post">

    <label>Loan Type:</label>
    <select name="loanType" required>
        <option value="">Select</option>
        <option value="HOME">Home Loan</option>
        <option value="PERSONAL">Personal Loan</option>
        <option value="EDUCATION">Education Loan</option>
    </select><br><br>

    <label>Amount:</label>
    <input type="number" name="amount" required /><br><br>

    <label>Tenure (Months):</label>
    <input type="number" name="tenure" required /><br><br>

    <button type="submit">Apply</button>

</form>

<p class="error">${error}</p>
<p class="message">${message}</p>

</div>

</body>
</html>