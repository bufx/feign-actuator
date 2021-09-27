package com.bocloud.paas.boms.qo;

/**
 * API调用关系
 * @author: buubiu
 * @create: 2021/9/24 16:31
 */
public class ApiRequestMappingSaveQo {

    /**
     * API接口名
     */
    private String apiUrl;

    /**
     * API所在的应用名
     */
    private String apiServiceEnName;

    /**
     * API接口URL请求类型（支持POST，GET，PUT，DELETE，PATCH）
     */
    private String apiUrlRequestType;

    /**
     * 调用API的应用名
     */
    private String invokeApiServiceEnName;

    /**
     * 上报类型（SDK：SDK上传 ,PORTAL_AC：平台访问权限上传）
     */
    private String reportType;

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getApiServiceEnName() {
        return apiServiceEnName;
    }

    public void setApiServiceEnName(String apiServiceEnName) {
        this.apiServiceEnName = apiServiceEnName;
    }

    public String getApiUrlRequestType() {
        return apiUrlRequestType;
    }

    public void setApiUrlRequestType(String apiUrlRequestType) {
        this.apiUrlRequestType = apiUrlRequestType;
    }

    public String getInvokeApiServiceEnName() {
        return invokeApiServiceEnName;
    }

    public void setInvokeApiServiceEnName(String invokeApiServiceEnName) {
        this.invokeApiServiceEnName = invokeApiServiceEnName;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }
}
