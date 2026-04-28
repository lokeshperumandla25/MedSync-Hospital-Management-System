<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>

<h2>Welcome Admin</h2>
<!--  
<div class="stats">
    <div class="card">Total Customers<br>${totalCustomers}</div>
    <div class="card">Active Customers<br>${activeCustomers}</div>
    <div class="card">Inactive Customers<br>${inactiveCustomers}</div>
    <div class="card">Pending Loans<br>${pendingLoans}</div>
    <div class="card">Approved Loans<br>${approvedLoans}</div>
</div>
-->

<ul>
    <li><a href="/MajorProject1/admin/create-customer">Create Customer</a></li>
    <li><a href="/MajorProject1/admin/customers">Manage Customers</a></li>
    <li><a href="/MajorProject1/admin/loans">Manage Loans</a></li>
   <!--  <li><a href="${pageContext.request.contextPath}/admin/reports">Generate Reports</a></li> -->
</ul>

</body>
</html>