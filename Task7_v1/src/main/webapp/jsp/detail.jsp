<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2018/6/8
  Time: 21:50
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>个人信息</title>
</head>
<body>
<main>



        <div class="container">
            <div class="row row1">

                <div class="col-lg-12 headline">
                    <p>个人信息</p>
                </div>


                <div class="col-lg-4 carrer1">
                    <div class="headpic">
                        <div class="item1"><img class="img-responsive" src="img/head22.png"></div>
                        <div class="item2">
                            <h4>${vocation.NAME}</h4>
                            <p>${vocation.summary}</p>
                        </div>
                    </div>
                    <table class="table table-bordered table1">
                        <tbody>
                        <tr>
                            <td class="grey">门槛
                                <c:forEach begin="1" end="${vocation.threshold}">
                                    <img src="img/star.png">
                                </c:forEach>
                            </td>
                            <td class="grey">难易程度

                                <c:forEach begin="1" end="${vocation.difficulty_Level}">
                                    <img src="img/star.png">
                                </c:forEach>

                            </td>
                        </tr>
                        <tr>
                            <td class="grey">成长周期<span class="red">${vocation.growth_cycle}</span>年</td>
                            <td class="grey">稀缺程度<span class="red">${vocation.rareness}</span>家公司需要</td>
                        </tr>
                        <tr>
                            <th rowspan="3" class="salary">薪资待遇</th>
                            <td class="money"><span>0-1年</span><span>${vocation.salary_first}</span></td>
                        </tr>
                        <tr>
                            <td class="money3"><span>1-3年</span><span>${vocation.salary_second}</span></td>
                        </tr>
                        <tr>
                            <td class="money2"><span>3-5年</span><span>${vocation.salary_third}</span></td>
                        </tr>
                        <tr>
                            <th colspan="2">有<span class="red1">${vocation.number}人</span>在学</th>
                        </tr>
                        <tr>
                            <th colspan="2" class="youneed"><p>提示:在你学习之前你应该已经拥有${vocation.basic_knowledge}等领域的基础</p></th>
                        </tr>
                        </tbody>
                    </table>
                        <%--<div class="transparent">
                            <p style="font-size: 20px">IOS工程师</p>
                            <br/>
                            <br/>
                            <br/>
                            <p style="font-size: 40px">FUCK YOU</p>
                        </div>--%>
                </div>

            </div>

        </div>


</main>

</body>
</html>
