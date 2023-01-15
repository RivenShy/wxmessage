package com.example.demo.controller;

import com.example.demo.entity.Consultant;
import com.example.demo.result.R;
import com.example.demo.service.IConsultantService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.StringUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/consultant")
public class ConsultantController {

    private static Logger logger = Logger.getLogger(BindApplyController.class);

    @Autowired
    private IConsultantService consultantService;

    @GetMapping("/consultants")
    public R<PageInfo<Consultant>> list(@RequestParam(value = "start", defaultValue = "1") int start, @RequestParam(value = "size", defaultValue = "5") int size, @RequestParam(value = "deleted", defaultValue = "0") int deleted) throws Exception {
        PageHelper.startPage(start,size,"id desc");
        List<Consultant> hs = consultantService.list(deleted);
        if(hs == null) {
            logger.error("查询实施顾问信息失败");
            R.fail("查询实施顾问信息失败");
        }
        PageInfo<Consultant> page = new PageInfo<>(hs,5); //5表示导航分页最多有5个，像 [1,2,3,4,5] 这样
        return R.data(page);
    }

    @PostMapping("/add")
    public R add(@RequestBody Consultant consultant) {

        if(StringUtil.isEmpty(consultant.getConsultCode())) {
            return R.fail("consultCode不能为空");
        }
        Consultant consultantDB = consultantService.selectByConsultCode(consultant.getConsultCode());
        if(consultantDB != null) {
            return R.fail("该账号已存在");
        }
        int result = consultantService.add(consultant);
        return R.status(result == 1);
    }

    @PostMapping("/update")
    public R update(@RequestBody Consultant consultant) {

        if(StringUtil.isEmpty(consultant.getConsultCode())) {
            return R.fail("consultCode不能为空");
        }
        Consultant consultantDB = consultantService.get(consultant.getId());
        if(consultantDB == null) {
            return R.fail("查询实施顾问信息失败");
        }
        int count = consultantService.checkIfExistConsultCode(consultant);
        if(count > 0) {
            return R.fail("该账号已存在");
        }
        int result = consultantService.update(consultant);
        return R.status(result == 1);
    }

    @PostMapping("/delete/{id}")
    public R delete(@PathVariable int id) {
        int result = consultantService.delete(id);
        return R.status(result == 1);
    }
}
