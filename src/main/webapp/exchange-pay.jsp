<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
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
    <script src="${pageContext.request.contextPath}/js/calcFloat.js"></script>
</head>

<body>
<header class="mui-bar mui-bar-nav">
    <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left" onclick="history.back();"></a>
    <h1 class="mui-title">支付详情</h1>
</header>
<div class="mui-content">
    <div style="margin-top:20px" class="exchange_box">
        <div class="recommend_box pay_top">
            <div class="recommend_list">
                <a href="">
                    <ul class="clearfix">
                        <li class="l fr">
                            <p>${requestScope.goods.name}</p>
                            <span>剩余${requestScope.haveNum}个</span>
                            <p class="price_single">
                                <c:if test="${requestScope.goods.type==1}">
                                    ${requestScope.goods.priceIntegral}积分
                                </c:if>
                                <c:if test="${requestScope.goods.type==2}">
                                    ${requestScope.goods.priceBonus}赠分
                                </c:if>
                            </p>
                        </li>
                        <li class="r fl">
                            <div class="img"><img src="${requestScope.goods.pic}" alt=""/></div>
                        </li>
                    </ul>
                </a>
            </div>
        </div>
        <div class="count_box clearfix">
            <p style="float: left;margin-right: 5px;">兑换数量</p>
            <div class="count_btn fl">
                <input class="min" name="" type="button" value="-"/>
                <input class="text_box" type="text" value="1" readonly/>
                <input class="add" name="" type="button" value="+"/>
            </div>
            <p class="fr" id="havPoints">
                <c:if test="${requestScope.dealPrice1!='0' || requestScope.dealPrice2!='0' || requestScope.dealPrice3!='0'}">
                    <c:if test="${sessionScope.user.workPoints-requestScope.dealPrice2>=0}">
                        兑换后剩余：
                        ${sessionScope.user.workPoints-requestScope.dealPrice2}
                        工分
                    </c:if>
                    <c:if test="${sessionScope.user.workPoints-requestScope.dealPrice2<0}">
                        工分不足
                    </c:if>
                </c:if>
            </p>
        </div>
        <div class="explain">
            <p class="tit">抵扣说明</p>
            <div class="explain_details">
                <c:if test="${requestScope.goods.type==1}">
                    <p>1、商品可用积分+工分形式来兑换产品。</p>

                    <c:if test="${fn:contains(requestScope.goods.priceWork,'~')}">
                        <p>2、义工贡献度最高可抵扣规则：<br>
                            <c:set value="${fn:split(requestScope.goods.priceWork, '~')}" var="wp"></c:set>
                            <c:forEach items="${wp}" var="p" varStatus="i">
                                ${i.index+1}星=${p}积分&nbsp;&nbsp;
                            </c:forEach>
                        </p>
                    </c:if>

                    <c:if test="${not fn:contains(requestScope.goods.priceWork,'~')}">
                        <p>2、工分最高可抵扣：${requestScope.goods.priceWork}积分</p>
                    </c:if>
                    <p>3、抵扣1积分需要${(fn:length(configs2))>0?configs2[0].content:"(管理员未设置)"}工分</p>
                </c:if>
                <c:if test="${requestScope.goods.type==2}">
                    <p>1、商品可用积分+工分+赠分形式来兑换产品。</p>
                    <p>
                        <c:if test="${not empty requestScope.config1}">2、</c:if>
                        <c:forEach items="${requestScope.config1}" var="c">
                            ${c.term}${c.content}赠分&nbsp;&nbsp;
                        </c:forEach>
                    </p>
                </c:if>
            </div>
        </div>
    </div>
    <div class="total_bottom">
        <ul class="clearfix">
            <li class="fl total_box">
                <p>合计：</p>
                <p class="total">
                    <c:if test="${requestScope.dealPrice1=='0' && requestScope.dealPrice2=='0' && requestScope.dealPrice3=='0'}">
                        <span>积分不足，无法兑换</span>
                    </c:if>
                    <c:if test="${requestScope.dealPrice1!='0' || requestScope.dealPrice2!='0' || requestScope.dealPrice3!='0'}">
                        <span class="num1">${requestScope.dealPrice1}</span>积分
                        +<span class="num2">${requestScope.dealPrice2}</span>工分
                        <c:if test="${requestScope.goods.type==2}">
                            +<span class="num3">${requestScope.dealPrice3}</span>赠分
                        </c:if>
                    </c:if>
                </p>
            </li>
            <c:if test="${requestScope.dealPrice1!='0' || requestScope.dealPrice2!='0' || requestScope.dealPrice3!='0'}">
                <li class="fr total_pay" onclick="duihuan()">
                    <a href="javascript:;" class="pay_btn">确认支付</a>
                </li>
            </c:if>
        </ul>
    </div>
</div>
</body>
<script src="${pageContext.request.contextPath}/js/mui.min.js"></script>
<script src="${pageContext.request.contextPath}/js/all.js"></script>
<script>
    layui.use(['laypage', 'layer'], function () {
        var layer = layui.layer;
    });

    function duihuan() {
        var t = $(".text_box");
        var num = parseInt(t.val());
        layer.confirm('确定兑换吗?', {icon: 3, title: '提示'}, function (index) {
            $.ajax({
                type: "post",
                url: "${pageContext.request.contextPath}/goods/exchange",
                data: {goodsId: '${requestScope.goods.pkGoodsId}', stationId: '${requestScope.stationId}', num: num},
                success: function (ret) {
                    if (ret == "ok") {
                        layer.alert('兑换成功', function (index) {
                            history.back();
                        });
                        //window.location.href = "${pageContext.request.contextPath}/goods/appGetInfo?goodsId=${requestScope.goods.pkGoodsId}&stationId=${requestScope.stationId}";
                    } else if (ret == "noGoods") {
                        layer.alert('商品已经兑换完了，下次尽快哦~', {icon: 2});
                    } else if (ret == "onlyIntreButNotEnough") {
                        layer.alert('积分不足，兑换失败', {icon: 2});
                    } else if (ret == "notEnough") {
                        layer.alert('积分不足，兑换失败', {icon: 2});
                    } else if (ret == "noSetIntreNotEnough") {
                        layer.alert('积分不足，兑换失败', {icon: 2});
                    } else if (ret == "error") {
                        layer.alert('兑换失败', {icon: 2});
                    }
                },
                dataType: "text"
            });
            layer.close(index);
        });
    }

    $(document).ready(function () {
        //获得文本框对象
        var t = $(".text_box");
        var j = $(".num1");
        var jtext = $(".num1").text();
        var g = $(".num2");
        var gtext = $(".num2").text();
        var bool = ${requestScope.goods.type==2};
        if(bool){
            var k = $(".num3");
            var ktext = $(".num3").text();
        }
        var userWorkP = '${sessionScope.user.workPoints}';
        //初始化数量为1,并失效减
        $('.min').attr('disabled', true);
        //数量增加操作
        $(".add").click(function () {
            t.val(Math.abs(parseInt(t.val())) + 1);
            if (parseInt(t.val()) != 1) {
                $('.min').attr('disabled', false);
            } else {
                $('.min').attr('disabled', true);
            }
            j.text(floatMul(t.val(), jtext));
            g.text(floatMul(t.val(), gtext));
            if(bool){
                k.text(floatMul(t.val(), ktext));
            }
            if (userWorkP - g.text() >= 0) {
                $("#havPoints").text("兑换后剩余：" + (userWorkP - g.text()) + "工分");
            } else {
                $("#havPoints").text("工分不足");
            }
        })
        //数量减少操作
        $(".min").click(function () {
            t.val(Math.abs(parseInt(t.val())) - 1);
            if (parseInt(t.val()) == 1) {
                $('.min').attr('disabled', true);
            }
            j.text(floatMul(jtext, (Math.abs(parseInt(t.val())))));
            g.text(floatMul(gtext, (Math.abs(parseInt(t.val())))));
            if(bool){
                k.text(floatMul(ktext, (Math.abs(parseInt(t.val())))));
            }
            if (userWorkP - g.text() >= 0) {
                $("#havPoints").text("兑换后剩余：" + (userWorkP - g.text()) + "工分");
            } else {
                $("#havPoints").text("工分不足");
            }
        })
    });
</script>
</html>