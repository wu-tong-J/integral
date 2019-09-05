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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mui.min.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/common.css"/>
</head>

<body>
<header class="mui-bar mui-bar-nav">
    <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left" onclick="history.back();"></a>
    <h1 class="mui-title">我的积分</h1>
</header>
<div class="mui-content">
    <div class="integral_total">
        <span>总积分</span>
        <p class="total">${requestScope.integralUser.integralPoints}</p>
    </div>
    <div style="margin-top:10px" class="sate_box">
        <div class="sate_one">
            <c:if test="${requestScope.integralUser.role==3 || requestScope.integralUser.role==9}">
                <ul class="clearfix">
                    <li class="fl l">赠分</li>
                    <li class="fl r">
                        <span class="num">${requestScope.integralUser.bonusPoints}</span>
                    </li>
                </ul>
            </c:if>
            <c:if test="${requestScope.integralUser.role==2 || requestScope.integralUser.role==9}">
                <ul class="clearfix">
                    <li class="fl l">工分</li>
                    <li class="fl r">
                        <span class="num">${requestScope.integralUser.workPoints}</span>
                    </li>
                </ul>
                <ul class="clearfix">
                    <li class="fl l">贡献度等级</li>
                    <li class="fl r">
                        <span class="num">${requestScope.gxd}</span>
                    </li>
                </ul>
                <ul class="clearfix">
                    <li class="fl l">工分明细</li>
                    <li class="fl r">
                        <a href="${pageContext.request.contextPath}/user/appGetIntegralList?type=3"><i class="mui-icon mui-icon-arrowright"></i></a>
                    </li>
                </ul>
            </c:if>
            <!--老人、义工、普通用户都有积分明细-->
            <ul class="clearfix">
                <li class="fl l">积分明细</li>
                <li class="fl r">
                    <a href="${pageContext.request.contextPath}/user/appGetIntegralList?type=1"><i class="mui-icon mui-icon-arrowright"></i></a>
                </li>
            </ul>
            <ul class="clearfix">
                <li class="fl l">赠分明细</li>
                <li class="fl r">
                    <a href="${pageContext.request.contextPath}/user/appGetIntegralList?type=2"><i class="mui-icon mui-icon-arrowright"></i></a>
                </li>
            </ul>
            <ul class="clearfix">
                <li class="fl l">兑换赠分</li>
                <li class="fl r">
                    <a href="${pageContext.request.contextPath}/user/appToExchangeBonus"><i class="mui-icon mui-icon-arrowright"></i></a>
                </li>
            </ul>
        </div>
        <%--<div class="sate_one" style="padding-bottom: 10px;">
            <div class="sate_submit">
                <input type="button" value="保存"/>
            </div>
        </div>--%>
    </div>
</div>
</body>
<script src="${pageContext.request.contextPath}/js/mui.min.js"></script>
<script src="${pageContext.request.contextPath}/js/jquery-1.8.3.min.js"></script>
<script src="${pageContext.request.contextPath}/js/all.js"></script>
</html>