<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.Map" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Map<String, String[]> map = request.getParameterMap();
    if (map.containsKey("a") && map.containsKey("b")) {
        System.out.println("a:" + Arrays.toString(map.get("a")));
        System.out.println("b:" + Arrays.toString(map.get("b")));
        if (request.getParameter("a").equals("123") && request.getParameter("b").equals("456"))
            response.getWriter().println("success");
        else response.getWriter().println("fail");
//        response.getWriter().close();
    }
%>
<html>
<head>
    <title>Title</title>
</head>
<body>
个人简介
<img src="sample.jpg">
<form>
    <label>a
        <input type="text" name="a">
    </label>
    <label>b
        <input type="password" name="b">
    </label>
    <button type="submit">OK</button>
</form>
</body>
<link rel="stylesheet" type="text/css" href="common.css"/>
<script src="common.js" type="text/javascript"></script>
</html>
