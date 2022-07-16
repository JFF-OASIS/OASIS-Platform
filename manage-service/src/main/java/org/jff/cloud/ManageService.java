package org.jff.cloud;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jff.cloud.dto.*;
import org.jff.cloud.entity.*;
import org.jff.cloud.global.ResponseVO;
import org.jff.cloud.global.ResultCode;
import org.jff.cloud.mapper.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ManageService {

    private final TeachingClassMapper teachingClassMapper;

    private final GroupMapper groupMapper;

    private final RoleMapper roleMapper;

    private final StudentMapper studentMapper;

    private final UserMapper userMapper;


    public List<Long> findStudentIdByClassId(Long classId) {
        log.info("findStudentIdByClassId: {}", classId);
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("student_id");
        queryWrapper.eq("class_id", classId);
        List<Long> studentIdList = studentMapper.selectList(queryWrapper).stream().map(Student::getStudentId)
                .collect(Collectors.toList());
        log.info("studentIdList: {}", studentIdList);
        return studentIdList;
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
        ClassDTO classDTO = ClassDTO.builder()
                .classId(teachingClass.getClassId())
                .className(teachingClass.getClassName())
                .teacherId(teachingClass.getTeacherId())
                .engineerId(teachingClass.getEngineerId())
                .teachingPlanId(teachingClass.getTeachingPlanId())
                .build();
        //查询班级中学生信息
        List<StudentDTO> students = new ArrayList<>();
        List<Student> studentList = studentMapper.selectList(new QueryWrapper<Student>().eq("class_id", classId));
        for (Student student : studentList) {
            StudentDTO studentDTO = StudentDTO.builder()
                    .studentId(student.getStudentId())
                    .studentName(student.getName())
                    .build();
            students.add(studentDTO);
        }
        classDTO.setStudents(students);
        return new ResponseVO(ResultCode.SUCCESS, classDTO);
    }

    public User getUser(Long userId) {
        return userMapper.selectById(userId);
    }

    public ResponseVO deleteUser(Long userId) {
        userMapper.deleteById(userId);
        return new ResponseVO(ResultCode.SUCCESS, "删除用户成功");
    }

    public ResponseVO deleteClass(Long classId) {
        teachingClassMapper.deleteById(classId);
        return new ResponseVO(ResultCode.SUCCESS, "删除班级成功");
    }

    public ResponseVO updateClass(String className, Long teacherId, Long engineerId, Long teachingPlanId, Long classId) {
        TeachingClass teachingClass = teachingClassMapper.selectById(classId);
        teachingClass.setClassName(className);
        teachingClass.setTeacherId(teacherId);
        teachingClass.setEngineerId(engineerId);
        teachingClass.setTeachingPlanId(teachingPlanId);
        teachingClassMapper.updateById(teachingClass);
        return new ResponseVO(ResultCode.SUCCESS, "修改班级成功");
    }

    public GroupDTO getGroupInfo(Long groupId) {
        GroupDTO groupDTO = new GroupDTO();
        Group group = groupMapper.selectById(groupId);
        groupDTO.setGroup(group);
        //查询该组中的学生信息
        List<StudentDTO> students = new ArrayList<>();
        List<Student> studentList = studentMapper.selectList(new QueryWrapper<Student>().eq("group_id", groupId));
        for (Student student : studentList) {
            StudentDTO studentDTO = StudentDTO.builder()
                    .studentId(student.getStudentId())
                    .studentName(student.getName())
                    .build();
            students.add(studentDTO);
        }
        groupDTO.setStudents(students);
        return groupDTO;
    }

    public int getStudentScore(Long studentId) {
        //TODO: 学生成绩的计算
        return studentMapper.selectById(studentId).getScore();
    }

    public ResponseVO updateGroup(Group group) {
        groupMapper.updateById(group);
        return new ResponseVO(ResultCode.SUCCESS, "修改分组信息成功");
    }

    public List<SimpleGroupDTO> getGroupList(Long classId) {
        List<SimpleGroupDTO> groupList = new ArrayList<>();
        List<Group> groups = groupMapper.selectList(new QueryWrapper<Group>().eq("class_id", classId));
        for (Group group : groups) {
            SimpleGroupDTO simpleGroupDTO = SimpleGroupDTO.builder()
                    .groupId(group.getGroupId())
                    .groupName(group.getGroupName())
                    .build();
            groupList.add(simpleGroupDTO);
        }
        return groupList;
    }


    public ResponseVO addStudentToGroup(Long groupId, List<Long> studentIds) {
        studentIds.forEach(studentId -> {
            Student student = studentMapper.selectById(studentId);
            student.setGroupId(groupId);
            studentMapper.updateById(student);
        });
        return new ResponseVO(ResultCode.SUCCESS, "添加学生成功");
    }

    public List<UserDTO> getUserList() {
        //获取所有用户信息
        List<UserDTO> userList = new ArrayList<>();

        userMapper.selectList(new QueryWrapper<User>()).forEach(user -> {
            UserDTO userDTO = new UserDTO();
            userDTO.setUserId(user.getUserId());
            userDTO.setUsername(user.getUsername());
            //查询用户对应的权限
            QueryWrapper<Role> roleQueryWrapper = new QueryWrapper<>();
            roleQueryWrapper.eq("user_id", user.getUserId());
            List<String> roles = roleMapper.findRolesByUserId(user.getUserId());
            log.info("roles: {}", roles);
            //异常处理：如果由用户没有设置权限，就设置为null
            if (roles.isEmpty()) {
               userDTO.setRole("");
            } else {
                userDTO.setRole(roles.get(0));
            }
            userList.add(userDTO);
        });

        return userList;
    }

    public List<SimpleClassDTO> getClassList() {
        List<SimpleClassDTO> classList = new ArrayList<>();
        teachingClassMapper.selectList(new QueryWrapper<TeachingClass>()).forEach(teachingClass -> {
            SimpleClassDTO simpleClassDTO = SimpleClassDTO.builder()
                    .classId(teachingClass.getClassId())
                    .className(teachingClass.getClassName())
                    .teacherId(teachingClass.getTeacherId())
                    .build();
            //查询teacherId对应的名字
            User teacher = userMapper.selectById(teachingClass.getTeacherId());
            simpleClassDTO.setTeacherName(teacher.getUsername());
            classList.add(simpleClassDTO);
        });
        return classList;
    }
}
