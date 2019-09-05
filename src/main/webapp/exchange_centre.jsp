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
    <h1 class="mui-title">我的兑换</h1>
</header>
<div class="mui-content">
    <div class="exchange_centre">
        <c:forEach items="${requestScope.list}" var="o">
            <div class="exchange_list">
                <ul class="clearfix">
                    <li class="fl l">兑换产品</li>
                    <li class="fl r"><p>${o.goodsName}</p></li>
                </ul>
                <ul class="clearfix">
                    <li class="fl l">消耗积分</li>
                    <li class="fl r">${o.price}</li>
                </ul>
                <ul class="clearfix">
                    <li class="fl l">兑换时间</li>
                    <li class="fl r">${o.createTime}</li>
                </ul>
                <%--<ul class="clearfix">
                    <li class="fl l">活动状态</li>
                    <li class="fl r"><span>未结束</span></li>
                </ul>--%>
                <c:if test="${o.status==0}">
                    <ul class="clearfix">
                        <li class="fl l">订单状态</li>
                        <li class="fl r">未领取</li>
                    </ul>
                    <div class="a_exchange"><a href="${pageContext.request.contextPath}/order/appToGet?orderId=${o.pkOrderId}&page=${requestScope.curr}" class="a_exchange_btn">去领取</a></div>
                </c:if>
                <c:if test="${o.status==1}">
                    <ul class="clearfix">
                        <li class="fl l">订单状态</li>
                        <li class="fl r">已发货</li>
                    </ul>
                    <div class="a_exchange"><a href="${pageContext.request.contextPath}/order/appToEval?orderId=${o.pkOrderId}&page=${requestScope.curr}" class="a_exchange_btn">去评价</a></div>
                </c:if>
                <c:if test="${o.status==2}">
                    <ul class="clearfix">
                        <li class="fl l">订单状态</li>
                        <li class="fl r">已完成</li>
                    </ul>
                    <div class="a_exchange"><a href="${pageContext.request.contextPath}/order/appToEval?orderId=${o.pkOrderId}&page=${requestScope.curr}" class="a_exchange_btn">查看评价</a></div>
                </c:if>
            </div>
        </c:forEach>
    </div>
    <div class="mui-content-padded" id="page" style="position: absolute;left: 50%;transform: translateX(-58%);">
    </div>
</div>
<script>
    layui.use('laypage', function () {
        var laypage = layui.laypage;

        //执行一个laypage实例
        laypage.render({
            elem: 'page' //注意，这里的 test1 是 ID，不用加 # 号
            , count: '${requestScope.count}' //数据总数，从服务端得到
            , prev: '&laquo;'
            , next: '&raquo;'
            , groups: 4
            , limit: 5
            , curr:'${requestScope.curr}'
            , layout: ['prev', 'page', 'next']
            , theme: '#cd473f'
            , jump: function (obj, first) {
                //首次不执行
                if (!first) {
                    window.location.href = "${pageContext.request.contextPath}/order/appGetMyOrderList?page="+obj.curr+"&limit="+obj.limit;
                }
            }
        });
    });
</script>
</body>
<script src="${pageContext.request.contextPath}/js/mui.min.js"></script>
<script src="${pageContext.request.contextPath}/js/all.js"></script>
</html>