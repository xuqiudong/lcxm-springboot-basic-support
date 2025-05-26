package cn.xuqiudong.common.base.statistics;

import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 描述:项目的一些基本信息统计
 * @author Vic.xu
 * @since 2022-12-15 10:16
 */
@SuppressWarnings("PMD")
public class ProjectInfoStatistics {

    /**
     * controller 以及请求统计
     * @param applicationContext ApplicationContext
     */
    public static void controller(ApplicationContext applicationContext) {
        Map<String, Object> restControllerMap = applicationContext.getBeansWithAnnotation(RestController.class);
        Map<String, Object> controllerMap = applicationContext.getBeansWithAnnotation(Controller.class);
        System.out.println("rest controller  size: " + restControllerMap.size());
        System.out.println("controller  size: " + restControllerMap.size());
        controllerMap.putAll(restControllerMap);

        List<ControllerRequest> controllerRequestList = new ArrayList<>();
        int urlNumber = 0;
        for (Map.Entry<String, Object> entry : restControllerMap.entrySet()) {
            Object value = entry.getValue();
            Class<?> clazz = AopUtils.getTargetClass(value);
            System.out.println("\t Controller: " + entry.getKey() + " = " + clazz.getSimpleName());
            ControllerRequest request = getRequest(clazz);
            controllerRequestList.add(request);
            urlNumber += request.urlNumber();
        }
        StringBuilder outline = new StringBuilder("controller总数为：");
        outline.append(controllerRequestList.size())
                .append(", url总数为：").append(urlNumber);
        System.err.println(outline);
        controllerRequestList.forEach(request -> {
            System.out.println(request.urlList());
        });
        System.err.println(outline);
    }

    public static void service(ApplicationContext applicationContext) {
        Map<String, Object> serviceMap = new HashMap<>();
        //serviceMap.putAll(applicationContext.getBeansWithAnnotation(Component.class));
        serviceMap.putAll(applicationContext.getBeansWithAnnotation(Service.class));
        System.out.println("service 总数为：" + serviceMap.size());
        Set<Map.Entry<String, Object>> entries = serviceMap.entrySet();
        int j = 0;
        for (Map.Entry<String, Object> entry : entries) {
            if (j % 5 == 0) {
                System.out.println("\t");
            }
            Class<?> clazz = AopUtils.getTargetClass(entry.getValue());
            System.out.print("\t" + clazz.getSimpleName());
            j++;
        }
        System.out.println();
    }


    public static ControllerRequest getRequest(Class<?> clazz) {

        String parentUrl = "";
        if (clazz.isAnnotationPresent(RequestMapping.class)) {
            parentUrl = String.join(",", clazz.getAnnotation(RequestMapping.class).value());
        }
        List<Method> publicMethods = Arrays.asList(clazz.getMethods());
        ControllerRequest request = new ControllerRequest(clazz.getName());
        for (Method publicMethod : publicMethods) {
            if (publicMethod.isAnnotationPresent(RequestMapping.class)) {
                request.requestMappingMap.add(parentUrl + String.join(",", publicMethod.getAnnotation(RequestMapping.class).value()));
            }
            if (publicMethod.isAnnotationPresent(GetMapping.class)) {
                request.getRequestMappingMap.add(parentUrl + String.join(",", publicMethod.getAnnotation(GetMapping.class).value()));
            }
            if (publicMethod.isAnnotationPresent(PostMapping.class)) {
                request.getRequestMappingMap.add(parentUrl + String.join(",", publicMethod.getAnnotation(PostMapping.class).value()));
            }
        }
        return request;
    }


    private static class ControllerRequest {
        String className;
        List<String> requestMappingMap = new ArrayList<>();

        List<String> getRequestMappingMap = new ArrayList<>();

        List<String> postRequestMappingMap = new ArrayList<>();

        public ControllerRequest(String className) {
            this.className = className;
        }

        public int urlNumber() {
            return requestMappingMap.size() * 8 + getRequestMappingMap.size() + postRequestMappingMap.size();
        }

        public String urlList() {
            StringBuilder stringBuilder = new StringBuilder("");
            stringBuilder.append(className).append("(").append(urlNumber()).append(") ");
            stringBuilder.append("\n\tAll[" + requestMappingMap.size() + "]:").append("\n\t\t ").append(String.join("\n\t\t", requestMappingMap));
            stringBuilder.append("\n\tGet[" + getRequestMappingMap.size() + "]:").append("\n\t\t").append(String.join("\n\t\t", getRequestMappingMap));
            stringBuilder.append("\n\tPost[" + postRequestMappingMap.size() + "]:").append("\n\t\t").append(String.join("\n\t\t", postRequestMappingMap));
            return stringBuilder.toString();
        }

    }
}
