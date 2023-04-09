package com.example.mybatplusdemo.controller;


import com.zhuozhengsoft.pageoffice.FileSaver;
import com.zhuozhengsoft.pageoffice.OpenModeType;
import com.zhuozhengsoft.pageoffice.PageOfficeCtrl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * PageOffice Demo
 */
@Controller
@RequestMapping("/page")
public class PageOfficeController {

    /**
     * 进入测试
     * @return
     */
    @RequestMapping(value="/index", method= RequestMethod.GET)
    public ModelAndView showIndex(){
        ModelAndView mv = new ModelAndView("index");
        return mv;
    }

    /**
     * office online打开
     * @param request
     * @param map
     * @return
     */
    @RequestMapping(value="/word", method=RequestMethod.GET)
    public ModelAndView showWord(HttpServletRequest request, Map<String,Object> map){

        //--- PageOffice的调用代码 开始 -----
        PageOfficeCtrl poCtrl=new PageOfficeCtrl(request);
        poCtrl.setServerPage("/poserver.zz");//设置授权程序servlet
        poCtrl.addCustomToolButton("保存","Save()",1); //添加自定义按钮
        poCtrl.addCustomToolButton("打印", "PrintFile()", 6);
        poCtrl.addCustomToolButton("全屏/还原", "IsFullScreen()", 4);
        poCtrl.addCustomToolButton("关闭", "CloseFile()", 21);
        poCtrl.setSaveFilePage("/page/save");//设置保存的action
        poCtrl.webOpen("D:\\page\\test.docx", OpenModeType.docAdmin,"张三");
        poCtrl.setCaption("信息平台");
        map.put("pageoffice",poCtrl.getHtmlCode("PageOfficeCtrl1"));
        //--- PageOffice的调用代码 结束 -----
        ModelAndView mv = new ModelAndView("word");
//        map.put("pageoffice", poCtrl.getHtmlCode("PageOfficeCtrl1"));
        System.out.println("poCtrl.getHtmlCode(\"PageOfficeCtrl1\"):" + poCtrl.getHtmlCode("PageOfficeCtrl1"));
        return mv;
    }

    @RequestMapping(value="/wordReturnString", method=RequestMethod.GET)
    @ResponseBody
    public String wordReturnString(HttpServletRequest request, Map<String,Object> map){

        //--- PageOffice的调用代码 开始 -----
        PageOfficeCtrl poCtrl=new PageOfficeCtrl(request);
        poCtrl.setServerPage("/poserver.zz");//设置授权程序servlet
        poCtrl.addCustomToolButton("保存","Save()",1); //添加自定义按钮
        poCtrl.addCustomToolButton("打印", "PrintFile()", 6);
        poCtrl.addCustomToolButton("全屏/还原", "IsFullScreen()", 4);
        poCtrl.addCustomToolButton("关闭", "CloseFile()", 21);
        poCtrl.setSaveFilePage("/page/save");//设置保存的action
        poCtrl.webOpen("D:\\page\\test.docx", OpenModeType.docAdmin,"张三");
        poCtrl.setCaption("信息平台");
        map.put("pageoffice",poCtrl.getHtmlCode("PageOfficeCtrl1"));
        //--- PageOffice的调用代码 结束 -----
        ModelAndView mv = new ModelAndView("word");
//        map.put("pageoffice", poCtrl.getHtmlCode("PageOfficeCtrl1"));
        System.out.println("poCtrl.getHtmlCode(PageOfficeCtrl1):" + poCtrl.getHtmlCode("PageOfficeCtrl1"));
        return poCtrl.getHtmlCode("PageOfficeCtrl1");
    }

    /**
     * 保存
     * @param request
     * @param response
     */
    @RequestMapping("/save")
    public void saveFile(HttpServletRequest request, HttpServletResponse response){
        FileSaver fs = new FileSaver(request, response);
        fs.saveToFile("d:\\page\\" + fs.getFileName());
        fs.close();
    }

}

