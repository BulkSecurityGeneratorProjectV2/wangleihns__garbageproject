package com.jin.env.garbage.utils;

/**
 * Created by abc on 2018/5/24.
 */
public class Constants {
    public enum loginStatus{
        UsernameNotFound(10010),
        BadCredentials(10011),
        AccountExpired(10012),
        Locked(10013),
        Disabled(10014),
        CredentialsExpired(10015),
        LoginSuccess(200);
        private Integer status;

        loginStatus(Integer status) {
            this.status = status;
        }
        public Integer getStatus() {
            return status;
        }
    }


    public enum accountStatus{
        UsernameNotFound("用户找不到"),
        BadCredentials("密码错误"),
        AccountExpired("账户过期"),
        Locked("账户锁定"),
        Disabled("账户不可用"),
        CredentialsExpired("证书过期");
        private String status;

        accountStatus(String status) {
            this.status = status;
        }

        public String getStatus() {
            return status;
        }
    }

    public enum tokenStatus {
        TokenExp(2000),
        TokenChange(3000),
        TOKEN_NOT_EXIST(5000),
        SignNotRight(40000);
        private Integer status;
        tokenStatus(int status) {
            this.status = status;
        }

        public int getStatus() {
            return status;
        }
    }

    /**
     * 签名秘钥
     */
    public enum signSecret {
        Secret("www.winding.top");
        private String content;

        signSecret(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }
    }

    public enum responseStatus{
        Success(200),
        Failure(500);
        private int status;

        responseStatus(int status) {
            this.status = status;
        }

        public int getStatus() {
            return status;
        }
    }

    /**
     * 数据状态
     */
    public enum dataType{
        DISABLE(0),ENABLE(1);
        private int type;

        dataType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }
    }

    /**
     * 图标类型枚举
     */
    public enum icon{
        DEVICE("设备"),
        LINKAGE("联动"),
        ROOM("房间"),
        SCENE("场景");

        private String type;

        icon(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }


    public enum deviceStatus{
        START,
        STOP
    }

    public enum activeStatus {
        ACTIVE,
        NO_ACTIVE
    }

    public enum taskStatus {
        WAITING,
        RUNNING,
        NO_ACTIVE
    }

    public enum taskType{
        START,  //开始定时任务
        END     // 结束定时任务
    }

}
