<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>商品管理</title>
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
                    <label class="layui-form-label">状态</label>
                    <div class="layui-input-block">
                        <input type="radio" name="status" value="" title="所有" checked>
                        <input type="radio" name="status" value="1" title="上架">
                        <input type="radio" name="status" value="0" title="下架">
                    </div>
                </div>
                <div class="layui-inline">
                    <label class="layui-form-label">类型</label>
                    <div class="layui-input-block">
                        <input type="radio" name="type" value="" title="所有" checked>
                        <input type="radio" name="type" value="1" title="实体商品">
                        <input type="radio" name="type" value="2" title="服务">
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
            <table id="LAY-goods-list" lay-filter="LAY-goods-list"></table>
            <script type="text/html" id="btn-goods-list">
                <a class="layui-btn layui-btn-warm layui-btn-xs" lay-event="seeDetail">查看详情</a>
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
    }).use(['index', 'form','table'], function () {
        var $ = layui.$
            , form = layui.form
            , table = layui.table;

        table.render({
            elem: '#LAY-goods-list'
            , url: '${pageContext.request.contextPath}/goods/getBusinessGoodsList?userId=${requestScope.userId}'
            , method: "post"
            , cols: [[
                {type: 'checkbox', fixed: 'left'}
                , {field: 'name', title: '名称'}
                , {
                    field: 'type', title: '类型', templet: function (d) {
                        if (d.type == 1) {
                            return "实体商品";
                        } else if (d.type == 2) {
                            return "服务";
                        }
                    }
                }
                , {
                    field: 'priceIntegral', title: '价格', templet: function (d) {
                        if (d.type == 1) {
                            return d.priceIntegral+"积分";
                        } else if (d.type == 2) {
                            return d.priceBonus+"赠分";
                        }
                    }
                }
                , {field: 'num', title: '数量'}
                , {
                    field: 'percentage', title: '提成', templet: function (d) {
                        if(d.percentage!=null && d.percentage!=""){
                            return d.percentage+"%";
                        }else{
                            return "未设置";
                        }
                    }
                }
                , {
                    field: 'status', title: '状态', templet: function (d) {
                        if (d.status == 0) {
                            return "下架";
                        } else if (d.status == 1) {
                            return "上架";
                        }
                    }
                }
                , {
                    field: 'getType', title: '领取方式', templet: function (d) {
                        if (d.getType == 1) {
                            return "驿站";
                        } else if (d.getType == 2) {
                            return "商户实体店";
                        }
                    }
                }
                //此处用templet和toolbar都可以
                , {title: '操作', width:200,align: 'center', fixed: 'right', toolbar: '#btn-goods-list'}
            ]]
            , page: true
            , limit: 20//每页显示数目
            , height: 'full-200'
            , text: '对不起，加载出现异常！'
            , done: function (res, curr, count) {
                //表格渲染完后的回调函数

            }
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
            if (obj.event === 'seeDetail') {
                layer.open({
                    type: 2
                    , title: '详情'
                    , content: '${pageContext.request.contextPath}/common/toDetail?goodsId=' + data.pkGoodsId
                    ,area: ['90%', '90%']
                    , maxmin : true
                    , success: function (layero, index) {
                        //最大化
                        layer.full(index);
                    }
                });
            }
        });


        //事件
        var active = {
            batchStatus1: function () {
                var checkStatus = table.checkStatus('LAY-goods-list')
                    , checkData = checkStatus.data; //得到选中的数据
                if (checkData.length === 0) {
                    return layer.msg('请选择数据');
                }
                var check_val = [];
                for (var i=0;i<checkData.length;i++){
                    check_val.push(checkData[i].pkGoodsId);
                }
                layer.confirm("确定上架吗？", {icon: 3, title:'提示'}, function(index){
                    $.ajax({
                        type: "post",
                        url: "${pageContext.request.contextPath}/goods/updateStatus",
                        data: {status:1,pkGoodsIds:check_val},
                        success: function (ret) {
                            if (ret.msg == "ok") {
                                layer.alert('操作成功', {icon: 1});
                                table.reload('LAY-goods-list');
                                layer.close(index);
                            } else {
                                layer.alert('操作失败!', {icon: 2});
                            }
                        },
                        traditional:true,
                        dataType: "json"
                    });
                });
            }
            , batchStatus0: function () {
                var checkStatus = table.checkStatus('LAY-goods-list')
                    , checkData = checkStatus.data; //得到选中的数据
                if (checkData.length === 0) {
                    return layer.msg('请选择数据');
                }
                var check_val = [];
                $.each(checkData,function(i,val){
                    check_val.push(val.pkGoodsId)
                });
                layer.confirm("确定下架吗？", {icon: 3, title:'提示'}, function(index){
                    $.ajax({
                        type: "post",
                        url: "${pageContext.request.contextPath}/goods/updateStatus",
                        data: {status:0,pkGoodsIds:check_val},
                        success: function (ret) {
                            if (ret.msg == "ok") {
                                layer.alert('操作成功', {icon: 1});
                                table.reload('LAY-goods-list');
                                layer.close(index);
                            } else {
                                layer.alert('操作失败!', {icon: 2});
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
