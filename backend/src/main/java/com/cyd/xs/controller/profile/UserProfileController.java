package com.cyd.xs.controller.profile;

import com.cyd.xs.Response.Result;
import com.cyd.xs.Utils.SecurityUtils;
import com.cyd.xs.dto.profile.DTO.UserPrivacyUpdateDTO;
import com.cyd.xs.dto.profile.DTO.UserProfileUpdateDTO;
import com.cyd.xs.dto.profile.VO.PersonalHomePageVO;
import com.cyd.xs.dto.profile.VO.UserPrivacyVO;
import com.cyd.xs.dto.profile.VO.UserProfileVO;
import com.cyd.xs.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/profile")
public class UserProfileController {

    @Autowired
    private UserService userService;

    /** 获取个人基础信息（展示用） */
    @GetMapping
    public Result<UserProfileVO> getPersonalProfile() {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) return Result.error("未登录或登录态无效");
        UserProfileVO profileVO = userService.getPersonalProfileByUserId(userId);
        return Result.success("获取个人信息成功", profileVO);
    }

    /** 编辑个人基础信息（保存用） */
    @PutMapping
    public Result<Void> updatePersonalProfile(@Valid @RequestBody UserProfileUpdateDTO updateDTO) {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) return Result.error("未登录或登录态无效");
        userService.updatePersonalProfileByUserId(userId, updateDTO);
        return Result.success("编辑个人信息成功");
    }

    /** 获取隐私设置 */
    @GetMapping("/privacy")
    public Result<UserPrivacyVO> getPrivacySettings() {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) return Result.error("未登录或登录态无效");
        UserPrivacyVO privacyVO = userService.getPrivacySettingsByUserId(userId);
        return Result.success("获取隐私设置成功", privacyVO);
    }

    /** 修改隐私设置 */
    @PutMapping("/privacy")
    public Result<Void> updatePrivacySettings(@Valid @RequestBody UserPrivacyUpdateDTO updateDTO) {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) return Result.error("未登录或登录态无效");
        userService.updatePrivacySettingsByUserId(userId, updateDTO);
        return Result.success("隐私设置修改成功");
    }

    /** 获取个人主页（你原来写错返回类型了） */
    @GetMapping("/homepage")
    public Result<PersonalHomePageVO> getPersonalHomePage() {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) return Result.error("未登录或登录态无效");
        PersonalHomePageVO homePageVO = userService.getPersonalHomePageByUserId(userId);
        return Result.success("获取个人主页成功", homePageVO);
    }

    /** 更新用户身份（职业阶段） */
    @PutMapping("/career-stage")
    public Result<Void> updateCareerStage(@RequestParam String careerStage) {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) return Result.error("未登录或登录态无效");
        userService.updateCareerStage(userId, careerStage);
        return Result.success("身份更新成功");
    }
}
