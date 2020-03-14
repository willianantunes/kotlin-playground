package br.com.willianantunes.kotlinplayground.rest

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired


import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GreetingControllerIT(@Autowired val restTemplate: TestRestTemplate) {
    @Test
    fun `Should return hello word with ID 1 given this is the first request to the endpoint`() {
        val entity = restTemplate.getForEntity("/greeting", String::class.java)
        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(entity.body).isEqualTo("""{"id":1,"content":"Hello, World"}""")
    }
}
