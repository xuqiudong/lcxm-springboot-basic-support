package cn.xuqiudong.basic.quartz.helper;

import org.quartz.JobDataMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * 描述:
 * 为定时任务设置和获取当前用户信息
 *
 * @author Vic.xu
 * @since 2025-01-20 11:46
 */
public class JobUserHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobUserHolder.class);

    public static final String USER_ID = JobUserHolder.class.getSimpleName() + ".USER_ID";
    public static final String USER_NAME = JobUserHolder.class.getSimpleName() + ".USER_NAME";

    private static ThreadLocal<JobDataMap> jobUserThreadLocal = new ThreadLocal<>();

    /**
     * 设置 用户信息
     *
     * @param jobDataMap
     */
    public static void setJobUser(JobDataMap jobDataMap) {
        jobUserThreadLocal.set(jobDataMap);
    }

    /**
     * 清除与欧诺个户信息
     */
    public static void clearJobUser() {
        jobUserThreadLocal.remove();
    }

    /**
     * 获取当前执行任务的用户id：
     * 在活动执行一次的时候设置操作用户为当前用户，然后在任务内部获取
     */
    public static Optional<String> getUserId() {
        JobDataMap jobDataMap = jobUserThreadLocal.get();
        if (jobDataMap == null) {
            LOGGER.error("只允许quartz线程访问user id");
            return Optional.empty();
        }
        String userId = null;
        Object userIdObject = jobDataMap.get(USER_ID);
        if (userIdObject != null) {
            userId = userIdObject.toString();
        }
        return Optional.ofNullable(userId);
    }

    public static Optional<String> getUserName() {
        JobDataMap jobDataMap = jobUserThreadLocal.get();
        if (jobDataMap == null) {
            LOGGER.error("只允许quartz线程访问user name");
            return Optional.empty();
        }
        return Optional.ofNullable(jobDataMap.getString(USER_NAME));
    }

    public static String getUserIdNotNull() {
        return getUserId().orElse("-1");

    }

    public static String getUserNameNotNull() {
        return getUserName().orElse("系统");

    }

    /**
     * 构建任务数据：用户信息
     *
     * @param userId   user id
     * @param userName user name
     */
    public static JobDataMap buildJobDataMap(String userId, String userName) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(USER_ID, userId);
        jobDataMap.put(USER_NAME, userName);
        return jobDataMap;
    }


    public static void main(String[] args) {
        Optional<Object> empty = Optional.empty();
        empty.get();
    }


}
