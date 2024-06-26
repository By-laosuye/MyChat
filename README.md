# 多人群聊系统 

一个高性能的多人群聊应用，利用Spring Boot构建服务端，Netty处理高并发网络通信，Redis实现消息缓存与发布订阅，以及MyBatis-Plus简化数据库操作，旨在提供流畅、即时的聊天体验。

## 特性

- **实时通信**：借助Netty实现低延迟、高吞吐量的消息传输。
- **多人群聊**：支持创建多个聊天室，用户可以自由加入或退出。
- **消息持久化**：使用MyBatis-Plus与数据库交互，确保消息的可靠存储。
- **消息缓存**：Redis缓存机制减少数据库访问压力，提升响应速度。
- **可扩展性**：模块化设计，易于添加新功能或集成其他服务。

## 快速开始

### 环境要求

- Java 8
- Maven
- Redis
- MySQL
