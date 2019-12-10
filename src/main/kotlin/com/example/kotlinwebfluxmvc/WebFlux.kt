package com.example.kotlinwebfluxmvc

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RenderingResponse
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toMono
import java.net.URI
import java.time.Duration

@Configuration
@Profile("webflux")
class WebFluxConfig {

  @Bean
  fun infiniteStream() = Flux.interval(Duration.ofSeconds(1))
      .map { "Hello $it infinite WebFlux stream not from @RestController!" }
      .share()

  @Bean
  fun routes(infiniteStream: Flux<String>) = router {
    "".nest {
      listOf("", "/", "/404").forEach {
        GET(it) {
          RenderingResponse.create("index")
              .modelAttribute("message", "Hello not from @Controller!")
              .build()
        }
      }
      "/api".nest {
        GET("/message") {
          ok().contentType(MediaType.APPLICATION_JSON)
              .body(mapOf("message" to "Hello not from @RestController").toMono())
        }
        GET("/messages") {
          ok().contentType(MediaType.TEXT_EVENT_STREAM)
              .body(infiniteStream)
        }
      }
      path("/**") {
        // // RenderingResponse.create("forward:/404").build() // wrong: use redirect instead!
        // // https://jar-download.com/artifacts/org.thymeleaf/thymeleaf-spring5/3.0.10.RELEASE/source-code/org/thymeleaf/spring5/view/reactive/ThymeleafReactiveViewResolver.java
        // RenderingResponse.create("redirect:/404").build()
        //
        // seeOther(URI.create("/404")).build()
        //
        // permanentRedirect(URI.create("/404")).build()
        //
        // ok().render("redirect:/404")
        //
        // ok().header("X-FALLBACK-PATH", it.path())
        //     .header("X-FALLBACK-METHOD", it.methodName())
        //     .render("index", "message" to "WebFlux fallback ${it.path()} from RenderingResponse!")
        //
        RenderingResponse.create("index")
            .header("X-FALLBACK-PATH", it.path())
            .header("X-FALLBACK-METHOD", it.methodName())
            .header("X-FALLBACK-QUERY-PARAMS", it.queryParams()
                .entries.map { it.key to it.value.orEmpty().joinToString(separator = ",") { "$it" } }
                .map { "${it.first}='${it.second}'" }
                .foldRight("") { first, second -> "$first,$second" })
            .modelAttributes("message" to "WebFlux fallback ${it.path()} from RenderingResponse!")
            .build()
      }
    }
  }
}
