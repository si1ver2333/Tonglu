package com.cyd.xs.service.Impl;

import com.cyd.xs.dto.Home.HomeDTO;
import com.cyd.xs.dto.Home.RecommendRefreshDTO;
import com.cyd.xs.entity.Home.Activity;
import com.cyd.xs.entity.User.UserContent;
import com.cyd.xs.mapper.Home.CarouselMapper;
import com.cyd.xs.mapper.Home.HotActivityMapper;
import com.cyd.xs.mapper.UserContentMapper;
import com.cyd.xs.mapper.UserMapper;
import com.cyd.xs.service.HomeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {

    private final CarouselMapper carouselMapper;
    private final HotActivityMapper hotActivityMapper;
    private final UserMapper userMapper;
    private final UserContentMapper userContentMapper;

    // =====================================================
    // 首页数据
    // =====================================================
    @Override
    public HomeDTO getHomeData(Long userId) {
        log.info("用户 {} 获取首页数据", userId);

        HomeDTO homeDTO = new HomeDTO();

        try {
            // 1. 用户身份
            String userIdentity = getUserIdentity(userId);
            homeDTO.setUserIdentity(userIdentity);

            // 2. 轮播图
            homeDTO.setCarousel(getCarouselData());

            // 3. 热门活动
            homeDTO.setHotActivities(getHotActivities());

            // 4. 为你推荐（内容流，来自 user_contents）
            HomeDTO.RecommendedContent recommendedContent =
                    getHomeRecommendedContent(userIdentity, 1, 5);
            homeDTO.setRecommendedContent(recommendedContent);

            return homeDTO;
        } catch (Exception e) {
            log.error("获取首页数据失败", e);
            throw new RuntimeException("获取首页数据失败");
        }
    }

    // =====================================================
    // 为你推荐内容（首页内容流）
    // =====================================================
    private HomeDTO.RecommendedContent getHomeRecommendedContent(
            String userIdentity, Integer pageNum, Integer pageSize) {

        int offset = (pageNum - 1) * pageSize;
        List<UserContent> contents;

        if (userIdentity != null && !userIdentity.isEmpty()) {
            contents = userContentMapper
                    .findRecommendedContentsByIdentity(userIdentity, pageSize);
        } else {
            contents = userContentMapper
                    .findRecommendedContentsByPage(offset, pageSize);
        }

        HomeDTO.RecommendedContent result = new HomeDTO.RecommendedContent();
        result.setTotal(userContentMapper.countPublishedContents());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);

        List<HomeDTO.ContentItem> list = contents.stream().map(content -> {
            HomeDTO.ContentItem item = new HomeDTO.ContentItem();
            item.setId(content.getId());
            item.setTitle(content.getTitle());
            item.setType(content.getType());
            item.setAuthor(content.getAuthorName());
            item.setAvatarUrl(content.getAvatarUrl());
            item.setLikeCount(content.getLikeCount());
            item.setCollectCount(content.getCollectCount());
            item.setCommentCount(content.getCommentCount());
            item.setSummary(content.getSummary());
            item.setCoverImage(content.getCoverImage());
            item.setPublishTime(
                    content.getCreatedAt() != null
                            ? content.getCreatedAt()
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                            : ""
            );
            item.setLink("/api/v1/content/" + content.getId());
            return item;
        }).collect(Collectors.toList());

        result.setList(list);
        return result;
    }

    // =====================================================
    // 刷新推荐（接口方法，必须实现）
    // =====================================================
    @Override
    public RecommendRefreshDTO refreshRecommend(
            Long userId, Integer pageNum, Integer pageSize) {

        log.info("用户 {} 刷新推荐内容, pageNum={}, pageSize={}",
                userId, pageNum, pageSize);

        int offset = (pageNum - 1) * pageSize;

        List<UserContent> contents =
                userContentMapper.findRecommendedContentsByPage(offset, pageSize);

        Long total = userContentMapper.countPublishedContents();

        RecommendRefreshDTO result = new RecommendRefreshDTO();
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);

        List<RecommendRefreshDTO.ContentItem> list =
                contents.stream().map(content -> {
                    RecommendRefreshDTO.ContentItem item =
                            new RecommendRefreshDTO.ContentItem();
                    item.setId(content.getId());
                    item.setTitle(content.getTitle());
                    item.setType(content.getType());
                    item.setAuthor(content.getAuthorName());
                    item.setAvatarUrl(content.getAvatarUrl());
                    item.setLikeCount(
                            content.getLikeCount() != null ? content.getLikeCount() : 0);
                    item.setCollectCount(
                            content.getCollectCount() != null ? content.getCollectCount() : 0);
                    item.setCommentCount(
                            content.getCommentCount() != null ? content.getCommentCount() : 0);
                    item.setSummary(content.getSummary());
                    item.setCoverImage(content.getCoverImage());
                    item.setPublishTime(
                            content.getCreatedAt() != null
                                    ? content.getCreatedAt()
                                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                                    : ""
                    );
                    item.setLink("/api/v1/content/" + content.getId());
                    return item;
                }).collect(Collectors.toList());

        result.setList(list);
        return result;
    }

    // =====================================================
    // 用户身份
    // =====================================================
    private String getUserIdentity(Long userId) {
        try {
            String role = userMapper.getUserRole(userId);
            return (role != null && !role.isEmpty()) ? role : "student";
        } catch (Exception e) {
            log.warn("获取用户身份失败", e);
            return "student";
        }
    }

    // =====================================================
    // 轮播图
    // =====================================================
    private List<HomeDTO.Carousel> getCarouselData() {
        try {
            List<Activity> activities = carouselMapper.findActiveCarousels(5);
            return activities.stream().map(activity -> {
                HomeDTO.Carousel carousel = new HomeDTO.Carousel();
                carousel.setId(activity.getId());
                carousel.setTitle(activity.getTitle());
                carousel.setImageUrl(activity.getImageUrl());
                carousel.setDesc(activity.getDescription());
                carousel.setLink("/api/v1/activity/" + activity.getId());
                return carousel;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取轮播图失败", e);
            return List.of();
        }
    }

    // =====================================================
    // 热门活动
    // =====================================================
    private List<HomeDTO.HotActivity> getHotActivities() {
        try {
            List<Activity> activities = hotActivityMapper.findHotActivities(3);
            return activities.stream().map(activity -> {
                HomeDTO.HotActivity hot = new HomeDTO.HotActivity();
                hot.setId(activity.getId());
                hot.setTitle(activity.getTitle());

                if (activity.getStartTime() != null && activity.getEndTime() != null) {
                    String start = activity.getStartTime()
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                    String end = activity.getEndTime()
                            .format(DateTimeFormatter.ofPattern("HH:mm"));
                    hot.setTime(start + " - " + end);
                } else {
                    hot.setTime("时间待定");
                }

                hot.setParticipantCount(activity.getParticipantCount());
                hot.setLink("/api/v1/activity/" + activity.getId());
                return hot;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取热门活动失败", e);
            return List.of();
        }
    }
}
