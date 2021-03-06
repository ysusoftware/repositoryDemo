package org.software.ysu.controller;

import org.apache.commons.codec.digest.DigestUtils;
import org.software.ysu.pojo.*;
import org.software.ysu.service.Interface.IUserService;
import org.software.ysu.utils.DESUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Erisu
 * @date 2018/12/13 20:32
 * @Description 用户管理
 * @Version 1.0
 **/
@RestController
@RequestMapping("user")
public class userController {
    @Resource
    IUserService userService;
    @RequestMapping("initAddUser.do")
    public String randomAddUsers(){
        String text="Erisu";
        for (int i=0;i<10;i++){
            User user=new User();
            user.setUserAccount(String.valueOf(Math.random()*new Date().getTime()));
            //加密后的字符串
            //double key= Math.random();
            String key=text+i;
            String encodeStr=DigestUtils.md5Hex(key);
            user.setUserName("cookiezi");
            user.setUserPassword(encodeStr);
            if(Math.random()>=0.5){
                user.setUserAuthority(1);
            }else{
                user.setUserAuthority(2);
            }
            user.setUserRole(1);
            user.setUserLevel(1);
            user.setUserDes("i robot");
            user.setUserImg("no img");
            user.setUserLastdate(new Date());
            userService.addUser(user);
        }
        return "success";
    }
    @RequestMapping("cacheDel.do")
    public String delCache(){
        //清理introduction表的缓存
        // pass
        return "success";
    }
    @RequestMapping("userShow.do")
    public tableResponse getSubjects(Page page){
        System.out.println("page="+page.toString());
        UserExample userExample = new UserExample();
        if (page.getContext() != null){
            userExample.createCriteria().andUserNameLike("%" + page.getContext() + "%");
        }
        List<User> users=userService.showUser(userExample);
        int tempMin=Math.min(users.size(),page.getPage() * page.getLimit()+1);
        //前台真正显示的数据
        List<User>userPages=new ArrayList<>();
        for(int i=(page.getPage() - 1) * page.getLimit();i<tempMin;i++){
            userPages.add(users.get(i));
        }
        tableResponse tableResponse=new tableResponse("0","",users.size(),userPages);
        System.out.println(tableResponse.toString());
        return tableResponse;
    }
    @RequestMapping("userEdit.do")
    public String editUser(User user){
        System.out.println(user.toString());
        user.setUserPassword(DigestUtils.md5Hex(user.getUserPassword()));
        userService.updateUser(user);
        return "success";
    }
    @RequestMapping("editUserImage.do")
    public String editUserImage(User user){
        //System.out.println("beforeaaaaaaaaa:" + user.getUserId() + "  " + user.getUserImg()+ "   " + user.getUserDes());
        userService.updateUser(user);
        return "success";
    }
    @RequestMapping("userAll.do")
    public List<User> getAllUsers(){
        List<User> users = userService.showUser(new UserExample());
        return users;
    }
    @RequestMapping("userAdd.do")
    public String addUser(User user){
        user.setUserPassword(DigestUtils.md5Hex(user.getUserPassword()));
        int i = userService.addUser(user);
        if(i>0){
            return "success";
        }else{
            return "fail";
        }
    }
    @RequestMapping("userDel.do")
    public String delUser(int userId){
        int i=userService.delUser(userId);
        if(i==0) {
            return "fail";
        }else{
            return "success";
        }
    }
    @RequestMapping("userOnLoad.do")
    public layuiResponse imgOnLoad(@RequestParam(value = "file") MultipartFile img){
        String fileUrl=fileController.uploadFile("userImg",img);
        //StringBuilder URL=new StringBuilder();
        //URL.append("http://47.105.187.18/pictures/");
        //URL.append(fileUrl);
        //System.out.println("url=" + URL);
        layuiResponse layuiResponse=new layuiResponse("0","",fileUrl);
        return layuiResponse;
    }
    @RequestMapping("getUserById.do")
    public User getUserById(int userId){
//        UserExample userExample = new UserExample();
//        userExample.createCriteria().andUserIdEqualTo(userId);
        return userService.getUserById(userId);
    }
    @RequestMapping("getNameByCookie.do") //不加.do也能用？
    public String getNameByCookie(HttpServletRequest request){
        //对于userAccount的解码与存储
        String labUserCookie=request.getParameter("labUserCookie");
        String baseAccount=DESUtils.decode(labUserCookie.substring(1,labUserCookie.length()-1));
        baseAccount=baseAccount.substring(0,baseAccount.length()-5);
        UserExample userExample=new UserExample();
        userExample.createCriteria().andUserAccountEqualTo(baseAccount);
        User user=userService.showUser(userExample).get(0);
        return user.getUserName();
    }
    @RequestMapping("resetTime.do")
    public void resetTime(HttpServletRequest request){
        //对于userAccount的解码与存储
        String labUserCookie=request.getParameter("labUserCookie");
        String baseAccount=DESUtils.decode(labUserCookie.substring(1,labUserCookie.length()-1));
        baseAccount=baseAccount.substring(0,baseAccount.length()-5);
        UserExample userExample=new UserExample();
        userExample.createCriteria().andUserAccountEqualTo(baseAccount);
        User user=userService.showUser(userExample).get(0);
        //SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        user.setUserLastdate(new Date());
        userService.updateUser(user);
    }
}
