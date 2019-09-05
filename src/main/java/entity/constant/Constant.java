package entity.constant;

/**
 * @description 角色常量类
 **/
public class Constant {
	//普通
	public static final int ROLE_COMMON = 1;
	//义工
	public static final int ROLE_VOLUNTEER_WORKER = 2;
	//老人
	public static final int ROLE_OLD_MAN = 3;
	//员工
	public static final int ROLE_EMPLOYEE = 4;
	//商户
	public static final int ROLE_BUSINESS = 5;
	//驿站管理员
	public static final int ROLE_STATION_ADMIN = 6;
	//上级管理员
	public static final int ROLE_HIGHER_ADMIN = 7;
	//超级管理员
	public static final int ROLE_SUPER_ADMIN = 8;
	//老人+义工
	public static final int ROLE_OLD_MAN_AND_VOLUNTEER_WORKER = 9;

	//身份认证状态
	public static final int ROLE_STATUS_NEEDDEAL = 0;
	public static final int ROLE_STATUS_PASS = 1;
	public static final int ROLE_STATUS_NOPASS = 2;


	//领取方式：驿站
	public static final int GET_TYPE_STATION = 1;
	//领取方式：商户实体店
	public static final int GET_TYPE_BUSINESS = 2;

	//配置项名称
	public static final String CONFIG_AUTH_OLD_MAN = "认证老人";
	public static final String CONFIG_STAR = "星级规则";
	public static final String CONFIG_EXCHANGE = "赠分兑换汇率";
	public static final String CONFIG_AUTO_POINTS = "自动赠分";
	public static final String CONFIG_DEDUCTION = "工分抵扣积分汇率";
}
