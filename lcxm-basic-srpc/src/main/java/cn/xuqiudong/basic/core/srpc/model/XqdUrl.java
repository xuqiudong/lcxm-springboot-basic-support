package cn.xuqiudong.basic.core.srpc.model;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * 描述: 资源定位符：可以是请求地址
 * @author Vic.xu
 * @date 2022-02-15 11:25
 */
public class XqdUrl implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 地址
     */
    private String address;

    /**
     * 端口
     */
    private Integer port;

    /***
     * 接口全限定名
     */
    private String interfaceName;

    public XqdUrl() {
    }

    public XqdUrl(String address, Integer port, String interfaceName) {
        this.address = address;
        this.port = port;
        this.interfaceName = interfaceName;
    }



    /**
     * 当前节点
     * @return
     */
    public String getNode(){
        return address + ":" + port;
    }




    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "XqdUrl{" +
                "address='" + address + '\'' +
                ", port=" + port +
                ", interfaceName='" + interfaceName + '\'' +
                '}';
    }


    /**
     * 通过HashCodeBuilder重写hashCode方法
     * @return
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(address).append(port).append(interfaceName).toHashCode();
    }
}
