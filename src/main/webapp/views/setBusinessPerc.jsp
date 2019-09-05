<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>设置商户提成</title>
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
                    <label class="layui-form-label">用户名</label>
                    <div class="layui-input-inline">
                        <input type="text" name="username" placeholder="请输入" autocomplete="off" class="layui-input">
                    </div>
                </div>
                <div class="layui-inline">
                    <label class="layui-form-label">手机号</label>
                    <div class="layui-input-inline">
                        <input type="text" name="phone" placeholder="请输入" autocomplete="off" class="layui-input">
                    </div>
                </div>
                <div class="layui-inline">
                    <label class="layui-form-label">驿站</label>
                    <div class="layui-input-block">
                        <select name="stationId" id="stationList" lay-filter="stationList">
                            <%--<option value="" selected>未选择</option>--%>
                        </select>
                    </div>
                </div>
                <div class="layui-inline">
                    <button class="layui-btn layuiadmin-btn-sb" id="sub" lay-submit lay-filter="LAY-sb-search">
                        <i class="layui-icon layui-icon-search layuiadmin-button-btn"></i>
                    </button>
                </div>
            </div>
        </div>

        <div class="layui-card-body">
            <div style="padding-bottom: 10px;">
                <button class="layui-btn layuiadmin-btn-useradmin" data-type="batchSetPerc">批量设置提成</button>
            </div>
            <table id="LAY-sb-list" lay-filter="LAY-sb-list"></table>
            <script type="text/html" id="btn-sb-list">
                <a class="layui-btn layui-btn-xs" lay-event="setPerc">设置提成</a>
            </script>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/layuiadmin/layui/layui.js"></script>
<script>
    var selectedStationId="";
    layui.config({
        base: '${pageContext.request.contextPath}/layuiadmin/' //静态资源所在路径
    }).extend({
        index: 'lib/index' //主入口模块
    }).use(['index', 'form', 'table'], function () {
        var $ = layui.$
            , form = layui.form
            , table = layui.table;

        $(function () {
            $.ajax({
                type: "post",
                url: "${pageContext.request.contextPath}/user/getStationListByUserId",
                data: {},
                success: function (ret) {
                    for (var i=0;i<ret.length;i++){
                        $("#stationList").append("<option value='"+ret[i].pkStationId+"'>"+ret[i].name+"</option>");
                    }
                    selectedStationId = $("#stationList option:selected").val();
                    form.render();
                },
                dataType: "json"
            });
        })

        table.render({
            elem: '#LAY-sb-list'
            , url: '${pageContext.request.contextPath}/user/getSetPercBusiList?stationId='+selectedStationId
            , method: "post"
            , cols: [[
                {type: 'checkbox', fixed: 'left'}
                , {
                    field: 'username', title: '用户名',
                    templet: function (d) {
                        if(d.user.username==null){
                            return "";
                        }else{
                            return d.user.username;
                        }
                    }
                }
                , {
                    field: 'phone', title: '手机号',
                    templet: function (d) {
                        return d.user.phone;
                    }
                }
                , {
                    field: 'birthday', title: '生日',
                    templet: function (d) {
                        if (d.user.birthday == null || d.user.birthday == "") {
                            return "未填写";
                        }
                        return d.user.birthday;
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
                , {
                    field: 'station', title: '所属驿站', templet: function (d) {
                        if (d.station.name == null) {
                            return "无";
                        }
                        return d.station.name;
                    }
                }
                //此处用templet和toolbar都可以
                , {title: '操作', align: 'center', fixed: 'right', toolbar: '#btn-sb-list'}
            ]]
            , page: true
            , limit: 20//每页显示数目
            , height: 'full-200'
            , text: '对不起，加载出现异常！'
            , done: function (res, curr, count) {
                //表格渲染完后的回调函数

            }
        });

        /*form.on('select(stationList)', function(data){
            selectedStationId = $("#stationList option:selected").val();
            $('#sub').click();
        });*/

        //监听搜索
        form.on('submit(LAY-sb-search)', function (data) {
            var field = data.field;
            table.reload('LAY-sb-list', {
                where: field
            });
        });

        //监听工具条
        table.on('tool(LAY-sb-list)', function (obj) {
            var data = obj.data;
            debugger;
            if (obj.event === 'setPerc') {
                layer.prompt({
                    formType: 0,
                    value: '',
                    title: '请输入提成',
                    area: ['800px', '350px'] //自定义文本域宽高
                }, function (value, index, elem) {
                    var regex = /^(([1-9][0-9]*)|(([0]\.\d{1,2}|[1-9][0-9]*\.\d{1,2})))$/;
                    if (!regex.test(value)) {
                        layer.alert('请输入正确格式的数字', {icon: 2});
                        return false;
                    }
                    if(Number(value)>100){
                        layer.alert('提成不能超过100', {icon: 2});
                        return false;
                    }
                    $.ajax({
                        type: "post",
                        url: "${pageContext.request.contextPath}/user/setBusinessPerc",
                        data: {perc: value, pkBusiIds: data.businessId, stationIds: data.stationId},
                        success: function (ret) {
                            if (ret.msg == "ok") {
                                layer.alert('设定成功', {icon: 1});
                                table.reload('LAY-sb-list');
                                layer.close(index);
                            } else if (ret.msg == "error") {
                                layer.alert('操作失败!', {icon: 2});
                            } else {
                                layer.alert(ret.msg, {icon: 2});
                                layer.close(index);
                                table.reload('LAY-sb-list');
                            }
                        },
                        traditional: true,
                        dataType: "json"
                    });
                });
            }
        });


        //事件
        var active = {
            batchSetPerc: function () {
                var checkStatus = table.checkStatus('LAY-sb-list')
                    , checkData = checkStatus.data; //得到选中的数据
                if (checkData.length === 0) {
                    return layer.msg('请选择数据');
                }
                var busiIds = [];
                var stationIds = [];
                for (var i = 0; i < checkData.length; i++) {
                    busiIds.push(checkData[i].businessId);
                    stationIds.push(checkData[i].stationId);
                }
                layer.prompt({
                    formType: 0,
                    value: '',
                    title: '请输入提成',
                    area: ['800px', '350px'] //自定义文本域宽高
                }, function (value, index, elem) {
                    var regex = /^(([1-9][0-9]*)|(([0]\.\d{1,2}|[1-9][0-9]*\.\d{1,2})))$/;
                    if (!regex.test(value)) {
                        layer.alert('请输入正确格式的数字', {icon: 2});
                        return false;
                    }
                    $.ajax({
                        type: "post",
                        url: "${pageContext.request.contextPath}/user/setBusinessPerc",
                        data: {perc: value, pkBusiIds: busiIds, stationIds: stationIds},
                        success: function (ret) {
                            if (ret.msg == "ok") {
                                layer.alert('设定成功', {icon: 1});
                                table.reload('LAY-sb-list');
                                layer.close(index);
                            } else if (ret.msg == "error") {
                                layer.alert('操作失败!', {icon: 2});
                            } else {
                                layer.alert(ret.msg, {icon: 2});
                                layer.close(index);
                                table.reload('LAY-sb-list');
                            }
                        },
                        traditional: true,
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
