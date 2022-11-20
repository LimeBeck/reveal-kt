import dev.limebeck.revealkt.core.RevealKt

title = "(Де)мистифицируем Gradle"

configuration {
    controls = false
    progress = true
    theme = RevealKt.Configuration.Theme.Predefined.BLACK
    additionalCssStyle = """
        @import url('https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500&display=swap');

		.reveal h1,
		.reveal h2,
		.reveal h3,
		.reveal h4,
		.reveal h5,
		.reveal h6 {
			font-family: 'Roboto', sans-serif;
		}

		.reveal .slide {
			font-family: 'Roboto', sans-serif;
		}

		.container {
			display: flex;
		}

		.col {
			flex: 1;
		}
    """.trimIndent()
}

slides {
    regularSlide {

        +title(fitText = true) { "(Де)мистифицируем" }
        +img(src = "gradle-white-primary.png")
        +note("Спросить, кто вообще пользовался гредлом")
    }

    regularSlide {

        +mediumTitle { "Кто я такой?" }
        +note("Рассказать, что я вкатился сначала на гредл, а потом только попробовал мавен")
        +row {
            column {
                +img(src = "me.jpg") {
                    height = 400
                }
            }
            column {
                +unorderedListOf(
                    listOf(
                        "Фанат Linux с 15 лет",
                        "Ex-фанат Python",
                        "Вкатился с Python на Kotlin",
                        "Фанат Kotlin 3 года \uD83E\uDD37\u200D♂️",
                        "Начал пользоваться Gradle раньше Maven",
                        "Писал микросервисы",
                    )
                )
            }
        }
    }

    regularSlide {
        +title(fitText = true) { "DANGER" }.apply {
            addStyle("color", "red")
        }
        +mediumTitle { "Холиварная тема" }
    }

    regularSlide {
        +mediumTitle { "Что же такое Gradle?" }
        +unorderedListOf(
            listOf(
                "Тянет зависимости",
                "Вызывает компиляторы Kotlin и Java",
                "Генерит код из всяких xsd",
                "Пакует jar",
                "И прочие скучные задачи"
            )
        )
    }

    val asMavenTitle = title(fitText = true) { "Так же как и Maven?" }
    regularSlide {
        +asMavenTitle
    }
    regularSlide {
        +asMavenTitle
        +img(src = "no.png") {
            stretch = true
        }
    }

    //language=XML
    val mavenStyleCode = """
        <?xml version="1.0" encoding="UTF-8"?>
        <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
      
          <modelVersion>4.0.0</modelVersion>
      
          <groupId>org.example</groupId>
          <artifactId>maven-module</artifactId>
          <version>1.0-SNAPSHOT</version>
          <packaging>jar</packaging>
      
          <name>org.example maven-module</name>
      
          <properties>
              <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
              <kotlin.version>1.6.10</kotlin.version>
              <kotlin.code.style>official</kotlin.code.style>
              <junit.version>4.12</junit.version>
          </properties>
      
          <dependencies>
              <dependency>
                  <groupId>org.jetbrains.kotlin</groupId>
                  <artifactId>kotlin-stdlib</artifactId>
                  <version>\${'$'}{kotlin.version}</version>
              </dependency>
              <dependency>
                  <groupId>org.jetbrains.kotlin</groupId>
                  <artifactId>kotlin-test-junit</artifactId>
                  <version>\${'$'}{kotlin.version}</version>
                  <scope>test</scope>
              </dependency>
              <dependency>
                  <groupId>junit</groupId>
                  <artifactId>junit</artifactId>
                  <version>\${'$'}{junit.version}</version>
                  <scope>test</scope>
              </dependency>
          </dependencies>
      
          <build>
              <sourceDirectory>src/main/kotlin</sourceDirectory>
              <testSourceDirectory>src/test/kotlin</testSourceDirectory>
      
              <plugins>
                  <plugin>
                      <groupId>org.jetbrains.kotlin</groupId>
                      <artifactId>kotlin-maven-plugin</artifactId>
                      <version>\${'$'}{kotlin.version}</version>
                      <executions>
                          <execution>
                              <id>compile</id>
                              <phase>compile</phase>
                              <goals>
                                  <goal>compile</goal>
                              </goals>
                          </execution>
                          <execution>
                              <id>test-compile</id>
                              <phase>test-compile</phase>
                              <goals>
                                  <goal>test-compile</goal>
                              </goals>
                          </execution>
                      </executions>
                  </plugin>
              </plugins>
          </build>
      </project>
    """.trimIndent()

    regularSlide {
        +title(fitText = true) { "Maven-style" }
        +note("И такое еще на 70 строк + нельзя указать в одном месте версии зависимостей для всех подпроектов")
        +code(lines = "1-30|30-50|50-70") { mavenStyleCode }
    }

    //language=kotlin
    val gradleStyleCode = """
        plugins {
            kotlin("jvm") version "1.6.10"
        }
        
        group = "org.example"
        version = "1.0-SNAPSHOT"
        
        repostories {
            mavenCentral()
        }
        
        dependencies {
            implementation(kotlin("stdlib"))
        }
    """.trimIndent()

    regularSlide {
        +title(fitText = true) { "Gradle-style" }
        +code(lang = "kotlin") { gradleStyleCode }
    }

    regularSlide {
        +title(fitText = true) { "Лично моя реакция" }
        +img(src = "cracker-bird.png") {
            stretch = true
        }
    }

    regularSlide {
        +title(fitText = true) { "Взгляд на сборку от Maven" }
        note {
            """
                У мавена три фиксированные фазы
                There are three built-in build lifecycles: default, clean and site.
                The default lifecycle handles your project deployment,
                the clean lifecycle handles project cleaning,
                while the site lifecycle handles the creation of your project's web site.
            """.trimIndent()
        }
        +img(src = "maven-lifecycle.png") {
            stretch = true
        }
    }

    regularSlide {
        +title(fitText = true) { "DAG-и* от Gradle" }
        note {
            """
                У гредла - абсолютно произвольный направленный ацикличный граф задач,
                который вычисляется перед выполнением,
                что дает множество преимуществ, особенно,
                если у нас многомодульный проект, либо особо мудреная сборка
            """.trimIndent()
        }
        +img(src = "gradle-dag.png") {
            stretch = true
        }
        +mediumTitle(fitText = true) { "*DAG - Directed Acyclic Graph" }
    }

    regularSlide {
        +title(fitText = true) { "И что с того?" }
    }

    regularSlide {
        +title { "А вот что" }
        +note {
            """
            Первый плюс гредла перед мавеном:
            <br />За счет предварительного построения графа задач, гредл более производительным, чем мавен,
            <br />т.к. можно вычислить, какие из тасков нужно выполнить для достижения нужного эффекта.
            <br />Особенно с учетом того, что он умеет кешировать результат выполнения тасков и даже делать
            инкрементальную компиляцию
            """.trimIndent()
        }
        +unorderedListOf(
            listOf(
                "Производительность",
                "Инкрементальная сборка",
                "Инкрементальная компиляция",
                "Кеш результатов сборки",
                "Гибкость в настройке",
                "Эффективно параллелит выполнение задач",
            )
        )
    }

    regularSlide {
        +title(fitText = true) { "Еще у Gradle есть демон" }
        +img(src = "father.jpg") {
            stretch = true
        }
    }

    regularSlide {
        +title(fitText = true) { "Производительность в сравнении с Maven (по версии Gradle.org)" }
        +img(src = "build-time.png") {
            stretch = true
        }
    }

    regularSlide {
        +title(fitText = true) { "Киллер-фича" }
    }

    regularSlide {
        +title(fitText = true) { "Kotlin DSL" }
        +img(src = "maskot.jpg") {
            stretch = true
        }
    }

    val groovyTitle = title(fitText = true) { "Groovy DSL?" }
    regularSlide {
        +groovyTitle
        +note {
            """
                Хуже поддержка в ide, дольше работает
                Kotlin dsl копилится перед выполнением и работает быстрее
            """.trimIndent()
        }
    }

    regularSlide {
        +groovyTitle
        +note {
            """
                Хуже поддержка в ide, дольше работает, много магии динамической типизации
                Kotlin dsl копилится перед выполнением и работает быстрее
            """.trimIndent()
        }
        +img(src = "no-thanks.jpg") {
            stretch = true
        }
    }

    regularSlide {
        +title(fitText = true) { "Пример написания задачи в Gradle" }
        +note {
            """
                Тут мы получаем результат всех задач в подпроекте с именем bootJar и копируем его в директорию dist
                А после указывем, что это действие нужно выполнить после завершения всех запланированных задач
                bootJar
            """.trimIndent()
        }
        +code(lang = "kotlin") {
            """
                val copyJarsToDist by tasks.registering {
                    doLast {
                        val bootJars = subprojects.mapNotNull {
                            it.tasks.findByName("bootJar")
                        }
                        copy {
                            from(bootJars)
                            into(project.rootDir.path + "/dist")
                        }
                    }
                }
                
                subprojects {
                    tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar>().configureEach {
                        finalizedBy(copyJarsToDist)
                    }
                }
            """.trimIndent()
        }
    }

    val gradleFeaturesTitle = title(fitText = true) { "Еще фишки Gradle" }
    regularSlide {
        +gradleFeaturesTitle
        +orderedListOf(
            listOf(
                "Гибкость",
                "Адекватное управление зависимостями",
                "Легкость написания новых тасков",
                "Контроль над происходящим при сборке",
                "Gradle wrapper",
            )
        )
    }
    regularSlide {
        +gradleFeaturesTitle
        +orderedListOf(
            listOf(
                regular { "Гибкость" },
                regular { "Адекватное управление зависимостями" },
                regular { "Легкость написания новых тасков" },
                regular { "Контроль над происходящим при сборке" },
                strike { "Gradle wrapper" }
            ),
            fragmented = false
        )
        +img(src = "secure.png") {
            stretch = true
        }
    }

    val versionsTitle = title(fitText = true) { "Работа с версиями" }
    regularSlide {
        +versionsTitle
        +smallTitle { "Простой кейс" }
        +code(lang = "kotlin") {
            """
                |dependencies {
				|	implementation("org.springframework:spring-web")
				|}
				|...
				|dependencies {
				|	constraints {
				|		implementation("org.springframework:spring-web:5.0.2.RELEASE")
				|	}
				|}
            """.trimMargin()
        }
        +note { "Зависимости можно описывать в одном месте, а применять - в другом" }
    }

    regularSlide {
        +versionsTitle
        +smallTitle { "Сложный кейс" }
        +note {
            """
                Тут подключен slf4j с гибкики ограничениями: любая патч версия 1.7, если никто не пишет точнее -
                1.7.25
                В спринге - может быть выше 4.2.9.RELEASE, но только не 4.3.16.RELEASE
            """.trimIndent()
        }
        +code(lang = "kotlin") {
            """
                |dependencies { // Подключить зависимость
				|	implementation("org.slf4j:slf4j-api") {
				|		version {
				|			strictly("[1.7, 1.8[") // Самая строгая. Переписывает транзитивные зависимости
				|			prefer("1.7.25") // Будет использоваться, если больше никого не волнует конкретная версия
				|			//require("[1.7, 1.8[") // Как strictly, но может быть больше правой границы из-за транзитивных
				|			//rejects("1.7.15") // Отбрасывает эту версию
				|			// А еще тут внезапно важен порядок записи
				|		}
				|	}
				|
				|	constraints { // Только наложить ограничения
				|		add("implementation", "org.springframework:spring-core") {
				|			version {
				|				require("4.2.9.RELEASE")
				|				reject("4.3.16.RELEASE")
				|			}
				|		}
				|	}
				|}
            """.trimMargin()
        }
    }

    regularSlide {
        +title(fitText = true) { "Когда же использовать Gradle?" }
    }

    regularSlide {
        +title(fitText = true) { "Нестандартная структура проекта" }
        +img(src = "mud.png") {
            stretch = true
        }
    }

    regularSlide {
        +title(fitText = true) { "Много подпроектов и зависимостей (см пункт 1)" }
        +img(src = "BigBallOfMud-4.png") {
            stretch = true
        }
    }

    regularSlide {
        +title(fitText = true) { "Есть микрозадачи, которые хочется автоматизировать" }
        +img(src = "automate.jpg") {
            stretch = true
        }
    }

    regularSlide {
        +title(fitText = true) { "Kotlin ❤️" }
    }

    regularSlide {
        +title(fitText = true) { "А что насчет Maven?" }
    }

    regularSlide {
        +title(fitText = true) { "Всё еще самый популярный для JVM в целом" }
        +img(src = "toosl-stats.png") {
            stretch = true
        }
        +smallTitle {
            "По данным Snyk в 2021 г.:\n" +
                    "\t\t\t\t\thttps://snyk.io/jvm-ecosystem-report-2021"
        }
    }

    regularSlide {
        +title(fitText = true) { "Почему?" }
        +orderedListOf(
            listOf(
                "Проверенный",
                "Общеизвестный",
                "Простой",
                "Охватывает потребности Enterprise",
                "Давно используется, страшно менять (легаси)",
            )
        )
    }

    regularSlide {
        +title(fitText = true) { "Но уступает позиции в Kotlin-разработке Gradle" }
        +img(src = "kotlin-stat.png")
        +smallTitle { "По данным опроса Jetbrains в 2020 г.: https://www.jetbrains.com/lp/kotlin-census-2020/" }
    }

    regularSlide {
        +title(fitText = true) { "Почему?" }
        +orderedListOf(
            listOf(
                "Хочется один язык",
                "Сложно конфигурировать нестандартные проекты",
                "Тяжело расширять",
                "Gradle - стильно, модно, молодежно",
                "Тулинг Kotlin затачивается под Gradle",
            )
        )
    }

    regularSlide {
        +note {
            """
                Выводы: я бы советовал изучить gradle, как минимум, чтобы лучше узнать, как работают системы сборки
                В том числе, это поможет лучше понять Maven
                Использовать ли его в своих проектах - нужно оценить, чего будет стоить переход и какие профиты это
                принесет
            """.trimIndent()
        }
        +img(src = "thatsall.jpg") {
            stretch = true
        }
    }

    regularSlide {
        +title(fitText = true) { "Полезные ссылки" }
        +orderedListOf(
            listOf(
                "https://gradle.org/",
                "Гайд от команды Spring: https://spring.io/guides/gs/gradle/",
                "Stackoverflow, когда напишете свой таск, а он не будет работать",
                "Аргументы за Maven: https://phauer.com/2018/moving-back-from-gradle-to-maven/",
            )
        )
    }
}