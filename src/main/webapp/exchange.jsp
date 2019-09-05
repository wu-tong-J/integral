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
    <script type="text/javascript" charset="utf-8">
        mui.init();
    </script>
</head>
<body>
<header class="mui-bar mui-bar-nav">
    <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left" onclick="history.back();"></a>
    <div class="change_tab">
        <a <c:if test="${requestScope.goodsType==1}">class="on"</c:if> href="${pageContext.request.contextPath}/goods/appGetListByStationId?goodsType=1&stationId=${requestScope.stationId}">商品兑换</a>
        <a <c:if test="${requestScope.goodsType==2}">class="on"</c:if> href="${pageContext.request.contextPath}/goods/appGetListByStationId?goodsType=2&stationId=${requestScope.stationId}">服务兑换</a>
    </div>
</header>
<div class="mui-content">
    <ul class="mui-table-view mui-grid-view">
        <c:forEach items="${requestScope.list}" var="g">
            <li class="mui-table-view-cell mui-media mui-col-xs-6">
                <a href="${pageContext.request.contextPath}/goods/appGetInfo?goodsId=${g.goods.pkGoodsId}&stationId=${requestScope.stationId}">
                    <div class="table_list">
                        <img class="mui-media-object" src="${g.goods.pic}">
                        <p class="list_name">${g.goods.name}</p>
                        <c:if test="${g.goods.type==1}">
                            <p class="pro_price">${g.goods.priceIntegral}积分<%--<span>还剩14份</span>--%></p>
                        </c:if>
                        <c:if test="${g.goods.type==2}">
                            <p class="pro_price">${g.goods.priceBonus}赠分</p>
                        </c:if>
                        <i class="a_btn">查看详情</i>
                    </div>
                </a>
            </li>
        </c:forEach>
    </ul>
    <!--分页-->
    <c:if test="${not empty requestScope.list}">
        <div class="mui-content-padded" id="page" style="position: absolute;left: 50%;transform: translateX(-68%);">
        </div>
    </c:if>
    <!--这是将中间内容底撑到大于固定导航高度的条-->
    <div class="height"></div>
</div>
<jsp:include page="./nav-bottom.jsp"></jsp:include>
</body>
<script>
    layui.use('laypage', function () {
        var laypage = layui.laypage;

        //执行一个laypage实例
        laypage.render({
            elem: 'page' //注意，这里的 test1 是 ID，不用加 # 号
            , count: '${requestScope.count}' //数据总数，从服务端得到
            , prev: '&laquo;'
            , next: '&raquo;'
            , groups: 3
            , limit: 6
            , curr: '${requestScope.curr}'
            , layout: ['prev', 'page', 'next']
            , theme: '#cd473f'
            , jump: function (obj, first) {
                //首次不执行
                if (!first) {
                    window.location.href = "${pageContext.request.contextPath}/goods/appGetListByStationId?goodsType=${requestScope.goodsType}&pageNum=" + obj.curr + "&limit=" + obj.limit;
                }
            }
        });
    });
</script>
</html>