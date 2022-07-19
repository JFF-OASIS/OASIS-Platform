package org.jff.cloud;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jff.cloud.dto.LeaveRecordDTO;
import org.jff.cloud.entity.*;
import org.jff.cloud.global.ResponseVO;
import org.jff.cloud.global.ResultCode;
import org.jff.cloud.mapper.AttendanceRecordMapper;
import org.jff.cloud.mapper.LeaveRecordMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
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
                .teacherStatus(LeaveRecordStatus.WAITING)
                .engineerStatus(LeaveRecordStatus.WAITING)
                .build();
        //查询该学生对应的teacher和engineer
        //需要调用manage service的接口
        String url = "http://manage-service/api/v1/manage/student/findTeacherIdAndEngineerIdByStudentId";
        Map<String,Long> params = restTemplate
                .getForObject(url+"?studentId="+studentId, Map.class);
        log.info("params: {}", params);
        record.setTeacherId(Long.parseLong(String.valueOf(params.get("teacherId"))));
        record.setEngineerId(Long.parseLong(String.valueOf(params.get("engineerId"))));
        leaveRecordMapper.insert(record);

        return new ResponseVO(ResultCode.SUCCESS, "请假申请成功");

    }

    public List<LeaveRecordDTO> getLeaveRecordList(Long userId, RoleStatus role) {
        List<LeaveRecordDTO> leaveRecordDTOList = new ArrayList<>();
        List<LeaveRecord> leaveRecords = null;

        //分为三种情况
        //1. 学生查看自己的请假信息
        if (role==RoleStatus.ROLE_STUDENT) {
            leaveRecords = leaveRecordMapper.selectList(new QueryWrapper<LeaveRecord>()
                    .eq("student_id", userId));
        }
        //2. 工程师查看学生的请假信息
        if (role==RoleStatus.ROLE_ENGINEER) {
            leaveRecords = leaveRecordMapper.selectList(new QueryWrapper<LeaveRecord>()
                    .eq("engineer_id", userId));
        }
        //3. 老师查看学生的请假信息
        if (role==RoleStatus.ROLE_TEACHER) {
            leaveRecords = leaveRecordMapper.selectList(new QueryWrapper<LeaveRecord>()
                    .eq("teacher_id", userId));
        }

        for (LeaveRecord leaveRecord : leaveRecords) {
            LeaveRecordDTO leaveRecordDTO = LeaveRecordDTO.builder()
                    .id(leaveRecord.getId())
                    .startDate(leaveRecord.getStartDate())
                    .endDate(leaveRecord.getEndDate())
                    .reason(leaveRecord.getReason())
                    .phoneNumber(leaveRecord.getPhoneNumber())
                    .department(leaveRecord.getDepartment())
                    .studentId(leaveRecord.getStudentId())
                    .engineerId(leaveRecord.getEngineerId())
                    .teacherId(leaveRecord.getTeacherId())
                    .progress(leaveRecord.getProgress())
                    .engineerStatus(leaveRecord.getEngineerStatus())
                    .teacherStatus(leaveRecord.getTeacherStatus())
                    .build();
            //需要得到对应的学生名字
            String studentName = restTemplate
                    .getForObject("http://manage-service/api/v1/manage/student/getStudentNameByStudentId?studentId=" + leaveRecord.getStudentId().toString(),
                            String.class);

            leaveRecordDTO.setStudentName(studentName);

            leaveRecordDTOList.add(leaveRecordDTO);

        }

        return leaveRecordDTOList;
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

    public ResponseVO updateAttendanceRecord(Long id, AttendanceStatus status) {
        AttendanceRecord record = attendanceRecordMapper.selectOne(new QueryWrapper<AttendanceRecord>()
                .eq("id", id));
        record.setStatus(status);
        attendanceRecordMapper.updateById(record);
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
