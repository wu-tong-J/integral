<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>添加管理员</title>
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
        <div class="layui-form" lay-filter="layuiadmin-form-useradmin" id="layuiadmin-form-useradmin"
             style="padding: 20px 0 0 0;">
            <div class="layui-form-item" lay-filter="sex">
                <label class="layui-form-label">角色</label>
                <div class="layui-input-block">
                    <input type="radio" name="role" value="6" title="驿站管理员" checked>
                    <c:if test="${sessionScope.user.role==8}">
                        <input type="radio" name="role" value="7" title="上级管理员">
                    </c:if>
                </div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label">用户名</label>
                <div class="layui-input-inline">
                    <input type="text" name="username" lay-verify="lessOne" placeholder="请输入用户名" autocomplete="off"
                           class="layui-input">
                </div>
                <label class="layui-form-label">手机号码</label>
                <div class="layui-input-inline">
                    <input type="text" name="phone" id="LAY-phone" lay-verify="lessOne" placeholder="请输入号码"
                           autocomplete="off"
                           class="layui-input">
                </div>
                <label class="layui-form-label">验证码</label>
                <div class="layui-input-inline">
                    <input type="text" name="passcode" id="LAY-passcode" lay-verify="passcode" placeholder="验证码"
                           autocomplete="off"
                           class="layui-input" style="float: left;width: 40%">
                    <button type="button" class="layui-btn layui-btn-primary layui-btn-fluid" id="LAY-send"
                            style="width: 60%">获取验证码
                    </button>
                </div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label">密码</label>
                <div class="layui-input-inline">
                    <input type="password" name="password" lay-verify="required" placeholder="请输入密码" autocomplete="off"
                           class="layui-input">
                </div>
                <label class="layui-form-label">确认密码</label>
                <div class="layui-input-inline">
                    <input type="password" lay-verify="required|same" placeholder="确认密码"
                           autocomplete="off"
                           class="layui-input">
                </div>
                <div class="layui-input-inline">
                    <label class="layui-form-label"></label>
                    <input type="button" class="layui-btn layuiadmin-btn-useradmin" lay-submit
                           lay-filter="LAY-user-front-submit" id="LAY-user-front-submit"
                           value="添加">
                </div>
            </div>
        </div>

        <hr class="layui-bg-red">

        <div class="layui-form" lay-filter="layuiadmin-form-search" id="layuiadmin-form-search"
             style="padding: 20px 0 0 0;">
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
                    <label class="layui-form-label">角色</label>
                    <div class="layui-input-block">
                        <c:if test="${sessionScope.user.role==8}">
                            <input type="radio" name="role" value="" title="不限">
                        </c:if>
                        <input type="radio" name="role" value="6" title="驿站管理员">
                        <c:if test="${sessionScope.user.role==8}">
                            <input type="radio" name="role" value="7" title="上级管理员">
                        </c:if>
                    </div>
                </div>
                <div class="layui-inline">
                    <button class="layui-btn layuiadmin-btn-useradmin" lay-submit lay-filter="LAY-user-front-search">
                        <i class="layui-icon layui-icon-search layuiadmin-button-btn"></i>
                    </button>
                </div>
            </div>
        </div>

        <div class="layui-card-body" style="margin-top: -20px">
            <table id="LAY-manager-list" lay-filter="LAY-manager-list"></table>
            <script type="text/html" id="btn-manager-list">
                <a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="del">删除</a>
                {{#  if(d.role==7){ }}
                <a class="layui-btn layui-btn-xs" lay-event="giveYZ">分配下属驿站管理员</a>
                {{#  } }}
            </script>
        </div>
    </div>
</div>
<script src="${pageContext.request.contextPath}/layuiadmin/layui/layui.js"></script>
<script>
    function getRoleName(role) {
        if (role == 1) {
            return '普通用户'
        } else if (role == 2) {
            return '义工'
        } else if (role == 3) {
            return '老人'
        } else if (role == 4) {
            return '员工'
        } else if (role == 5) {
            return '商户'
        } else if (role == 6) {
            return '驿站管理员'
        } else if (role == 7) {
            return '上级管理员'
        } else if (role == 8) {
            return '超级管理员'
        } else if (role == 9) {
            return '义工+老人'
        }
    }

    layui.config({
        base: '${pageContext.request.contextPath}/layuiadmin/' //静态资源所在路径
    }).extend({
        index: 'lib/index' //主入口模块
    }).use(['index', 'form', 'table', 'admin'], function () {
        var $ = layui.$
            , form = layui.form
            , admin = layui.admin
            , table = layui.table;
        //发送短信验证码
        admin.sendAuthCode({
            elem: '#LAY-send'
            , elemPhone: '#LAY-phone'
            , elemVercode: '#LAY-passcode'
            , ajax: {
                url: '${pageContext.request.contextPath}/user/sendPassCode' //实际使用请改成服务端真实接口
            }
        });

        table.render({
            elem: '#LAY-manager-list'
            , url: '${pageContext.request.contextPath}/user/getNoStationManagerList' //模拟接口
            , method: "post"
            , cols: [[
                {field: 'phone', title: '手机号', sort: true}
                , {field: 'username', title: '用户名', sort: true}
                , {
                    field: 'role', title: '角色', sort: true
                    , templet: function (d) {
                        return getRoleName(d.role);
                    }
                }
                , {field: 'birthday', title: '生日', sort: true}
                //此处用templet和toolbar都可以
                , {title: '操作', align: 'center', fixed: 'right', toolbar: '#btn-manager-list'}
            ]]
            , page: true
            , limit: 20//每页显示数目
            , height: 'full-140'
            , text: '对不起，加载出现异常！'
        });

        table.on('tool(LAY-manager-list)', function (obj) {
            var data = obj.data;
            if (obj.event === 'del') {
                layer.confirm('确定删除?', {icon: 3, title: '提示'}, function (index) {
                    $.ajax({
                        type: "post",
                        url: "${pageContext.request.contextPath}/user/del",
                        data: {pkUserId: data.pkUserId},
                        success: function (ret) {
                            if (ret.msg == "ok") {
                                layer.alert('操作成功', {icon: 1});
                            } else {
                                layer.alert('操作失败!', {icon: 2});
                            }
                            table.reload('LAY-manager-list');
                            layer.close(index);
                        },
                        dataType: "json"
                    });
                });
            }
            if (obj.event === 'giveYZ') {
                layer.open({
                    type: 2
                    , title: '分配下属'
                    , content: '${pageContext.request.contextPath}/user/toManageStationManager?userId=' + data.pkUserId
                    , success: function (layero, index) {
                        //最大化
                        layer.full(index);
                        layero.find('iframe').height(document.documentElement.clientHeight - 44);
                    }
                });
            }
        });

        form.on('submit(LAY-user-front-submit)', function (data) {
            var field = data.field;
            $.ajax({
                type: "post",
                url: "${pageContext.request.contextPath}/user/createManager",
                data: field,
                success: function (ret) {
                    if (ret.msg == "ok") {
                        layer.alert('添加成功', {icon: 1});
                        table.reload('LAY-manager-list');
                        $("input[type!='radio']input[type!='button']").val("");
                    } else if (ret.msg == "passcodeError") {
                        layer.alert('验证码错误!', {icon: 2});
                    } else {
                        layer.alert('添加失败!', {icon: 2});
                    }
                },
                dataType: "json"
            });
        });

        //监听搜索
        form.on('submit(LAY-user-front-search)', function (data) {
            var field = data.field;
            table.reload('LAY-manager-list', {
                where: field
            });
        });

        form.verify({
            lessOne: function (value) {
                var username = $("input[name='username']").val();
                var phone = $("input[name='phone']").val();
                if ((username == null || username == "") && (phone == null || phone == "")) {
                    return '用户名和手机号至少填写一项';
                }
                var msg = "";
                if (username != null && username != "") {
                    //验证用户名唯一性
                    $.ajax({
                        type: "post",
                        async: false,
                        url: "${pageContext.request.contextPath}/user/checkNameUnique",
                        data: {username: username},
                        success: function (ret) {
                            if (ret.msg != "ok") {
                                msg = "用户名已存在";
                            }
                        },
                        dataType: "json"
                    });
                    if (msg != "") {
                        return msg;
                    }
                }
            }
            , passcode: function (value) {
                var phone = $("input[name='phone']").val();
                if (phone != null && phone != "" && (value == null || value == "")) {
                    return '验证码不能为空';
                }
            }
            , same: function (value) {
                var password = $("input[name='password']").val();
                if (password != value) {
                    return '两次密码不一致';
                }
            }
        });
    })
</script>
</body>
</html>