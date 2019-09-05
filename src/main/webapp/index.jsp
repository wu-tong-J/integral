<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport"
          content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no"/>
    <title></title>
    <script src="${pageContext.request.contextPath}/js/mui.min.js"></script>
    <link href="${pageContext.request.contextPath}/css/mui.min.css" rel="stylesheet"/>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/common.css"/>
    <script src="${pageContext.request.contextPath}/js/jquery-3.3.1.min.js"></script>
    <script type="text/javascript" charset="UTF-8">
        mui.init()
    </script>
</head>
<body>
<header class="mui-bar mui-bar-nav">
    <!--<a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left" onclick="history.back();"></a>-->
    <h1 class="mui-title">综合推荐</h1>
</header>
<div class="mui-content">
    <div class="site_box">
        <p class="site_tit">驿站推荐</p>
        <c:forEach items="${requestScope.stationList}" var="s">
            <div class="site_list">
                <ul class="clearfix">
                    <li class="fl l">
                        <div class="sites">
                            <p>${s.name}</p>
                            <span>${s.address}</span>
                        </div>
                        <em>正在进行${s.goodsNum}个商品兑换活动</em>
                    </li>
                    <li class="fr r" onclick="toGetGoodsList('${s.pkStationId}')">
                        <p>前去兑换</p>
                    </li>
                </ul>
            </div>
        </c:forEach>
        <a href="${pageContext.request.contextPath}/station/appGetStationList" class="click_more">查看更多驿站推荐</a>
    </div>
    <div class="recommend_box">
        <p class="site_tit">活动推荐</p>
        <div class="recommend_list">
            <a href="${pageContext.request.contextPath}/activity/appGetInfo?actId=${requestScope.act.pkActId}">
                <c:if test="${requestScope.act==null}">
                    <ul class="clearfix" style="width: 102px;margin: auto;">
                        <span>暂无活动推荐</span>
                    </ul>
                </c:if>
                <c:if test="${requestScope.act!=null}">
                    <ul class="clearfix">
                        <li class="l fl">
                            <p>${requestScope.act.title}</p>
                            <span>报名上限${requestScope.act.num}名</span>
                            <span>剩余名额${requestScope.haveActNum}名</span>
                        </li>
                        <li class="r fl">
                            <div class="img"><img src="${requestScope.act.pic}" alt=""/></div>
                        </li>
                    </ul>
                </c:if>
            </a>
        </div>
        <a href="${pageContext.request.contextPath}/activity/appGetActList" class="click_more">查看更多活动推荐</a>
    </div>
    <div class="recommend_box">
        <p class="site_tit">商品推荐</p>
        <div class="recommend_list">
            <a href="${pageContext.request.contextPath}/goods/appGetInfo?goodsId=${requestScope.goods.pkGoodsId}&stationId=${requestScope.goodsStationId}">
                <c:if test="${requestScope.goods==null}">
                    <ul class="clearfix" style="width: 102px;margin: auto;">
                        <span>暂无商品推荐</span>
                    </ul>
                </c:if>
                <c:if test="${requestScope.goods!=null}">
                    <ul class="clearfix">
                        <li class="l fl">
                            <p>${requestScope.goods.name}</p>
                            <span>剩余${requestScope.haveActNum}个</span>
                        </li>
                        <li class="r fl">
                            <div class="img"><img src="${requestScope.goods.pic}" alt=""/></div>
                        </li>
                    </ul>
                </c:if>
            </a>
        </div>
        <a href="${pageContext.request.contextPath}/station/appGetStationList" class="click_more">查看更多商品推荐</a>
    </div>
    <!--这是将中间内容底撑到大于固定导航高度的条-->
    <div class="height"></div>
</div>
<jsp:include page="./nav-bottom.jsp"></jsp:include>
</body>
<script type="text/javascript" charset="utf-8">
    function toGetGoodsList(stationId) {
        window.location.href = "${pageContext.request.contextPath}/goods/appGetListByStationId?stationId=" + stationId;
    }
</script>
</html>