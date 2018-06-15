
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
    <form role="form" action="${contextpath}/u/verify" method="post" id="sub">
        <div class="form-group">
            <input type="text" class="form-control" id="name" placeholder="请输入手机号码" onchange="test()" value="" name="phone">
            <p class="alert"></p>
        </div>
        <div class="form-group">
            <input type="text" class="form-control" id="code" placeholder="请输入验证码" name="temcode">
            <p class="alert1"></p>
            <input type="button" class="btn btn-default" id="btn" style="margin-top:50px" onclick="sendemail()" value="发送验证码">
            &#12288;&#12288;&#12288;&#12288;&#12288;&#12288;
            <input type="submit" class="btn btn-default" id="verify" style="margin-top:50px" onclick="verify()" value="绑定">
            <input type="hidden"  name="type"  value="phone">
            <p class="alert2"></p>
        </div>
    </form>
</div>
<script>
    var countdown=60;
    function sendemail(){
        var phone=$("#name").val();
        var myreg = /^(((13[0-9]{1})|(15[0-9]{1})|(18[0-9]{1}))+\d{8})$/;
        if(!myreg.test(phone))
        {
            $(".alert").html("请输入有效手机号码");
            return false;
        }else {
            var obj = $("#btn");
            settime(obj);
            var phone=$("#name").val();
            var data = {"phone": phone};
            alert(phone)
            $.ajax({
                type:"POST",
                url:"${contextpath}/u/getcode/phone",
                /*表示返回值的类型*/
                datatype:'json',
                /*表明request的数据类型是json*/
                contentType: "application/json;charset=utf-8",
                data:JSON.stringify(data),
                success: function (data) {
                    var msg = data.msg_phone_binding;
                    $(".alert2").html("<font color='white'><b>请输入手机验证码</b></font>");
                    console.log(data);
                    var code = data.code;
                    if (code == "ok") {
                        $(".alert1").html("<font color='white'><b>请输入手机验证码</b></font>");
                    }
                }
            })
        }

    }
    function test(){
        var phone=$("#name").val();
        var myreg = /^(((13[0-9]{1})|(15[0-9]{1})|(18[0-9]{1}))+\d{8})$/;
        if(!myreg.test(phone))
        {
            $(".alert").html("请输入有效的手机号码");
            return false;
        }
    }
    function settime(obj) { //发送验证码倒计时
        if (countdown == 0) {
            obj.attr('disabled',false);
            //obj.removeattr("disabled");
            obj.val("获取验证码");
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