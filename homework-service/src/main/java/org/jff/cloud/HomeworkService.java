package org.jff.cloud;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jff.cloud.dto.HomeworkRecordDTO;
import org.jff.cloud.entity.Homework;
import org.jff.cloud.entity.HomeworkRecord;
import org.jff.cloud.entity.HomeworkStatus;
import org.jff.cloud.global.ResponseVO;
import org.jff.cloud.global.ResultCode;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class HomeworkService {

    private final RestTemplate restTemplate;

    private final HomeworkMapper homeworkMapper;

    private final HomeworkRecordMapper homeworkRecordMapper;

    public ResponseVO assignHomework(Homework homework) {
        //1. 将homework插入数据库
        homeworkMapper.insert(homework);

        //TODO：向ManageService发送请求，得到所有学生的id
        List<Integer> studentIdList = new ArrayList<>();

        studentIdList = restTemplate
                .getForObject("http://manage-service/api/v1/manage/class/findStudentIdByClassId?classId="+homework.getClassId(), List.class);

        log.info("studentIdList: {}", studentIdList);

        //2. 给班级中每个学生新添一条记录
        for (Integer studentId : studentIdList) {

            HomeworkRecord homeworkRecord = new HomeworkRecord();
            homeworkRecord.setStudentId(Long.valueOf(studentId));
            homeworkRecord.setHomeworkId(homework.getHomeworkId());
            homeworkRecord.setSubmitStatus(HomeworkStatus.UNSUBMITTED);
            homeworkRecordMapper.insert(homeworkRecord);
        }

        return new ResponseVO(ResultCode.SUCCESS, "作业发布成功");
    }

    public ResponseVO submitHomework(MultipartFile file, Long homeworkId, Long uploaderId) {

        QueryWrapper<Homework> homeworkQueryWrapper = new QueryWrapper<>();
        homeworkQueryWrapper.eq("homework_id", homeworkId);
        Homework homework = homeworkMapper.selectOne(homeworkQueryWrapper);

        QueryWrapper<HomeworkRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", uploaderId);
        queryWrapper.eq("homework_id", homeworkId);
        HomeworkRecord homeworkRecord = homeworkRecordMapper.selectOne(queryWrapper);

        //异常处理
        //如果超过时间，则提交失败，并修改record信息
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(homework.getDeadline())) {

            homeworkRecord.setSubmitStatus(HomeworkStatus.OVERDUE);
            homeworkRecord.setScore(0);
            homeworkRecordMapper.updateById(homeworkRecord);
            //TODO:是否有必要定一套有意义的ResultCode
            return new ResponseVO(ResultCode.FAILED, "提交超时");
        }


        //1.先上传文件，得到url

        //1.1 先将文件格式转换为File
        File uploadFile = multipartToFile(file, file.getOriginalFilename());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("uploadFile", new FileSystemResource(uploadFile));
        params.add("homeworkId", homeworkId);
        params.add("studentId", uploaderId);

        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("http://material-service/api/v1/material/homework", httpEntity, String.class);
        log.info("response={}", response);
        String url = response.getBody();
        log.info("url={}", url);

        //2.修改数据库中的记录

        homeworkRecord.setSubmitTime(now);
        homeworkRecord.setSubmitStatus(HomeworkStatus.SUBMITTED);
        homeworkRecord.setContentUrl(url);
        homeworkRecordMapper.updateById(homeworkRecord);

        return new ResponseVO(ResultCode.SUCCESS, "作业提交成功");
    }


    public static File multipartToFile(MultipartFile multipart, String fileName) {
        File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + fileName);
        try {
            multipart.transferTo(convFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return convFile;
    }

    public ResponseVO updateHomework(Homework homework) {
        homeworkMapper.updateById(homework);
        return new ResponseVO(ResultCode.SUCCESS, "作业修改成功");
    }

    public ResponseVO deleteHomework(Long homeworkId) {
        //一份homework只能属于一个班，所以可以根据homeworkId直接在record中删除


        QueryWrapper<HomeworkRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("homework_id", homeworkId);
        homeworkRecordMapper.delete(queryWrapper);

        //2. 删除homework中的记录
        homeworkMapper.deleteById(homeworkId);
        return new ResponseVO(ResultCode.SUCCESS, "作业删除成功");
    }

    public ResponseVO markHomework(Long homeworkId, Long studentId, Integer score) {
        QueryWrapper<HomeworkRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("homework_id", homeworkId);
        queryWrapper.eq("student_id", studentId);
        HomeworkRecord homeworkRecord = homeworkRecordMapper.selectOne(queryWrapper);
        homeworkRecord.setScore(score);
        homeworkRecord.setSubmitStatus(HomeworkStatus.EVALUATED);
        homeworkRecordMapper.updateById(homeworkRecord);
        return new ResponseVO(ResultCode.SUCCESS, "作业评分成功");
    }

    public List<Homework> getHomeworkList(Long classId) {
        List<Homework> homeworkList = homeworkMapper.selectList(new QueryWrapper<Homework>().eq("class_id", classId));
        log.info("homeworkList: {}", homeworkList);
        return homeworkList;
    }

    public List<HomeworkRecordDTO> getHomeworkRecordListByStudent(Long studentId, List<LocalDate> publishTimeList) {
        List<HomeworkRecordDTO> list = new ArrayList<>();
        for (LocalDate publishTime : publishTimeList) {
            QueryWrapper<HomeworkRecord> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("student_id", studentId);
            queryWrapper.eq("publish_time", publishTime);
            HomeworkRecord homeworkRecord = homeworkRecordMapper.selectOne(queryWrapper);
            HomeworkRecordDTO record = HomeworkRecordDTO.builder()
                    .submitTime(homeworkRecord.getSubmitTime())
                    .submitStatus(homeworkRecord.getSubmitStatus())
                    .contentUrl(homeworkRecord.getContentUrl())
                    .score(homeworkRecord.getScore())
                    .build();
            list.add(record);
        }
        return list;
    }

    public List<HomeworkRecord> getHomeworkRecordList(Long homeworkId) {
        List<HomeworkRecord> homeworkRecordList = homeworkRecordMapper.selectList(new QueryWrapper<HomeworkRecord>().eq("homework_id", homeworkId));
        log.info("homeworkRecordList: {}", homeworkRecordList);
        return homeworkRecordList;
    }
}
