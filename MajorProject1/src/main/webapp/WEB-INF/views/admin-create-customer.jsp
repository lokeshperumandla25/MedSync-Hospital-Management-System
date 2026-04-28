<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>

<h2>Create Customer</h2>

    <form action="/MajorProject1/admin/create-customer" method="post">

        <label>Full Name:</label>
        <input type="text" name="fullName" required><br><br>

        <label>Email:</label>
        <input type="email" name="email" required><br><br>

        <label>Mobile:</label>
        <input type="text" name="mobile" required><br><br>

        <button type="submit">Create Customer</button>
    </form>

    <p class="message">
        ${message}
    </p>

</body>
</html>