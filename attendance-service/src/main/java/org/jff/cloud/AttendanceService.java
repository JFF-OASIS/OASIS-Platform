package org.jff.cloud;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jff.cloud.entity.LeaveRecord;
import org.jff.cloud.entity.LeaveRecordStatus;
import org.jff.cloud.entity.RoleStatus;
import org.jff.cloud.global.ResponseVO;
import org.jff.cloud.global.ResultCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class AttendanceService {

    private final LeaveRecordMapper leaveRecordMapper;

    private final RestTemplate restTemplate;

    public ResponseVO addLeaveRecord(Long studentId, LocalDate startDate, LocalDate endDate, String reason, Long phoneNumber, String department) {
        LeaveRecord record = LeaveRecord.builder()
                .studentId(studentId)
                .startDate(startDate)
                .endDate(endDate)
                .reason(reason)
                .phoneNumber(phoneNumber)
                .department(department)
                .progress(1)
                .build();
        //查询该学生对应的teacher和engineer
        //需要调用manage service的接口
        String url = "http://manage-service/api/v1/manage/student/findTeacherIdAndEngineerIdByStudentId";
        Map<String,Long> params = restTemplate
                .getForObject(url+"?studentId="+studentId, Map.class);
        log.info("params: {}", params);
        record.setTeacherId(params.get("teacherId"));
        record.setEngineerId(params.get("engineerId"));
        leaveRecordMapper.insert(record);

        return new ResponseVO(ResultCode.SUCCESS, "请假申请成功");

    }

    public List<LeaveRecord> getLeaveRecordList(Long userId, RoleStatus role) {
        //分为三种情况
        //1. 学生查看自己的请假信息
        if (role==RoleStatus.ROLE_STUDENT) {
            return leaveRecordMapper.selectList(new QueryWrapper<LeaveRecord>()
                    .eq("student_id", userId));
        }
        //2. 工程师查看学生的请假信息
        if (role==RoleStatus.ROLE_ENGINEER) {
            return leaveRecordMapper.selectList(new QueryWrapper<LeaveRecord>()
                    .eq("engineer_id", userId));
        }
        //3. 老师查看学生的请假信息
        if (role==RoleStatus.ROLE_TEACHER) {
            return leaveRecordMapper.selectList(new QueryWrapper<LeaveRecord>()
                    .eq("teacher_id", userId));
        }
        return null;
    }

    public ResponseVO approveLeaveRecord(Long leaveRecordId, LeaveRecordStatus status, RoleStatus role) {
        LeaveRecord record = leaveRecordMapper.selectById(leaveRecordId);
        //分为两种情况
        //1. 审批的是工程师
        if (role==RoleStatus.ROLE_ENGINEER) {
            record.setEngineerStatus(status);
            record.setProgress(2);
        }
        //2. 审批的是老师
        if (role==RoleStatus.ROLE_TEACHER) {
            record.setTeacherStatus(status);
            record.setProgress(3);
        }

        leaveRecordMapper.updateById(record);
        return new ResponseVO(ResultCode.SUCCESS, "审批成功");
    }

    public ResponseVO reportBack(Long leaveRecordId) {
        log.info("leaveRecordId: {}", leaveRecordId);
        LeaveRecord record = leaveRecordMapper.selectById(leaveRecordId);
        record.setProgress(4);
        leaveRecordMapper.updateById(record);
        return new ResponseVO(ResultCode.SUCCESS, "销假成功");
    }
}
