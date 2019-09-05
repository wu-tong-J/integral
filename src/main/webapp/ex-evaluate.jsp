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
    <h1 class="mui-title">${requestScope.goodsName}</h1>
</header>
<div class="mui-content">
    <div class="ex_details">
        <div id="star" class="clearfix">
            <span>我的评分</span>
            <ul>
                <li><a href="javascript:;">1</a></li>
                <li><a href="javascript:;">2</a></li>
                <li><a href="javascript:;">3</a></li>
                <li><a href="javascript:;">4</a></li>
                <li><a href="javascript:;">5</a></li>
            </ul>
        </div>
        <form action="">
            <div class="aboard">
                <textarea name="" rows="" cols="" placeholder="说说你的使用心得，分享给其他想要兑换的人"></textarea>
            </div>
        </form>
        <a href="javascript:;" onclick="eval()" class="ex_submit_btn">发布</a>
    </div>
</div>
</body>
<script type="text/javascript">
    function eval(){
        $.ajax({
            type: "post",
            url: "${pageContext.request.contextPath}/order/eval",
            data: {orderId:'${requestScope.orderId}',star:$("li[class='on']").length,eval:$("textarea").val()},
            success: function (ret) {
                if (ret == "ok") {
                    window.location.href = '${pageContext.request.contextPath}/order/appGetMyOrderList?page=${requestScope.curr}&limit=5';
                }
            },
            dataType: "json"
        });
    }

    $(function () {
        $("textarea").val('${requestScope.content}');
        var star = "${requestScope.star}";
        $("li:lt("+star+")").addClass("on");
    })

    window.onload = function () {
        var oStar = document.getElementById("star");
        var aLi = oStar.getElementsByTagName("li");
        var oUl = oStar.getElementsByTagName("ul")[0];
        var oSpan = oStar.getElementsByTagName("span")[1];
        var oP = oStar.getElementsByTagName("p")[0];
        var i = iScore = iStar = 0;

        for (i = 1; i <= aLi.length; i++) {
            aLi[i - 1].index = i;
            //鼠标移过显示分数
            aLi[i - 1].onmouseover = function () {
                fnPoint(this.index);
                //浮动层显示
            };
            //鼠标离开后恢复上次评分
            aLi[i - 1].onmouseout = function () {
                fnPoint();
            };
            //点击后进行评分处理
            aLi[i - 1].onclick = function () {
                iStar = this.index;
            }
        }

        //评分处理
        function fnPoint(iArg) {
            //分数赋值
            iScore = iArg || iStar;
            for (i = 0; i < aLi.length; i++) aLi[i].className = i < iScore ? "on" : "";
        }
    };
</script>
</html>