package com.tools.sockettools.control;


import java.util.List;
import java.util.Map;


public class ReturnResult {

    private Long total;
    
    private List<?> root;
    
    private Boolean success;

    private String message;

    private String errorCode;
    
    //private Map<String, Object> data;
    private Object data;
    
    public ReturnResult(){}
    
    public ReturnResult(Boolean success, String message){
        this.success = success;
        this.message = message;
    }
    
    public ReturnResult(Boolean success, String errorCode, String message){
        this.success = success;
        this.message = message;
        this.errorCode = errorCode;
    }
    
    public ReturnResult(Boolean success, Long total, List<?> root, String message){
        this.success = success;
        this.message = message;
        this.total = total;
        this.root = root;
    }
    
    public ReturnResult(Boolean success, Object data, String message){
        this.success = success;
        this.message = message;
        this.data = data;
    }
    
    public ReturnResult(Boolean success, Object data){
        this.success = success;
        this.data = data;
    }
    
    public ReturnResult(Boolean success){
        this.success = success;
    }
    
    public static ReturnResult success() {
        return success("");
    }
    
    public static ReturnResult success(String msg) {
        return (new ReturnResult(true, msg));
    }
    
    public static ReturnResult success(Long total, List<?> root) {
        String msg = "-";
        return (new ReturnResult(true, total, root, msg));
    }
    
    
    public static ReturnResult success(Object data) {
        return (new ReturnResult(true, data));
    }

    public static ReturnResult success(Object retData, String msg) {
        return (new ReturnResult(true, retData, msg));
    }
    
    public static ReturnResult failure() {
        return failure("");
    }
    
    public static ReturnResult failure(String msg) {
        return (new ReturnResult(false,(long)0,null,msg));
    }
    
    public static ReturnResult failure(String errorCode, String msg) {
        return (new ReturnResult(false, errorCode, msg));
    }
    
    //*****************************************************

    public Boolean isSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public Long getTotal() {
        return total;
    }

    
    public void setTotal(Long total) {
        this.total = total;
    }

    
    public List<?> getRoot() {
        return root;
    }

    
    public void setRoot(List<?> root) {
        this.root = root;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
