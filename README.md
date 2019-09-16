# miaosha

## git easy use:

git pull origin master --allow-unrelated-histories

git add .

git commit -m ""

git remote add origin git@github.com:LingTian1998/my_miaosha_system.git

 git push -u origin master

## RabbitMQ install docker

```shell
docker pull rabbitmq:management
docker run -d -p 5672:5672 -p 15672:15672 --name rabbitmq rabbitmq:management
```

带有management的版本才有管理界面

通过访问localhost:15672可以进入管理界面，默认账号密码都是guest

[https://www.rabbitmq.com](https://www.rabbitmq.com/)

["guest" user can only connect from localhost](https://www.rabbitmq.com/access-control.html#loopback-users)

<https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-rabbitmq>

