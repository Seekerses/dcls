# dcls
 DCLS - Dynamic changes loading system
  
 Инструкция по запуску:
 
 Необходимо установить JDK с расширением DCEVM. Вариант для Java 17:
 
  Готовые JDK с патчем можно найти на странице релизов проекта TravaOpenJDK:
  
  https://github.com/TravaOpenJDK/trava-jdk-11-dcevm/releases 
  
  Проверена работоспособность на релизе Dcevm-11.0.15+1 (2022-04-28) c артефактом Openjdk11u-dcevm-linux-x64.tar.gz
  
  Необходимо распаковать архив и занести путь до распакованного архива в переменные среды PATH и JAVA_HOME.
  
    export PATH="$HOME/toPath:<Путь до распакованного архива>/dcevm-11.0.15+1/bin:$PATH"
    
    export JAVA_HOME="<Путь до распакованного архива>/dcevm-11.0.15+1"
    
Следующим этапом необходимо собрать проект в jar-файл.

  Из корневой папки проекта исполняем команду:
  
    mvn clean package
  
  После сборки jar-file будет находиться в <sourceDir>/target. Необходимый файл будет называться dcls-agent-jar-with-dependecies.jar.
  
Чтобы агент работал в вашем проекте необходимо задать его в параметре -javaagent при запуске проекта.

    java <Ваши опции> -javaagent:<Путь к jar-файлу агента>=<Путь до корневой папки исходного кода вашего проекта> <Ваш исполняемый файл>
    
  Путь до корневой папки должен указывать на папку с java-файлами. В большинстве случаев это будет путь <Путь к корневой папке проекта>/src/main/java.
  
  При запуске вы увидите логи инициализации агента. Успешный запуск агента ознаменуется сообщением "Sync started."