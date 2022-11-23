package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.Customer;
import com.example.demo.entity.Server;
import com.example.demo.service.CustomerService;
import com.example.demo.util.Constant;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.StringUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    private static Logger logger = Logger.getLogger(CustomerController.class);

    @GetMapping("/listCustomer")
    public ModelAndView listUserInfo() {
        return new ModelAndView("listCustomer");
    }

    @GetMapping("/customers")
    public PageInfo<Customer> list(@RequestParam(value = "start", defaultValue = "1") int start, @RequestParam(value = "size", defaultValue = "5") int size) throws Exception {
        PageHelper.startPage(start,size,"id desc");
        List<Customer> hs = customerService.list();
//        System.out.println(hs.size());
//        for(Message message : hs) {
//            System.out.println("sendTime = " + message.getSendTime());
//        }
        PageInfo<Customer> page = new PageInfo<>(hs,5);

        return page;
    }

    @PostMapping("/add")
    public String add(@RequestBody Customer customer) {
        logger.info(customer);
        JSONObject jsonObjectReturn = new JSONObject();
        if(StringUtil.isEmpty(customer.getCustomerName())) {
            logger.error("客户名称不能为空，customerName = " + customer.getCustomerName());
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "参数错误");
        }
        int result = customerService.add(customer);
        if(result == 0) {
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "添加失败");
            return jsonObjectReturn.toString();
        }
        jsonObjectReturn.put(Constant.code, 200);
        jsonObjectReturn.put(Constant.success, true);
        jsonObjectReturn.put(Constant.data, null);
        jsonObjectReturn.put(Constant.msg, "操作成功");
        return jsonObjectReturn.toString();
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
        List<Customer> customerList = customerService.list();
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
}
