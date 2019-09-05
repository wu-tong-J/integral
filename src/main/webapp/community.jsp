<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport"
          content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no"/>
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
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
    <h1 class="mui-title">社区活动</h1>
</header>
<!--底部导航-->
<!--<nav class="mui-bar mui-bar-tab">
        <a class="mui-tab-item" href="#tabbar">
            <span class="mui-icon home"></span>
            <span class="mui-tab-label">发现</span>
        </a>
        <a class="mui-tab-item" href="./exchange.html">
            <span class="mui-icon exchange"></span>
            <span class="mui-tab-label">兑换</span>
        </a>
        <a class="mui-tab-item mui-active" href="./community.jsp">
            <span class="mui-icon community"></span>
            <span class="mui-tab-label">社区</span>
        </a>
        <a class="mui-tab-item" href="#tabbar-with-map">
            <span class="mui-icon centre"></span>
            <span class="mui-tab-label">我的</span>
        </a>
    </nav>-->
<div class="mui-content">
    <ul class="mui-table-view mui-grid-view">
        <c:forEach items="${requestScope.list}" var="act">
            <li class="mui-table-view-cell mui-media mui-col-xs-6">
                <a href="${pageContext.request.contextPath}/activity/appGetInfo?actId=${act.pkActId}">
                    <div class="table_list">
                        <img class="mui-media-object" src="${act.pic}">
                        <p class="list_name">${act.title}</p>
                        <div class="enter_p">
                            <p>报名上限${act.num}名</p>
                            <p>剩余名额${act.remain}名</p>
                        </div>
                        <p class="pro_price">${act.deductedFraction}积分</p>
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
            , curr:'${requestScope.curr}'
            , layout: ['prev', 'page', 'next']
            , theme: '#cd473f'
            , jump: function (obj, first) {
                //首次不执行
                if (!first) {
                    window.location.href = "${pageContext.request.contextPath}/activity/appGetActList?pageNum="+obj.curr+"&limit="+obj.limit;
                }
            }
        });
    });
</script>
</body>

</html>