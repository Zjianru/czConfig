# czConfig 配置中心
[czConfig](https://github.com/zjianru/czConfig)是一个持久化的配置中心中间件。
从头创建一个配置中心 逐步完成高级功能  并与 czRpc 集成

模型参考 apollo 和 nacos 这里简要介绍其背景和原理

一个完整的配置中心 包括 server 和 client 两部分

server 保存所有的持久化配置数据 client 可以通过 server 提供的 API 拿到所有的需要的配置集合  
并且在 server 端数据变化时拿到新的配置数据

与 spring 集成:
springboot 里存在两种配置的绑定方式
一个是通过 @Value 注解绑定的属性
一个是通过 configurationProperties 绑定的属性类

需要考虑这两种不同配置方式的处理

## 项目包括如下几个部分

* [czConfig-server](./czConfig-server)：配置中心服务端，负责配置的存储、发布、同步等。czConfig-server：配置中心服务端，负责配置的存储、发布、同步等。
* [czConfig-client](./czConfig-client)：配置中心客户端，负责配置的获取、更新等。
* [czConfig-demo](./czConfig-demo)：czConfig-demo: 配置中心客户端demo。

## 当前进展

* czConfig-server 完成v1实现三个接口。

