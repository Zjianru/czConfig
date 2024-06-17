# czConfig 配置中心
[czConfig](https://github.com/zjianru/czConfig)是一个持久化的配置中心中间件。
从头创建一个配置中心 逐步完成高级功能  并与 czRpc 集成

模型参考 apollo 和 nacos 

这里简要介绍其背景和原理

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

- [x] 配置中心化存储
- [X] 配置变更时推送配置，覆盖 `@Value` 注解与 `@ConfigurationProperties` 注解
- [X] 配置中心集群化，可完成主节点选举并给到客户端反馈

## 配置中心替换配置原理
![img.png](noteImg/img.png)

### 配置替换逻辑
#### 启动时替换配置
入口: `EnableCzConfig`
注解中 import 了启动逻辑 `BeanRegistrar` ， 实现 `ImportBeanDefinitionRegistrar` 接口，注入 `PropertySourcesProcess` 处理器进行逻辑处理

`PropertySourcesProcess` 负责获取配置中心配置并将配置打包为 `CompositePropertySource` 注入进 `ConfigurableEnvironment` 中

#### 主动替换配置
1. 定时任务线程轮询，版本号变更时发送刷新事件，交由 spring 完成配置刷新

相关实现代码 `com.cz.config.client.reporsitory.invoke.HttpRepo#heartBeat`

2. spring value 方式
   需要额外进行处理，大致步骤:
   1. 扫描所有的 springvalue 注解 保存起来
   2. 监听配置中心
   3. 配置中心有变化，更新springvalue
扫描 - 可在 bean 初始化阶段使用 BeanPostProcessor 完成扫描
监听变化 - 两种方式 @EventListener 注解 或实现 ApplicationListener 接口的 onApplicationEvent 方法
applicationListener 的 spring 示例， 可参考`org.springframework.cloud.context.properties.ConfigurationPropertiesRebinder`

## 配置中心集群化选主

### 单数据库在集群场景下实现分布式锁，进行选主的基本逻辑

czConfig的选主逻辑与 czRegistry 不同 ， czConfig 控制配置的存储会依赖内部数据库，所以 czConfig 在此基础上，在数据库中引入了一张表 `Locks`

该表中初始会写入一条记录，占据 id 为 1 的记录，并使用 `select for update` 获取到该记录，获取到记录的同时也就获取到了锁

集群中的所有节点使用同一数据库，因此只需要在数据库层面完成数据一致性即可

在实现上，未使用 mybatis ， 而是直接使用 jdbc 进行操作 ， mybatis 中存在数据库连接池配置，但是在本场景下需要同步修改数据库锁等待时间`innodb_lock_wait_timeout`参数

该参数较为重要，如果使用连接池，则链接被复用时可能会造成污染

具体实现可参考`com.cz.config.server.lock.DistributedLocks`

### 客户端如何感知

客户端可采取与 czRegistry 相同的策略，即开放线程池轮询

另有一种做法，同时依赖数据库完成，此方式也需要:

新开辟数据表，数据表内存储主节点信息，包括 url ， ip ， 端口 ，上下文， namespace 等信息，选主完成后，同步更新该条记录
server 开辟 url 供客户端获取主节点信息，任何客户端都可以通过该 url 获取主节点信息

## 优化点
1. 获取配置信息的 `/list` 接口可添加本地缓存或分布式缓存，只有在变更时才更新缓存，可能衍生问题为：挡住从节点切换，可能会产生数据丢失

处理方式：在主从节点变更后，主节点同时更新缓存
2. 客户端优化，添加临时文件，以防止 server 宕机或超时响应时数据异常
- 添加应急开关
- 添加临时文件