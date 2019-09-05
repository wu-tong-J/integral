<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>指定员工身份</title>
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
        <hr class="layui-bg-red">
        <b style="margin-left: 20px">驿站员工列表</b>
        <hr class="layui-bg-red">
        <div class="layui-form layui-card-header layuiadmin-card-header-auto">
            <div class="layui-form-item" style="margin-top: -22px">
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
                <%--<c:if test="${not empty requestScope.stationList}">
                    <div class="layui-inline">
                        <label class="layui-form-label">驿站</label>
                        <div class="layui-input-block">
                            <select name="stationId" id="stationList" lay-filter="btn-select">
                                <c:forEach items="${requestScope.stationList}" var="s">
                                    <option value="${s.pkStationId}">${s.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                </c:if>--%>
                <div class="layui-inline">
                    <button class="layui-btn layuiadmin-btn-useradmin" lay-submit
                            lay-filter="LAY-stationUserList-search">
                        <i class="layui-icon layui-icon-search layuiadmin-button-btn"></i>
                    </button>
                </div>
                <div class="layui-inline">
                    <button class="layui-btn layuiadmin-btn-useradmin" data-type="batchdel">批量恢复普通身份</button>
                </div>
            </div>
        </div>

        <div class="layui-card-body" style="margin-top: -25px">
            <table id="LAY-stationUserList" lay-filter="LAY-stationUserList"></table>
        </div>
        <hr class="layui-bg-red">
        <b style="margin-left: 20px">可指定用户列表</b>
        <hr class="layui-bg-red">
        <div class="layui-form layui-card-header layuiadmin-card-header-auto">
            <div class="layui-form-item" style="margin-top: -22px">
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
                    <button class="layui-btn layuiadmin-btn-useradmin" lay-submit
                            lay-filter="LAY-stationUserList-add-search">
                        <i class="layui-icon layui-icon-search layuiadmin-button-btn"></i>
                    </button>
                </div>
                <div class="layui-inline">
                    <button class="layui-btn layuiadmin-btn-useradmin" data-type="batchadd">批量添加为员工</button>
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
            elem: '#LAY-stationUserList'
            ,
            url: '${pageContext.request.contextPath}/user/getStationUserList?stationId=${sessionScope.user.stationId}'
            ,
            method: "post"
            ,
            cols: [[
                {type: 'checkbox', fixed: 'left'}
                , {field: 'phone', title: '手机号', sort: true}
                , {field: 'username', title: '用户名', sort: true}
                , {field: 'birthday', title: '生日', sort: true}
                //此处用templet和toolbar都可以
                /*, {title: '操作', width: 150, align: 'center', fixed: 'right', toolbar: '#btn-station-list'}*/
            ]]
            ,
            page: true
            ,
            limit: 20//每页显示数目
            ,
            height: 'full-130'
            ,
            text: '对不起，加载出现异常！'
        });

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

        //监听搜索
        form.on('submit(LAY-stationUserList-search)', function (data) {
            var field = data.field;
            table.reload('LAY-stationUserList', {
                where: field
            });
        });
        form.on('submit(LAY-stationUserList-add-search)', function (data) {
            var field = data.field;
            table.reload('LAY-stationUserList-add', {
                where: field
            });
        });

        //事件
        var active = {
            batchdel: function () {
                var checkStatus = table.checkStatus('LAY-stationUserList')
                    , checkData = checkStatus.data; //得到选中的数据
                if (checkData.length === 0) {
                    return layer.msg('请选择要恢复的员工');
                }
                var ids = [];
                $.each(checkData, function (i, val) {
                    ids.push(val.pkUserId)
                });
                layer.confirm('确定恢复吗？', function (index) {
                    $.ajax({
                        type: "post",
                        url: "${pageContext.request.contextPath}/user/addOrDelEmp",
                        traditional: true,
                        data: {userIds: ids},
                        success: function (ret) {
                            if (ret.msg == "error") {
                                layer.alert('操作失败!', {icon: 2});
                            } else {
                                table.reload('LAY-stationUserList');
                                table.reload('LAY-stationUserList-add');
                                layer.msg('已删除');
                            }
                        },
                        dataType: "json"
                    });
                });
            }
            , batchadd: function () {
                var checkStatus = table.checkStatus('LAY-stationUserList-add')
                    , checkData = checkStatus.data; //得到选中的数据
                if (checkData.length === 0) {
                    return layer.msg('请选择要添加的用户');
                }
                var ids = [];
                $.each(checkData, function (i, val) {
                    ids.push(val.pkUserId)
                });
                layer.confirm('确定添加吗？', function (index) {
                    $.ajax({
                        type: "post",
                        url: "${pageContext.request.contextPath}/user/addOrDelEmp",
                        traditional: true,
                        data: {userIds: ids, stationId: '${sessionScope.user.stationId}'},
                        success: function (ret) {
                            if (ret.msg == "error") {
                                layer.alert('操作失败!', {icon: 2});
                            } else {
                                table.reload('LAY-stationUserList');
                                table.reload('LAY-stationUserList-add');
                                layer.msg('已添加');
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
