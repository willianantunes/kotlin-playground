package br.com.willianantunes.kotlinplayground.rest


import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

val REQUEST_PATH_GREETING = "/greeting"

@WebMvcTest
class GreetingControllerTest(@Autowired val mockMvc: MockMvc) {
    @Test
    fun `Should return hello word with ID 1 given this is the first request to the endpoint`() {
        mockMvc.perform(get(REQUEST_PATH_GREETING))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json("""{"id":1,"content":"Hello, World"}"""))
    }
}
