<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>指定商户身份</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=0">
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.3.1.min.js"></script>
</head>
<body>
<input type="hidden" id="contextPath" value="${pageContext.request.contextPath}">
<input type="hidden" id="stationId" value="${requestScope.stationId}">
<div class="layui-fluid">
    <div class="layui-card">
        <div class="layui-form layui-card-header layuiadmin-card-header-auto">
            <div class="layui-form-item">
                <div class="layui-inline">
                    <label class="layui-form-label">手机号</label>
                    <div class="layui-input-block">
                        <input type="text" name="phone" placeholder="请输入" autocomplete="off" class="layui-input">
                    </div>
                </div>
                <div class="layui-inline">
                    <label class="layui-form-label">用户名</label>
                    <div class="layui-input-block">
                        <input type="text" name="username" placeholder="请输入" autocomplete="off" class="layui-input">
                    </div>
                </div>
                <div class="layui-inline">
                    <button class="layui-btn layuiadmin-btn-useradmin" lay-submit lay-filter="LAY-stationUserList-add-search">
                        <i class="layui-icon layui-icon-search layuiadmin-button-btn"></i>
                    </button>
                </div>
                <div class="layui-inline">
                    <button class="layui-btn layuiadmin-btn-useradmin" data-type="batchadd">批量指定为商户</button>
                </div>
            </div>
        </div>

        <div class="layui-card-body" style="margin-top: -25px">
            <table id="LAY-stationUserList-add" lay-filter="LAY-stationUserList-add"></table>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/layuiadmin/layui/layui.js"></script>
<script>
    layui.config({
        base: '${pageContext.request.contextPath}/layuiadmin/' //静态资源所在路径
    }).extend({
        index: 'lib/index' //主入口模块
    }).use(['index', 'table', 'form'], function () {
        var $ = layui.$
            , form = layui.form
            , table = layui.table;

        table.render({
            elem: '#LAY-stationUserList-add'
            , url: '${pageContext.request.contextPath}/user/getCommonUserList'
            , method: "post"
            , cols: [[
                {type: 'checkbox', fixed: 'left'}
                , {field: 'phone', title: '手机号', sort: true}
                , {field: 'username', title: '用户名', sort: true}
                , {field: 'birthday', title: '生日', sort: true}
            ]]
            , page: true
            , limit: 20//每页显示数目
            , height: 'full-130'
            , text: '对不起，加载出现异常！'
        });

        form.on('submit(LAY-stationUserList-add-search)', function (data) {
            var field = data.field;
            table.reload('LAY-stationUserList-add', {
                where: field
            });
        });

        //事件
        var active = {
             batchadd: function () {
                var checkStatus = table.checkStatus('LAY-stationUserList-add')
                    , checkData = checkStatus.data; //得到选中的数据
                if (checkData.length === 0) {
                    return layer.msg('请选择要指定的用户');
                }
                var ids = [];
                $.each(checkData,function(i,val){
                    ids.push(val.pkUserId)
                });
                layer.confirm('确定指定吗？', function (index) {
                    $.ajax({
                        type: "post",
                        url: "${pageContext.request.contextPath}/user/toBeBusi",
                        traditional:true,
                        data: {userIds:ids},
                        success: function (ret) {
                            if (ret.msg == "error") {
                                layer.alert('操作失败!', {icon: 2});
                            } else {
                                table.reload('LAY-stationUserList-add');
                                layer.msg('操作成功');
                            }
                        },
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
