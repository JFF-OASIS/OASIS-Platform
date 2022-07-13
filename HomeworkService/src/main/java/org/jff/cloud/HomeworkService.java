package org.jff.cloud;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jff.cloud.entity.Homework;
import org.jff.cloud.entity.HomeworkRecord;
import org.jff.cloud.global.ResponseVO;
import org.jff.cloud.global.ResultCode;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class HomeworkService {

    private final HomeworkMapper homeworkMapper;

    private final HomeworkRecordMapper homeworkRecordMapper;

    public ResponseVO assignHomework(Long classId, Homework homework) {
        //1. 将homework插入数据库
        homeworkMapper.insert(homework);

        //TODO：向ManageService发送请求，得到所有学生的id
        List<Long> studentIdList = new ArrayList<>();

        //2. 给班级中每个学生新添一条记录
        for (Long studentId : studentIdList) {
            HomeworkRecord homeworkRecord = new HomeworkRecord();
            homeworkRecord.setStudentId(studentId);
            homeworkRecord.setHomeworkId(homework.getHomeworkId());
            //TODO: 对状态进行定义
            homeworkRecord.setSubmitStatus("未提交");
            homeworkRecordMapper.insert(homeworkRecord);
        }

        return new ResponseVO(ResultCode.SUCCESS, "作业发布成功");
    }
}
