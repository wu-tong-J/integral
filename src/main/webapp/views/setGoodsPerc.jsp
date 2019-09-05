<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>商品提成设置</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=0">
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.3.1.min.js"></script>
</head>
<body>
<input type="hidden" id="contextPath" value="${pageContext.request.contextPath}">
<div class="layui-fluid">
    <div class="layui-card">
        <div class="layui-form" style="padding: 20px 0 0 0;">
            <div class="layui-form-item">
                <div class="layui-inline">
                    <label class="layui-form-label">商品名称</label>
                    <div class="layui-input-inline">
                        <input type="text" name="name" placeholder="请输入" autocomplete="off" class="layui-input">
                    </div>
                </div>
                <div class="layui-inline">
                    <button class="layui-btn layuiadmin-btn-goods" lay-submit lay-filter="LAY-goods-search">
                        <i class="layui-icon layui-icon-search layuiadmin-button-btn"></i>
                    </button>
                </div>
            </div>
        </div>

        <div class="layui-card-body">
            <div style="padding-bottom: 10px;">
                <button class="layui-btn layuiadmin-btn-useradmin" data-type="batchSetPerc">批量设置提成</button>
            </div>
            <table id="LAY-goods-list" lay-filter="LAY-goods-list"></table>
            <script type="text/html" id="btn-goods-list">
                <a class="layui-btn layui-btn-xs" lay-event="setPerc">设置提成</a>
            </script>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/layuiadmin/layui/layui.js"></script>
<script>
    layui.config({
        base: '${pageContext.request.contextPath}/layuiadmin/' //静态资源所在路径
    }).extend({
        index: 'lib/index' //主入口模块
    }).use(['index', 'form', 'table'], function () {
        var $ = layui.$
            , form = layui.form
            , table = layui.table;

        table.render({
            elem: '#LAY-goods-list'
            , url: '${pageContext.request.contextPath}/goods/getSetPercGoodsList'
            , method: "post"
            , cols: [[
                {type: 'checkbox', fixed: 'left'}
                , {
                    field: 'name', title: '商品名称', templet: function (d) {
                        return d.goods.name;
                    }
                }
                , {
                    field: 'type', title: '商品类型', templet: function (d) {
                        if (d.goods.type == 1) {
                            return "实体商品";
                        } else if (d.goods.type == 2) {
                            return "服务";
                        }
                    }
                }
                , {
                    field: 'price', title: '商品价格', templet: function (d) {
                        if (d.goods.type == 1) {
                            return d.goods.priceIntegral+"积分";
                        } else if (d.goods.type == 2) {
                            return d.goods.priceBonus+"赠分";
                        }
                    }
                }
                , {
                    field: 'perc', title: '商品提成', templet: function (d) {
                        if(d.percentage==null){
                            return "未设置";
                        }
                        return d.percentage+"%";
                    }
                }
                , {
                    field: 'busi', title: '所属商家', templet: function (d) {
                        if(d.user.username==null || d.user.username==""){
                            return d.user.phone;
                        }else{
                            return d.user.username;
                        }
                    }
                }
                , {
                    field: 'station', title: '所属驿站', templet: function (d) {
                        return d.station.name;
                    }
                }
                , {
                    field: 'getType', title: '领取方式', templet: function (d) {
                        if (d.goods.getType == 1) {
                            return "驿站";
                        } else if (d.goods.getType == 2) {
                            return "商户实体店";
                        }
                    }
                }
                , {
                    field: 'status', title: '状态', templet: function (d) {
                        if (d.goods.status == 0) {
                            return "已下架";
                        } else if (d.goods.status == 1) {
                            return "上架";
                        }
                    }
                }
                , {
                    field: 'perc', title: '提成', templet: function (d) {
                        if (d.percentage == null) {
                            return "未设置";
                        }
                        return d.percentage + "%";
                    }
                }
                , {title: '操作', align: 'center', fixed: 'right', toolbar: '#btn-goods-list'}
            ]]
            , page: true
            , limit: 20//每页显示数目
            , height: 'full-200'
            , text: '对不起，加载出现异常！'
        });

        //监听搜索
        form.on('submit(LAY-goods-search)', function (data) {
            var field = data.field;
            table.reload('LAY-goods-list', {
                where: field
            });
        });

        //监听工具条
        table.on('tool(LAY-goods-list)', function (obj) {
            var data = obj.data;
            if (obj.event === 'setPerc') {
                layer.prompt({
                    formType: 0,
                    value: '',
                    title: '请输入提成',
                    area: ['800px', '350px'] //自定义文本域宽高
                }, function(value, index, elem){
                    var regex = /^(([1-9][0-9]*)|(([0]\.\d{1,2}|[1-9][0-9]*\.\d{1,2})))$/;
                    if(!regex.test(value)){
                        layer.alert('请输入正确格式的数字', {icon: 2});
                        return false;
                    }
                    if(Number(value)>100){
                        layer.alert('提成不能超过100', {icon: 2});
                        return false;
                    }
                    $.ajax({
                        type: "post",
                        url: "${pageContext.request.contextPath}/goods/setPerc",
                        data: {perc:value,goodsIdsAndStationIds:data.goodsId+"-"+data.stationId},
                        success: function (ret) {
                            if (ret.msg == "ok") {
                                layer.alert('设定成功', {icon: 1});
                                table.reload('LAY-goods-list');
                                layer.close(index);
                            } else if(ret.msg == "error"){
                                layer.alert('操作失败!', {icon: 2});
                            } else{
                                layer.alert("该商品的商户:"+ret.msg+"已经设置了提成，请勿再设置", {icon: 2});
                            }
                        },
                        traditional:true,
                        dataType: "json"
                    });
                });
            }
        });

        var active = {
            batchSetPerc: function () {
                var checkStatus = table.checkStatus('LAY-goods-list')
                    , checkData = checkStatus.data; //得到选中的数据
                if (checkData.length === 0) {
                    return layer.msg('请选择数据');
                }
                var check_val = [];
                for (var i=0;i<checkData.length;i++){
                    check_val.push(checkData[i].goodsId+"-"+checkData[i].stationId);
                }
                layer.prompt({
                    formType: 0,
                    value: '',
                    title: '请输入提成',
                    area: ['800px', '350px'] //自定义文本域宽高
                }, function(value, index, elem){
                    var regex = /^(([1-9][0-9]*)|(([0]\.\d{1,2}|[1-9][0-9]*\.\d{1,2})))$/;
                    if(!regex.test(value)){
                        layer.alert('请输入正确格式的数字', {icon: 2});
                        return false;
                    }
                    $.ajax({
                        type: "post",
                        url: "${pageContext.request.contextPath}/goods/setPerc",
                        data: {perc:value,goodsIdsAndStationIds:check_val},
                        success: function (ret) {
                            if (ret.msg == "ok") {
                                layer.alert('设定成功', {icon: 1});
                                table.reload('LAY-goods-list');
                                layer.close(index);
                            } else if(ret.msg == "error"){
                                layer.alert('操作失败!', {icon: 2});
                            } else{
                                layer.alert("以下商品的商户已经设置了提成，请勿再设置"+ret.msg, {icon: 2});
                            }
                        },
                        traditional:true,
                        dataType: "json"
                    });
                });
            }
        };

        $('.layui-btn.layuiadmin-btn-useradmin').on('click', function () {
            var type = $(this).data('type');
            active[type] ? active[type].call(this) : '';
        });

    });
</script>
</body>
</html>
