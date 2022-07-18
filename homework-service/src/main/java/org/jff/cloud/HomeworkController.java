package org.jff.cloud;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jff.cloud.dto.HomeworkRecordDTO;
import org.jff.cloud.entity.Homework;
import org.jff.cloud.global.ResponseVO;
import org.jff.cloud.utils.SecurityUtil;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/homework")
public class HomeworkController {

    private final HomeworkService homeworkService;

    private final SecurityUtil securityUtil;

    @GetMapping()
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

    @PutMapping()
    //工程师修改班级作业
    public ResponseVO updateHomework(@RequestBody Map<String,Object> params){
        Homework homework = (Homework) params.get("homework");
        return homeworkService.updateHomework(homework);
    }

    @DeleteMapping
    //工程师删除班级作业
    public ResponseVO deleteHomework(@RequestBody Map<String,String> params){
        Long homeworkId = Long.parseLong(params.get("homeworkId"));
        return homeworkService.deleteHomework(homeworkId);
    }



    @GetMapping("/submit")
    //查看作业提交情况
    public List<HomeworkRecordDTO> getHomeworkRecordList(@RequestParam("publishTimeList") List<LocalDate> publishTimeList) {
        Long studentId = securityUtil.getUserId();
        return homeworkService.getHomeworkRecordList(studentId,publishTimeList);
    }
    @PutMapping("/submit")
    //学生提交作业
    public ResponseVO submitHomework(
            @RequestPart("uploadFile") MultipartFile file,
            @RequestPart("homeworkId") Long homeworkId
    ){
        Long uploaderId = securityUtil.getUserId();
        return homeworkService.submitHomework(file,homeworkId,uploaderId);
    }

    @PutMapping("/mark")
    //工程师对作业进行评分
    public ResponseVO markHomework(@RequestBody Map<String,Object> params){
        Long homeworkId = Long.parseLong(params.get("homeworkId").toString());
        Integer score = Integer.parseInt(params.get("score").toString());
        Long studentId = Long.parseLong(params.get("studentId").toString());
        return homeworkService.markHomework(homeworkId,studentId,score);
    }


//    @GetMapping("/record")
    //工程师查看某次作业的提交情况



}
