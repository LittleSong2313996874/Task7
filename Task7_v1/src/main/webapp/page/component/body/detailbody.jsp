<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%--引入JSTL核心标签库 --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page isELIgnored="false" %>


<main>



        <div class="container">
            <div class="row row1">

                <div class="col-lg-12 headline">
                    <p>个人信息</p>
                </div>


                <div class="col-lg-4 carrer1">
                    <div class="headpic">
                        <div class="item1"><img class="img-responsive" src="${user.portrait}"></div>
                        &#12288;&#12288;
                        <div class="item2">
                            <h4>昵称： ${user.username}</h4>
                            <p>登录时间：${user.loginTime}</p>
                        </div>
                    </div>
                    <table class="table table-bordered table1">
                        <tbody>
                            <tr>
                                <th colspan="2" class="grey">
                                    <c:if test="${user.portrait == null }">
                                        <p><a href="${contextpath}/jsp/upload.jsp">上传头像</a><p>
                                    </c:if>
                                    <c:if test="${user.portrait != null }">
                                        <p><a href="${contextpath}/jsp/upload.jsp">更换头像</a><p>
                                    </c:if>
                                </th>
                            </tr>
                            <tr>
                                <td class="grey">手机
                                </td>
                                <td class="grey">
                                    <c:if test="${user.phone != null && !user.phone.equals('')}">
                                        <p>${user.phone}  </p>
                                        <p><a href="${contextpath}/u/bind?action=undo&target=phone">解绑(还没做)</a></p>
                                    </c:if>
                                    <c:if test="${user.phone == null|| user.phone.equals('')}">
                                        <p>无 </p>
                                        <p><a href="${contextpath}/u/bind?action=do&target=phone">去绑定</a></p>
                                    </c:if>
                                </td>
                            </tr>

                            <tr>
                                <td class="grey">邮箱
                                </td>
                                <td class="grey">
                                    <c:if test="${user.email != null }">
                                        <p>${user.email}</p>
                                        <p><a href="${contextpath}/u/bind?action=undo&target=email">解绑(还没做)</a></p>
                                    </c:if>
                                    <c:if test="${user.email == null }">
                                        <p>无</p>
                                        <p><a href="${contextpath}/u/bind?action=do&target=email">去绑定</a></p>
                                    </c:if>
                                </td>
                            </tr>
                            <tr>
                                <td colspan="2"><span class="">The cycle of life and death continues,we will live,they will die.</span><br/>&nbsp;&nbsp;——沙漠死神</td>
                            </tr>
                        </tbody>
                    </table>

                </div>

            </div>

        </div>


</main>

