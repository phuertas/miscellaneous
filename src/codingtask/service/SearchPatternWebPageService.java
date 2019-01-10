/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codingtask.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author pablo
 */
public class SearchPatternWebPageService implements Runnable {
    
    private String urlWebPage=null;
    private String pattern=null;
    private String patterName=null;
    private String parentFilePath=null;
    private String properName=null;
    
    public SearchPatternWebPageService(String url, String p,String pn,String pf,String pname){
        urlWebPage=url;
        pattern=p;
        patterName=pn;
        parentFilePath=pf;
        properName=pname;
    }
    
    public void run() {                    
        String name=urlWebPage.replace("http://","");
        name=name.replaceAll("https://", "");
        name=name.replaceAll("/", "");
        File fdir=new File(parentFilePath);
        if(!fdir.exists()){
            fdir.mkdir();
        }                
        String pathName=parentFilePath+"/"+name+"_"+patterName+".txt";
        File fn=new File(pathName);
        if(fn.exists()){
            fn.delete();
        }   
        try{                                
            Connection httpConnection = Jsoup.connect(this.urlWebPage);
            httpConnection.timeout(10000);
            Document htmlDocument = httpConnection.get();
            htmlDocument.select("script, style").remove();
            StringBuilder result = new StringBuilder();            
            if("propername".equals(patterName)){
                pattern=String.format(pattern, Pattern.quote(properName));
            }          
            Pattern pa = Pattern.compile(pattern);
            Matcher matcher = pa.matcher(htmlDocument.html());
            while (matcher.find()) {
                result.append(matcher.group(0)).append(System.lineSeparator());
            }
             BufferedWriter writer = new BufferedWriter(new FileWriter(pathName));
            if(!"".equals(result.toString())){                 
                writer.write(result.toString());               
            }else{
                writer.write("There are not results");
            }
             writer.close();
        }catch(UnknownHostException uhe){
            try{
                BufferedWriter writer = new BufferedWriter(new FileWriter(pathName));
                writer.write("Unknow Host Exception : " + uhe.getMessage()+" - "+uhe.getCause());     
                writer.close();  
            }catch(Exception e){
                System.out.println(e);
            }                                  
        }catch(Exception e){
            System.out.println(e);
        }        
    }    
}
