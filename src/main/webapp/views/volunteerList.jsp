<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>选择服务方</title>
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
    <div class="layui-card" style="padding-top: 15px">
        <div class="layui-form layui-card-header layuiadmin-card-header-auto">
            <input type="hidden" value="2" name="roles">
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
                    <button class="layui-btn layuiadmin-btn-useradmin" lay-submit lay-filter="LAY-userList-search">
                        <i class="layui-icon layui-icon-search layuiadmin-button-btn"></i>
                    </button>
                </div>
                <div class="layui-inline">
                    <button class="layui-btn layuiadmin-btn-useradmin" data-type="batchSel">选择</button>
                </div>
            </div>
        </div>
        <div class="layui-card-body" style="margin-top: -25px">
            <table id="LAY-userList" lay-filter="LAY-userList"></table>
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
            elem: '#LAY-userList'
            , url: '${pageContext.request.contextPath}/user/getUserListByRoles?roles=2'
            , method: "post"
            , cols: [[
                {type: 'checkbox', fixed: 'left'}
                , {field: 'phone', title: '手机号', sort: true}
                , {field: 'workPoints', title: '工分', sort: true}
                , {field: 'username', title: '用户名', sort: true}
                , {field: 'birthday', title: '生日', sort: true}
                //此处用templet和toolbar都可以
                /*, {title: '操作', width: 150, align: 'center', fixed: 'right', toolbar: '#btn-station-list'}*/
            ]]
            , page: true
            , limit: 20//每页显示数目
            , height: 'full-130'
            , text: '对不起，加载出现异常！'
        });
        //监听搜索
        form.on('submit(LAY-userList-search)', function (data) {
            var field = data.field;
            table.reload('LAY-userList', {
                where: field
            });
        });

        //事件
        var active = {
            batchSel: function () {
                var checkStatus = table.checkStatus('LAY-userList')
                    , checkData = checkStatus.data; //得到选中的数据
                if (checkData.length === 0) {
                    return layer.msg('至少需要选择一个');
                }
                var selUserIds = $("#selUserIds", parent.document).val();
                var selectVolunteer = $("#selectVolunteer", parent.document).val();
                $.each(checkData,function(i,val){
                    //防止重复添加
                    if(selUserIds.indexOf(val.pkUserId)==-1){
                        selUserIds = selUserIds + val.pkUserId + ";";
                        if(val.username==null || val.username==""){
                            selectVolunteer = selectVolunteer + val.phone + ";";
                        }else{
                            selectVolunteer = selectVolunteer + val.username + ";";
                        }
                    }
                });
                layer.confirm('确定选择吗？', function (index) {
                    $("#selUserIds", parent.document).val(selUserIds);
                    $("#selectVolunteer", parent.document).val(selectVolunteer);
                    layer.msg('添加成功');
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
