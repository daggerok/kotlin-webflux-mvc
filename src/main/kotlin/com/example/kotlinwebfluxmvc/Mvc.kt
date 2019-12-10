package com.example.kotlinwebfluxmvc

import org.apache.logging.log4j.LogManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.result.view.Rendering
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebExceptionHandler
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration

@Controller
@Profile("mvc")
class IndexPage {

  @GetMapping("", "/", "/404")
  fun index() = Rendering.view("index")
      .modelAttribute("message", "Hello from @Controller!")
      .build()
}

@Configuration
@Profile("mvc")
class WebFluxMvcConfig {

  @Bean
  fun infiniteStream() = Flux.interval(Duration.ofSeconds(1))
      .map { "Hello $it infinite WebFlux stream from @RestController!" }
      .share()
}

@Profile("mvc")
@RestController
class MessageResource(private val infiniteStream: Flux<String>) {

  @GetMapping("/api/message")
  fun getMessage() = mapOf("message" to "Hello from @RestController!")

  @GetMapping(path = ["/api/messages"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
  fun getMessages() = infiniteStream
}

/**
 * @see: <a href="https://stackoverflow.com/questions/47631243/spring-5-reactive-webexceptionhandler-is-not-getting-called">Useful stackoverflow answer</a>
 * @see: <a href="https://docs.spring.io/spring-boot/docs/2.0.0.M7/reference/html/boot-features-developing-web-applications.html#boot-features-webflux-error-handling">Error handling reference</a>
 */
@Component
@Profile("mvc")
@Order(Int.MIN_VALUE)
class FallbackHandler : WebExceptionHandler {
  companion object {
    private val log = LogManager.getLogger()
  }

  override fun handle(exchange: ServerWebExchange, e: Throwable): Mono<Void> = Mono.fromRunnable {
    val message = e.localizedMessage ?: ""
    log.info("handling {} error", message)
    val redirect = "/404?details=" + message.replace("[^a-zA-Z0-9_\\-\\\\.]".toRegex(), "_")
    val response = exchange.response
    response.statusCode = HttpStatus.PERMANENT_REDIRECT
    response.headers[HttpHeaders.LOCATION] = listOf(redirect)
  }
}
