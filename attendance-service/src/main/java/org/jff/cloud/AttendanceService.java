package org.jff.cloud;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jff.cloud.entity.LeaveRecord;
import org.jff.cloud.global.ResponseVO;
import org.jff.cloud.global.ResultCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
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
}
