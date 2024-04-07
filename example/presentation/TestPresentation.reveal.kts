import dev.limebeck.revealkt.core.RevealKt
import qrcode.color.Colors

title = "Hello from my presentation"

configuration {
    controls = false
    progress = false
    theme = RevealKt.Configuration.Theme.Predefined.BLACK
    additionalCssStyle = """

        .hljs {
          display: block;
          overflow-x: auto;
          padding: 0.5em;
          background: #3f3f3f;
          color: #dcdcdc;
        }

        .hljs-keyword,
        .hljs-selector-tag,
        .hljs-tag {
          color: #e3ceab;
        }

        .hljs-template-tag {
          color: #dcdcdc;
        }

        .hljs-number {
          color: #8cd0d3;
        }

        .hljs-variable,
        .hljs-template-variable,
        .hljs-attribute {
          color: #efdcbc;
        }

        .hljs-literal {
          color: #efefaf;
        }

        .hljs-subst {
          color: #8f8f8f;
        }

        .hljs-title,
        .hljs-name,
        .hljs-selector-id,
        .hljs-selector-class,
        .hljs-section,
        .hljs-type {
          color: #efef8f;
        }

        .hljs-symbol,
        .hljs-bullet,
        .hljs-link {
          color: #dca3a3;
        }

        .hljs-deletion,
        .hljs-string,
        .hljs-built_in,
        .hljs-builtin-name {
          color: #cc9393;
        }

        .hljs-addition,
        .hljs-comment,
        .hljs-quote,
        .hljs-meta {
          color: #7f9f7f;
        }


        .hljs-emphasis {
          font-style: italic;
        }

        .hljs-strong {
          font-weight: bold;
        }
    """.trimIndent()
}

slides {
    regularSlide {
        autoanimate = true
        +title { "Test 3" }
    }
    regularSlide {
        autoanimate = true
        +qrCode("https://github.com/LimeBeck/reveal-kt") {
            stretch = true
            transformBuilder {
                val logo = loadAsset("logo2.png")
                it.withSize(20).withColor(Colors.css("#B125EA")).withLogo(logo, 150, 150, clearLogoArea = true)
            }
        }
    }
    verticalSlide {
        val title = Title { "Some text" }
        slide {
            autoanimate = true
            +title
            +note {
                "Some note"
            }
        }
        slide {
            autoanimate = true
            +title
            +title { "Updated text" }
            +note {
                "Some note"
            }
        }
        slide {
            //language=XML
            val jsonCode =  """
                <?xml version="1.0" encoding="UTF-8"?>
                <project xmlns="http://maven.apache.org/POM/4.0.0" 
                     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                     xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
                
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
            autoanimate = true
            +title
//            +code(
//                trim = false
//            ) {
//                jsonCode
//            }
            +code(
                lines = "1|12|30",
                trim = false
            ) {
                jsonCode
            }
        }
        slide {
            +img(src = "image.png") {
                stretch = true
            }
        }
    }
}