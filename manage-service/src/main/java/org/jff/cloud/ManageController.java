package org.jff.cloud;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jff.cloud.global.NotResponseBody;
import org.jff.cloud.global.ResponseVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/manage")
public class ManageController {

    private final ManageService manageService;

    @GetMapping("/class")
    //查询班级信息
    public ResponseVO getClassInfo(@RequestParam("classId") Long classId) {
        return manageService.getClassInfo(classId);
    }
    @PostMapping("/class")
    //管理员新建班级
    public ResponseVO addClass(@RequestBody Map<String, String> params) {
        String className = params.get("className");
        Long teacherId = Long.parseLong(params.get("teacherId"));
        Long engineerId = Long.parseLong(params.get("engineerId"));
        Long teachingPlanId = Long.parseLong(params.get("teachingPlanId"));
        return manageService.addClass(className, teacherId, engineerId, teachingPlanId);
    }

    @PostMapping("/class/student")
    //管理员添加班级成员
    public ResponseVO addStudentToClass(@RequestBody Map<String, Object> params) {
        Long classId = Long.parseLong(params.get("classId").toString());
        List<Long> studentIds = (List<Long>) params.get("studentIds");
        return manageService.addStudentToClass(classId, studentIds);
    }


    @DeleteMapping("/class/student")
    //管理员删除班级成员
    public ResponseVO deleteStudentFromClass(@RequestBody Map<String, Object> params) {
        Long classId = Long.parseLong(params.get("classId").toString());
        List<Long> studentIds = (List<Long>) params.get("studentIds");
        return manageService.deleteStudentFromClass(classId, studentIds);
    }

    @PostMapping("/group")
    //工程师新建分组
    public ResponseVO addGroup(@RequestBody Map<String, Object> params) {
        String groupName = params.get("groupName").toString();
        Long managerId = Long.parseLong(params.get("managerId").toString());
        List<Long> studentIds = (List<Long>) params.get("studentIds");
        return manageService.addGroup(groupName, managerId, studentIds);
    }

    @DeleteMapping("/group")
    //工程师删除分组
    public ResponseVO deleteGroup(@RequestBody Map<String, Object> params) {
        Long groupId = Long.parseLong(params.get("groupId").toString());
        return manageService.deleteGroup(groupId);
    }

    @DeleteMapping("/group/student")
    //工程师删除小组成员
    public ResponseVO deleteStudentFromGroup(@RequestBody Map<String, String> params) {
        Long studentId = Long.parseLong(params.get("studentId"));
        return manageService.deleteStudentFromGroup(studentId);
    }


    @NotResponseBody
    @GetMapping("/class/findStudentIdByClassId")
    public List<Long> findStudentIdByClassId(Long classId) {
        return manageService.findStudentIdByClassId(classId);
    }


}
