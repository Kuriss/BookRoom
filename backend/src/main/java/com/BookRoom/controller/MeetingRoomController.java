package com.BookRoom.controller;

import com.BookRoom.dto.MeetingRoomFilterRequest;
import com.BookRoom.dto.PaymentRequest;
import com.BookRoom.entity.meetingRoom.MeetingRoom;
import com.BookRoom.entity.meetingRoom.MeetingRoomSelection;
import com.BookRoom.mapper.MeetingRoomSelectionMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.BookRoom.dto.MeetingRoomRequest;
import com.BookRoom.dto.MeetingRoomSelectRequest;
import com.BookRoom.entity.view.MeetingRoomSelectView;
import com.BookRoom.entity.view.MeetingRoomView;
import com.BookRoom.service.MeetingRoomService;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * 会议室控制器，处理会议室相关的HTTP请求
 * 包括会议室查询、分页获取等功能
 */
@RestController
public class MeetingRoomController {
    private final MeetingRoomService meetingRoomService;

    private MeetingRoomSelectionMapper meetingRoomSelectionMapper;

    /**
     * 构造函数，通过依赖注入初始化服务
     *
     * @param meetingRoomService 会议室服务，处理会议室相关的业务逻辑
     */
    public MeetingRoomController(MeetingRoomService meetingRoomService) {
        this.meetingRoomService = meetingRoomService;
    }

    /**
     * 分页获取所有会议室信息
     * <p>
     * 该方法用于分页获取系统中所有会议室的基本信息。通过MeetingRoomService调用数据访问层，
     * 检索并返回指定页码和每页记录数的会议室记录。支持按院系ID列表筛选会议室。
     *
     * @param current       当前页码，默认为1
     * @param size          每页记录数，默认为16
     * @return 包含分页会议室信息的Page对象，包含总记录数、总页数、当前页数据等信息
     */
    @GetMapping("/meetingRoom/getPage")
    public Page<MeetingRoomView> getAllMeetingRoom(
            @RequestParam(value = "current", defaultValue = "1") long current,
            @RequestParam(value = "size", defaultValue = "16") long size,
            @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(value = "minCapacity", required = false) Integer minCapacity) {
        return meetingRoomService.getAllMeetingRoomsByPage(current, size,minPrice, maxPrice,
                minCapacity);
    }

    /**
     * 根据会议室ID获取会议室详细信息
     * <p>
     * 支持分页查询，默认每页16条记录
     *
     * @param current       当前页码，默认为1
     * @param size          每页记录数，默认为16
     * @param meetingRoomId 会议室ID
     * @return 包含分页会议室信息的Page对象
     */
    @GetMapping("/meetingRoom/{meetingRoomId}")
    public Page<MeetingRoomView> getMeetingRoomDetails( // 修改返回类型
            @RequestParam(value = "current", defaultValue = "1") long current,
            @RequestParam(value = "size", defaultValue = "16") long size,
            @PathVariable Integer meetingRoomId) {
        return meetingRoomService.getMeetingRoomDetails(meetingRoomId, current, size); // 修改方法名
    }
    // /**
    // * 根据会议室ID获取会议室信息及关联的员工信息
    // * <p>
    // * 该方法用于根据会议室ID获取会议室的基本信息和关联的员工信息。通过MeetingRoomService调用数据访问层，
    // * 检索并返回指定会议室ID的会议室记录及其关联的员工信息。支持分页查询。
    // *
    // * @param current 当前页码，默认为1
    // * @param size 每页记录数，默认为16
    // * @param meetingRoomId 会议室ID，用于指定需要查询的会议室
    // * @return 包含分页会议室信息及员工信息的Page对象，包含总记录数、总页数、当前页数据等信息
    // */
    // @GetMapping("/meetingRoom/{meetingRoomId}")
    // public Page<MeetingRoomWithEmployeeView>
    // getMeetingRoomWithEmployeesByMeetingRoomId(
    // @RequestParam(value = "current", defaultValue = "1") long current,
    // @RequestParam(value = "size", defaultValue = "16") long size,
    // @PathVariable Integer meetingRoomId) {
    // return
    // meetingRoomService.getMeetingRoomWithEmployeesByMeetingRoomId(meetingRoomId,
    // current, size);
    // }

    /**
     * 创建或更新会议室
     * <p>
     * 该方法用于创建新会议室或更新现有会议室的信息。通过MeetingRoomService调用数据访问层，
     * 将会议室信息和员工关联关系保存到数据库。创建者ID从当前登录用户中获取。
     *
     * @param request            HTTP请求对象，用于获取当前登录用户的ID
     * @param meetingRoomRequest 包含会议室信息和员工ID列表的请求对象
     * @return 会议室ID
     */
    @PostMapping("/meetingRoom/createOrUpdate")
    public Integer createOrUpdateMeetingRoom(
            HttpServletRequest request,
            @RequestBody MeetingRoomRequest meetingRoomRequest) {
        return meetingRoomService.createOrUpdateMeetingRoom(
                meetingRoomRequest.meetingRoom());
    }

    @PostMapping("/meetingRoom/reverse")
    public boolean selectMeetingRoom(
            HttpServletRequest request,
            @RequestBody MeetingRoomSelectRequest meetingRoomSelectRequest) {
        String customerId = (String) request.getAttribute("userId");
        System.out.println("用户ID: " + customerId);
        System.out.println("开始时间: " + meetingRoomSelectRequest.startTime());
        System.out.println("结束时间: " + meetingRoomSelectRequest.endTime());
        // 解析时间字符串
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse(meetingRoomSelectRequest.startTime(), formatter);
        LocalDateTime endTime = LocalDateTime.parse(meetingRoomSelectRequest.endTime(), formatter);

        return meetingRoomService.selectMeetingRoom(
                meetingRoomSelectRequest.meetingRoomId(),
                customerId,
                startTime,
                endTime);
    }

    @GetMapping("/customer/meetingRoom/getPage")
    public Page<MeetingRoomSelectView> getCustomerMeetingRoomSelections(
            HttpServletRequest request,
            @RequestParam(value = "current", defaultValue = "1") long current,
            @RequestParam(value = "size", defaultValue = "16") long size) {
        String customerId = (String) request.getAttribute("userId");
        return meetingRoomService.getCustomerMeetingRoomSelections(customerId, current, size);
    }

    @GetMapping("/meetingRoom/{meetingRoomId}/customers/getPage")
    public Page<MeetingRoomSelectView> getMeetingRoomCustomers(
            @RequestParam(value = "current", defaultValue = "1") long current,
            @RequestParam(value = "size", defaultValue = "16") long size,
            @PathVariable Integer meetingRoomId,
            @RequestParam(value = "employeeId") String employeeId) {
        return meetingRoomService.getMeetingRoomCustomers(meetingRoomId, current, size);
    }

    @PutMapping("/meetingRoom/{meetingRoomId}/pay")
    public ResponseEntity<?> confirmPayment(
            @PathVariable Integer meetingRoomId,
            @RequestBody PaymentRequest requestBody, // 新增请求体参数
            HttpServletRequest request
    ) {
        String customerId = (String) request.getAttribute("userId");

        try {
            boolean success = meetingRoomService.confirmPayment(
                    meetingRoomId,
                    customerId,
                    requestBody.getStartTime(),
                    requestBody.getEndTime()
            );

            return success
                    ? ResponseEntity.ok().build()
                    : ResponseEntity.badRequest().body(Map.of(
                    "code", 4001,
                    "message", "未找到待支付订单"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "code", 5001,
                    "message", "支付处理失败: " + e.getMessage()
            ));
        }
    }
    /**
     * 删除会议室
     * @param meetingRoomId 会议室ID
     * @return 是否删除成功
     */
    @DeleteMapping("/meetingRoom/{meetingRoomId}")
    public boolean deleteMeetingRoom(@PathVariable Integer meetingRoomId) {
        return meetingRoomService.deleteMeetingRoom(meetingRoomId);
    }

    @PostMapping("/meetingRoom/available")
    public ResponseEntity<Page<MeetingRoom>> getAvailableRooms(@RequestBody MeetingRoomFilterRequest filterRequest) {
        System.out.println("接收到参数：" + filterRequest);

        Page<MeetingRoom> page = new Page<>(filterRequest.getCurrent(), filterRequest.getSize());
        return ResponseEntity.ok(
                meetingRoomService.getAvailableRooms(
                        page,
                        filterRequest.getStartTime(),
                        filterRequest.getEndTime(),
                        filterRequest.getHasProjector(),
                        filterRequest.getHasAudio(),
                        filterRequest.getHasNetwork()
                )
        );
    }

}
