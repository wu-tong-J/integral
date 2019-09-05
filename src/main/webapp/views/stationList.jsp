<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>驿站管理</title>
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
                    <label class="layui-form-label">名称</label>
                    <div class="layui-input-block">
                        <input type="text" name="name" placeholder="请输入" autocomplete="off" class="layui-input">
                    </div>
                </div>
                <div class="layui-inline">
                    <button class="layui-btn layuiadmin-btn-useradmin" lay-submit lay-filter="LAY-user-front-search">
                        <i class="layui-icon layui-icon-search layuiadmin-button-btn"></i>
                    </button>
                </div>
                <div class="layui-inline">
                    <%--<button class="layui-btn layuiadmin-btn-useradmin" data-type="batchdel">删除</button>--%>
                    <button class="layui-btn layuiadmin-btn-useradmin" data-type="add">添加</button>
                </div>
            </div>
        </div>

        <div class="layui-card-body">
            <table id="LAY-station-list" lay-filter="LAY-station-list"></table>

            <script type="text/html" id="btn-station-list">
                <a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="updateInfo">编辑</a>
                <c:if test="${sessionScope.user.role!=6}">
                    <a class="layui-btn layui-btn-normal layui-btn-xs" lay-event="employeeManage">员工管理</a>
                </c:if>
                {{#  if(d.ifRecommend == 0 || d.ifRecommend == null){ }}
                <a class="layui-btn layui-btn-xs" lay-event="recommend1">首页推荐</a>
                {{#  } }}
                {{#  if(d.ifRecommend == 1){ }}
                <a class="layui-btn layui-btn-warm layui-btn-xs" lay-event="recommend0">取消推荐</a>
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
    }).use(['index', 'station', 'table'], function () {
        var $ = layui.$
            , form = layui.form
            , table = layui.table;

        //监听搜索
        form.on('submit(LAY-user-front-search)', function (data) {
            var field = data.field;

            table.reload('LAY-station-list', {
                where: field
            });
        });

        //事件
        var active = {
            batchdel: function () {
                var checkStatus = table.checkStatus('LAY-auth-user')
                    , checkData = checkStatus.data; //得到选中的数据

                if (checkData.length === 0) {
                    return layer.msg('请选择数据');
                }

                layer.prompt({
                    formType: 1
                    , title: '敏感操作，请验证口令'
                }, function (value, index) {
                    layer.close(index);

                    layer.confirm('确定删除吗？', function (index) {

                        //执行 Ajax 后重载
                        /*
                        admin.req({
                          url: 'xxx'
                          //,……
                        });
                        */
                        table.reload('LAY-auth-user');
                        layer.msg('已删除');
                    });
                });
            }
            , add: function () {
                layer.open({
                    type: 2
                    , title: '添加驿站'
                    , content: '${pageContext.request.contextPath}/views/stationForm.jsp'
                    , maxmin: true
                    , area: ['800px', '650px']
                    , btn: ['确定', '取消']
                    , yes: function (index, layero) {
                        var iframeWindow = window['layui-layer-iframe' + index]
                            , submitID = 'LAY-station-front-submit'
                            , submit = layero.find('iframe').contents().find('#' + submitID);

                        //监听提交
                        iframeWindow.layui.form.on('submit(' + submitID + ')', function (data) {
                            var field = data.field; //获取提交的字段
                            $.ajax({
                                type: "post",
                                url: contextPath + "/station/save",
                                data: field,
                                success: function (ret) {
                                    if (ret.msg == "error") {
                                        layer.alert('操作失败', {icon: 2});
                                        //数据刷新
                                        table.reload('LAY-stationAdmin-list');
                                    } else {
                                        layer.alert('操作成功!', {icon: 1});
                                        table.reload('LAY-station-list'); //数据刷新
                                        layer.close(index); //关闭弹层
                                    }
                                },
                                dataType: "json"
                            });
                        });

                        submit.trigger('click');
                    }
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
