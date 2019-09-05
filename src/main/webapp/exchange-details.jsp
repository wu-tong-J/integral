<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>积分</title>
    <meta name="viewport" content="width=device-width, initial-scale=1,maximum-scale=1,user-scalable=no">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <!--标准mui.css-->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mui.min.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/common.css"/>
    <script src="${pageContext.request.contextPath}/js/jquery-3.3.1.min.js"></script>
    <!--App自定义的css-->
    <style type="text/css">
        .mui-preview-image.mui-fullscreen {
            position: fixed;
            z-index: 20;
            background-color: #000
        }

        .mui-preview-footer, .mui-preview-header {
            position: absolute;
            width: 100%;
            left: 0;
            z-index: 10
        }

        .mui-preview-header {
            height: 44px;
            top: 0
        }

        .mui-preview-footer {
            height: 50px;
            bottom: 0
        }

        .mui-preview-header .mui-preview-indicator {
            display: block;
            line-height: 25px;
            color: #fff;
            text-align: center;
            margin: 15px auto 4;
            width: 70px;
            background-color: rgba(0, 0, 0, .4);
            border-radius: 12px;
            font-size: 16px
        }

        .mui-preview-image {
            display: none;
            -webkit-animation-duration: .5s;
            animation-duration: .5s;
            -webkit-animation-fill-mode: both;
            animation-fill-mode: both
        }

        .mui-preview-image.mui-preview-in {
            -webkit-animation-name: fadeIn;
            animation-name: fadeIn
        }

        .mui-preview-image.mui-preview-out {
            background: 0 0;
            -webkit-animation-name: fadeOut;
            animation-name: fadeOut
        }

        .mui-preview-image.mui-preview-out .mui-preview-header,
        .mui-preview-image.mui-preview-out .mui-preview-footer {
            display: none;
        }

        .mui-zoom-scroller {
            position: absolute;
            display: -webkit-box;
            display: -webkit-flex;
            display: flex;
            -webkit-box-align: center;
            -webkit-align-items: center;
            align-items: center;
            -webkit-box-pack: center;
            -webkit-justify-content: center;
            justify-content: center;
            left: 0;
            right: 0;
            bottom: 0;
            top: 0;
            width: 100%;
            height: 100%;
            margin: 0;
            -webkit-backface-visibility: hidden
        }

        .mui-zoom {
            -webkit-transform-style: preserve-3d;
            transform-style: preserve-3d
        }

        .mui-slider .mui-slider-group .mui-slider-item img {
            width: auto;
            height: auto;
            max-width: 100%;
            max-height: 100%
        }

        .mui-android-4-1 .mui-slider .mui-slider-group .mui-slider-item img {
            width: 100%
        }

        .mui-android-4-1 .mui-slider.mui-preview-image .mui-slider-group .mui-slider-item {
            display: inline-table
        }

        .mui-android-4-1 .mui-slider.mui-preview-image .mui-zoom-scroller img {
            display: table-cell;
            vertical-align: middle
        }

        .mui-preview-loading {
            position: absolute;
            width: 100%;
            height: 100%;
            top: 0;
            left: 0;
            display: none
        }

        .mui-preview-loading.mui-active {
            display: block;
        }

        .mui-preview-loading .mui-spinner-white {
            position: absolute;
            top: 50%;
            left: 50%;
            margin-left: -25px;
            margin-top: -25px;
            height: 50px;
            width: 50px
        }

        .mui-preview-image img.mui-transitioning {
            -webkit-transition: -webkit-transform 0.5s ease, opacity 0.5s ease;
            transition: transform 0.5s ease, opacity 0.5s ease;
        }

        @-webkit-keyframes fadeIn {
            0% {
                opacity: 0;
            }
            100% {
                opacity: 1;
            }
        }

        @keyframes fadeIn {
            0% {
                opacity: 0;
            }
            100% {
                opacity: 1;
            }
        }

        @-webkit-keyframes fadeOut {
            0% {
                opacity: 1;
            }
            100% {
                opacity: 0;
            }
        }

        @keyframes fadeOut {
            0% {
                opacity: 1;
            }
            100% {
                opacity: 0;
            }
        }

        p img {
            max-width: 100%;
            height: auto;
        }
    </style>

</head>

<body>
<header class="mui-bar mui-bar-nav">
    <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left" onclick="history.back();"></a>
    <h1 class="mui-title">商品详情</h1>
</header>
<div class="mui-content">
    <div class="mui-content-padded">
        <p><img src="${requestScope.goods.pic}" data-preview-src="" data-preview-group="1"/></p>
    </div>
    <div class="exchange_box">
        <c:if test="${requestScope.goods.type==1}">
            <p class="exchange_price">${requestScope.goods.priceIntegral}积分</p>
            <%--<span>最高可抵扣掉${requestScope.goods.priceWork}积分</span>--%>
        </c:if>
        <c:if test="${requestScope.goods.type==2}">
            <p class="exchange_price">${requestScope.goods.priceBonus}赠分</p>
        </c:if>

        <p class="exchange_name">${requestScope.goods.name}</p>
        <div class="exchange_info">
            <table border="0" cellspacing="1" cellpadding="1">
                <tr>
                    <td>库存</td>
                    <td>还剩${requestScope.goods.num}份</td>
                </tr>
                <tr>
                    <td>销量</td>
                    <td>已兑${requestScope.goods.sellCount}份</td>
                </tr>
                <tr>
                    <td>领取方式&nbsp;&nbsp;</td>
                    <td>${requestScope.getType}</td>
                </tr>
                <tr>
                    <td>领取地址&nbsp;&nbsp;</td>
                    <td>${requestScope.address}</td>
                </tr>
            </table>
        </div>
    </div>
    <div class="exchange_details">
        <div class="details_tab">
            <ul class="clearfix">
                <li class="current"><a href="" onclick="whichOne(1)">商品详情</a></li>
                <li><a href="" onclick="whichOne(2)">商品评论</a></li>
            </ul>
        </div>
        <!--商品详情-->
        <div class="details_box" id="detail">
            ${requestScope.content}
        </div>
        <!--商品评论-->
        <div class="details_box" id="pinglun">
            <c:if test="${requestScope.goods.sellCount==0}">
                <p class="borad_dot" style="width: 61px;margin-left: auto;margin-right: auto">暂无数据</p>
            </c:if>
            <c:if test="${requestScope.goods.sellCount>0}">
                <c:if test="${empty requestScope.evalStar}">
                    <p class="borad_dot" style="width: 85px;margin-left: auto;margin-right: auto">无人评价</p>
                </c:if>
                <c:if test="${not empty requestScope.evalStar}">
                    <p class="borad_dot" style="width: 85px;margin-left: auto;margin-right: auto">好评度
                        <span>${requestScope.evalStar}星</span></p>
                </c:if>
                <div class="board_box" id="board_box">
                        <%--<div class="board_list clearfix">
                            <div class="board_list_l fl">
                                <div class="board_pic">
                                    <img src="images/img_5.jpg" alt="">
                                </div>
                            </div>
                            <div class="board_list_r fl">
                                <div class="board_text">
                                    <h5>吃耳朵的猫<i>义工</i></h5>
                                    <p>这个真好用，也很划算。</p>
                                    <span class="board_time">2019-1-1</span>
                                </div>
                            </div>
                        </div>--%>
                </div>
                <!--分页-->
                <div class="mui-content-padded" id="page"
                     style="position: absolute;left: 50%;transform: translateX(-68%);margin-top: -14px">
                </div>
            </c:if>
        </div>
    </div>
    <!--正常兑换时的按钮 添加类名 light-->
    <!--当没有名额时 添加类名 gray-->
    <c:if test="${requestScope.goods.num==0}">
        <div class="exchange_btn gray">
            <a href="javacript:void(0);">兑换</a>
        </div>
    </c:if>
    <c:if test="${requestScope.goods.num>0}">
        <div class="exchange_btn light" onclick="duihuan()">
            <a href="javacript:void(0);">兑换</a>
        </div>
    </c:if>
</div>
</body>
<script src="${pageContext.request.contextPath}/js/mui.min.js"></script>
<script src="${pageContext.request.contextPath}/js/mui.zoom.js"></script>
<script src="${pageContext.request.contextPath}/js/mui.previewimage.js"></script>
<script>
    mui.previewImage();
</script>
<script>
    $(function () {
        if($("#board_box").html()==null || $("#board_box").html()==""){
            $("#board_box").hide();
        }
    });

    layui.use(['laypage', 'layer'], function () {
        var laypage = layui.laypage
            , layer = layui.layer;

        //执行一个laypage实例
        laypage.render({
            elem: 'page' //注意，这里的 test1 是 ID，不用加 # 号
            , count: '${requestScope.count}' //数据总数，从服务端得到
            , prev: '&laquo;'
            , next: '&raquo;'
            , groups: 3
            , limit: 5
            , curr: 1
            , layout: ['prev', 'page', 'next']
            , theme: '#cd473f'
            , jump: function (obj, first) {
                //首次不执行
                if (!first) {
                    getList(obj.curr, obj.limit);
                }
            }
        });
    });

    function duihuan() {
        window.location.href = "${pageContext.request.contextPath}/goods/toExchange?goodsId=${requestScope.goods.pkGoodsId}&stationId=${requestScope.stationId}";
    }

    function getRoleName(role) {
        if (role == 1) {
            return '普通用户'
        } else if (role == 2) {
            return '义工'
        } else if (role == 3) {
            return '老人'
        } else if (role == 4) {
            return '员工'
        } else if (role == 5) {
            return '商户'
        } else if (role == 6) {
            return '驿站管理员'
        } else if (role == 7) {
            return '上级管理员'
        } else if (role == 8) {
            return '超级管理员'
        } else if (role == 9) {
            return '义工+老人'
        }
    }

    function nullValue(v) {
        if (v == null) {
            return "";
        } else {
            return v;
        }
    }

    function getList(curr, limit) {
        $.ajax({
            type: "post",
            url: "${pageContext.request.contextPath}/order/appGetListByGoodsId",
            data: {page: curr, limit: limit, goodsId: '${requestScope.goods.pkGoodsId}'},
            success: function (ret) {
                $("#board_box").empty();
                for (var i = 0; i < ret.data.length; i++) {
                    var data = ret.data[i];
                    $("#board_box").append([
                        "<div class=\"board_list clearfix\">",
                        "<div class=\"board_list_l fl\">",
                        "<div class=\"board_pic\">",
                        "<img alt=\"\" src=\"" + nullValue(data.user.pic) + "\">",
                        "</div>",
                        "</div>",
                        "<div class=\"board_list_r fl\">",
                        "<div class=\"board_text\">",
                        "<h5>" + data.user.username + "<i>" + getRoleName(data.user.role) + "</i></h5>",
                        "<p>" + data.contentString + "</p>",
                        "<span class=\"board_time\">" + data.evalTime + "</span>",
                        "</div>",
                        "</div>",
                        "</div>"
                    ].join(""));
                }
            },
            dataType: "json"
        });
    }

    $(function () {
        $("#pinglun").hide();
        getList(1, 5);
    });

    $('.details_tab li').click(function () {
        var i = $(this).index();
        $(this).addClass('current').siblings('').removeClass('current');
        $('.board_list').eq(i).show().siblings('board_list').hide()
    })

    function whichOne(num) {
        if (num == 1) {
            $("#detail").show();
            $("#pinglun").hide();
        } else if (num == 2) {
            $("#detail").hide();
            $("#pinglun").show();
        }
    }
</script>
</html>