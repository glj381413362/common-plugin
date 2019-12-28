# sms-plugin 插件的使用

![](https://raw.githubusercontent.com/12711/picture/master/20190605225230.png)

# sms-plugin 功能如下
+ 发送消息
+ 如果为验证码类型的信息，信息自动保存到redis，提供校验验证码api
+ 如果为营销类信息，默认不进行存储，可手动开启（下文演示），开启后需要提供客制化的缓存策略进行存储（下文演示）
+ 如果默认的发送策略以及存储策略都不满足您，我们支持用户客制化发送策略以及存储策略
+ 发送策略和存储策略可自由组合

### 如何使用？
> 使用时只需要将插件的jar的maven坐标引入到当前的项目pom文件中即可，插件会自动装配redis所需对象，需操作redis时注入HmallRedisServer即可
+ 引入插件坐标
       
  ```xml
   <dependency>
       <groupId>org.hmall</groupId>
       <artifactId>sms-plugin</artifactId>
       <version>0.0.1-RELEASE</version>
   </dependency>
  ```
+ 牵扯到付费没有默认，需要进行相关的平台信息配置
  
  + 插件默认开启ali发送短信的策略，需进行如下配置
    ```yaml
     sms:
       ali:
         # 信息的产品
         product: xxx(默认 Dysmsapi)
         # 访问域名
         domain: xxx(默认 dysms.aliyuncs.com）
         accessKeyId: 
         accessKeySecret: 
         defaultConnectTimeout: 默认10000
         defaultReadTimeout: 默认10000
    ```
  + 验证码短信相关配置
    
    配置验证码保存redis的namespace前缀以及验证码过期时间
    ```yaml 
       sms:
         checkcode:
           prefix: hmall  # 前缀
           expireTime: 120 # 
    ```  
+ 使用如下
  + 发送短信
  ```java
     @RestController
     public class UserController {  
         /**
          * 依赖注入短信发送的service
          */
         @Autowired
         private SmsService smsService;
     
     
         @RequestMapping("/send")
         public String index() throws IOException {  
           
            /**
             * !!!!!!!  验证码类型短信发送如下：
             * 
             * param: 为短信默认中需要填充的字段
             * checkCode: 为验证码的值
             * checkCodeType 验证码短信的具体类型（登录验证码[LOGIN],注册验证码[REGISTER]）,最终体现在redis中的namespace
             * type: 短信类型（验证码[code]/营销类短信）
             * signName: 短信的签名  
             * phoneNumbers: 电话号码支持多个，使用逗号隔开
             * templateCode: 第三方平台短信模版编码
             */
             Map<String,String> param = new HashMap<>();
             param.put("code","123456");
             smsService.sendSms(
               new SmsReqData()
               .setCheckCode("123456")
               .setCheckCodeType("CHECK_CODE")
               .setType("CODE")
               .setSignName("手选内购")
               .setPhoneNumbers("17680723818")
               .setSmsContentJson(param)
               .setTemplateCode("SMS_120760005")
             );
           
            /**
             * !!!!!!!  非验证码类型短信发送如下：
             * 
             * param: 为短信默认中需要填充的字段
             * signName: 短信的签名  
             * phoneNumbers: 电话号码支持多个，使用逗号隔开
             * templateCode: 第三方平台短信模版编码
             */  
             Map<String,String> param = new HashMap<>();
                          param.put("name","xxx");
                          smsService.sendSms(
                            new SmsReqData()
                            .setSignName("手选内购")
                            .setPhoneNumbers("17680723818,15197867623")
                            .setSmsContentJson(param)
                            .setTemplateCode("SMS_120760005")
                          );
     
             return "OK";
         }  
     }
  ```
  + 如果为验证码类型的短信，可调用插件的校验api来进行验证码校验

    ```
      /**
       * code：验证码
       * phone： 电话号码
       */  
      smsService.checkcode(code,phone);
     
    ```
    返回true验证码验证通过，false为验证码失败
    
  + 如果我们想将发送短信进行存储需要手动开启存储开关以及客制化一个存储的策略
  
    + 在application.yaml中手动开启存储开关
      
      ```yaml
         sms:
           isStore: true
      ```
    + 客制化存储策略
      
      + 继承AbstractSmsStoreStrategyService接口,而AbstractSmsStoreStrategyService实现SmsStoreStrategyService接口
        
        我们例子将以将信息保存到数据库方式进行演示
        ```java
           public class CustomerStoreStrategyServiceImpl extends AbstractSmsStoreStrategyService {
               @Autowired
               private MessageMapper messageMapper;
           
           
               @Override
               public void storeCheckCode(SmsCheckCodeData smsCheckCodeData) {
                   messageMapper.insert(new Message().setContent(JSON.toJSONString(smsCheckCodeData)));
               }
           
               @Override
               public SmsCheckCodeData queryCheckCode(SmsCheckCodeData smsCheckCodeData) {
                   return null;
               }
           }
        ``` 
      + 告诉插件需要使用我们客制化的保存策略
        
        ```text
           在resources目录下新建一个META-INF文件文件夹，并且新建一个txt文件命名为
           `SmsStoreStrategyService`接口的全限定名org.hmall.sms.service.SmsStoreStrategyService(这个步骤我们定义他为s1)
           内容为自定义实现类的全限定名com.xxx.service.CustomerStoreStrategyServiceImpl(这个步骤我们定义他为s2)
   
        ```   
      + 完成上面的配置后，再次发送短信 短信将会根据你客制化的策略逻辑进行保存
        
      + 如果你自定义多种保存策略，你只需要将s2的类容替换成你的策略实现类即可  
     
+ 客制化发送策略
    
    和上面的客制化保存策略大致相同：
    + 继承实现AbstractSmsStrategyService抽象类，AbstractSmsStrategyService实现了SmsStoreStrategyService接口（这里只提供百度短信发送的伪代码，具体逻辑具体实现）
      ```java
                 public class CustomerServiceImpl extends AbstractSmsStrategyService {
                     private Logger LOG = LoggerFactory.getLogger(CustomerServiceImpl.class);
                 
                     @Override
                     public void sendSms(SmsReqData smsReqData) {
                         System.out.println("使用百度短信进行发送");
                     }
                 }

       ``` 
    
    + 告诉插件需要使用我们客制化的发送策略
      
      ```text
         在resources目录下新建一个META-INF文件文件夹，并且新建一个txt文件命名为
         `SmsStoreStrategyService`接口的全限定名org.hmall.sms.service.SmsStrategyService(这个步骤我们定义他为s1)
         内容为自定义实现类的全限定名com.xxx.CustomerServiceImpl(这个步骤我们定义他为s2)
     
       ``` 
    + 完成上面的配置后，再次发送短信 短信将会根据你客制化的策略逻辑进行保存
            
    + 如果你自定义多种保存策略，你只需要将s2的类容替换成你的策略实现类即可   