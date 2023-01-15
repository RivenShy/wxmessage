package com.example.mybatplusdemo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.mybatplusdemo.mapper.UserMapper;
import com.example.mybatplusdemo.model.User;
import com.example.mybatplusdemo.model.UserQuery;
import com.example.mybatplusdemo.service.IUserService;
import com.example.mybatplusdemo.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController("")
@RequestMapping("/hello")
public class HelloController {

    @Autowired
    private IUserService userService;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/success")
    public R<String> success() {
        return R.success("Hello Spring Boot!");
    }

    @GetMapping("/fail")
    public R<String> fail() {
        return R.fail("fail");
    }

    @PostMapping("/save")
    public R<Boolean> save(@RequestBody User user) {
        return R.status(userService.save(user));
    }

    @GetMapping("/removeById/{id}")
    public R<Boolean> save(@PathVariable Long id) {
        return R.status(userService.removeById(id));
    }

    @PostMapping("/update")
    public R<Boolean> update(@RequestBody User user) {
        return R.status(userService.updateById(user));
    }

    @GetMapping("/getById/{id}")
    public R<User> getUser(@PathVariable("id") Long id) {
        return R.data(userService.getById(id));
    }

    @GetMapping("/getAll")
    public R<List<User>> getAll() {
        return R.data(userService.list(null));
    }

    @GetMapping("/getUserAndOther")
    public R<Map<String, Object>> getUserAndOther() {
        Map<String, Object> map = new HashMap<>();
        map.put("others", "others");
        map.put("user", userService.getById(1L));
        return R.data(map);
    }

    @GetMapping("/getListUserCondition/{age}")
    public R<List<User>> getListUserCondition(@PathVariable Integer age) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.lt("age", age);
        return R.data(userService.list(queryWrapper));
    }

    @GetMapping("/selectPage/{startIndex}/{pageSize}")
    public R<IPage> selectPage(@PathVariable Integer startIndex, @PathVariable Integer pageSize) {
        // 创建IPage分页对象，设置分页参数，1为当前页码，3为每页显示的记录数
        IPage<User> page = new Page<>(startIndex, pageSize);
        // 执行分页查询
        IPage<User> pageResult = userService.page(page);
        // 获取分页结果
        System.out.println("当前页码值："+page.getCurrent());
        System.out.println("每页显示数："+page.getSize());
        System.out.println("一共多少页："+page.getPages());
        System.out.println("一共多少数："+page.getTotal());
        System.out.println("数据："+page.getRecords());
        System.out.println("数据："+page.getRecords());
        return R.data(pageResult);
    }

    @GetMapping("/queryWrapperLambda/{age}")
    public R<List<User>> queryWrapperLambda(@PathVariable Integer age) {
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().lt(User::getAge, age);
        return R.data(userService.list(queryWrapper));
    }

    @GetMapping("/lambdaQueryWrapper/{age}")
    public R<List<User>> lambdaQueryWrapper(@PathVariable Integer age) {
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper();
        lqw.lt(User::getAge, age);
        return R.data(userService.list(lqw));
    }

    @GetMapping("/lambdaQueryWrapper/{ageMin}/{ageMax}")
    public R<List<User>> lambdaQueryWrapper(@PathVariable Integer ageMin, @PathVariable Integer ageMax) {
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper();
        lqw.gt(User::getAge, ageMin);
        lqw.lt(User::getAge, ageMax);
        return R.data(userService.list(lqw));
    }

    @PostMapping("/lambdaQueryUser")
    public R<List<User>> lambdaQueryUser(@RequestBody UserQuery userQuery) {
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper();
        lqw.gt(null != userQuery.getAge(), User::getAge,userQuery.getAge())
                .lt(null != userQuery.getAge2(), User::getAge, userQuery.getAge2());
        return R.data(userService.list(lqw));
    }

    @GetMapping("/getNameAndEmail")
    public R<List<User>> getNameAndTel() {
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<User>();
        //查询结果只展示name和tel
        lqw.select(User::getNameXXX,User::getEmail);
        List<User> userList = userService.list(lqw);
        System.out.println(userList);
        return R.data(userService.list(lqw));
    }

    @GetMapping("/getNameAndEmailQueryWrapper")
    public R<List<User>> getNameAndTelQueryWrapper() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        //查询结果只展示name和tel
        queryWrapper.select("name,email");
        List<User> userList = userService.list(queryWrapper);
        System.out.println(userList);
        return R.data(userService.list(queryWrapper));
    }

    @GetMapping("/getCount")
    public R<List<User>> getCount() {
        QueryWrapper<User> queryWrapper=new QueryWrapper<User>();
        //select count(*) as count form user;
        queryWrapper.select("count(*) as count");
        List<Map<String, Object>> list = userService.listMaps(queryWrapper);
        System.out.println(list);
        return R.data(userService.list(queryWrapper));
    }

    @GetMapping("/getCountGroup")
    public R<List<User>> getCountGroup() {
        QueryWrapper<User> queryWrapper=new QueryWrapper<User>();
        //查询结果按tel分组
        //SELECT count(*) as count,tel FROM user GROUP BY tel
        queryWrapper.select("count(*) as count,email");
        queryWrapper.groupBy("email");
        List<Map<String, Object>> list = userService.listMaps(queryWrapper);
        System.out.println(list);
        return R.data(userService.list(queryWrapper));
    }

    @GetMapping("/getQueryWrapperEq")
    public R<User> getQueryWrapperEq() {
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<User>();
        //根据用户名和密码查询用户信息
        lqw.eq(User::getNameXXX,"Jack")
                .eq(User::getEmail,"test2@baomidou.com");
        //如果查询出来的结果只有一条记录，可以使用selectOne
        User user = userService.getOne(lqw);
        System.out.println(user);
        return R.data(userService.getOne(lqw));
    }

    @GetMapping("/getLambdaQueryWrapperBetween")
    public R<List<User>> getLambdaQueryWrapperBetween() {
        LambdaQueryWrapper<User> lqw=new LambdaQueryWrapper<User>();
        lqw.between(User::getAge,0,2);
        List<User> users = userService.list(lqw);
        System.out.println(users);
        return R.data(userService.list(lqw));
    }

    @GetMapping("/getLambdaQueryWrapperLike")
    public R<List<User>> getLambdaQueryWrapperLike() {
        LambdaQueryWrapper<User> lqw=new LambdaQueryWrapper<User>();
        lqw.like(User::getNameXXX,"n");
//        lqw.likeLeft(User::getName,"唐");
//        lqw.likeRight(User::getName,"唐");
        List<User> users = userService.list(lqw);
        System.out.println(users);
        return R.data(userService.list(lqw));
    }

    @GetMapping("/getLambdaQueryWrapperOrderBy")
    public R<List<User>> getLambdaQueryWrapperOrderBy() {
        LambdaQueryWrapper<User> lqw=new LambdaQueryWrapper<User>();
        /*condition:
         *    条件返回boolean,true进行排序，false不排序
         *isAsc:
         *      true为升序，false为降序
         * columns:
         *      需要操作的列
         *
         * */
        lqw.orderBy(true,false,User::getId);
        List<User> users = userService.list(lqw);
        System.out.println(users);
        return R.data(userService.list(lqw));
    }

    @GetMapping("/removeBatchByIds")
    public R<Boolean> removeBatchByIds() {
        List<String> list = new ArrayList<String>();
        list.add("1596508470133682176");
        list.add("2596508470133682176");
        int i = userMapper.deleteBatchIds(list);
        System.out.println("删除"+i+"行");
        return R.status(userService.removeBatchByIds(list));
    }

    @GetMapping("/listByIds")
    public R<List<User>> listByIds() {
        List<String> list = new  ArrayList<String>();
        list.add("1");
        list.add("2");
        List<User> users = userMapper.selectBatchIds(list);
        System.out.println(users);
        return R.data(userService.listByIds(list));
    }

    @GetMapping("/LogicRemoveById")
    public R<Boolean> LogicRemoveById() {
//        userDao.deleteById("4");
        return R.status(userService.removeById("4"));
    }

    @GetMapping("/selectAllIncludeDelete")
    public R<List<User>> selectAllIncludeDelete() {
        return R.data(userService.selectAllIncludeDelete());
    }

    @GetMapping("/updateByOptimisticLocker")
    public R<Boolean> updateByOptimisticLocker() {
        //先通过id将要修改的数据查询出来
        User user = userService.getById("3");
        //要修改的属性逐一设置进去
        user.setNameXXX("Jock666");
        userService.updateById(user);
        return R.status(userService.updateById(user));
    }

    @GetMapping("/updateByConCurrent")
    public R<Boolean> updateByConCurrent() {
        //先通过id将要修改的数据查询出来
        User user = userService.getById("3");
        User user2 = userService.getById("3");
        //要修改的属性逐一设置进去
        user.setNameXXX("Jock aaa");
        userService.updateById(user);

        user2.setNameXXX("Jock bbb");
//        userService.updateById(user2);

        return R.status(userService.updateById(user2));
    }

    @GetMapping("/selectAllFromXml")
    public R<List<User>> selectAllFromXml() {
        return R.data(userService.selectAllFromXml());
    }
}