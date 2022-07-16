package org.jff.cloud;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jff.cloud.dto.GroupDTO;
import org.jff.cloud.dto.SimpleClassDTO;
import org.jff.cloud.dto.SimpleGroupDTO;
import org.jff.cloud.dto.UserDTO;
import org.jff.cloud.entity.Group;
import org.jff.cloud.entity.Project;
import org.jff.cloud.entity.User;
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

    @GetMapping("/user")
    //查看基本资料
    public User getUser(@RequestParam("userId") Long userId) {
        return manageService.getUser(userId);
    }

    @DeleteMapping("/user")
    public ResponseVO deleteUser(@RequestParam("userId") Long userId) {
        return manageService.deleteUser(userId);
    }

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

    @PutMapping("/class")
    //管理员修改班级信息
    public ResponseVO updateClass(@RequestBody Map<String, String> params) {
        Long classId = Long.parseLong(params.get("classId"));
        String className = params.get("className");
        Long teacherId = Long.parseLong(params.get("teacherId"));
        Long engineerId = Long.parseLong(params.get("engineerId"));
        Long teachingPlanId = Long.parseLong(params.get("teachingPlanId"));
        return manageService.updateClass(className, teacherId, engineerId, teachingPlanId, classId);
    }

    @DeleteMapping("/class")
    //管理员删除班级
    public ResponseVO deleteClass(@RequestParam("classId") Long classId) {
        return manageService.deleteClass(classId);
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

    @GetMapping("/group")
    //查询小组信息
    public GroupDTO getGroupInfo(@RequestParam("groupId") Long groupId) {
        return manageService.getGroupInfo(groupId);
    }

    @PostMapping("/group")
    //工程师新建分组
    public ResponseVO addGroup(@RequestBody Map<String, Object> params) {
        String groupName = params.get("groupName").toString();
        Long managerId = Long.parseLong(params.get("managerId").toString());
        List<Long> studentIds = (List<Long>) params.get("studentIds");
        return manageService.addGroup(groupName, managerId, studentIds);
    }

    @PutMapping("/group")
    //工程师修改分组信息（包括小组阶段成绩）
    public ResponseVO updateGroup(@RequestBody Group group) {
        return manageService.updateGroup(group);
    }

    @DeleteMapping("/group")
    //工程师删除分组
    public ResponseVO deleteGroup(@RequestBody Map<String, Object> params) {
        Long groupId = Long.parseLong(params.get("groupId").toString());
        return manageService.deleteGroup(groupId);
    }

    @PostMapping("/group/student")
    //工程师添加小组成员
    public ResponseVO addStudentToGroup(@RequestBody Map<String, Object> params) {
        Long groupId = Long.parseLong(params.get("groupId").toString());
        List<Long> studentIds = (List<Long>) params.get("studentIds");
        return manageService.addStudentToGroup(groupId, studentIds);
    }

    @DeleteMapping("/group/student")
    //工程师删除小组成员
    public ResponseVO deleteStudentFromGroup(@RequestBody Map<String, String> params) {
        Long studentId = Long.parseLong(params.get("studentId"));
        return manageService.deleteStudentFromGroup(studentId);
    }


    @GetMapping("/student/score")
    //查询学生成绩
    public int getStudentScore(@RequestParam("studentId") Long studentId) {
        return manageService.getStudentScore(studentId);
    }

    @GetMapping("/user/list")
    //获取全部用户列表
    public List<UserDTO> getUserList (){
        return manageService.getUserList();
    }

    @GetMapping("/group/list")
    //查询小组列表
    public List<SimpleGroupDTO> getGroupList(@RequestParam("classId") Long classId) {
        return manageService.getGroupList(classId);
    }

    @GetMapping("/class/list")
    //获取所有班级列表
    public List<SimpleClassDTO> getClassList() {
        return manageService.getClassList();
    }


    @NotResponseBody
    @GetMapping("/class/findStudentIdByClassId")
    public List<Long> findStudentIdByClassId(@RequestParam Long classId) {
        return manageService.findStudentIdByClassId(classId);
    }


}
