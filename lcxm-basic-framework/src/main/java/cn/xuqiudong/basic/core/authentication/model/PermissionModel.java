package cn.xuqiudong.basic.core.authentication.model;

import java.util.HashSet;
import java.util.Set;

/**
 * 描述: 权限对象：  所拥有权限code列表和权限url列表
 * @author Vic.xu
 * @since 2022-12-13 11:24
 */
public class PermissionModel {

    /**
     * code 列表
     */
    private Set<String> codes;

    /**
     * url 列表
     */
    private Set<String> urls;

    public PermissionModel() {
        this.codes = new HashSet<String>();
        this.urls = new HashSet<String>();
    }

    public PermissionModel(Set<String> codes, Set<String> urls) {
        this.codes = codes;
        this.urls = urls;
    }

    public PermissionModel addCode(String code) {
        this.codes.add(code);
        return this;
    }

    public PermissionModel addUrl(String url) {
        this.urls.add(url);
        return this;
    }


    public Set<String> getCodes() {
        return codes;
    }

    public void setCodes(Set<String> codes) {
        this.codes = codes;
    }

    public Set<String> getUrls() {
        return urls;
    }

    public void setUrls(Set<String> urls) {
        this.urls = urls;
    }
}
