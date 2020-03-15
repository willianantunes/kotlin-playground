package br.com.willianantunes.kotlinplayground

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

val REQUEST_PATH_ACTUATOR = "/actuator/health"

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class KotlinPlaygroundApplicationTests @Autowired constructor(val restTemplate: TestRestTemplate) {
    @Test
    fun `Should load Spring Boot context`() {
    }

    @Test
    fun `Should configure actuator`() {
        val entity = restTemplate.getForEntity(REQUEST_PATH_ACTUATOR, String::class.java)
        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(entity.headers.contentType.toString()).isEqualTo(MediaType.APPLICATION_JSON_VALUE)
        assertThat(entity.body).isEqualTo("""{"status":"UP"}""")
    }
}
