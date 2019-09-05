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
    <script src="${pageContext.request.contextPath}/js/jquery-3.3.1.min.js"></script>
</head>

<body>
<header class="mui-bar mui-bar-nav">
    <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left" onclick="history.back();"></a>
    <h1 class="mui-title">${requestScope.orderDetail.goodsName}</h1>
</header>
<div class="mui-content">
    <div class="ex_details" style="height: 680px;">
        <div class="exchange_list">
            <ul class="clearfix">
                <li class="fl l">兑换产品</li>
                <li class="fl r"><p>${requestScope.orderDetail.goodsName}</p></li>
            </ul>
            <ul class="clearfix">
                <li class="fl l">消耗积分</li>
                <li class="fl r">${requestScope.orderDetail.price}</li>
            </ul>
            <ul class="clearfix">
                <li class="fl l">兑换时间</li>
                <li class="fl r">${requestScope.orderDetail.createTime}</li>
            </ul>
        </div>
        <div class="addr">
            <ul class="clearfix">
                <li class="fl l">领取方式</li>
                <li class="fl r">${requestScope.getType}</li>
            </ul>
            <ul class="clearfix">
                <li class="fl l">领取地址</li>
                <li class="fl r">${requestScope.address}</li>
            </ul>
            <p>领取码</p>
            <img src="${pageContext.request.contextPath}/common/getQRImage?text=${requestScope.orderDetail.pkOrderId}" alt="" style="position: absolute;left: 50%;transform: translateX(-50%);"/>
        </div>
    </div>
</div>
</body>
<script src="${pageContext.request.contextPath}/js/mui.min.js"></script>
<script src="${pageContext.request.contextPath}/js/all.js"></script>
</html>