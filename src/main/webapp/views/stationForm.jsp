<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>添加/编辑驿站</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=0">
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.3.1.min.js"></script>
</head>
<body>
<input type="hidden" id="contextPath" value="${pageContext.request.contextPath}">
<div class="layui-fluid">
    <div class="layui-form" lay-filter="layuiadmin-form-useradmin" id="layuiadmin-form-useradmin"
         style="padding: 20px 0 0 0;">
        <div class="layui-form-item">
            <label class="layui-form-label">驿站名称</label>
            <div class="layui-input-inline">
                <input type="text" name="name" value="${requestScope.station.name}" lay-verify="required" placeholder="请输入驿站名称" autocomplete="off"
                       class="layui-input">
            </div>
            <label class="layui-form-label">驿站管理员</label>
            <div class="layui-input-inline">
                <input type="text" name="managerName" lay-verify="required" placeholder="请选择驿站管理员" autocomplete="off"
                       class="layui-input" readonly>
                <input type="text" name="manager" value="" autocomplete="off" class="layui-hide">
                <input type="text" name="pkStationId" value="${requestScope.station.pkStationId}" autocomplete="off" class="layui-hide">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">驿站地址</label>
            <div class="layui-input-inline">
                <input type="text" name="address" value="${requestScope.station.address}" lay-verify="required" placeholder="请输入驿站地址" autocomplete="off"
                       class="layui-input" style="width: 263%">
            </div>
        </div>
        <div class="layui-form-item layui-hide">
            <input type="button" lay-submit lay-filter="LAY-station-front-submit" id="LAY-station-front-submit" value="确认">
        </div>
    </div>
    <div class="layui-card">
        <div class="layui-form layui-card-header layuiadmin-card-header-auto">
            <div class="layui-form-item">
                <div class="layui-inline">
                    <label class="layui-form-label">检索-用户名</label>
                    <div class="layui-input-block">
                        <input type="text" name="username" placeholder="请输入" autocomplete="off" class="layui-input">
                    </div>
                </div>
                <div class="layui-inline">
                    <label class="layui-form-label">检索-手机号</label>
                    <div class="layui-input-block">
                        <input type="text" name="phone" placeholder="请输入" autocomplete="off" class="layui-input">
                    </div>
                </div>
                <div class="layui-inline">
                    <button class="layui-btn layuiadmin-btn-useradmin" lay-submit lay-filter="LAY-station-front-search">
                        <i class="layui-icon layui-icon-search layuiadmin-button-btn"></i>
                    </button>
                </div>
            </div>
            <div class="layui-card-body" style="margin-top: -35px">
                <table id="LAY-stationAdmin-list" lay-filter="LAY-stationAdmin-list"></table>
                <script type="text/html" id="btn-stationAdmin-list">
                    <a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="selectStationAdmin">选择</a>
                </script>
            </div>
        </div>
    </div>
</div>
<script src="${pageContext.request.contextPath}/layuiadmin/layui/layui.js"></script>
<script>
    $(function () {
        var b = '${not empty requestScope.station.user.username}';
        var b2 = '${not empty requestScope.station.user.phone}';
        if(b=='true'){
            $("input[name='managerName']").val("用户名：${requestScope.station.user.username}");
        }else if(b2=='true'){
            $("input[name='managerName']").val("手机号：${requestScope.station.user.phone}");
        }
        $("input[name='manager']").val("${requestScope.station.manager}");
    })

    layui.config({
        base: '${pageContext.request.contextPath}/layuiadmin/' //静态资源所在路径
    }).extend({
        index: 'lib/index' //主入口模块
    }).use(['index', 'form', 'table'], function () {
        var $ = layui.$
            , table = layui.table
            , form = layui.form;

        //监听搜索
        form.on('submit(LAY-station-front-search)', function (data) {
            var field = data.field;
            table.reload('LAY-stationAdmin-list', {
                where: field
            });
        });

        table.render({
            elem: '#LAY-stationAdmin-list'
            , url: '${pageContext.request.contextPath}/user/getStationManagerList' //模拟接口
            , method: "post"
            , cols: [[
                {field: 'phone', title: '手机号', sort: true}
                , {field: 'username', title: '用户名', sort: true}
                , {field: 'birthday', title: '生日', sort: true}
                //此处用templet和toolbar都可以
                , {title: '操作', align: 'center', fixed: 'right', toolbar: '#btn-stationAdmin-list'}
            ]]
            , page: true
            , limit: 20//每页显示数目
            , height: 'full-200'
            , text: '对不起，加载出现异常！'
        });

        table.on('tool(LAY-stationAdmin-list)', function (obj) {
            var data = obj.data;
            if (obj.event === 'selectStationAdmin') {
                var managerName;
                if(data.username==null || data.username==""){
                    managerName = "手机号："+data.phone;
                }else{
                    managerName = "用户名："+data.username;
                }
                $("input[name='managerName']").val(managerName);
                $("input[name='manager']").val(data.pkUserId);
            }
        });
    })
</script>
</body>
</html>