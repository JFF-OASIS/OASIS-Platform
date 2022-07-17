package org.jff.cloud;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jff.cloud.global.ResponseVO;
import org.jff.cloud.utils.SecurityUtil;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    private final SecurityUtil securityUtil;


    @GetMapping("/leave")
    public

    @PostMapping("/leave")
    //学生提交请假申请
    public ResponseVO addLeaveRecord(@RequestBody Map<String, String> params) {
        Long studentId  = securityUtil.getUserId();
        LocalDate startDate = LocalDate.parse(params.get("startDate"));
        LocalDate endDate = LocalDate.parse(params.get("endDate"));
        String reason = params.get("reason");
        Long phoneNumber = Long.parseLong(params.get("phoneNumber"));
        String department = params.get("department");
        return attendanceService
                .addLeaveRecord(studentId, startDate, endDate, reason, phoneNumber, department);
    }

}
