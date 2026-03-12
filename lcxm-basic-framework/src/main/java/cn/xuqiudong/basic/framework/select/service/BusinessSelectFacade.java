package cn.xuqiudong.basic.framework.select.service;

import cn.xuqiudong.basic.core.model.SelectOption;
import cn.xuqiudong.basic.framework.select.function.BusinessSelectHandler;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 描述:
 * 业务下拉框门面:
 * 1. 注册 BusinessSelectService 的实现类
 * 2. 支持静态注册业务下拉框服务   #register
 *
 * @author Vic.xu
 * @since 2026-01-08 10:14
 */
public class BusinessSelectFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessSelectFacade.class);

    private final ObjectProvider<BusinessSelectProvider> provider;

    /**
     * 缓存服务实例
     */
    private static Map<String, BusinessSelectProvider> serviceMap = new ConcurrentHashMap<>();

    public BusinessSelectFacade(ObjectProvider<BusinessSelectProvider> provider) {
        this.provider = provider;
    }

    /**
     * 静态注册业务下拉框服务
     * 形如：  BusinessSelectFacade.register("default", () -> Collections.emptyList());
     */
    public static void register(String type, BusinessSelectHandler handler) {
        Assert.isTrue(StringUtils.isNotBlank(type), "下拉框类型不能为空");

        Assert.notNull(handler, "下拉框处理逻辑不能为空");

        String key = type.trim();
        if (serviceMap.containsKey(key)) {
            LOGGER.warn("【业务下拉框】静态注册：type={}已存在，将覆盖原有逻辑", key);
        }
        serviceMap.put(key, new BusinessSelectProvider() {
            @Override
            public String selectType() {
                return key;
            }

            @Override
            public List<SelectOption> getSelectOptions() {
                return handler.getSelectOptions();
            }
        });
        LOGGER.info("【业务下拉框】静态注册成功：type={}", key);
    }

    @PostConstruct
    public void init() {
        provider.orderedStream().forEach(service -> {
            String selectType = service.selectType();
            // 1 校验type为null
            if (selectType == null || selectType.trim().isEmpty()) {
                String serviceName = service.getClass().getName();
                LOGGER.warn("【业务下拉框】服务{}的selectType返回空，已忽略该服务", serviceName);
                return;
            }
            //  2 校验重复type
            if (serviceMap.containsKey(selectType)) {
                String existingService = serviceMap.get(selectType).getClass().getName();
                String newService = service.getClass().getName();
                LOGGER.warn("【业务下拉框】发现重复的selectType：{}，已有服务{}，新服务{}将覆盖原有服务",
                        selectType, existingService, newService);
            }
            serviceMap.put(selectType.trim(), service);
        });
        LOGGER.info("【业务下拉框】初始化服务完成，有效服务数量：{}，已注册的type列表：{}",
                serviceMap.size(), serviceMap.keySet());
    }


    /**
     * 获取指定类型的下拉选项，空type直接返回空列表
     */
    public List<SelectOption> getSelectOptions(String type) {
        if (StringUtils.isBlank(type)) {
            LOGGER.warn("【业务下拉框】入参type为空，返回空列表");
            return Collections.emptyList(); // 返回空列表，避免创建新ArrayList
        }
        BusinessSelectProvider service = serviceMap.get(type.trim());
        if (service == null) {
            LOGGER.warn("【业务下拉框】未找到type为{}的业务下拉框服务，已注册type：{}", type, serviceMap.keySet());
            return Collections.emptyList();
        }
        // 防护4：服务返回null的情况
        List<SelectOption> options = service.getSelectOptions();
        return Optional.ofNullable(options).orElse(Collections.emptyList());
    }

    /**
     * 获取所有已注册的业务下拉框type
     */
    public List<String> types(){
        return Collections.unmodifiableList(serviceMap.keySet().stream().toList());
    }

}
