package com.kjlink.cloud.mybatis.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;


/**
 * <p>
 * 用户
 * </p>
 *
 * @author wanghy
 * @since 2023-01-17
 */
@TableName("t_sys_user")
@Schema(name = "User", description = "用户")
public class User extends BaseEntity {

    private static final long serialVersionUID = 7958203851940544551L;

    @Schema(description = "用户名")
    @TableField("user_name")
    private String userName;

    @Schema(description = "用户姓名")
    @TableField("full_name")
    private String fullName;

    @Schema(description = "用户英文姓名")
    @TableField("full_en_name")
    private String fullEnName;

    @Schema(description = "性别")
    @TableField("gender")
    private String gender;

    @Schema(description = "所属法人")
    @TableField("legal_org_id")
    private String legalOrgId;

    @Schema(description = "email")
    @TableField("email")
    private String email;

    @Schema(description = "手机号码")
    @TableField("mobile")
    private String mobile;

    @Schema(description = "电话号码")
    @TableField("telephone")
    private String telephone;

    @Schema(description = "是否锁定")
    @TableField("is_locked")
    private Boolean isLocked;

    @Schema(description = "是否部门负责人")
    @TableField("is_organ_manager")
    private Boolean isOrganManager;

    @Schema(description = "用户描述")
    @TableField(value = "memo", select = false)
    private String memo;

    @Schema(description = "职位")
    @TableField("position")
    private String position;

    @Schema(description = "授权方式")
    @TableField("accredit_method")
    private String accreditMethod;

    @Schema(description = "所属部门")
    @TableField("org_id")
    private String orgId;

    @Schema(description = "企业微信号")
    @TableField("work_wei_xin")
    private String workWeiXin;

    @Schema(description = "语言ZH_CN中文，EN_US英文")
    @TableField("language")
    private Language language;

    @Schema(description = "员工工号")
    @TableField("employee_no")
    private String employeeNo;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFullEnName() {
        return fullEnName;
    }

    public void setFullEnName(String fullEnName) {
        this.fullEnName = fullEnName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLegalOrgId() {
        return legalOrgId;
    }

    public void setLegalOrgId(String legalOrgId) {
        this.legalOrgId = legalOrgId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Boolean getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(Boolean locked) {
        isLocked = locked;
    }

    public Boolean getIsOrganManager() {
        return isOrganManager;
    }

    public void setIsOrganManager(Boolean organManager) {
        isOrganManager = organManager;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getAccreditMethod() {
        return accreditMethod;
    }

    public void setAccreditMethod(String accreditMethod) {
        this.accreditMethod = accreditMethod;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getWorkWeiXin() {
        return workWeiXin;
    }

    public void setWorkWeiXin(String workWeiXin) {
        this.workWeiXin = workWeiXin;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getEmployeeNo() {
        return employeeNo;
    }

    public void setEmployeeNo(String employeeNo) {
        this.employeeNo = employeeNo;
    }
}
