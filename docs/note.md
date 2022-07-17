# Note

## Java 与 数据库类型对应

|     Java      | Postgres  |
|:-------------:|:---------:|
|   LocalDate   |   date    |
|   LocalTime   |   time    |
| LocalDateTime | timestamp |
|               |           |
|               |           |



## Current Dev-monitor server
140.210.206.239

SELECT * FROM pg_stat_activity;



## Spring Security
把User和LoginUser分开
LoginUser实现UserDetails接口
在loadByUsername中从数据库中查找相应的数据库
给出以下信息：
1. username
2. password
3. role
然后进行认证




   

架构：
SecurityService：
进行登录，返回token

前端带token去访问所有端口

每个子模块的自己控制权限，即自己加入SpringSecurity模组，实现一个简单的Filter
每个子模块只能从JWT中获取用户信息，即每个子模块都会连接redis，如果redis中没有相应信息，就会返回403



