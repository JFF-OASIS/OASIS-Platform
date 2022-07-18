package org.jff.cloud;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jff.cloud.dto.HomeworkDTO;
import org.jff.cloud.dto.HomeworkRecordDTO;
import org.jff.cloud.entity.Homework;
import org.jff.cloud.entity.HomeworkRecord;
import org.jff.cloud.global.ResponseVO;
import org.jff.cloud.utils.SecurityUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/homework")
public class HomeworkController {

    private final HomeworkService homeworkService;

    private final SecurityUtil securityUtil;

    @GetMapping("/all")
    //查看班级作业列表
    public List<Homework> getHomeworkList(@RequestParam("classId") Long classId) {
        return homeworkService.getHomeworkList(classId);
    }

    @PostMapping()
    //工程师布置班级作业
    public ResponseVO assignHomework(@RequestBody Homework homework) {
        log.info("assignHomework: {}", homework);
        log.info("time:{}", LocalDateTime.now());
        return homeworkService.assignHomework(homework);
    }

    @GetMapping()
    //在教学计划/教学天的页面上所需的数据
    public List<HomeworkDTO> getHomeworkRecordList(@RequestParam("classId") Long classId, @RequestParam("teachingDate") String dateStr) {
        LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return homeworkService.getHomeworkDTOList(classId,date);
    }

    @PutMapping()
    //工程师修改班级作业
    public ResponseVO updateHomework(@RequestBody Map<String,Object> params){
        Homework homework = (Homework) params.get("homework");
        return homeworkService.updateHomework(homework);
    }

    @DeleteMapping()
    //工程师删除班级作业
    public ResponseVO deleteHomework(@RequestBody Map<String,String> params){
        Long homeworkId = Long.parseLong(params.get("homeworkId"));
        return homeworkService.deleteHomework(homeworkId);
    }



    @GetMapping("/submit")
    //TODO:修改返回值类型！！！
    //查看作业提交情况
    public List<HomeworkRecordDTO> getHomeworkRecordList(@RequestParam("publishTimeList") List<LocalDate> publishTimeList) {
        Long studentId = securityUtil.getUserId();
        return homeworkService.getHomeworkRecordListByStudent(studentId,publishTimeList);
    }
    @PutMapping("/submit/{homeworkId}")
    //学生提交作业
    public ResponseVO submitHomework(
            @RequestPart("uploadFile") MultipartFile file,
            @PathVariable("homeworkId") Long homeworkId
    ){
        Long uploaderId = securityUtil.getUserId();
        return homeworkService.submitHomework(file,homeworkId,uploaderId);
    }

    @PutMapping("/mark")
    //工程师对作业进行评分
    public ResponseVO markHomework(@RequestBody Map<String,String> params){
        Long homeworkRecordId = Long.parseLong(params.get("homeworkRecordId"));
        Integer score = Integer.parseInt(params.get("score"));
        log.info("markHomework: homeworkRecordId:  {}, score: {}", homeworkRecordId,score);
        return homeworkService.markHomework(homeworkRecordId,score);
    }


    @GetMapping("/record")
    //工程师查询某次作业的学生提交情况并为评分做准备
    public List<HomeworkRecordDTO> getHomeworkRecordList(@RequestParam Long homeworkId) {
        return homeworkService.getHomeworkRecordList(homeworkId);
    }



}
