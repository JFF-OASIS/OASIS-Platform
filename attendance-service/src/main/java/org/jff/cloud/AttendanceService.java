package org.jff.cloud;


import com.alibaba.druid.sql.visitor.functions.Lcase;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jff.cloud.entity.AttendanceRecord;
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

    private final AttendanceRecordMapper attendanceRecordMapper;

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

    public ResponseVO addAttendanceRecord(AttendanceRecord attendanceRecord) {
        attendanceRecordMapper.insert(attendanceRecord);
        return new ResponseVO(ResultCode.SUCCESS, "考勤成功");
    }

    public List<AttendanceRecord> getAttendanceRecordList(Long classId, LocalDate date) {
        //需要判断这个班是否在今天已经考过勤了(根据classId和date)

        //1. 根据classId查学生id列表
        Long[] studentList = restTemplate.
                getForObject("http://manage-service/api/v1/manage/class/findStudentIdByClassId?classId=" + classId,
                        Long[].class);
        //2. 根据学生id列表和date查考勤记录
        //2.1 先查第一个学生的记录，如果不存在，就对整个班级的记录进行新建
        AttendanceRecord firstRecord = attendanceRecordMapper.selectOne(new QueryWrapper<AttendanceRecord>()
                .eq("student_id", studentList[0])
                .eq("date", date));
        if (firstRecord==null) {
            for (Long studentId : studentList) {
                AttendanceRecord record = AttendanceRecord.builder()
                        .studentId(studentId)
                        .date(date)
                        .build();
                attendanceRecordMapper.insert(record);
            }
        }
        //2.2 返回相应的考勤记录
        return attendanceRecordMapper.selectList(new QueryWrapper<AttendanceRecord>()
                .in("student_id", studentList)
                .eq("date", date));
    }
}
