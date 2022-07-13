package org.jff.cloud;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jff.cloud.entity.Group;
import org.jff.cloud.entity.Student;
import org.jff.cloud.entity.TeachingClass;
import org.jff.cloud.global.ResponseVO;
import org.jff.cloud.global.ResultCode;
import org.jff.cloud.mapper.GroupMapper;
import org.jff.cloud.mapper.StudentMapper;
import org.jff.cloud.mapper.TeachingClassMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class ManageService {

    private final TeachingClassMapper teachingClassMapper;

    private final GroupMapper groupMapper;

    private final StudentMapper studentMapper;


    public List<Long> findStudentIdByClassId(Long classId) {
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("student_id");
        queryWrapper.eq("class_id", classId);
        return studentMapper.selectList(queryWrapper).stream().map(Student::getStudentId).collect(java.util.stream.Collectors.toList());
    }

    public ResponseVO addClass(String className, Long teacherId, Long engineerId, Long teachingPlanId) {
        TeachingClass teachingClass = TeachingClass.builder()
                .className(className)
                .teacherId(teacherId)
                .engineerId(engineerId)
                .teachingPlanId(teachingPlanId)
                .build();
        teachingClassMapper.insert(teachingClass);
        return new ResponseVO(ResultCode.SUCCESS, "添加班级成功");
    }

    public ResponseVO addGroup(String groupName, Long managerId, List<Long> studentIds) {
        //新增group
        Group group = Group.builder()
                .groupName(groupName)
                .managerId(managerId)
                .requirementAnalysisScore(0)
                .designScore(0)
                .projectDevelopmentScore(0)
                .projectReportScore(0)
                .qualityScore(0)
                .build();
        groupMapper.insert(group);
        //修改学生信息
        studentIds.forEach(studentId -> {
            Student student = studentMapper.selectById(studentId);
            student.setGroupId(group.getGroupId());
            studentMapper.updateById(student);
        });
        return new ResponseVO(ResultCode.SUCCESS, "添加分组成功");
    }

    public ResponseVO addStudentToClass(Long classId, List<Long> studentIds) {
        studentIds.forEach(studentId -> {
            Student student = studentMapper.selectById(studentId);
            student.setClassId(classId);
            studentMapper.updateById(student);
        });
        return new ResponseVO(ResultCode.SUCCESS, "添加学生成功");
    }

    public ResponseVO deleteStudentFromClass(Long classId, List<Long> studentIds) {
        studentIds.forEach(studentId -> {
            Student student = studentMapper.selectById(studentId);
            student.setClassId(null);
            studentMapper.updateById(student);
        });
        return new ResponseVO(ResultCode.SUCCESS, "删除学生成功");
    }

    public ResponseVO deleteGroup(Long groupId) {
        //先把学生从分组中删除
        List<Student> students = studentMapper.selectList(new QueryWrapper<Student>().eq("group_id", groupId));
        students.forEach(student -> {
            student.setGroupId(0L);
            studentMapper.updateById(student);
        });
        //再删除分组
        groupMapper.deleteById(groupId);
        return new ResponseVO(ResultCode.SUCCESS, "删除分组成功");
    }

    public ResponseVO deleteStudentFromGroup(Long studentId) {
        Student student = studentMapper.selectById(studentId);
        student.setGroupId(0L);
        studentMapper.updateById(student);
        return new ResponseVO(ResultCode.SUCCESS, "删除学生成功");
    }

    public ResponseVO getClassInfo(Long classId) {
        TeachingClass teachingClass = teachingClassMapper.selectById(classId);
        return new ResponseVO(ResultCode.SUCCESS, teachingClass);
    }
}