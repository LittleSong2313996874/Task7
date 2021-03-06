
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%--引入JSTL核心标签库 --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page isELIgnored="false" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
    <link href="https://cdn.bootcss.com/bootstrap/4.1.1/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://cdn.bootcss.com/jquery/3.3.1/jquery.min.js"></script>
    <script src="https://cdn.bootcss.com/bootstrap/4.1.1/js/bootstrap.min.js"></script>
</head>
<style>
    .content {
        width: 400px;
        padding:50px 50px;
        margin: 100px auto;
        background-color: #0F9E5E;
        height: 400px;
        border-radius: 50px;
    }
    .form-group{
        margin-top: 50px;
    }
    .alert{
        color: darkred;
    }
</style>
<body>
<c:set var="contextpath" value="${pageContext.request.contextPath}" scope="request" />
<div class="content">
    <form role="form" action="${contextpath}/u/email/getLink" method="post" id="sub">
        <div class="form-group">
            <input type="text" class="form-control" id="name" placeholder="请输入邮箱" onchange="test()" value="" name="email">
            <p class="alert"></p>
        </div>
        <div class="form-group">
            <p class="alert1"></p>
            &#12288;&#12288;&#12288;&#12288;&#12288;&#12288;
            <input type="submit" class="btn btn-default" id="verify" style="margin-top:50px" onclick="verify()" value="点击获取激活连接">
        </div>
    </form>
</div>
<script>
    var countdown=60;
    function sendemail(){
        var phone=$("#name").val();
        var myreg = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\.[a-zA-Z0-9_-]{2,3}){1,2})$/;
        if(!myreg.test(phone))
        {
            $(".alert").html("请输入有效的邮箱");
            return false;
        }else {
            var obj = $("#btn");
            settime(obj);
            var phone=$("#name").val();
            var data = {"phone": phone};
            alert(phone)
            $.ajax({
                type:"POST",
                url:"${contextpath}/u/getcode",
                /*表示返回值的类型*/
                datatype:'json',
                /*表明request的数据类型是json*/
                contentType: "application/json;charset=utf-8",
                data:JSON.stringify(data),
                success: function (data) {
                    console.log(data);
                    var code = data.code;
                    if (code != "ok") {
                        // 这时让用户无法再更换手机号，仅能输入验证码并提交
                        var obj = $("#name");
                        obj.attr('disabled',true);
                        $(".alert1").html("<font color='white'><b>请输入邮箱激活码</b></font>");
                    }
                }
            })
        }

    }
    function test(){
        var phone=$("#name").val();
        var myreg = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\.[a-zA-Z0-9_-]{2,3}){1,2})$/;
        if(!myreg.test(phone))
        {
            $(".alert").html("请输入有效的邮箱");
            return false;
        }
    }
    function settime(obj) { //发送验证码倒计时
        if (countdown == 0) {
            obj.attr('disabled',false);
            //obj.removeattr("disabled");
            obj.val("获取激活码");
            countdown = 60;
            return;
        } else {
            obj.attr('disabled',true);
            obj.val("重新发送(" + countdown + ")");
            countdown--;
        }
        setTimeout(function() {
                settime(obj) }
            ,1000)
    }

    function verify() {
        var formElement = document.forms[0];
        //提交表单，提交到action属性指定的地方
        formElement.submit();

    }


</script>
</body>
</html>