package com.newsinhand.fileoperate;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

public class SetCode {

	public static String getHtmlByUrl(String url){  
        String html = null;  
        HttpClient httpClient = new HttpClient();//创建httpClient对象  
        GetMethod getMethod = new GetMethod(url);   
         //设置页面编码   
        // if(url=="http://news.sina.com.cn/")
        getMethod.getParams().setContentCharset("utf-8");
        // else 
            // getMethod.getParams().setContentCharset("GB2312"); 
        try {  
        	int statusCode = httpClient.executeMethod(getMethod);
        	if (statusCode==200){//返回成功状态码200
        	//读取页面HTML源码
            html=getMethod.getResponseBodyAsString();
        	}
        } catch (Exception e) {  
            System.out.println("访问【"+url+"】出现异常!");  
            e.printStackTrace();  
        } 
        return html;  
	}
}
