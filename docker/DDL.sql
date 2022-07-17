create table platform_user (
    user_id bigint,
    username varchar(255),
    password varchar(255),
    email varchar(255),
    primary key (user_id)
);

create table user_role (
    id bigserial,
    user_id bigint,
    role_id bigint,
    primary key (id)
);

create table role(
    role_id bigserial,
    name varchar(255),
    primary key (role_id)
);

create table student(
    student_id bigint,
    name varchar(255),
    class_id bigint,
    group_id bigint,
    line_of_code int,
    score int,
    primary key (student_id)

);

create table class_group(
    group_id bigserial,
    group_name varchar(255),
    manager_id int,
    class_id int,
    project_id int,
    requirement_analysis_score int,
    design_score int,
    project_development_score int,
    project_report_score int,
    quality_score int,
    primary key (group_id)
);

create table class(
    class_id bigserial,
    class_name varchar(255),
    teaching_plan_id bigint,
    engineer_id bigint,
    teacher_id bigint,
    primary key (class_id)

);

-- 请假条
create table leave_record(
    id bigserial,
    start_date date,
    end_date date,
    reason varchar(255),
    phone_number bigint,
    department varchar(255),
    student_id bigint,
    engineer_id bigint,
    teacher_id bigint,
    progress int,
    engineer_status varchar(255),
    teacher_status varchar(255),
    primary key (id)
);


create table material_record(
    material_id bigserial,
    name varchar(255),
    publish_time date,
    url varchar(255),
    class_id bigint,
    category varchar(255),
    primary key (material_id)
);

create table project(
    project_id bigserial,
    name varchar(255),
    description varchar(1023),
    status varchar(255),
    degree_of_difficulty int,
    technology_requirement varchar(255),
    language varchar(255),
    count_of_file int,
    line_of_code int,
    quality_of_code int,
    primary key (project_id)
);

create table attendance_record(
    id bigserial,
    student_id bigint,
    time date,
    exact_time time,
    status varchar(255),
    primary key (id)
)


create table homework(
    homework_id bigserial,
    class_id bigint,
    name varchar(255),
    publish_time date,
    deadline timestamp,
    content varchar(1023),
    primary key (homework_id)
);

create table submit_homework_record(
    id bigserial,
    student_id bigint,
    homework_id bigint,
    submit_time timestamp,
    submit_status varchar(20),
    score int,
    content_url varchar(255),
    primary key (id)
);

create table submit_code_record(
    id bigserial,
    student_id bigint,
    submit_time timestamp,
    line_of_code int,
    primary key (id)
);

create table file(
    id bigserial,
    name varchar(255),
    type varchar(255),
    url varchar(1023),
    key varchar(1023),
    upload_time date,
    uploader_id bigint,
    primary key (id)
);

create table teaching_plan(
    id bigserial,
    name varchar(255),
    description varchar(1023),
    start_date date,
    end_date date,
    primary key (id)
);

create table teaching_day(
    id bigserial,
    teaching_plan_id bigint,
    name varchar(255),
    description varchar(1023),
    teaching_date date,
    primary key (id)
);

create table day_material(
    id bigserial,
    teaching_day_id bigint,
    material_id bigint,
    primary key (id)
);






