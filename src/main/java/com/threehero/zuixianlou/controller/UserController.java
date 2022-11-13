package com.threehero.zuixianlou.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.threehero.zuixianlou.common.R;
import com.threehero.zuixianlou.pojo.User;
import com.threehero.zuixianlou.service.UserService;
import com.threehero.zuixianlou.utils.SMSUtils;
import com.threehero.zuixianlou.utils.ValidateCodeUtils;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

  @Autowired
  private UserService userService;

  @PostMapping("/sendMsg")
  public R<String> sendMsg(@RequestBody User user, HttpSession session) {
    String phone = user.getPhone();

    if (StringUtils.isNotEmpty(phone)) {
      String code = ValidateCodeUtils.generateValidateCode(6).toString();
      log.info("code= {} ", code);
      // SMSUtils.sendMessage("醉仙楼", "", phone, code);

      session.setAttribute(phone, code);
      return R.success("短信验证码发送成功");
    }

    return R.error("短信发送失败");
  }

  @PostMapping("/login")
  public R<User> login(@RequestBody Map map, HttpSession session) {

    String phone = map.get("phone").toString();
    String code = map.get("code").toString();
    Object codeInSession = session.getAttribute(phone);
    if (codeInSession != null && codeInSession.equals(code)) {
      LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
      queryWrapper.eq(User::getPhone, phone);
      User user = userService.getOne(queryWrapper);
      if (user == null) {
        user = new User();
        user.setStatus(1);
        user.setPhone(phone);
        userService.save(user);
      }
      session.setAttribute("user", user.getId());
      return R.success(user);
    }
    return R.error("登录失败");
  }

  /**
   * 退出登录
   * @param request
   * @return
   */
  @PostMapping("/loginout")
  public R<String> logout(HttpServletRequest request){
    request.getSession().removeAttribute("user");
    return R.success("安全退出成功！");
  }
}
