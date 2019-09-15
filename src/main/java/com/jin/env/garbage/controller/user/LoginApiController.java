package com.jin.env.garbage.controller.user;

import com.aliyuncs.exceptions.ClientException;
import com.jin.env.garbage.entity.user.GarbageUserEntity;
import com.jin.env.garbage.jwt.JwtUtil;
import com.jin.env.garbage.service.user.GarbageUserService;
import com.jin.env.garbage.utils.Constants;
import com.jin.env.garbage.utils.ResponseData;
import com.jin.env.garbage.utils.ResponsePageData;
import com.jin.env.garbage.utils.SmsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(value = "api/v1/user/")
public class LoginApiController {
    private Logger logger = LoggerFactory.getLogger(LoginApiController.class);

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private GarbageUserService garbageUserService;

    @RequestMapping(value = "login",method = RequestMethod.POST)
    public  ResponseData login(String password, String username, String from){
        Assert.hasText(username, "登录名不能为空");
        Assert.hasText(password, "密码不能为空");
        ResponseData responseData = garbageUserService.findByPhoneOrLoginNameOrENoOrIdCard(password, username, from);
        return responseData;
    }

    @RequestMapping(value = "register",method = RequestMethod.POST)
    public ResponseData register(String jsonParam){
        Assert.hasText(jsonParam,"注册信息不能为空");
        ResponseData responseData = garbageUserService.register(jsonParam);
        return responseData;
    }



    @RequestMapping(value = "refreshAccessToken",method = RequestMethod.GET)
    public ResponseData refreshAccessToken(String refreshToken, HttpServletRequest request){
        String jwt = request.getHeader("Authorization").split(" ")[1];
       ResponseData  responseData=  garbageUserService.refreshAccessToken(refreshToken,jwt);
        return responseData;
    }

    @RequestMapping(value = "getVerificationCode",method = RequestMethod.GET)
    public ResponseData getVerificationCode(String phoneNum){
        ResponseData responseData = new ResponseData();
        SmsUtil smsUtil = SmsUtil.getSmsUtil();
        String randomCode = smsUtil.randomCode(6);
//        randomCode = "123456";
        boolean flag = false;
        try {
            flag =  smsUtil.sendSms(phoneNum,randomCode);
        } catch (ClientException e) {
            e.printStackTrace();
            responseData.setStatus(500);
            responseData.setMsg("短信模板配置不正确，请校验");
            logger.info("短信模板配置不正确，请校验");
            return responseData;
        }
        if (flag){
            redisTemplate.opsForValue().set("randomCode:"+phoneNum, randomCode,30*60*1000, TimeUnit.MILLISECONDS);
            responseData.setStatus(200);
            responseData.setMsg("短信验证码");
            responseData.setData(randomCode);
        }
        return responseData;
    }

    @RequestMapping(value = "updatePassword", method = RequestMethod.POST)
    public ResponseData updatePassword(String oldPassword, String newPassword, String repeatPassword, HttpServletRequest request){
        Assert.hasText(oldPassword, "原始密码不能为空");
        Assert.hasText(newPassword, "新密码不能空");
        Assert.hasText(repeatPassword, "重复密码不能为空");
        Assert.state(newPassword.equals(repeatPassword), "新密码与重复密码不一致");
        String jwt = request.getHeader("Authorization").split(" ")[1];
        ResponseData responseData = garbageUserService.updatePassword(jwt, oldPassword, newPassword);
        return responseData;
    }

    /**
     * 查看收集员或者评分员
     * @param cityId
     * @param countryId
     * @param townId
     * @param villageId
     * @param orderBys
     * @param request
     * @return
     */
    @RequestMapping(value = "collectorList", method = RequestMethod.GET)
    public ResponseData collectorList(String type, String keyWord,
                                          Long cityId, Long countryId, Long townId, Long villageId, Integer communityId,
                                          String[] orderBys, HttpServletRequest request){
        String jwt = request.getHeader("Authorization").split(" ")[1];
        Integer pageNo = Integer.valueOf(request.getParameter("pageNo"));
        Integer pageSize = Integer.valueOf(request.getParameter("pageSize"));
        ResponseData responsePageData = garbageUserService.collectorList(type, keyWord, cityId, countryId, townId, villageId, communityId, jwt, pageNo, pageSize, orderBys);
        return responsePageData;
    }

    @RequestMapping(value = "getUserInfoById", method = RequestMethod.GET)
    public ResponseData getUserInfoById(HttpServletRequest request){
        String jwt = request.getHeader("Authorization").split(" ")[1];
        ResponseData responseData = garbageUserService.getUserInfoById(jwt);
        return responseData;
    }

    @RequestMapping(value = "deleteUserById", method = RequestMethod.POST)
    public ResponseData deleteUserById(HttpServletRequest request, Integer userId,  Integer status){
        String jwt = request.getHeader("Authorization").split(" ")[1];
        ResponseData responseData = garbageUserService.deleteUserById(userId, status);
        return responseData;
    }

    @RequestMapping(value = "updateUserInfo", method = RequestMethod.POST)
    public ResponseData updateUserInfo(Integer userId, String name, Integer sex, String idCard, String phone,  Long provinceId,
                                       String provinceName, Long cityId, String cityName, Long countryId, String countryName,
                                       Long townId, String townName, Long villageId, String villageName, String address,  Integer fileId){
        Assert.state(userId != null, "用戶id必传");
        Assert.state(name != null, "用戶姓名必传");
        Assert.state(sex != null, "用戶性别必传");
        Assert.hasText(phone, "用户手机号必传");
        Assert.hasText(address, "用户地址必传");
//        Assert.hasText(idCard, "用用户身份证必传");
//        Assert.state(provinceId != null, "用户所在省必传");
//        Assert.state(cityId != null, "用户所在市必传");
//        Assert.state(countryId != null, "用户所在区县必传");
//        Assert.state(townId != null, "用户所在乡镇必传");
//        Assert.state(villageId != null, "用户所在村必传");
        ResponseData responseData = garbageUserService.updateUserInfo(userId, name, sex, idCard, phone, provinceId, provinceName, cityId,cityName, countryId, countryName,
                townId,townName, villageId, villageName, address ,fileId);
        return responseData;
    }

    @RequestMapping(value = "residentList", method = RequestMethod.GET)
    public ResponseData residentList(String type, String keyWord, Long provinceId,
                                     Long cityId, Long countryId,  Long townId, Long villageId, Integer communityId,
                                     String checkType,  String[] orderBys, HttpServletRequest request){
        String jwt = request.getHeader("Authorization").split(" ")[1];
        Integer pageNo = Integer.valueOf(request.getParameter("pageNo"));
        Integer pageSize = Integer.valueOf(request.getParameter("pageSize"));
        ResponseData responseData = garbageUserService.residentList(type, keyWord, provinceId, cityId, countryId, townId, villageId, communityId, checkType, jwt, pageNo, pageSize, orderBys);
        return responseData;
    }

    /**
     * 主页会员数量 今日手机 订单回收，积分商品兑换统计信息
     * @param request
     * @return
     */
    @RequestMapping(value = "getSummaryInfoInManagerCenter", method = RequestMethod.GET)
    public ResponseData getSummaryInfoInManagerCenter(HttpServletRequest request){
        String jwt = request.getHeader("Authorization").split(" ")[1];
        ResponseData responseData = garbageUserService.getSummaryInfoInManagerCenter(jwt);
        return responseData;
    }

    /**
     * 近5个月的用户注册量
     * @return
     */
    @RequestMapping(value = "getRegisterUserCountInMonth", method = RequestMethod.GET)
    public ResponseData getRegisterUserCountInMonth(){
        ResponseData responseData = garbageUserService.getRegisterUserCountInMonth();
        return responseData;
    }

    /**
     * 大数据中心的统计信息
     * @param request
     * @return
     */
    @RequestMapping(value = "getSummaryInfoInBigDataCenter", method = RequestMethod.GET)
    public ResponseData getSummaryInfoInBigDataCenter(HttpServletRequest request){
        ResponseData responseData = garbageUserService.getSummaryInfoInBigDataCenter();
        return responseData;
    }

    @RequestMapping(value = "insertUserInfoBatch", method = RequestMethod.POST)
    public ResponseData insertUserInfoBatch(@RequestParam("file") MultipartFile multipartFile, HttpServletRequest request){
        ResponseData responseData = garbageUserService.insertUserInfoBatch(multipartFile);
        return responseData;
    }
    @RequestMapping(value = "getUserInfoByEno", method = RequestMethod.GET)
    public ResponseData getUserInfoByEno(String eNo){
        Assert.hasText(eNo, "请输入电子卡eNo");
        ResponseData responseData = garbageUserService.getUserInfoByEno(eNo);
        return responseData;
    }

    @RequestMapping(value = "userManagement", method = RequestMethod.GET)
    public ResponseData userManagement(Integer pageNo, Integer pageSize, String name, String phone, String[] orderBys, HttpServletRequest request){
        String jwt = request.getHeader("Authorization").split(" ")[1];
        ResponseData responseData = garbageUserService.userManagement(pageNo, pageSize, name, phone, jwt, orderBys);
        return responseData;
    }

    /**
     *  用户授予角色
     * @param userId
     * @param roleId
     * @return
     */
    @RequestMapping(value = "addRoleToUser", method = RequestMethod.POST)
    public ResponseData addRoleToUser(Integer userId, Integer[] roleId){
        Assert.state(userId !=null, "用户id必传");
        Assert.state(roleId.length > 0 , "角色id必传");
        ResponseData responseData = garbageUserService.addRoleToUser(userId, roleId);
        return responseData;
    }

}
