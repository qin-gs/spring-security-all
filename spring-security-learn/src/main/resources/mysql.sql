create database spring_security;

create table user
(
    id       bigint auto_increment primary key unique,
    username varchar(32) null,
    password varchar(32) null
) engine = Innodb
  default charset = utf8;

-- 这张表 spring 其实 JdbcTokenRepositoryImpl 会默认创建的，不用自己建
create table persistent_logins
(
    username  varchar(64) not null,
    series    varchar(64) primary key,
    token     varchar(64) not null,
    last_used timestamp   not null
) engine = Innodb
  default charset = utf8;