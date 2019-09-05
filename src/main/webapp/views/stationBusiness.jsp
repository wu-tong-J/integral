<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>商户可见性</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=0">
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.3.1.min.js"></script>
</head>
<body>
<input type="hidden" id="contextPath" value="${pageContext.request.contextPath}">
<div class="layui-fluid">
    <div class="layui-card" id="changeHeight">
        <div class="layui-form layui-card-header layuiadmin-card-header-auto">
            <div class="layui-form-item">
                <div class="layui-inline">
                    <label class="layui-form-label">驿站名称</label>
                    <div class="layui-input-block">
                        <input type="text" name="name" placeholder="请输入" autocomplete="off" class="layui-input">
                    </div>
                </div>
                <div class="layui-inline">
                    <button class="layui-btn layuiadmin-btn-useradmin" lay-submit lay-filter="LAY-user-front-search">
                        <i class="layui-icon layui-icon-search layuiadmin-button-btn"></i>
                    </button>
                </div>
            </div>
        </div>

        <div class="layui-card-body">
            <table id="LAY-sb-list" lay-filter="LAY-sb-list"></table>

            <script type="text/html" id="btn-sb-list">
                {{#  layui.each(d.sbgList, function(index, item){
                        if(item.ifApproval==1){
                }}
                <a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="seeOrNot0">改为不可见</a>
                    {{#  } }}
                {{#  }); }}
                {{#  if(d.sbgList.length==0){ }}
                <a class="layui-btn layui-btn-normal layui-btn-xs" lay-event="seeOrNot1">申请可见</a>
                {{#  } }}


            </script>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/layuiadmin/layui/layui.js"></script>
<script>
    $(function () {
        $("#changeHeight").height(document.documentElement.clientHeight - 30);
    });

    layui.config({
        base: '${pageContext.request.contextPath}/layuiadmin/' //静态资源所在路径
    }).extend({
        index: 'lib/index' //主入口模块
    }).use(['index', 'table','form','laytpl'], function () {
        var $ = layui.$
            , form = layui.form
            , laytpl = layui.laytpl
            , table = layui.table;

        table.render({
            elem: '#LAY-sb-list'
            , url: '${pageContext.request.contextPath}/station/getStationSBGList'
            , method: "post"
            , cols: [[
                {type: 'checkbox', fixed: 'left'}
                , {field: 'name', title: '名称', sort: true}
                , {
                    field: 'manager', title: '管理员'
                    , templet: function (d) {
                        if (d.user.username == null || d.user.username == "") {
                            return "手机号：" + d.user.phone;
                        }
                        return "用户名：" + d.user.username;
                    }
                }
                , {
                    field: 'sbgList', title: '状态',
                    templet: function (d) {
                        var sbgList = d.sbgList;
                        if(sbgList.length>0){
                            for(var i=0;i<sbgList.length;i++){
                                if(sbgList[i].ifApproval==0){
                                    return "审批中";
                                }else if(sbgList[i].ifApproval==1){
                                    return "可见";
                                }
                            }
                        }
                        return "不可见";
                    }
                }
                //此处用templet和toolbar都可以
                , {title: '操作', align: 'center', fixed: 'right', toolbar: '#btn-sb-list'}
            ]]
            , page: true
            , limit: 20//每页显示数目
            , height: 'full-130'
            , text: '对不起，加载出现异常！'
        });

        //监听搜索
        form.on('submit(LAY-user-front-search)', function (data) {
            var field = data.field;
            table.reload('LAY-sb-list', {
                where: field
            });
        });

        table.on('tool(LAY-sb-list)', function (obj) {
            var data = obj.data;
            if (obj.event === 'seeOrNot0' || obj.event === 'seeOrNot1') {
                var type;
                var msg;
                if(obj.event.indexOf("0")!=-1){
                    type = 0;
                    msg = "确定改为对该驿站不可见吗？";
                }else {
                    type = 1;
                    msg = "确定申请对该驿站可见吗？";
                }
                layer.confirm(msg, {icon: 3, title:'提示'}, function(index){
                    $.ajax({
                        type: "post",
                        url: "${pageContext.request.contextPath}/station/seeOrNot",
                        data: {type:type,stationId:data.pkStationId},
                        success: function (ret) {
                            if (ret.msg == "ok") {
                                layer.alert('操作成功!', {icon: 1});
                                table.reload('LAY-sb-list');
                                layer.close(index);
                            } else {
                                layer.alert('操作失败!', {icon: 2});
                            }
                        },
                        dataType: "json"
                    });
                });
            }
        });

    });
</script>
</body>
</html>
