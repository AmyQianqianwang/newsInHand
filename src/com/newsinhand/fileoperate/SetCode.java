package com.newsinhand.fileoperate;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

public class SetCode {

	public static String getHtmlByUrl(String url){  
        String html = null;  
        HttpClient httpClient = new HttpClient();//����httpClient����  
        GetMethod getMethod = new GetMethod(url);   
         //����ҳ�����   
        // if(url=="http://news.sina.com.cn/")
        getMethod.getParams().setContentCharset("utf-8");
        // else 
            // getMethod.getParams().setContentCharset("GB2312"); 
        try {  
        	int statusCode = httpClient.executeMethod(getMethod);
        	if (statusCode==200){//���سɹ�״̬��200
        	//��ȡҳ��HTMLԴ��
            html=getMethod.getResponseBodyAsString();
        	}
        } catch (Exception e) {  
            System.out.println("���ʡ�"+url+"�������쳣!");  
            e.printStackTrace();  
        } 
        return html;  
	}
}
