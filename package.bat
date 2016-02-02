@ECHO OFF

SET WORKING_SPACE=F:\work_e\sms-git\asura


CD %WORKING_SPACE%\asura-base
CALL mvn -Dmaven.test.skip=true clean package
CD %WORKING_SPACE%\asura-log
CALL mvn -Dmaven.test.skip=true clean package
CD %WORKING_SPACE%\asura-cache
CALL mvn -Dmaven.test.skip=true clean package
CD %WORKING_SPACE%\asura-conf
CALL mvn -Dmaven.test.skip=true clean package
CD %WORKING_SPACE%\asura-dao
CALL mvn -Dmaven.test.skip=true clean package
CD %WORKING_SPACE%\asura-dubbo
CALL mvn -Dmaven.test.skip=true clean package
CD %WORKING_SPACE%\asura-monitor
CALL mvn -Dmaven.test.skip=true clean package
CD %WORKING_SPACE%\asura-web
CALL mvn -Dmaven.test.skip=true clean package
CD %WORKING_SPACE%\asura-quartz
CALL mvn -Dmaven.test.skip=true clean package


CALL mvn deploy:deploy-file -DgroupId=com.asura -DartifactId=com-asura-framework-logback -Dversion=0.0.1 -Dpackaging=jar -Dfile=%WORKING_SPACE%\asura-log\target\com-asura-framework-logback-0.0.1.jar -Durl=http://maven.ziroom.com:8081/nexus/content/repositories/releases/ -DrepositoryId=releases
CALL mvn deploy:deploy-file -DgroupId=com.asura -DartifactId=com-asura-framework-cache -Dversion=0.0.1 -Dpackaging=jar -Dfile=%WORKING_SPACE%\asura-cache\target\com-asura-framework-cache-0.0.1.jar -Durl=http://maven.ziroom.com:8081/nexus/content/repositories/releases/ -DrepositoryId=releases
CALL mvn deploy:deploy-file -DgroupId=com.asura -DartifactId=com-asura-framework-base -Dversion=0.0.1 -Dpackaging=jar -Dfile=%WORKING_SPACE%\asura-base\target\com-asura-framework-base-0.0.1.jar -Durl=http://maven.ziroom.com:8081/nexus/content/repositories/releases/ -DrepositoryId=releases
CALL mvn deploy:deploy-file -DgroupId=com.asura -DartifactId=com-asura-framework-dao -Dversion=0.0.1 -Dpackaging=jar -Dfile=%WORKING_SPACE%\asura-dao\target\com-asura-framework-dao-0.0.1.jar -Durl=http://maven.ziroom.com:8081/nexus/content/repositories/releases/ -DrepositoryId=releases
CALL mvn deploy:deploy-file -DgroupId=com.asura -DartifactId=com-asura-framework-dubbo-scheduler -Dversion=0.0.1 -Dpackaging=jar -Dfile=%WORKING_SPACE%\asura-dubbo\target\com-asura-framework-dubbo-scheduler-0.0.1.jar -Durl=http://maven.ziroom.com:8081/nexus/content/repositories/releases/ -DrepositoryId=releases
CALL mvn deploy:deploy-file -DgroupId=com.asura -DartifactId=com-asura-framework-web-spring -Dversion=0.0.1 -Dpackaging=jar -Dfile=%WORKING_SPACE%\asura-web\target\com-asura-framework-web-spring-0.0.1.jar -Durl=http://maven.ziroom.com:8081/nexus/content/repositories/releases/ -DrepositoryId=releases
CALL mvn deploy:deploy-file -DgroupId=com.asura -DartifactId=com-asura-framework-web-oauth -Dversion=0.0.1 -Dpackaging=jar -Dfile=%WORKING_SPACE%\asura-web\target\com-asura-framework-web-oauth-0.0.1.jar -Durl=http://maven.ziroom.com:8081/nexus/content/repositories/releases/ -DrepositoryId=releases
CALL mvn deploy:deploy-file -DgroupId=com.asura -DartifactId=com-asura-framework-publish -Dversion=0.0.1 -Dpackaging=jar -Dfile=%WORKING_SPACE%\asura-conf\target\com-asura-framework-publish-0.0.1.jar -Durl=http://maven.ziroom.com:8081/nexus/content/repositories/releases/ -DrepositoryId=releases
CALL mvn deploy:deploy-file -DgroupId=com.asura -DartifactId=com-asura-framework-subscribe -Dversion=0.0.1 -Dpackaging=jar -Dfile=%WORKING_SPACE%\asura-conf\target\com-asura-framework-subscribe-0.0.1.jar -Durl=http://maven.ziroom.com:8081/nexus/content/repositories/releases/ -DrepositoryId=releases
CALL mvn deploy:deploy-file -DgroupId=com.asura -DartifactId=com-asura-framework-monitor-center -Dversion=0.0.1 -Dpackaging=jar -Dfile=%WORKING_SPACE%\asura-monitor\target\com-asura-framework-monitor-center-0.0.1.jar -Durl=http://maven.ziroom.com:8081/nexus/content/repositories/releases/ -DrepositoryId=releases
CALL mvn deploy:deploy-file -DgroupId=com.asura -DartifactId=com-asura-framework-monitor-client -Dversion=0.0.1 -Dpackaging=jar -Dfile=%WORKING_SPACE%\asura-monitor\target\com-asura-framework-monitor-client-0.0.1.jar -Durl=http://maven.ziroom.com:8081/nexus/content/repositories/releases/ -DrepositoryId=releases
CALL mvn deploy:deploy-file -DgroupId=com.asura -DartifactId=com-asura-framework-quartz-ext -Dversion=1.8.6 -Dpackaging=jar -Dfile=%WORKING_SPACE%\asura-quartz\target\com-asura-framework-quartz-ext-1.8.6.jar -Durl=http://maven.ziroom.com:8081/nexus/content/repositories/releases/ -DrepositoryId=releases
CALL mvn deploy:deploy-file -DgroupId=com.asura -DartifactId=com-asuraquartz-framework-quartz-all -Dversion=1.8.6 -Dpackaging=jar -Dfile=%WORKING_SPACE%\asura-quartz\target\com-asuraquartz-framework-quartz-all-1.8.6.jar -Durl=http://maven.ziroom.com:8081/nexus/content/repositories/releases/ -DrepositoryId=releases




ECHO over!!!!!
PAUSE