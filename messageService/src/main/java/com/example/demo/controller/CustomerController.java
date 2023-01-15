package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.Customer;
import com.example.demo.entity.Server;
import com.example.demo.result.R;
import com.example.demo.service.CustomerService;
import com.example.demo.util.Constant;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.StringUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    private static Logger logger = Logger.getLogger(CustomerController.class);

    public static final String logoLogicPath = "/logoData/";
    //  61服务器真实logo路径
    public static final String logoRealPath  = "/home/customerLogo/";
    // 本地测试地址logo路径
//    public static final String logoRealPath = "D:/customerLogo/";

    @Autowired
    private CustomerService customerService;

    @GetMapping("/listCustomer")
    public ModelAndView listUserInfo() {
        return new ModelAndView("listCustomer");
    }

    @GetMapping("/customers")
    public PageInfo<Customer> list(@RequestParam(value = "start", defaultValue = "1") int start, @RequestParam(value = "size", defaultValue = "5") int size, @RequestParam(value = "deleted", defaultValue = "0") int deleted) throws Exception {
        PageHelper.startPage(start,size,"id desc");
        List<Customer> hs = customerService.list(deleted);
//        System.out.println(hs.size());
//        for(Message message : hs) {
//            System.out.println("sendTime = " + message.getSendTime());
//        }
        PageInfo<Customer> page = new PageInfo<>(hs,5);

        return page;
    }

    @PostMapping("/add")
    public R add(@RequestBody Customer customer) {
        logger.info(customer);
        if(StringUtil.isEmpty(customer.getCustomerName())) {
            logger.error("客户名称不能为空，customerName = " + customer.getCustomerName());
            return R.fail("参数错误");
        }
        Customer customerDB = customerService.getByCustomerName(customer.getCustomerName());
        if(customerDB != null) {
            return R.fail("已有相同名称的客户");
        }
        int result = customerService.add(customer);
        return R.status(result != 0);
    }

    @PostMapping("/update")
    public String update(@RequestBody Customer customer) {
        logger.info(customer);
        JSONObject jsonObjectReturn = new JSONObject();
        if(customer.getId() == 0 || StringUtil.isEmpty(customer.getCustomerName())) {
            logger.error("客户Id、名称不能为空，customerName = " + customer.getCustomerName());
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "参数错误");
        }
        int result = customerService.update(customer);
        if(result == 0) {
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "更新失败");
            return jsonObjectReturn.toString();
        }
        jsonObjectReturn.put(Constant.code, 200);
        jsonObjectReturn.put(Constant.success, true);
        jsonObjectReturn.put(Constant.data, null);
        jsonObjectReturn.put(Constant.msg, "操作成功");
        return jsonObjectReturn.toString();
    }

    @GetMapping("/list")
    public String list() {
        List<Customer> customerList = customerService.list(-1);
        JSONObject jsonObjectReturn = new JSONObject();
        if(customerList == null) {
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "查询失败");
        } else {
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, customerList);
            jsonObjectReturn.put(Constant.msg, "查询成功");
        }
        return jsonObjectReturn.toString();
    }

    @PostMapping("/uploadCustomerLogo")
    public String uploadCustomerLogo(HttpServletResponse request, @RequestParam("file") MultipartFile file, @RequestParam("id") Integer id) {
        JSONObject jsonObjectReturn = new JSONObject();
        if(file.isEmpty() || id == null) {
            logger.error("参数错误，file = " + file + ",id = " + id);
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "参数错误");
            return jsonObjectReturn.toString();
        }
        List<String> imgsType = new ArrayList<>(Arrays.asList("image/jpg", "image/jpeg", "image/png"));
        if (!imgsType.contains(file.getContentType().toLowerCase())) {
            logger.error("请传入JPG、JPEG、PNG 等格式的图片");
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "请传入JPG、JPEG、PNG 等格式的图片");
            return jsonObjectReturn.toString();
        }

        Customer customer = customerService.get(id);
        if(customer == null) {
            logger.error("参数错误，file = " + file + ",id = " + id);
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "查询客户信息失败");
            return jsonObjectReturn.toString();
        }
        File folder = new File(logoRealPath);
        if(!folder.isDirectory()) {
            folder.mkdirs();
        }
        String originName = file.getOriginalFilename();
        String newName = customer.getId() + originName.substring(originName.lastIndexOf("."), originName.length());
        // 判断有，删除文件
        File jpgFile = new File(logoRealPath + customer.getId() + ".jpg");
        if(jpgFile.exists()) {
            jpgFile.delete();
        }
        File jpegFile = new File(logoRealPath + customer.getId() + ".jpeg");
        if(jpegFile.exists()) {
            jpegFile.delete();
        }
        File pngFile = new File(logoRealPath + customer.getId() + ".png");
        if(pngFile.exists()) {
            pngFile.delete();
        }
        try {
            file.transferTo(new File(folder, newName));
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("保存logo失败");
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "保存logo失败");
            return jsonObjectReturn.toString();
        }
        String customergLogicPath = logoLogicPath  + newName;
        customer.setLogoPath(customergLogicPath);
        int result = customerService.updateLogoPathById(customer);
        if(result == 1) {
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, true);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "操作成功");
            return jsonObjectReturn.toString();
        } else {
            logger.error("更新图片路径失败，" + customer);
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "更新图片路径失败");
            return jsonObjectReturn.toString();
        }
    }

    @PostMapping("/remove")
    public R<Boolean> remove(@RequestParam Integer id) {
        if(id == null) {
            logger.error("id为null");
            return R.fail("id不能为空");
        }
        int result = customerService.removeById(id);
        return R.status(result != 0);
    }
}
