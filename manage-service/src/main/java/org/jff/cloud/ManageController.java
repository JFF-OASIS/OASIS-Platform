package org.jff.cloud;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jff.cloud.dto.*;
import org.jff.cloud.entity.Group;
import org.jff.cloud.entity.RoleStatus;
import org.jff.cloud.entity.Student;
import org.jff.cloud.entity.User;
import org.jff.cloud.global.NotResponseBody;
import org.jff.cloud.global.ResponseVO;
import org.jff.cloud.utils.SecurityUtil;
import org.jff.cloud.vo.UpdateStudentClassVO;
import org.jff.cloud.vo.UpdateStudentGroupVO;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/manage")
public class ManageController {

    private final ManageService manageService;

    private final SecurityUtil securityUtil;

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('ENGINEER','TEACHER','ADMIN','MANAGER','STUDENT')")
    //查看基本资料
    public User getUser(@RequestParam("userId") Long userId) {
        log.info("getUser: {}", userId);
        return manageService.getUser(userId);
    }

    @DeleteMapping("/user")
    @PreAuthorize("hasAnyRole('ENGINEER','TEACHER','ADMIN','MANAGER','STUDENT')")
    public ResponseVO deleteUser(@RequestParam("userId") Long userId) {
        log.info("deleteUser: {}", userId);
        return manageService.deleteUser(userId);
    }

    @GetMapping("/class")
    @PreAuthorize("hasAnyRole('ENGINEER','TEACHER','ADMIN','STUDENT','MANAGER')")
    //查询班级信息
    public ResponseVO getClassInfo(@RequestParam("classId") Long classId) {
        log.info("getClassInfo: {}", classId);
        return manageService.getClassInfo(classId);
    }
    @PostMapping("/class")
    @PreAuthorize("hasAnyRole('ADMIN')")
    //管理员新建班级
    public ResponseVO addClass(@RequestBody Map<String, String> params) {
        String className = params.get("className");
        Long teacherId = Long.parseLong(params.get("teacherId"));
        Long engineerId = Long.parseLong(params.get("engineerId"));
        Long teachingPlanId = Long.parseLong(params.get("teachingPlanId"));
        log.info("addClass: {}", className);
        return manageService.addClass(className, teacherId, engineerId, teachingPlanId);
    }

    @PutMapping("/class")
    @PreAuthorize("hasAnyRole('ADMIN')")
    //管理员修改班级信息
    public ResponseVO updateClass(@RequestBody Map<String, String> params) {
        Long classId = Long.parseLong(params.get("classId"));
        String className = params.get("className");
        Long teacherId = Long.parseLong(params.get("teacherId"));
        Long engineerId = Long.parseLong(params.get("engineerId"));
        Long teachingPlanId = Long.parseLong(params.get("teachingPlanId"));
        log.info("updateClass: {}", classId);
        return manageService.updateClass(className, teacherId, engineerId, teachingPlanId, classId);
    }

    @DeleteMapping("/class")
    @PreAuthorize("hasAnyRole('ADMIN')")
    //管理员删除班级
    public ResponseVO deleteClass(@RequestParam("classId") Long classId) {
        log.info("deleteClass: {}", classId);
        return manageService.deleteClass(classId);
    }

    @PostMapping("/class/student")
    @PreAuthorize("hasAnyRole('ADMIN')")
    //管理员添加班级成员
    public ResponseVO addStudentToClass(@RequestBody UpdateStudentClassVO updateStudentVO) {
        log.info("addStudentToClass: {}", updateStudentVO);
        return manageService.addStudentToClass(updateStudentVO);
    }

    @PutMapping("/class/student")
    @PreAuthorize("hasAnyRole('ADMIN')")
    //管理员修改班级成员信息
    public ResponseVO updateStudentInClass(@RequestBody UpdateStudentClassVO updateStudentVO) {
        log.info("updateStudentInClass: {}", updateStudentVO);
        return manageService.updateStudentInClass(updateStudentVO);
    }


    @DeleteMapping("/class/student")
    @PreAuthorize("hasAnyRole('ADMIN')")
    //管理员删除班级成员
    //TODO:检查正确性
    public ResponseVO deleteStudentFromClass(@RequestBody UpdateStudentClassVO updateStudentVO) {
        log.info("deleteStudentFromClass: {}", updateStudentVO);
        return manageService.deleteStudentFromClass(updateStudentVO);
    }

    @GetMapping("/group")
    @PreAuthorize("hasAnyRole('ENGINEER','TEACHER')")
    //查询小组信息
    public GroupDTO getGroupInfo(@RequestParam("groupId") Long groupId) {
        log.info("getGroupInfo: {}", groupId);
        return manageService.getGroupInfo(groupId);
    }

    @PostMapping("/group")
    @PreAuthorize("hasAnyRole('ENGINEER')")
    //工程师新建分组
    public ResponseVO addGroup(@RequestBody Map<String, String> params) {
        String groupName = params.get("groupName");
        Long classId = Long.parseLong(params.get("classId"));
        log.info("addGroup: {}", groupName);
        return manageService.addGroup(groupName, classId);
    }

    @PutMapping("/group")
    @PreAuthorize("hasAnyRole('ENGINEER')")
    //工程师修改分组信息（包括小组阶段成绩）
    public ResponseVO updateGroup(@RequestBody Group group) {
        log.info("updateGroup: {}", group);
        return manageService.updateGroup(group);
    }

    @DeleteMapping("/group")
    @PreAuthorize("hasAnyRole('ENGINEER')")
    //工程师删除分组
    public ResponseVO deleteGroup(@RequestBody Map<String, Object> params) {
        Long groupId = Long.parseLong(params.get("groupId").toString());
        log.info("deleteGroup: {}", groupId);
        return manageService.deleteGroup(groupId);
    }

    @PostMapping("/group/student")
    @PreAuthorize("hasAnyRole('ENGINEER')")
    //工程师添加小组成员
    public ResponseVO addStudentToGroup(@RequestBody UpdateStudentGroupVO updateStudentGroupVO) {

        return manageService.addStudentToGroup(updateStudentGroupVO);
    }

    @DeleteMapping("/group/student")
    @PreAuthorize("hasAnyRole('ENGINEER')")
    //工程师删除小组成员
    public ResponseVO deleteStudentFromGroup(@RequestBody Map<String, String> params) {
        Long studentId = Long.parseLong(params.get("studentId"));
        log.info("deleteStudentFromGroup: {}", studentId);
        return manageService.deleteStudentFromGroup(studentId);
    }

    @GetMapping("/student")
    @PreAuthorize("hasAnyRole('ENGINEER','TEACHER','ADMIN')")
    //查询全部学生信息
    public List<Student> getAllStudent() {
        log.info("getAllStudent");
        return manageService.getAllStudent();
    }


    @GetMapping("/student/score")
    @PreAuthorize("hasAnyRole('ENGINEER','TEACHER')")
    //查询学生成绩
    public int getStudentScore(@RequestParam("studentId") Long studentId) {
        log.info("getStudentScore: {}", studentId);
        return manageService.getStudentScore(studentId);
    }

    @GetMapping("/user/list")
    @PreAuthorize("hasAnyRole('ADMIN')")
    //获取全部用户列表
    public List<UserDTO> getUserList (){
        log.info("getUserList");
        return manageService.getUserList();
    }

    @GetMapping("/group/list")
    @PreAuthorize("hasAnyRole('ENGINEER','TEACHER')")
    //查询小组列表
    public List<GroupDTO> getGroupList(@RequestParam("classId") Long classId) {
        log.info("getGroupList: {}", classId);
        return manageService.getGroupList(classId);
    }

    @GetMapping("/class/list")
    @PreAuthorize("hasAnyRole('ENGINEER','TEACHER','ADMIN')")
    //获取所有班级列表
    //需要根据不同的角色区分
    public List<SimpleClassDTO> getClassList() {
        log.info("getClassList");
        Long userId = securityUtil.getUserId();
        RoleStatus role = securityUtil.getUserRole();
        log.info("getClassList:  userId: {} role:{} ", userId,role);
        return manageService.getClassList(userId, role);
    }

    @GetMapping("/class/plan")
    @PreAuthorize("hasAnyRole('ENGINEER','TEACHER')")
    //查询班级教学计划
    public TeachingPlanDTO getTeachingPlan(@RequestParam("classId") Long classId) {
        log.info("getTeachingPlan: {}", classId);
        return manageService.getTeachingPlan(classId);
    }


    @NotResponseBody
        @GetMapping("/class/findStudentIdByClassId")
    public List<Long> findStudentIdByClassId(@RequestParam Long classId) {
        log.info("findStudentIdByClassId: {}", classId);
        return manageService.findStudentIdByClassId(classId);
    }

    @NotResponseBody
    @GetMapping("/student/findTeacherIdAndEngineerIdByStudentId")
    public Map<String, Long> findTeacherIdAndEngineerIdByStudentId(@RequestParam Long studentId) {
        log.info("findTeacherIdAndEngineerIdByStudentId: {}", studentId);
        return manageService.findTeacherIdAndEngineerIdByStudentId(studentId);
    }

    @NotResponseBody
    @GetMapping("/group/findProjectIdListByClassId")
    public List<Long> findProjectIdListByClassId(@RequestParam Long classId) {
        log.info("findProjectIdListByClassId: {}", classId);
        return manageService.findProjectIdListByClassId(classId);
    }

    @NotResponseBody
    @GetMapping("/student/getStudentNameByStudentId")
    public String getStudentNameByStudentId(@RequestParam Long studentId) {
        log.info("getStudentNameByStudentId: {}", studentId);
        return manageService.getStudentNameByStudentId(studentId);
    }


}
