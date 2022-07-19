package org.jff.cloud;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jff.cloud.entity.Project;
import org.jff.cloud.entity.ProjectStatus;
import org.jff.cloud.global.ResponseVO;
import org.jff.cloud.vo.AuditProjectVO;
import org.jff.cloud.vo.ProjectVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/project")
public class ProjectController {

    private final ProjectService projectService;


    @GetMapping()
    @PreAuthorize("hasAnyRole('ENGINEER','TEACHER')")
    //查询小组项目信息
    public Project getProjectByGroupId(@RequestParam("groupId") Long groupId) {
        return projectService.getProjectByGroupId(groupId);
    }

    @PostMapping()
    @PreAuthorize("hasAnyRole('MANAGER')")
    public ResponseVO createProject(@RequestBody ProjectVO projectVO) {
        log.info("createProject: {}", projectVO);
        return projectService.createProject(projectVO);
    }

    @PutMapping()
    @PreAuthorize("hasAnyRole('MANAGER')")
    public ResponseVO updateProject(@RequestBody ProjectVO projectVO) {
        log.info("updateProject: {}", projectVO);
        return projectService.updateProject(projectVO);
    }

    @DeleteMapping()
    @PreAuthorize("hasAnyRole('MANAGER')")
    //组长删除选题
    public ResponseVO deleteProject(@RequestParam("projectId") Long projectId) {
        log.info("deleteProject: {}", projectId);
        return projectService.deleteProject(projectId);
    }

    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ENGINEER')")
    //工程师获取班级学生项目列表
    public List<Project> getProjectList(@RequestParam Long classId) {
        log.info("getProjectList: {}", classId);
        return projectService.getProjectList(classId);
    }

    @PutMapping("/audit")
    @PreAuthorize("hasAnyRole('ENGINEER')")
    //工程师审核学生自拟项目
    public ResponseVO auditProject(@RequestBody AuditProjectVO auditProjectVO) {
        log.info("auditProject: {}, {}", auditProjectVO.getProjectId(), auditProjectVO.getStatus());
        return projectService.auditProject(auditProjectVO.getProjectId(), auditProjectVO.getStatus());
    }



}
