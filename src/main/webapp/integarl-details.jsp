<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>积分系统</title>
    <meta name="viewport" content="width=device-width, initial-scale=1,maximum-scale=1,user-scalable=no">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <!--标准mui.css-->
    <script src="${pageContext.request.contextPath}/js/mui.min.js"></script>
    <link href="${pageContext.request.contextPath}/css/mui.min.css" rel="stylesheet"/>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/common.css"/>
    <script src="${pageContext.request.contextPath}/js/jquery-3.3.1.min.js"></script>
</head>

<body>
<header class="mui-bar mui-bar-nav">
    <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left" onclick="history.back();"></a>
    <h1 class="mui-title">${requestScope.typeName}</h1>
</header>
<div class="mui-content">
    <div class="total_details">
        <div class="total_top">
            <span>${requestScope.countName}</span>
            <p class="total">￥${requestScope.comeIn}</p>
        </div>
        <div style="margin-top:10px" class="sate_box">
            <div class="sate_one" style="height: 70px;">
                <c:forEach items="${requestScope.list}" var="i">
                    <ul class="clearfix">
                        <li class="fl l">
                            <c:if test="${i.points<0}">
                                <p>支出-<i>${i.remark}</i></p>
                            </c:if>
                            <c:if test="${i.points>0}">
                                <p>收入-<i>${i.remark}</i></p>
                            </c:if>
                            <span>${i.createTime}</span>
                        </li>
                        <li class="fl r">
                            <p>${i.points}</p>
                            <span>${i.remark}</span>
                        </li>
                    </ul>
                </c:forEach>
            </div>
        </div>
    </div>
    <c:if test="${not empty requestScope.list}">
        <div class="mui-content-padded" id="page" style="position: absolute;left: 50%;transform: translateX(-68%);">
        </div>
    </c:if>
</div>
<script>
    layui.use('laypage', function () {
        var laypage = layui.laypage;

        //执行一个laypage实例
        laypage.render({
            elem: 'page' //注意，不用加 # 号
            , count: '${requestScope.count}' //数据总数，从服务端得到
            , prev: '&laquo;'
            , next: '&raquo;'
            , groups: 3
            , limit: 10
            , curr: '${requestScope.curr}'
            , layout: ['prev', 'page', 'next']
            , theme: '#cd473f'
            , jump: function (obj, first) {
                //首次不执行
                if (!first) {
                    window.location.href = "${pageContext.request.contextPath}/user/appGetIntegralList?type=${requestScope.type}&pageNum=" + obj.curr + "&limit=" + obj.limit;
                }
            }
        });
    });
</script>
</body>
<script src="${pageContext.request.contextPath}/js/all.js"></script>
</html>