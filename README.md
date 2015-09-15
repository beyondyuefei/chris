# chris

chris是一个支持JDBC层数据库读写分离的框架，适用于JDBC、Spring JDBC、MyBatis、Hibernate编程

TODO

- failover
- 重构 动态代理 反射调用的优化
- 注解
- 测试
- 事务隔离问题的考虑 (比如 同一个事务中 后面的select是可以感知到前面的未提交 insert，但是不同的connection下...)
- 读写分离的环境搭建脚本
