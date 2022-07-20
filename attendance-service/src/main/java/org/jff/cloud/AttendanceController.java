package org.jff.cloud;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jff.cloud.dto.LeaveRecordDTO;
import org.jff.cloud.entity.*;
import org.jff.cloud.global.ResponseVO;
import org.jff.cloud.utils.SecurityUtil;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    private final SecurityUtil securityUtil;




    @GetMapping()
    @PreAuthorize("hasAnyRole('ENGINEER','TEACHER')")
    //查看考勤记录，以及考勤(如果没有对应的考勤记录，则会创建新的考勤记录)
    public List<AttendanceRecord> getAttendanceRecordList(@RequestParam("classId") Long classId,@RequestParam("date") String dateStr) {
        LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        log.info("getAttendanceRecordList: classId:  {}  date:  {}", classId,date);
        return attendanceService.getAttendanceRecordList(classId, date);
    }

    @GetMapping("/leave")
    @PreAuthorize("hasAnyRole('ENGINEER','TEACHER','STUDENT')")
    //查看请假信息
    //需要根据人物的不同角色返回不同的List
    public List<LeaveRecordDTO> getLeaveRecordList() {
        Long userId = securityUtil.getUserId();
        RoleStatus role = securityUtil.getUserRole();
        log.info("userId: {}, role: {}", userId, role);
        return attendanceService.getLeaveRecordList(userId,role);
    }

    @PostMapping("/leave")
    @PreAuthorize("hasAnyRole('STUDENT')")
    //学生提交请假申请
    public ResponseVO addLeaveRecord(@RequestBody Map<String, String> params) {
        log.info("addLeaveRecord: {}", params);
        //LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Long studentId  = securityUtil.getUserId();
        LocalDate startDate = LocalDate.parse(params.get("startDate"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate endDate = LocalDate.parse(params.get("endDate"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String reason = params.get("reason");
        Long phoneNumber = Long.parseLong(params.get("phoneNumber"));
        String department = params.get("department");
        return attendanceService
                .addLeaveRecord(studentId, startDate, endDate, reason, phoneNumber, department);
    }

    @PutMapping("/leave/reportBack")
    @PreAuthorize("hasAnyRole('STUDENT')")
    //学生销假
    public ResponseVO reportBack(@RequestBody Map<String, String> params) {
        Long leaveRecordId = Long.parseLong(params.get("leaveRecordId"));
        return attendanceService.reportBack(leaveRecordId);
    }

    @PutMapping("/leave")
    @PreAuthorize("hasAnyRole('ENGINEER','TEACHER')")
    //审批学生请假
    //工程师和老师都调用这个接口
    public ResponseVO approveLeaveRecord(@RequestBody Map<String, String> params) {
        Long leaveRecordId = Long.parseLong(params.get("leaveRecordId"));
        LeaveRecordStatus status = LeaveRecordStatus.valueOf(params.get("status"));
        log.info("leaveRecordId: {}, status: {}", leaveRecordId, status);
        RoleStatus role = securityUtil.getUserRole();
        return attendanceService.approveLeaveRecord(leaveRecordId, status,role);
    }


    @PutMapping()
    @PreAuthorize("hasAnyRole('ENGINEER','TEACHER')")
    public ResponseVO updateAttendanceRecord(@RequestBody Map<String, String> params) {
        Long id= Long.parseLong(params.get("id"));
        AttendanceStatus status = AttendanceStatus.valueOf(params.get("status"));
        return attendanceService.updateAttendanceRecord(id,status);
    }



}
