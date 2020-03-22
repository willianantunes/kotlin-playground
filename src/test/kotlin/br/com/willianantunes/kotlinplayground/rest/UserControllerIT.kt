package br.com.willianantunes.kotlinplayground.rest

import br.com.willianantunes.kotlinplayground.business.User
import br.com.willianantunes.kotlinplayground.services.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.util.UriTemplate

val REQUEST_PATH_USERS = "/api/users/"
val FIND_USER_BY_LOGIN = "{login}"

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerIT @Autowired constructor(val restTemplate: TestRestTemplate, val userRepository: UserRepository) {
    @BeforeEach
    fun setup() {
        userRepository.deleteAll()
    }

    @Test
    fun `Should return no users as there is none registered`() {
        val entity = restTemplate.getForEntity(REQUEST_PATH_USERS, String::class.java)
        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(entity.headers.contentType.toString()).isEqualTo(MediaType.APPLICATION_JSON_VALUE)
        assertThat(entity.body).isEqualTo("[]")
    }

    @Test
    fun `Should return all registered users`() {
        val jafar = User("jafar-vizier", "jafar", "vizier")
        userRepository.save(jafar)

        val entity = restTemplate.getForEntity(REQUEST_PATH_USERS, String::class.java)
        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(entity.headers.contentType.toString()).isEqualTo(MediaType.APPLICATION_JSON_VALUE)
        assertThat(entity.body).isEqualTo(
            """[{"login":"jafar-vizier","firstname":"jafar","lastname":"vizier","id":${jafar.id}}]"""
        )
    }

    @Test
    fun `Should find some user by its login`() {
        val jafar = User("jafar-vizier", "jafar", "vizier")
        userRepository.save(jafar)

        val url = UriTemplate(REQUEST_PATH_USERS + FIND_USER_BY_LOGIN).expand(jafar.login)
        val entity = restTemplate.getForEntity(url, String::class.java)
        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(entity.headers.contentType.toString()).isEqualTo(MediaType.APPLICATION_JSON_VALUE)
        assertThat(entity.body).isEqualTo(
            """{"login":"jafar-vizier","firstname":"jafar","lastname":"vizier","id":${jafar.id}}"""
        )
    }

    @Test
    fun `Should return 404 when no user is found given its login`() {
        val someFakeLogin = "fake-login"
        val url = UriTemplate(REQUEST_PATH_USERS + FIND_USER_BY_LOGIN).expand(someFakeLogin)
        val entity = restTemplate.getForEntity(url, String::class.java)
        assertThat(entity.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        assertThat(entity.headers.contentType.toString()).isEqualTo(MediaType.APPLICATION_JSON_VALUE)
        assertThat(entity.body).contains("""status":404,"error":"Not Found","message":"This user does not exist""")
    }
}
